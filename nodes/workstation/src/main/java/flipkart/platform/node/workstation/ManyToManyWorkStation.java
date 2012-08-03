package flipkart.platform.node.workstation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import flipkart.platform.node.jobs.ManyToManyJob;
import flipkart.platform.workflow.job.ExecutionFailureException;
import flipkart.platform.workflow.job.JobFactory;
import flipkart.platform.workflow.link.Link;
import flipkart.platform.workflow.node.RetryPolicy;
import flipkart.platform.workflow.queue.ConcurrentQueue;
import flipkart.platform.workflow.queue.MessageCtx;
import flipkart.platform.workflow.queue.MessageCtxBatch;
import flipkart.platform.workflow.queue.HQueue;
import flipkart.platform.workflow.utils.DefaultRetryPolicy;
import flipkart.platform.workflow.utils.RefCounter;

/**
 * A {@link flipkart.platform.node.workstation.WorkStation} that executes {@link flipkart.platform.node.jobs
 * .ManyToManyJob}.
 * <p/>
 * The workstation tries to minimize the number of jobs enqueued by grouping
 * together {@link #maxJobsToGroup} jobs together.
 *
 * @param <I>
 * @param <O>
 * @author shashwat
 */
public class ManyToManyWorkStation<I, O> extends WorkStation<I, O, ManyToManyJob<I, O>>
{
    private final int maxJobsToGroup;
    private final long maxDelay;

    private final RefCounter jobsInQueue = new RefCounter(0);
    private final SchedulerThread schedulerThread = new SchedulerThread();

    public ManyToManyWorkStation(String name, int numThreads, HQueue<I> queue, RetryPolicy<I> retryPolicy,
        JobFactory<? extends ManyToManyJob<I, O>> jobFactory, Link<O> oLink, int maxJobsToGroup, long maxDelayMs)
    {
        super(name, numThreads, queue, retryPolicy, jobFactory, oLink);
        if (maxJobsToGroup <= 1 || maxDelayMs < 0)
        {
            throw new IllegalArgumentException("Illegal int arguments to: " + getClass().getSimpleName());
        }
        this.maxJobsToGroup = maxJobsToGroup;
        this.maxDelay = maxDelayMs;
        schedulerThread.start();
    }

    @Override
    protected void scheduleWorker()
    {
        final long currentJobsCount = jobsInQueue.offer();
        if (currentJobsCount == maxJobsToGroup)
        {
            schedulerThread.interrupt();
        }
    }

    @Override
    protected void shutdownResources(boolean awaitTermination) throws InterruptedException
    {
        schedulerThread.shutdown();
        schedulerThread.join();
        super.shutdownResources(awaitTermination);
    }

    protected class SchedulerThread extends Thread
    {
        private volatile boolean shutdown = false;

        @Override
        public void run()
        {
            while (!shutdown)
            {
                try
                {
                    Thread.sleep(maxDelay);
                }
                catch (InterruptedException e)
                {
                    //
                }

                do
                {
                    final int jobsCommitted = (int) jobsInQueue.take(maxJobsToGroup);
                    if (jobsCommitted == 0)
                        break;
                    try
                    {
                        executeWorker(new ManyToManyWorker(jobsCommitted));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } while (jobsInQueue.peek() > maxJobsToGroup || interrupted()); // loop while we are being interrupted
            }
        }

        public void shutdown()
        {
            shutdown = true;
            interrupt();
        }
    }

    private class ManyToManyWorker extends WorkerBase
    {
        private final int jobsCommitted;

        private ManyToManyWorker(int jobsCommitted)
        {
            this.jobsCommitted = jobsCommitted;
        }

        @Override
        protected void execute(ManyToManyJob<I, O> job)
        {
            if (jobsCommitted > 0)
            {
                final MessageCtxBatch<I> messageCtxBatch = queue.read(jobsCommitted);

                final List<I> jobList = new ArrayList<I>(jobsCommitted);
                for (MessageCtx<I> messageCtx : messageCtxBatch)
                {
                    jobList.add(messageCtx.get());
                }

                try
                {
                    final Collection<O> outList = job.execute(jobList);
                    for (O o : outList)
                    {
                        sendForward(o);
                    }

                    for (MessageCtx<I> messageCtx : messageCtxBatch)
                    {
                        messageCtx.ack();
                    }
                    messageCtxBatch.commit();
                }
                catch (ExecutionFailureException ex)
                {
                    for (MessageCtx<I> messageCtx : messageCtxBatch)
                    {
                        if (!retryPolicy.retry(ManyToManyWorkStation.this, messageCtx))
                        {
                            job.failed(
                                messageCtx.get(),
                                new ExecutionFailureException("No more retries after exception: " + ex.getMessage(),
                                    ex));
                        }
                    }
                }
                catch (Exception ex)
                {
                    for (MessageCtx<I> messageCtx : messageCtxBatch)
                    {
                        job.failed(messageCtx.get(), ex);
                        messageCtx.discard(MessageCtx.DiscardAction.ENQUEUE);
                    }
                }

            }
        }
    }

    public static <I, O> ManyToManyWorkStation<I, O> create(String name, int numThreads, int maxAttempts,
        JobFactory<? extends ManyToManyJob<I, O>> jobFactory, Link<O> link, int maxElements, long maxDelayMs)
    {
        return new ManyToManyWorkStation<I, O>(name, numThreads, new ConcurrentQueue<I>(),
            new DefaultRetryPolicy<I>(maxAttempts), jobFactory, link, maxElements, maxDelayMs);
    }
}

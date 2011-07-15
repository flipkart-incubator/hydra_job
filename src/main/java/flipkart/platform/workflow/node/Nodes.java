package flipkart.platform.workflow.node;

import flipkart.platform.workflow.job.DefaultJobFactory;
import flipkart.platform.workflow.job.ManyToManyJob;
import flipkart.platform.workflow.job.OneToManyJob;
import flipkart.platform.workflow.job.OneToOneJob;
import flipkart.platform.workflow.link.BroadcastLink;
import flipkart.platform.workflow.link.SelectorLink;
import flipkart.platform.workflow.link.SelectorLink.Selector;
import flipkart.platform.workflow.link.SingleLink;
import flipkart.platform.workflow.node.workstation.ManyToManyWorkStation;
import flipkart.platform.workflow.node.workstation.OneToManyWorkStation;
import flipkart.platform.workflow.node.workstation.OneToOneWorkStation;

/**
 * Helper factory to create different types of nodes.
 * 
 * @author shashwat
 * 
 */
public abstract class Nodes
{
    /**
     * Create a {@link OneToOneWorkStation} node with {@link SingleLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToOneJob} job type
     * @return new {@link OneToOneWorkStation} node
     * @throws NoSuchMethodException
     */
    public static <I, O> OneToOneWorkStation<I, O> newO2ONode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToOneJob<I, O>> jobClass)
            throws NoSuchMethodException
    {
        return OneToOneWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), SingleLink.<O> create());
    }

    /**
     * Create a {@link OneToOneWorkStation} node with {@link SelectorLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToOneJob} job type
     * @param selector
     *            {@link Selector}
     * @return new {@link OneToOneWorkStation} node
     * @throws NoSuchMethodException
     */
    public static <I, O> OneToOneWorkStation<I, O> newO2ONode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToOneJob<I, O>> jobClass,
            Selector<O> selector) throws NoSuchMethodException
    {
        return OneToOneWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass),
                SelectorLink.create(selector));
    }

    /**
     * Create a {@link OneToOneWorkStation} node with {@link BroadcastLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToOneJob} job type
     * @param clazz
     *            dummy parameter
     * @return new {@link OneToOneWorkStation} node
     * @throws NoSuchMethodException
     */
    public static <I, O> OneToOneWorkStation<I, O> newO2ONode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToOneJob<I, O>> jobClass,
            Class<? extends BroadcastLink<?>> clazz)
            throws NoSuchMethodException
    {
        return OneToOneWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), BroadcastLink.<O> create());
    }

    /**
     * Create a {@link OneToManyWorkStation} node with {@link SingleLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToManyJob} job type
     * @return new {@link OneToManyWorkStation} node
     * @throws NoSuchMethodException
     */
    public static <I, O> OneToManyWorkStation<I, O> newO2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToManyJob<I, O>> jobClass)
            throws NoSuchMethodException
    {
        return OneToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), SingleLink.<O> create());
    }

    /**
     * Create a {@link OneToManyWorkStation} node with {@link SelectorLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToManyJob} job type
     * @param selector
     *            {@link Selector}
     * @return new {@link OneToManyWorkStation} node
     * @throws NoSuchMethodException
     */
    public static <I, O> OneToManyWorkStation<I, O> newO2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToManyJob<I, O>> jobClass,
            Selector<O> selector) throws NoSuchMethodException
    {
        return OneToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass),
                SelectorLink.create(selector));
    }

    /**
     * Create a {@link OneToManyWorkStation} node with {@link BroadcastLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link OneToManyJob} job type
     * @param clazz
     *            dummy parameter
     * @return new {@link OneToManyWorkStation} node
     * @throws NoSuchMethodException
     */

    public static <I, O> OneToManyWorkStation<I, O> newO2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends OneToManyJob<I, O>> jobClass,
            Class<? extends BroadcastLink<?>> clazz)
            throws NoSuchMethodException
    {
        return OneToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), BroadcastLink.<O> create());
    }

    /**
     * Create a {@link ManyToManyWorkStation} node with {@link SingleLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link ManyToManyJob} job type
     * @param maxJobsToGroup
     *            number of jobs to group together
     * 
     * @return new {@link ManyToManyWorkStation} node
     * @throws NoSuchMethodException
     */

    public static <I, O> ManyToManyWorkStation<I, O> newM2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends ManyToManyJob<I, O>> jobClass,
            int maxJobsToGroup) throws NoSuchMethodException
    {
        return ManyToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), SingleLink.<O> create(),
                maxJobsToGroup);
    }

    /**
     * Create a {@link ManyToManyWorkStation} node with {@link SelectorLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link ManyToManyJob} job type
     * @param selector
     *            {@link Selector}
     * @param maxJobsToGroup
     *            number of jobs to group together
     * @return new {@link ManyToManyWorkStation} node
     * @throws NoSuchMethodException
     */

    public static <I, O> ManyToManyWorkStation<I, O> newM2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends ManyToManyJob<I, O>> jobClass,
            Selector<O> selector, int maxJobsToGroup)
            throws NoSuchMethodException
    {
        return ManyToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass),
                SelectorLink.create(selector), maxJobsToGroup);
    }

    /**
     * Create a {@link ManyToManyWorkStation} node with {@link BroadcastLink}.
     * 
     * @param name
     *            node name
     * @param numThreads
     *            number of threads for thread pool
     * @param maxAttempts
     *            max number of tries, 1 for no retry
     * @param jobClass
     *            {@link ManyToManyJob} job type
     * @param clazz
     *            dummy parameter
     * @param maxJobsToGroup
     *            number of jobs to group together
     * 
     * @return new {@link ManyToManyWorkStation} node
     * @throws NoSuchMethodException
     */

    public static <I, O> ManyToManyWorkStation<I, O> newM2MNode(String name,
            int numThreads, final int maxAttempts,
            final Class<? extends ManyToManyJob<I, O>> jobClass,
            Class<? extends BroadcastLink<?>> clazz, int maxJobsToGroup)
            throws NoSuchMethodException
    {
        return ManyToManyWorkStation.create(name, numThreads, maxAttempts,
                DefaultJobFactory.create(jobClass), BroadcastLink.<O> create(),
                maxJobsToGroup);
    }
}

package flipkart.platform.hydra.queue;

/**
 * User: shashwat
 * Date: 23/07/12
 */
public interface HQueue<I>
{
    void enqueue(I i);

    MessageCtx<I> read();

    MessageCtxBatch<I> read(int limit);

    int size();

    boolean isEmpty();
}
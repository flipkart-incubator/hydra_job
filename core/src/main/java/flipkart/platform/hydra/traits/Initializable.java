package flipkart.platform.hydra.traits;

/**
 * Interface for initialize-able classes. The object will be initialized using
 * {@link #init()} before being used. {@link #destroy()} is called once object
 * is no more required.
 * 
 * All jobs are required to be Initializable.
 * 
 * @author shashwat
 * 
 */
public interface Initializable
{
    /**
     * Initialization
     */
    public void init();

    /**
     * Destruction
     */
    public void destroy();

    class LifeCycle
    {
        public static <T> void initialize(T t)
        {
            if(t instanceof Initializable)
            {
                ((Initializable)t).init();
            }
        }

        public static <T> void destroy(T t)
        {
            if(t instanceof Initializable)
            {
                ((Initializable)t).destroy();
            }
        }
    }

}
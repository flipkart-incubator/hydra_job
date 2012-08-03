package flipkart.platform.workflow.job;

import flipkart.platform.workflow.utils.ObjectFactory;

/**
 * User: shashwat
 * Date: 02/08/12
 */
public class JobObjectFactory<J> implements ObjectFactory<J>
{
    private final JobFactory<J> jobFactory;

    public static <J> JobObjectFactory<J> from(JobFactory<J> jobFactory)
    {
        return new JobObjectFactory<J>(jobFactory);
    }

    public JobObjectFactory(JobFactory<J> jobFactory)
    {
        this.jobFactory = jobFactory;
    }

    @Override
    public J newObject()
    {
        return jobFactory.newJob();
    }
}

package flipkart.platform.hydra.link;

import flipkart.platform.hydra.topology.LinkTopology;
import flipkart.platform.hydra.traits.CanGroup;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * User: shashwat
 * Date: 12/08/12
 */
public class JoinTestBase
{
    public static final int[][] sequences = {
        {10, 10, 10},
        {0, 0, 0},
        {0, 10, 0},
        {0, 0, 10},
        {10, 0, 0},
        {10, 10, 0},
        {0, 10, 10}
    };
    protected LinkTopology topology;

    public static class Person implements CanGroup
    {
        public final int casualLeaves;
        public final int paidLeaves;
        public final int unPaidLeaves;
        public final String name;

        Person(String name, int casualLeaves, int paidLeaves, int unPaidLeaves)
        {
            this.name = name;
            this.casualLeaves = casualLeaves;
            this.paidLeaves = paidLeaves;
            this.unPaidLeaves = unPaidLeaves;
        }

        @Override
        public String getGroupId()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return "Person{" +
                "casualLeaves=" + casualLeaves +
                ", paidLeaves=" + paidLeaves +
                ", unPaidLeaves=" + unPaidLeaves +
                ", name='" + name + '\'' +
                '}';
        }
    }

    public class LeaveORJoinPredicate<O> implements JoinPredicate<O, Boolean>
    {
        @Override
        public boolean apply(ForkUnit<O, Boolean> input)
        {
            return input.getFinishedForks().values().contains(Boolean.TRUE);
        }
    }

    public class LeaveAndJoinPredicate<O> implements JoinPredicate<O, Boolean>
    {
        @Override
        public boolean apply(ForkUnit<O, Boolean> input)
        {
            return input.isFinished() && !input.getFinishedForks().values().contains(Boolean.FALSE);
        }
    }

    @BeforeClass
    public static void classSetup() throws Exception
    {
        //ConsoleReporter.enable(50, TimeUnit.MILLISECONDS);
    }

    @AfterClass
    public static void classTearDown() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        topology = new LinkTopology();
    }
}

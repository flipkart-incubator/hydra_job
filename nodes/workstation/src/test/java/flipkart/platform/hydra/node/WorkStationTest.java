package flipkart.platform.hydra.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import flipkart.platform.hydra.link.Selector;
import flipkart.platform.hydra.node.builder.WSBuilder;
import flipkart.platform.hydra.utils.UnModifiableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static flipkart.platform.hydra.link.LinkBuilder.link;
import static flipkart.platform.hydra.link.LinkBuilder.using;
import static org.junit.Assert.*;

/**
 * User: shashwat
 * Date: 12/08/12
 */
public class WorkStationTest extends TestBase
{

    private static final String RANDOM_PARA =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin placerat, tortor non varius elementum, " +
            "mi erat adipiscing felis, sed sollicitudin neque augue id orci. Integer egestas tempus feugiat. Nullam " +
            "semper " +
            "ornare varius. Aenean facilisis sagittis urna a viverra. In tempor odio eget felis scelerisque mollis. " +
            "Phasellus" +
            " eu posuere lacus. Vivamus lorem tortor, pharetra ut malesuada a, fringilla vitae arcu. Donec nec " +
            "mollis" +
            " neque. " +
            "Suspendisse iaculis ultricies semper. Fusce in magna eget magna sagittis tristique nec sit amet lacus. " +
            "Integer " +
            "sed est ligula. Sed et tincidunt elit. Donec commodo aliquam nibh in pellentesque. Fusce feugiat tempor " +
            "" +
            "metus sed tempus.";

    private Node<String, String> splitLineNode;
    private Node<String, String> splitWordNode;
    private Node<String, Map<String, Integer>> freqNode;
    private Node<String, String> wordSanitizer;
    private Node<String, String> toUpper;
    private MergeWordFrequencies mergeNode;

    @Before
    public void setUp() throws Exception
    {
        splitLineNode = WSBuilder.withO2MJob(SentenceToLines.class).build();
        splitWordNode = WSBuilder.withO2MJob(LinesToWords.class).build();
        freqNode = WSBuilder.withM2MJob(CalculateWordFrequency.class).build();
        wordSanitizer = WSBuilder.withO2OJob(WordSanitizer.class).build();

        toUpper = WSBuilder.withO2OJob(ToUpperCase.class).build();

        mergeNode = new MergeWordFrequencies("merger");
    }

    @After
    public void tearDown() throws Exception
    {
        //Thread.sleep(100000);
    }

    @Test
    public void testSimpleTopology() throws Exception
    {
        assertTrue("Map must be empty", mergeNode.mergeFrequencyMap.isEmpty());

        link(splitLineNode).to(splitWordNode).to(freqNode).to(mergeNode);

        splitLineNode.accept(RANDOM_PARA);
        splitLineNode.shutdown(true);

        assertFalse("Map cannot be empty", mergeNode.mergeFrequencyMap.isEmpty());
        assertEquals("comma [,] occurs 6 times", 6, (long) mergeNode.mergeFrequencyMap.get(","));
        assertEquals("'semper' occurs 2 times", 2, (long) mergeNode.mergeFrequencyMap.get("semper"));
        assertEquals("'viverra' occurs 1 times", 1, (long) mergeNode.mergeFrequencyMap.get("viverra"));
    }

    @Test
    public void testNodeReturnsNull() throws Exception
    {
        assertTrue("Map must be empty", mergeNode.mergeFrequencyMap.isEmpty());

        // wordSanitizer returns null if the input string does not consists of only alphabets
        link(splitLineNode).to(splitWordNode).to(wordSanitizer).to(freqNode).to(mergeNode);

        splitLineNode.accept(RANDOM_PARA);
        splitLineNode.shutdown(true);

        assertFalse("Map cannot be empty", mergeNode.mergeFrequencyMap.isEmpty());
        assertNull("There is no comma [,]", mergeNode.mergeFrequencyMap.get(","));
        assertEquals("'semper' occurs 2 times", 2, (long) mergeNode.mergeFrequencyMap.get("semper"));
        assertEquals("'viverra' occurs 1 times", 1, (long) mergeNode.mergeFrequencyMap.get("viverra"));
    }

    @Test
    public void testNodeSelectorLink() throws Exception
    {
        assertTrue("Map must be empty", mergeNode.mergeFrequencyMap.isEmpty());

        // wordSanitizer returns null if the input string does not consists of only alphabets
        link(splitLineNode).to(splitWordNode).toOnly(wordSanitizer);
        using(new VowelSelector()).linkFrom(wordSanitizer).to(toUpper, freqNode);
        link(toUpper).to(freqNode).toOnly(mergeNode);

        splitLineNode.accept(RANDOM_PARA);
        splitLineNode.shutdown(true);

        assertFalse("Map cannot be empty", mergeNode.mergeFrequencyMap.isEmpty());
        assertNull("There is no comma [,]", mergeNode.mergeFrequencyMap.get(","));
        assertEquals("'semper' occurs 2 times", 2, (long) mergeNode.mergeFrequencyMap.get("semper"));
        assertEquals("'viverra' occurs 1 times", 1, (long) mergeNode.mergeFrequencyMap.get("viverra"));
        assertEquals("'AMET' occurs 2 times", 2, (long) mergeNode.mergeFrequencyMap.get("AMET"));
        assertNull("'a' occurs 0 times", mergeNode.mergeFrequencyMap.get("a"));
    }

    private void printWordFrequencyMap()
    {
        for (Map.Entry<String, Integer> entry : mergeNode.mergeFrequencyMap.entrySet())
        {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }

    private static class VowelSelector implements Selector<String>
    {
        @Override
        public Collection<Node<String, ?>> select(String i, UnModifiableMap<String, Node<String, ?>> nodes)
        {
            final ArrayList<Node<String, ?>> list = new ArrayList<Node<String, ?>>(1);
            if (i.matches("^[aeiouAEIOU].*"))
            {
                list.add(nodes.get("ToUpperCase"));
            }
            else
            {
                list.add(nodes.get("CalculateWordFrequency"));
            }
            return list;
        }
    }

    private void fn() throws InterruptedException
    {
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                toUpper.accept("Hello! World");
            }
        });

        thread.start();
        thread.join();
    }

    @Test
    public void testWeakReference() throws Exception
    {
        // TODO: write test cases
        fn();

        Runtime.getRuntime().gc();
        System.out.println("Sleeping");
        Thread.sleep(100);
        System.out.println("done");
    }
}
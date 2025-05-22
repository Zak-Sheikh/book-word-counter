import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Unit tests of the BookWordCounter class, to be run with JUnit 5.
 *
 * To compile against the JUnit classes on the command line, use
 * a command like
 * <pre>
 *   javac -cp .:./junit-platform-console-standalone-1.10.2.jar BookWordCounter.java BookWordCounterTest.java
 * </pre>
 * In order to run the tests in this class on the command line, use
 * a command like
 * <pre>
 *   java -jar ./junit-platform-console-standalone-1.10.2.jar -cp . --scan-classpath .
 * </pre>
 * 
 * @author Zak Sheikh
 */
public class BookWordCounterTest {

    public BookWordCounterTest() {
    }

    private BookWordCounter counter;

    @BeforeEach
    void setUp() {
        counter = new BookWordCounter(); 
    }

    @BeforeAll
    public static void setUpClass() {
        System.out.println("Running BookWordCounterTest 📚");
    }
    
    // test file
    private static final String testFile = "testBook.txt";

    /**
     * Tests the processBook() method to ensure it reads and counts words from a file correctly.
     */
    @Test
    void testProcessBook() throws IOException {
        System.out.println("Running processBook() test");
        // writes a test file
        String testContent = "Hi hi, my name is Zak. Test, test, book book book";
        Files.write(Paths.get(testFile), testContent.getBytes());

        // processes the test file
        counter.processBook(testFile);

        // checks the word counts of the test file 
        assertEquals(2, counter.getWordCount("hi"),"Expected: 2");
        assertEquals(1, counter.getWordCount("name"),"Expected: 1");
        assertEquals(1, counter.getWordCount("zak"),"Expected: 1");
        assertEquals(2, counter.getWordCount("test"),"Expected: 2");
        assertEquals(3, counter.getWordCount("book"),"Expected: 3");

        // deletes the test file
        Files.deleteIfExists(Paths.get(testFile));
    }

    /**
     * Tests the processLine() method to ensure words are counted correctly.
     */
    @Test
    void testProcessLine() {
        System.out.println("Running processLine() test");
        // processes a line of text from TheGreatGatsby.txt
        counter.processLine("Hi hi, my name is Zak. It's Zak's book. Test, test, book book book 123 Nice-Book!");

        // checks the word counts
        assertEquals(2, counter.getWordCount("hi"),"Expected for hi: 2");
        assertEquals(1, counter.getWordCount("name"),"Expected for name: 1");
        assertEquals(2, counter.getWordCount("zak"),"Expected for zak: 2 (from 'Zak' and 'Zak's')");
        assertEquals(2, counter.getWordCount("test"),"Expected for test: 2");
        assertEquals(5, counter.getWordCount("book"),"Expected for book: 5");
        assertEquals(0, counter.getWordCount("123"),"Expected for 123: 0");
        assertEquals(1, counter.getWordCount("nice"),"Expected: 1");

    }
    /**
     * Tests that processLine() handles empty strings without errors.
     */
    @Test
    void testProcessLineEmpty() {
        System.out.println("Running processLine() with empty input");
        counter.processLine("");
        // We expect no words to be counted
        assertEquals(0, counter.getAllWordCounts().size(), "Expected no words to be counted for empty input.");
    }

    /**
     * Tests that processLine() handles punctuation and symbols correctly.
     */
    @Test
    void testProcessLineWithPunctuation() {
        System.out.println("Running processLine() with punctuation");
        counter.processLine("It's Zak's well-being. Hello-world! wow—really?");

        // Assumes punctuation is stripped and hyphenated words are split
        assertEquals(1, counter.getWordCount("it"), "Expected: 1 for 'it'");
        assertEquals(1, counter.getWordCount("zak"), "Expected: 1 for 'zak'");
        assertEquals(1, counter.getWordCount("well"), "Expected: 1 for 'well'");
        assertEquals(1, counter.getWordCount("being"), "Expected: 1 for 'being'");
        assertEquals(1, counter.getWordCount("hello"), "Expected: 1 for 'hello'");
        assertEquals(1, counter.getWordCount("world"), "Expected: 1 for 'world'");
        assertEquals(1, counter.getWordCount("wow"), "Expected: 1 for 'wow'");
        assertEquals(1, counter.getWordCount("really"), "Expected: 1 for 'really'");
    }



    private static final String outputFile = "testWordCountResults.txt";

    /**
     * Tests the saveResults() method to ensure word counts are written to a file correctly.
     */
    @Test
    void testSaveResults() throws IOException {
        System.out.println("Running saveResults() test");
        // processes a line of text
        counter.processLine("Hi hi, my name is Zak. It's Zak's book. Test, test, book book book 123 Nice-Book!");

        // saves the word count results
        counter.saveResults(outputFile);

        // checks if the file exists
        assertTrue(Files.exists(Paths.get(outputFile)), "Expected the output file to exist."); 

        // checks if the file is not empty
        assertTrue(Files.size(Paths.get(outputFile)) > 0, "Expected the output file to contain data.");

        // checks if the file contains the correct word counts
        String content = new String(Files.readAllBytes(Paths.get(outputFile)));
        assertTrue(content.contains("hi: 2"), "Expected: 'hi: 2' in the file.");
        assertTrue(content.contains("zak: 2"), "Expected: 'zak: 2' in the file.");
        assertTrue(content.contains("s: 1"), "Expected: 's: 1' in the file.");
        assertTrue(content.contains("book: 5"), "Expected: 'book: 5' in the file.");

        // deletes the output file
        Files.deleteIfExists(Paths.get(outputFile));
    }

    /**
     * Tests the getWordCount() method to ensure word counts are correct.
     */
    @Test
    void testGetWordCount() {
        System.out.println("Running getWordCount() test");
        // processes a line of text
        counter.processLine("Hi hi hi, test test, zak, book book book book book");

        // checks the word counts
        assertEquals(3, counter.getWordCount("hi"), "Expected: 3");
        assertEquals(2, counter.getWordCount("test"), "Expected: 2");
        assertEquals(1, counter.getWordCount("zak"), "Expected: 1");
        assertEquals(5, counter.getWordCount("book"), "Expected: 5");
       
    }

    /**
     * Tests processing a very large input to ensure performance and correctness.
     */
    @Test
    void testLargeInput() {
        System.out.println("Running testLargeInput()");
        // creates a very large input
        StringBuilder sb = new StringBuilder();
        // adds 100,000 instances of "hello world"`
        for (int i = 0; i < 100_000; i++) {
            sb.append("hello world ");
        }
        //
        String largeInput = sb.toString();
        counter.processLine(largeInput);
        // checks the word counts
        assertEquals(100_000, counter.getWordCount("hello"), "Expected 100,000 for 'hello'");
        assertEquals(100_000, counter.getWordCount("world"), "Expected 100,000 for 'world'");
        assertEquals(0, counter.getWordCount("banana"), "Expected 0 for 'banana'");
    }

    /**
     * Tests edge cases such as null input, punctuation, and case sensitivity.
     */
    @Test
    void testEdgeCases() {
        System.out.println("Running testEdgeCases()");
        
        // process null (should not crash)
        try {
            counter.processLine(null);
        } catch (Exception e) {
            fail("processLine(null) should not throw an exception.");
        }

        // process input with varied case
        counter.processLine("HELLO Hello HeLLo hello");

        // process with punctuation
        counter.processLine("!!! ???");

        // check case-insensitive counting
        assertEquals(4, counter.getWordCount("hello"), "Expected 4 for 'hello' (case-insensitive)");

        // special characters should be ignored
        assertEquals(0, counter.getWordCount("???"), "Expected 0 for '???'");
        assertEquals(0, counter.getWordCount("!!!"), "Expected 0 for '!!!'");
    }


    
}

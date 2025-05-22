import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * BookWordCounter is a Java program that reads a book from a text file,
 * counts the occurrences of each word, and stores the results in a HashMap.
 * The program outputs the words in alphabetical order along with their count.
 *
 * @author Zak Sheikh
 */
public class BookWordCounter {

    /** A HashMap to store word counts */
    private Map<String, Integer> wordCountMap;

    /**
     * Constructor for the BookWordCounter class.
     * Initializes the HashMap to store word counts.
     */
    public BookWordCounter() {
        // initializes the HashMap
        wordCountMap = new HashMap<>();
    }

    /**
     * 
     * Retrieves the count of a specific word.
     *
     * @param word The word to search for.
     * @return The count of the word, or 0 if not found.
     */
    public int getWordCount(String word) {
        // returns the count of the given word
        return wordCountMap.getOrDefault(word.toLowerCase(), 0);
    }

    /**
     * Returns the total number of words in the book.
     * 
     * @return The total word count.
     */
    public int totalWordCount() {
        return wordCountMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Reads a text file and counts occurrences of words.
     *
     * @param filePath The path of the text file to be read.
     */
    public void processBook(String filePath) throws IOException {
        // reads the file
        BufferedReader reader = new BufferedReader(new FileReader(filePath)); 
        // reads the file line by line
        String line;
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }   
    }

    /**
     * Processes a line of text by extracting words and counting their occurrences.
     *
     * @param line The line of text to process.
     */
    public void processLine(String line) {
        if (line == null || line.isEmpty()) {
            return; // do nothing on null or empty input
        }
        // removes special characters and convert to lowercase
        line = line.toLowerCase().replaceAll("[^a-z']", " "); 
        String[] words = line.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ").split("\\s+");
        // counts the frequency of each word
        for (String word : words) {
            // checks if the word is a single character or a contraction
            if (word.length() > 1 && !word.matches("'[a-z]")) {
                // adds the word to the HashMap
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
            }
        }
    }

    /**
     * Writes the word count result to a file in dictionary order.
     *
     * @param outputFilePath The file path to save the results.
     */
    public void saveResults(String outputFilePath) throws IOException {
        // creates a new file 
        PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
        // sorts words in dictionary order
        List<String> sortedWords = new ArrayList<>(wordCountMap.keySet());
        Collections.sort(sortedWords);

        // writes the total word count 
        int totalWords = wordCountMap.values().stream().mapToInt(Integer::intValue).sum();
        writer.println("Total words counted: " + totalWords); 

        // writes each word and its count 
        for (String word : sortedWords) {
            writer.println(word + ": " + wordCountMap.get(word));
        }

        // closes the file
        writer.close();

    }
    /**
     * Retrieves all word counts from the HashMap.
     *
     * @return A copy of the HashMap containing word counts.
     */
    public Map<String, Integer> getAllWordCounts() {
        // returns a copy of the HashMap
        return new HashMap<>(wordCountMap);
    }


}

import java.io.*;
import java.util.*;
/**
 * Main class to run the BookWordCounter program.
 *  Usage: java Main <filename>
 * 
 * @author Zak Sheikh
 */
public class Main {
    public static void main(String[] args) {
        // creates an instance of BookWordCounter
        BookWordCounter counter = new BookWordCounter();
        Scanner scanner = new Scanner(System.in);

        // checks if a filename was given in the command-line
        if (args.length < 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }

        // gets file name from command-line argument
        String inputFile = args[0]; 
        File file = new File(inputFile);
        // gets the name of the book
        String fileNameOnly = file.getName().replace(".txt", ""); 
        // creates the output file name
        String outputFile = "../output/WordCountResults-" + fileNameOnly + ".txt"; 

        try {
            // processes the book and saves the results
            counter.processBook(inputFile);
            counter.saveResults(outputFile);
            System.out.println("Results saved to " + outputFile);
            System.out.println("Word count completed for " + inputFile);

            // CLI: allows user to ask for word counts of specific words
            while (true) {
                // asks the user for a word
                System.out.print("Enter a word to check its count (or type 'exit' to quit): ");
                // reads the user's input
                String word = scanner.nextLine().trim().toLowerCase();

                // checks if the user wants to exit
                if (word.equals("exit")) {
                    // exits the program
                    System.out.println("Goodbye");
                    break;
                }
                // checks if the word is in the HashMap
                int count = counter.getWordCount(word);
                // prints the word count for the given word
                System.out.println("The word '" + word + "' appears " + count + " times.");
            }
            
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

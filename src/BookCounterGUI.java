import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;


/**
 * BookCounterGUI presents a GUI to load books and count words.
 * It uses BookWordCounter to process and query word frequencies.
 * 
 * The user can:
 * - Load a book file with a file chooser.
 * - View the total word count.
 * - Enter a word and see how many times it appears.
 * 
 * @author Zak Sheikh
 */
public class BookCounterGUI extends JFrame {

    // intialize the gui components
    private BookWordCounter counter;
    private JTextField wordInput;
    private JTable wordTable;
    private DefaultTableModel tableModel;
    private JLabel fileLabel;
    private JComboBox<String> sortModeSelector;
    private JCheckBox stopWordsCheckbox;


    // constructor
    public BookCounterGUI() {
        // sets window title
        super("📖 Book Word Counter"); 
        counter = new BookWordCounter(); 

        // sets window layout
        setLayout(new BorderLayout());

        // ----- Top Panel: File chooser -----
        // creates a button to choose a file
        JButton chooseFileBtn = new JButton("Choose Book File"); 
        // creates a label to display the selected file
        fileLabel = new JLabel("No file selected");
        // adds the button and label
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(chooseFileBtn);
        topPanel.add(fileLabel);
        
        // ----- Filter Panel: Live search -----
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField filterInput = new JTextField(20);
        filterPanel.add(new JLabel("Search Table:"));
        filterPanel.add(filterInput);
        
        // Combines both panels into one vertical wrapper
        JPanel topWrapperPanel = new JPanel();
        topWrapperPanel.setLayout(new BoxLayout(topWrapperPanel, BoxLayout.Y_AXIS));
        topWrapperPanel.add(topPanel);
        topWrapperPanel.add(filterPanel);

        // Add the wrapper to the top of the frame
        add(topWrapperPanel, BorderLayout.NORTH);

        // adds a document listener to track input changes in the filter field
        filterInput.getDocument().addDocumentListener(new DocumentListener() {

            // triggers when characters are added
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            // triggers when characters are removed
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            // triggers when characters are changed
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            // filters the table
            private void filterTable() {
                // gets the search term from the input field
                String search = filterInput.getText().trim().toLowerCase();
                // creates a new row sorter based on the table model
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                // applies the sorter to the word table
                wordTable.setRowSorter(sorter);

                // if input is empty, remove any filters
                if (search.length() == 0) {
                    // removes any existing filters
                    sorter.setRowFilter(null);
                } else {
                    // creates a regex filter to match the search term
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
                }
            }
        });


        // ----- Center Panel: JTable for word display -----
        // creates a table to display word counts
        String[] columnNames = {"Word", "Count"};
        // creates a table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            // returns false to indicate that the table is not editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        // creates a table
        wordTable = new JTable(tableModel);
        // creates a scroll pane
        JScrollPane scrollPane = new JScrollPane(wordTable);
        // adds the scroll pane
        add(scrollPane, BorderLayout.CENTER);
    
        // Adds mouse click listener for dictionary lookup
        wordTable.addMouseListener(new MouseAdapter() {
            @Override
            // triggered when a word is double-clicked
            public void mouseClicked(MouseEvent e) {
                // gets the selected row
                int row = wordTable.getSelectedRow();
                // checks if a row is selected
                if (row != -1 && e.getClickCount() == 2) {
                    // gets the word from the selected row and its model index
                    int modelRow = wordTable.convertRowIndexToModel(row);
                    String word = tableModel.getValueAt(modelRow, 0).toString();

                    // opens a popup dialog with word definition from Dictionary API
                    SwingUtilities.invokeLater(() -> new DictionaryLookupDialog(BookCounterGUI.this, word).setVisible(true));

                }
            }
        });


        // ----- Top Panel: Stop word filtering checkbox -----
        // creates a checkbox for removing stop words
        stopWordsCheckbox = new JCheckBox("Remove common stop words");
        // unchecked by default
        stopWordsCheckbox.setSelected(false);
        // adds to the top panel
        topPanel.add(stopWordsCheckbox);
        // adds action listener
        stopWordsCheckbox.addActionListener(e -> populateTable());


        // ----- Top Panel: Sort mode selector -----
        // creates an array of sort options
        String[] sortOptions = {"Alphabetical", "Frequency (High to Low)"};
        // creates a combo box
        sortModeSelector = new JComboBox<>(sortOptions);
        // adds label and combo box
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortModeSelector);
        // adds action listener to combo box
        sortModeSelector.addActionListener(e -> populateTable());
        // sets default sort mode
        sortModeSelector.setSelectedIndex(1);


        // ----- Bottom Panel: Word input and search button -----
        JPanel bottomPanel = new JPanel(new FlowLayout());
        wordInput = new JTextField(15);
        // allows the user to enter a word
        JButton searchBtn = new JButton("Check Word Count");
        // adds the word input and search button
        bottomPanel.add(new JLabel("Word:"));
        bottomPanel.add(wordInput);
        bottomPanel.add(searchBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ----- Bottom Panel: Chart button -----
        JButton chartBtn = new JButton("Show Top 10 Chart");
        // adds the chart button
        bottomPanel.add(chartBtn);
        // adds action listener to chart button
        chartBtn.addActionListener(e -> {
            // gets all word counts and filters out stop words
            Map<String, Integer> wordCounts = counter.getAllWordCounts();
            List<String> stopWords = stopWordsCheckbox.isSelected() ? getStopWords() : List.of();

            // list to store filtered word counts
            List<Map.Entry<String, Integer>> filtered = new ArrayList<>(); 
            // filters out stop words
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) { 
                if (!stopWords.contains(entry.getKey())) {
                    filtered.add(entry);
                }
            }
            
            // sorts the word counts
            filtered.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            // gets the top 10
            List<Map.Entry<String, Integer>> top10 = filtered.subList(0, Math.min(10, filtered.size()));
            // displays the chart
            SwingUtilities.invokeLater(() -> new BarChartWindow(top10));
        });


        // ----- Bottom Panel: Export button -----
        JButton exportBtn = new JButton("Save as CSV");
        // adds the export button to the bottom panel
        bottomPanel.add(exportBtn);
        exportBtn.addActionListener(e -> {
            // creates a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            
            // suggest default file name
            fileChooser.setSelectedFile(new File("WordCountResults.csv"));

            // allows the user to save the file
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // gets the selected file
                File file = fileChooser.getSelectedFile();
                // writes the table data to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    // write headers
                    writer.println("Word,Count");
                    // write table rows
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String word = tableModel.getValueAt(i, 0).toString();
                        String count = tableModel.getValueAt(i, 1).toString();
                        writer.println(word + "," + count);
                    }
                    // display success message
                    JOptionPane.showMessageDialog(this,
                        "✅ CSV file saved to:\n" + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    // display error message
                    JOptionPane.showMessageDialog(this,
                        "❌ Error saving file: " + ex.getMessage(),
                        "Export Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // ----- File chooser button action -----
        chooseFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // allows the user to select a file
            int result = fileChooser.showOpenDialog(this);
            // if the user selects a file
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                fileLabel.setText(file.getName());
                try {
                    counter = new BookWordCounter();
                    counter.processBook(file.getAbsolutePath());
                    populateTable();  // new helper method to display data in table

                } catch (IOException ex) {
                    // displays an error message
                    JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        // ----- Search button action -----
        searchBtn.addActionListener(e -> {
            // gets the word entered by the user
            String word = wordInput.getText().trim().toLowerCase();
            // displays the word count
            if (!word.isEmpty()) {
                // gets the word count
                int count = counter.getWordCount(word);
                // displays the word count in a message dialog
                JOptionPane.showMessageDialog(this, "The word '" + word + "' appears " + count + " times.",
                "Word Count Result", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        // ----- Window settings -----
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    /** Helper method to display data in table */
    private void populateTable() {
        // clears the table
        tableModel.setRowCount(0); 
        // gets all word counts
        Map<String, Integer> wordCounts = counter.getAllWordCounts();
        // sorts the word counts
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCounts.entrySet());
        // gets the selected sort mode
        String selectedSort = (String) sortModeSelector.getSelectedItem();
        // sorts the word counts
        if ("Frequency (High to Low)".equals(selectedSort)) {
            // Sort by frequency
            entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        } else {
            // Sort alphabetically
            entries.sort(Map.Entry.comparingByKey());
        }

        // gets the stop words
        List<String> stopWords = stopWordsCheckbox.isSelected() ? getStopWords() : List.of();
        // adds the word counts to the table 
        for (Map.Entry<String, Integer> entry : entries) {
            // filters out stop words
            if (!stopWords.contains(entry.getKey())) {
                tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
        }
    }

    /** Helper method to get stop words */
    private List<String> getStopWords() {
        // returns a list of stop words
        return List.of(
            "a", "an", "and", "are", "as", "at", "be", "been", "being", "but", "by",
            "do", "did", "does", "for", "from", "had", "has", "have", "he", "her", "him", "his", "i",
            "if", "in", "into", "is", "it", "it's", "me", "my", "no", "not", "of",
            "on", "or", "so", "such", "that", "the", "their", "them", "then", "there",
            "these", "they", "this", "to", "was", "we", "were", "what", "when", "where",
            "which", "who", "will", "with", "would", "you", "your"
        );
    }



    // main
    public static void main(String[] args) {
        // creates the GUI
        SwingUtilities.invokeLater(BookCounterGUI::new);
    }
}


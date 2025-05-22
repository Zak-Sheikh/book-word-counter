import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * DictionaryLookupDialog is a pop-up window that shows the definition of a word
 * using the Free Dictionary API.
 * 
 * It is triggered from the main GUI when a word is double-clicked in the table.
 * The API fetch is done asynchronously to prevent UI blocking.
 * 
 * Example API: https://api.dictionaryapi.dev/api/v2/entries/en/<word>
 * 
 * @author Zak Sheikh
 */
public class DictionaryLookupDialog extends JDialog {

    /**
     * Constructs a modal dialog to display word definitions.
     * 
     * @param parent The parent JFrame that triggered this dialog.
     * @param word   The word to define.
     */
    public DictionaryLookupDialog(JFrame parent, String word) {
        // sets dialog title and modality
        super(parent, "Definition: " + word, true);
        setLayout(new BorderLayout());

        // creates a non-editable text area to show the definition
        JTextArea definitionArea = new JTextArea();
        definitionArea.setEditable(false);

        // wraps the text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(definitionArea);
        add(scrollPane, BorderLayout.CENTER);

        // sets the dialog size and centers it
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // fetches the definition in a background thread
        new Thread(() -> {
            try {
                // builds the URL for API request
                String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");

                // reads the API response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // parses the JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject firstEntry = jsonArray.getJSONObject(0);
                JSONArray meanings = firstEntry.getJSONArray("meanings");

                // formats the definition content
                StringBuilder definitions = new StringBuilder();
                for (int i = 0; i < meanings.length(); i++) {
                    JSONObject meaning = meanings.getJSONObject(i);
                    String partOfSpeech = meaning.getString("partOfSpeech");
                    JSONArray defs = meaning.getJSONArray("definitions");

                    for (int j = 0; j < defs.length(); j++) {
                        JSONObject def = defs.getJSONObject(j);
                        definitions.append(partOfSpeech).append(": ").append(def.getString("definition")).append("\n");
                        if (def.has("example")) {
                            definitions.append("Example: ").append(def.getString("example")).append("\n");
                        }
                        definitions.append("\n");
                    }
                }

                // updates the GUI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> definitionArea.setText(definitions.toString()));
            } catch (Exception e) {
                // shows error in the text area
                SwingUtilities.invokeLater(() ->
                    definitionArea.setText("❌ Error fetching definition:\n" + e.getMessage())
                );
            }
        }).start();
    }
}

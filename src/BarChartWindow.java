import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * BarChartWindow is a pop-up window that displays a bar chart
 * of the top 10 most frequent words from a selected book file.
 * 
 * It uses a custom JPanel (BarChartPanel) to render a scalable,
 * labeled chart with counts.
 * 
 * @author Zak Sheikh
 */
public class BarChartWindow extends JFrame {

    /**
     * Constructs the chart window using the top word-frequency entries.
     * 
     * @param topWords A list of the top 10 word-count pairs
     */
    public BarChartWindow(List<Map.Entry<String, Integer>> topWords) {
        // sets window title
        setTitle("Top 10 Words - Bar Chart");
        // closes only this window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // sets window size
        setSize(700, 500);
        // centers the window
        setLocationRelativeTo(null);
        // adds the chart panel
        add(new BarChartPanel(topWords));
        // makes the window visible
        setVisible(true);
    }

    /**
     * BarChartPanel is a custom JPanel that draws a proportional bar chart
     * of the word frequencies using Java 2D graphics.
     */
    private static class BarChartPanel extends JPanel {
        // stores the top 10 word-count entries
        private final List<Map.Entry<String, Integer>> topWords;

        /**
         * Constructor initializes the panel with word data and white background.
         * 
         * @param topWords A list of word-count pairs to display
         */
        public BarChartPanel(List<Map.Entry<String, Integer>> topWords) {
            this.topWords = topWords;
            // sets background color
            setBackground(Color.WHITE);
        }

        /**
         * Draws the bar chart by scaling bar height relative to max word count.
         * Also draws word labels and frequency numbers for each bar.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (topWords == null || topWords.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            // enables anti-aliasing for smoother graphics
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // chart layout variables
            int padding = 50;
            int chartWidth = getWidth() - 2 * padding;
            int chartHeight = getHeight() - 2 * padding;
            int barWidth = chartWidth / topWords.size() - 10;
            int maxBarHeight = chartHeight - 50;
            int maxCount = topWords.get(0).getValue();

            // draws each bar and label
            for (int i = 0; i < topWords.size(); i++) {
                Map.Entry<String, Integer> entry = topWords.get(i);
                String word = entry.getKey();
                int count = entry.getValue();

                // calculates position and height
                int x = padding + i * (barWidth + 10);
                int barHeight = (int) ((count / (double) maxCount) * maxBarHeight);
                int y = getHeight() - padding - barHeight;

                // draws the filled bar
                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(x, y, barWidth, barHeight);

                // draws the bar outline
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, barWidth, barHeight);

                // draws the count above the bar
                g2.drawString(String.valueOf(count), x + barWidth / 4, y - 5);
                // draws the word label below the bar
                g2.drawString(word, x + barWidth / 4, getHeight() - padding + 15);
            }

            // draws horizontal axis
            g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);
        }
    }
}

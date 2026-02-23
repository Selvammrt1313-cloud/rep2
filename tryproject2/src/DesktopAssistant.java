import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DesktopAssistant {

    static JTextArea chatArea;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Java Desktop AI Assistant");
        frame.setSize(450, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Dark Theme
        Color bg = new Color(20, 20, 20); 

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(bg);
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 15));

        JScrollPane scroll = new JScrollPane(chatArea);

        JTextField inputField = new JTextField();
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setForeground(Color.CYAN);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 15));

        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(70, 70, 70));
        sendBtn.setForeground(Color.WHITE);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> {
            String text = inputField.getText();
            appendUser(text);
            respond(text.toLowerCase());
            inputField.setText("");
        });

        frame.setVisible(true);
    }

    static void appendUser(String msg) {
        chatArea.append("You: " + msg + "\n");
    }

    static void appendBot(String msg) {
        chatArea.append("Bot: " + msg + "\n");
    }

    static void respond(String input) {

        try {

            // Normal Conversation
            if (input.contains("hi") || input.contains("hello")) {
                appendBot("Hello!  How can I help you?");
            }

            else if (input.contains("how are you")) {
                appendBot("I am fine! What about you?");
            }

            else if (input.contains("date")) {
                appendBot("Today's date is " + LocalDate.now());
            }

            else if (input.contains("time")) {
                appendBot("Current time is " + LocalTime.now().withNano(0));
            }

            else if (input.contains("youtube")) {
                appendBot("Opening YouTube...");
                Desktop.getDesktop().browse(new URI("https://www.youtube.com"));
            }

            else if (input.contains("bye")) {
                appendBot("Goodbye ");
            }

            // Question Detection (what, who, where, when, wht)
           else if (input.startsWith("what") || input.startsWith("who") ||
         input.startsWith("where") || input.startsWith("when") ||
         input.startsWith("wht")) {

    appendBot("Searching...");

    new SwingWorker<String, Void>() {

        @Override
        protected String doInBackground() {
            return searchGoogle(input);
        }

        @Override
        protected void done() {
            try {
                String answer = get();

                if (answer == null || answer.trim().isEmpty()) {

                    appendBot("Opening Google...");
                    Desktop.getDesktop().browse(
                            new URI("https://www.google.com/search?q=" + input.replace(" ", "+")));

                } else {
                    appendBot(answer);
                }

            } catch (Exception e) {
                appendBot("Error occurred.");
            }
        }

    }.execute();
}
            // Unknown → Open Google
            else {
                appendBot("I am not sure. Opening Google...");
                Desktop.getDesktop().browse(
                        new URI("https://www.google.com/search?q=" + input.replace(" ", "+")));
            }

        } catch (Exception e) {
            appendBot("Error occurred.");
        }
    }

    // ===== Improved Google Answer Extraction =====
    static String searchGoogle(String query) {

        try {
            String url = "https://www.google.com/search?q=" + query.replace(" ", "+");

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // Try multiple selectors for answer
            Element answer = doc.selectFirst("div.BNeawe.iBp4i.AP7Wnd"); // main snippet
            if (answer == null)
                answer = doc.selectFirst("div.BNeawe"); // fallback
            if (answer == null)
                answer = doc.selectFirst("div.VwiC3b"); // sometimes description
            if (answer == null)
                answer = doc.selectFirst("span.hgKElc"); // knowledge card

            if (answer != null) {
                return answer.text();
            }

        } catch (Exception e) {
            return "Sorry, I couldn't fetch the answer.";
        }

        return "No answer found.";
    }
}
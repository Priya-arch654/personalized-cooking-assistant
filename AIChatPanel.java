package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class AIChatPanel extends JPanel {

    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";
    private BufferedImage blurredBackground; // blurred version

    public AIChatPanel() {
        setLayout(new BorderLayout(10, 10));
        prepareBlurredBackground(backgroundImagePath); // create blur
        setOpaque(false);

        // --- Stylish title with big font ---
        JLabel title = new JLabel("AI Meal Assistant", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36)); // BIG FONT
        title.setForeground(Color.BLACK);
        add(title, BorderLayout.NORTH);

        // --- Chat area with big font ---
        JTextArea chatArea = new JTextArea(20, 60);
        chatArea.setEditable(false);
        chatArea.setOpaque(false);
        chatArea.setForeground(Color.BLACK);
        chatArea.setFont(new Font("Monospaced", Font.BOLD, 24)); // BIG FONT

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // --- Input panel ---
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.BOLD, 24)); // BIG FONT
        JButton sendBtn = UIUtils.colorfulButton("Send", new Color(0, 150, 136));
        sendBtn.setFont(new Font("Arial", Font.BOLD, 24)); // BIG FONT

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180)); // semi-transparent white
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // --- Action listeners ---
        sendBtn.addActionListener(e -> sendMessage(chatArea, inputField));
        inputField.addActionListener(e -> sendMessage(chatArea, inputField));
    }

    private void prepareBlurredBackground(String path) {
        if (path == null || path.isEmpty()) return;

        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            BufferedImage original = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = original.createGraphics();
            g2.drawImage(img, 0, 0, null);
            g2.dispose();

            // 5x5 blur kernel
            float[] matrix = new float[25];
            Arrays.fill(matrix, 1f / 25f);
            ConvolveOp op = new ConvolveOp(new Kernel(5, 5, matrix), ConvolveOp.EDGE_NO_OP, null);

            // Apply multiple passes for smoother blur
            BufferedImage temp = original;
            for (int i = 0; i < 3; i++) {
                temp = op.filter(temp, null);
            }
            blurredBackground = temp;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(JTextArea chatArea, JTextField inputField) {
        String userMsg = inputField.getText().trim();
        if (userMsg.isEmpty()) return;
        chatArea.append("You: " + userMsg + "\n");
        inputField.setText("");

        SwingUtilities.invokeLater(() -> {
            try {
                String response = getAIResponse(userMsg);
                chatArea.append("AI: " + response + "\n\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } catch (Exception ex) {
                chatArea.append("AI: Error fetching response: " + ex.getMessage() + "\n\n");
            }
        });
    }

    private String getAIResponse(String prompt) {
        try {
            URI uri = new URI("https://router.huggingface.co/v1/chat/completions");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer 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");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{"
                    + "\"model\": \"meta-llama/Llama-3.1-8B-Instruct\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]"
                    + "}";

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader er = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder err = new StringBuilder();
                String line;
                while ((line = er.readLine()) != null) err.append(line);
                return "AI ERROR (" + responseCode + "): " + err.toString();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);

            String result = response.toString();
            int start = result.indexOf("\"content\":\"");
            if (start != -1) {
                start += "\"content\":\"".length();
                int end = result.indexOf("\"", start);
                if (end != -1) {
                    String text = result.substring(start, end);
                    text = text.replace("\\n", "\n").replace("\\\"", "\"");
                    return text;
                }
            }
            return result;
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blurredBackground != null) {
            g.drawImage(blurredBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(245, 245, 245));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

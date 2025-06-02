package client;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientGUI extends JFrame {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea messageArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    public ClientGUI() {
        frame = new JFrame("Dosya Paylaşım Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Kullanıcı Adı:");
        userLabel.setBounds(50, 30, 100, 25);
        frame.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 180, 25);
        frame.add(usernameField);

        JLabel passLabel = new JLabel("Parola:");
        passLabel.setBounds(50, 70, 100, 25);
        frame.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 180, 25);
        frame.add(passwordField);

        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(150, 110, 100, 30);
        frame.add(loginButton);

        messageArea = new JTextArea();
        messageArea.setBounds(50, 160, 300, 80);
        messageArea.setEditable(false);
        frame.add(messageArea);

        loginButton.addActionListener(e -> handleLogin());

        frame.setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        new Thread(() -> {
            try (
                Socket socket = new Socket("localhost", 12345);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                in.readLine(); // USERNAME:
                out.println(username);

                in.readLine(); // PASSWORD:
                out.println(password);

                String response = in.readLine(); // BU değişkeni final yap

                System.out.println("SERVER RESPONSE: " + response);

                if ("LOGIN_SUCCESS".equals(response)) {
                    String groupLine = in.readLine();
                    String groupName = "NONE";
                    if (groupLine != null && groupLine.startsWith("GROUP:")) {
                        groupName = groupLine.substring(6).trim();
                    }

                    final String finalUsername = username;
                    final String finalGroupName = groupName;

                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                        new FileWriteGUI(finalUsername, finalGroupName);
                    });

                } else {
                    final String finalResponse = response; // 🔥 burada final değişken yarat
                    SwingUtilities.invokeLater(() -> {
                        messageArea.setText("❌ Giriş başarısız: " + finalResponse);
                        System.out.println("LOGIN FAILED: " + finalResponse);
                    });
                }

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    messageArea.setText("⚠️ Sunucuya bağlanılamadı.");
                });
                e.printStackTrace();
            }
        }).start();

    }


}

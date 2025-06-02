package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class FileWriteGUI extends JFrame {
    private JTextArea textArea;
    private JButton saveButton;
    private String username;
    private String groupName;

    public FileWriteGUI(String username, String groupName) {
        this.username = username;
        this.groupName = groupName;

        setTitle("Dosya Yazma - Kullanıcı: " + username + " Grup: " + groupName);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        saveButton = new JButton("Kaydet");
        add(saveButton, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveContent());

        setVisible(true);
    }

    private void saveContent() {
        String content = textArea.getText();

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("SAVE_MODE");
            out.println(username);
            out.println(groupName);
            out.println(content);

            String response = in.readLine();
            JOptionPane.showMessageDialog(this, response);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Sunucuya bağlanılamadı!");
            e.printStackTrace();
        }
    }
}

package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class EditorFrame extends JFrame {
    private JTextArea textArea = new JTextArea();
    private JList<String> fileList = new JList<>();
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public EditorFrame(Socket socket, BufferedReader in, PrintWriter out, String username) {
        this.in = in;
        this.out = out;
        this.username = username;

        setTitle("Metin Düzenleyici - " + username);
        setSize(600, 400);
        setLayout(new BorderLayout());

        textArea.setLineWrap(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton shareBtn = new JButton("Dosya Paylaş");
        JButton saveBtn = new JButton("Kaydet");
        panel.add(shareBtn);
        panel.add(saveBtn);
        add(panel, BorderLayout.SOUTH);

        shareBtn.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog("Dosya adı:");
            out.println("SHARE " + filename);
        });

        saveBtn.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog("Hangi dosyayı kaydetmek istiyorsun?");
            out.println("UPDATE " + filename + " " + textArea.getText());
        });

        new Thread(() -> listenServer()).start();
    }

    private void listenServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("FILES")) {
                    String[] files = line.substring(6).split(",");
                    fileList.setListData(files);
                } else if (line.startsWith("CONTENT")) {
                    textArea.setText(line.substring(8));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu başlatıldı. Bağlantılar bekleniyor...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Yeni bağlantı: " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        Map<String, String> userCredentials = UserManager.loadUsersFromFile();

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            out.println("USERNAME: ");
            out.flush();
            String username = in.readLine();

            out.println("PASSWORD: ");
            out.flush();
            String password = in.readLine();

            if (username != null && password != null
                && userCredentials.containsKey(username)
                && userCredentials.get(username).equals(password)) {

                out.println("🎉 LOGIN_SUCCESS");
                saveUserContent(username, "Giriş yaptı.");

                // Burada client'tan "CONTENT:..." ile başlayan satırı bekle
                String contentLine = in.readLine();
                if (contentLine != null && contentLine.startsWith("CONTENT:")) {
                    String content = contentLine.substring("CONTENT:".length());
                    saveUserContent(username, content);
                    out.println("CONTENT_SAVED");
                }

            } else {
                out.println("❌ LOGIN_FAILED");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }


    private static void saveUserContent(String username, String content) {
        File userFile = new File("user_contents/" + username + ".txt");
        userFile.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write(content);
            writer.newLine();
            System.out.println("📁 İçerik yazıldı: " + userFile.getPath());
        } catch (IOException e) {
            System.err.println("❌ Dosyaya yazma hatası: " + e.getMessage());
        }
    }

}

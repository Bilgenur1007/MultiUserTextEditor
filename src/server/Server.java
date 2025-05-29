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
            String username = in.readLine();

            out.println("PASSWORD: ");
            String password = in.readLine();

            // 1) Oturum doğrulama
            boolean authenticated = username != null
                && password != null
                && userCredentials.containsKey(username)
                && userCredentials.get(username).equals(password);

            if (authenticated) {
                out.println("LOGIN_SUCCESS");
                saveUserContent(username, "Giriş yaptı.");

                // 2) Grup listesini gönder ve seçim al
                List<String> groups = UserManager.getUserGroups(username);
                if (groups.isEmpty()) {
                    out.println("❌ Hiç grubunuz yok. Bağlantı kesiliyor.");
                    return;
                }
                out.println("🎯 Ait olduğunuz gruplar: " + String.join(", ", groups));
                out.println("📌 Bir grup seçin:");
                String chosenGroup = in.readLine();

                if (!groups.contains(chosenGroup)) {
                    out.println("🚫 Bu gruba erişim yetkiniz yok. Bağlantı kesiliyor.");
                    return;
                }

                // 3) Seçilen grubun dosyalarını listele
                sendGroupFilesForOne(out, chosenGroup);

                // 4) İçerik ekleme adımı
                out.println("📄 Bu gruba eklemek istediğiniz içeriği girin:");
                String content = in.readLine();
                saveToGroup(chosenGroup, username, content);
                out.println("✅ İçerik eklendi: " + chosenGroup);

            } else {
                out.println("LOGIN_FAILED");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignore) {}
        }
    }

    private static void saveUserContent(String username, String content) {
        File userFile = new File("user_contents/" + username + ".txt");
        userFile.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile, true))) {
            writer.println(content);
            // writer.flush();  // PrintWriter, println ile otomatik flush eder
            System.out.println("📁 İçerik yazıldı: " + userFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Dosyaya yazılamadı: " + e.getMessage());
        }
    }

    private static void sendGroupFiles(PrintWriter out, String username) {
        List<String> groups = UserManager.getUserGroups(username);
        out.println("🎯 Senin grupların: " + String.join(", ", groups));

        for (String group : groups) {
            File groupDir = new File("group_files/" + group);
            if (groupDir.exists() && groupDir.isDirectory()) {
                File[] files = groupDir.listFiles();
                if (files != null && files.length > 0) {
                    out.println("📂 Grup: " + group + " dosyaları:");
                    for (File f : files) {
                        out.println("   📄 " + f.getName());
                    }
                } else {
                    out.println("📁 Grup: " + group + " klasörü boş.");
                }
            } else {
                out.println("🚫 Grup klasörü bulunamadı: " + group);
            }
        }

        out.println("✅ Dosya listesi gönderildi.");
    }
    private static void sendGroupFilesForOne(PrintWriter out, String group) {
        File dir = new File("group_files/" + group);
        out.println("📂 " + group + " dosyaları:");
        for (File f : dir.listFiles((d, name) -> name.endsWith(".txt"))) {
            out.println(" - " + f.getName());
        }
    }

    private static void saveToGroup(String group, String username, String content) {
        File target = new File("group_files/" + group + "/" + username + "_post.txt");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(target, true))) {
            w.write(content);
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

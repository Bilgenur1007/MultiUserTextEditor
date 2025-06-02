package client;

import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost"; // Sunucu IP'si
    private static final int SERVER_PORT = 12345;        // Sunucu portu

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
        ) {
            // Kullanıcı adı ve parola gir
            System.out.print(in.readLine()); // USERNAME:
            String username = scanner.nextLine();
            out.println(username);

            System.out.print(in.readLine()); // PASSWORD:
            String password = scanner.nextLine();
            out.println(password);

            String loginResult = in.readLine();
            if ("LOGIN_SUCCESS".equals(loginResult)) {
                System.out.println("✅ Giriş başarılı.");

                String groupLine = in.readLine(); // Örn: GROUP:grup1
                System.out.println(groupLine);

                // Grup listesi ve seçim
                String groupList = in.readLine();
                System.out.println(groupList);

                String prompt = in.readLine(); // 📌 Bir grup seçin:
                System.out.print(prompt);
                String selectedGroup = scanner.nextLine();
                out.println(selectedGroup);

                String accessCheck = in.readLine();
                if (accessCheck.startsWith("🚫")) {
                    System.out.println(accessCheck);
                    return;
                }

                // Grup dosyalarını listele
                String line;
                while ((line = in.readLine()) != null && !line.startsWith("📄")) {
                    System.out.println(line);
                }

                // İçerik girme
                System.out.print("📄 Bu gruba eklemek istediğiniz içerik: ");
                String content = scanner.nextLine();
                out.println(content);

                String result = in.readLine();
                System.out.println(result);

            } else {
                System.out.println("❌ Giriş başarısız.");
            }

        } catch (IOException e) {
            System.err.println("⚠️ Sunucuya bağlanılamadı: " + e.getMessage());
        }
    }
}

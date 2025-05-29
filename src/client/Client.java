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
            // Kullanıcı adı isteği
            System.out.print(in.readLine()); // USERNAME:
            String username = scanner.nextLine();
            out.println(username);

            System.out.print(in.readLine()); // PASSWORD:
            String password = scanner.nextLine();
            out.println(password);

            // Giriş sonucu
            String response = in.readLine();
            if (response != null && response.contains("LOGIN_SUCCESS")) {
                System.out.println("🎉 Giriş başarılı!");

                System.out.println("📄 Lütfen dosyaya kaydetmek istediğiniz içeriği girin:");
                String content = scanner.nextLine();
                out.println("CONTENT:" + content);

                // İstersen server'dan onay bekleyebilirsin, örn:
                String confirm = in.readLine();
                if (confirm != null && confirm.contains("CONTENT_SAVED")) {
                    System.out.println("İçerik başarıyla kaydedildi.");
                }

            } else {
                System.out.println("❌ Giriş başarısız.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

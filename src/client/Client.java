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

            // Sunucudan login yanıtı
            String response = in.readLine();
            if (response.contains("LOGIN_SUCCESS")) {
                // 1) Grupları dinle
                System.out.println(in.readLine()); // “Ait olduğunuz gruplar...”
                System.out.print(in.readLine());   // “Bir grup seçin: ”
                String chosenGroup = scanner.nextLine();
                out.println(chosenGroup);

                // 2) Grup dosyalarını al
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("📄 Bu gruba eklemek")) break;
                    System.out.println(line);
                }

                // 3) İçerik ekleme
                System.out.print("Eklemek istediğiniz içeriği girin: ");
                String content = scanner.nextLine();
                out.println(content);

                // 4) Onayı al
                System.out.println(in.readLine());
            } else {
                System.out.println("LOGIN_FAILED");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

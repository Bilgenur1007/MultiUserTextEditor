package server;

import java.io.*;
import java.util.*;

public class UserManager {
    // Burada users.txt dosyasının yolu "users.txt" olarak değiştirildi.
    // Dosya, programı çalıştırdığın dizinde olmalı (örn. projenin kök dizini).
    private static final String USER_FILE = "src/server/users.txt";

    public static Map<String, String> loadUsersFromFile() {
        Map<String, String> credentials = new HashMap<>();
        File file = new File(USER_FILE);
        if (!file.exists()) {
            System.out.println("Kullanıcı dosyası bulunamadı, yeni dosya oluşturulacak: " + file.getAbsolutePath());
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Kullanıcı dosyası oluşturulamadı: " + e.getMessage());
            }
            return credentials;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(":");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    credentials.put(username, password);
                }
            }
        } catch (IOException e) {
            System.err.println("Kullanıcı dosyası okunamadı: " + e.getMessage());
        }

        System.out.println("Kullanıcılar yüklendi: " + credentials.keySet());
        return credentials;
    }

    public static boolean addUser(String username, String password) {
        Map<String, String> currentUsers = loadUsersFromFile();

        if (currentUsers.containsKey(username)) {
            System.out.println("Kullanıcı zaten var: " + username);
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            System.out.println("Kullanıcı eklendi: " + username);
            return true;
        } catch (IOException e) {
            System.err.println("Kullanıcı dosyasına yazılamadı: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Kullanım: java server.UserManager <username> <password>");
            return;
        }
        String username = args[0];
        String password = args[1];
        addUser(username, password);
    }
    public static List<String> getUserGroups(String username) {
        List<String> groups = new ArrayList<>();
        File groupFile = new File("src/server/user_groups.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(groupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(username)) {
                    String[] userGroups = parts[1].split(",");
                    for (String group : userGroups) {
                        groups.add(group.trim());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("❌ user_groups.txt okunamadı.");
        }

        return groups;
    }

}

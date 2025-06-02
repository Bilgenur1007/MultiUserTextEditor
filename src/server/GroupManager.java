package server;

import java.io.*;
import java.util.*;

public class GroupManager {
    private static final String USER_GROUPS_FILE = "src/server/user_groups.txt";

    // Dosyayı oku ve map olarak döndür
    private static Map<String, List<String>> loadAll() throws IOException {
        Map<String,List<String>> m = new HashMap<>();
        File f = new File(USER_GROUPS_FILE);
        if (!f.exists()) f.createNewFile();
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(":");
                if (p.length == 2) {
                    List<String> groups = new ArrayList<>();
                    for (String g : p[1].split(",")) groups.add(g.trim());
                    m.put(p[0].trim(), groups);
                }
            }
        }
        return m;
    }

    // Map'i dosyaya yaz
    private static void saveAll(Map<String,List<String>> m) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(USER_GROUPS_FILE,false))) {
            for (var e : m.entrySet()) {
                w.write(e.getKey() + ":" + String.join(",", e.getValue()));
                w.newLine();
            }
        }
    }

    private static void add(String user, String group) throws IOException {
        var m = loadAll();
        var lst = m.computeIfAbsent(user, k->new ArrayList<>());
        if (!lst.contains(group)) {
            lst.add(group);
            saveAll(m);
            System.out.println("✅ " + user + " eklendi " + group + " grubuna.");
        } else {
            System.out.println("ℹ️ " + user + " zaten " + group + " grubunda.");
        }
    }

    private static void remove(String user, String group) throws IOException {
        var m = loadAll();
        var lst = m.get(user);
        if (lst != null && lst.remove(group)) {
            saveAll(m);
            System.out.println("✅ " + user + " çıkarıldı " + group + " grubundan.");
        } else {
            System.out.println("ℹ️ " + user + " bu grupta değil.");
        }
    }

    private static void listGroups(String user) throws IOException {
        var m = loadAll();
        var lst = m.getOrDefault(user, Collections.emptyList());
        System.out.println(user + " grupları: " + String.join(", ", lst));
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Kullanım:");
            System.out.println("  add <user> <group>");
            System.out.println("  remove <user> <group>");
            System.out.println("  list-groups <user>");
            return;
        }
        switch (args[0]) {
            case "add":       add(args[1], args[2]); break;
            case "remove":    remove(args[1], args[2]); break;
            case "list-groups": listGroups(args[1]); break;
            default: System.out.println("Bilinmeyen komut");
        }
    }

    public static List<String> getUserGroups(String username) {
        List<String> groups = new ArrayList<>();  // boş listeyle başlıyoruz
        File file = new File("src/server/user_groups.txt");  // önce bu yolu sabitleyelim

        if (!file.exists()) {
            System.out.println("❌ user_groups.txt bulunamadı.");
            return groups; // boş ama null değil
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String user = parts[0].trim();
                    String[] groupArray = parts[1].split(",");
                    if (user.equals(username)) {
                        for (String group : groupArray) {
                            if (group != null && !group.trim().isEmpty()) {
                                groups.add(group.trim());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ user_groups.txt okunamadı: " + e.getMessage());
        }

        return groups;  // asla null dönmüyor
    }

}

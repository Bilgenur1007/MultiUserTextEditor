package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private Map<String, StringBuilder> sharedFiles;
    private List<ClientHandler> clients;

    public ClientHandler(Socket socket, Map<String, StringBuilder> sharedFiles, List<ClientHandler> clients) {
        this.socket = socket;
        this.sharedFiles = sharedFiles;
        this.clients = clients;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Kullanıcı adınızı giriniz:");
            username = in.readLine();

            // Kullanıcı dosya paylaşma veya görüntüleme komutu girer
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("SHARE")) {
                    String filename = line.split(" ")[1];
                    sharedFiles.putIfAbsent(filename, new StringBuilder());
                    broadcastFileList();
                } else if (line.startsWith("EDIT")) {
                    String filename = line.split(" ")[1];
                    out.println("CONTENT " + sharedFiles.get(filename));
                } else if (line.startsWith("UPDATE")) {
                    String filename = line.split(" ")[1];
                    String newText = line.substring(("UPDATE " + filename).length()).trim();
                    sharedFiles.put(filename, new StringBuilder(newText));
                    // Otomatik kaydetme:
                    try (FileWriter fw = new FileWriter("server_files/" + filename)) {
                        fw.write(newText);
                    }
                    broadcastFileList();
                }
            }

        } catch (IOException e) {
            System.out.println("İstemci bağlantısı kesildi: " + username);
        }
    }

    private void broadcastFileList() {
        for (ClientHandler client : clients) {
            client.out.println("FILES " + String.join(",", sharedFiles.keySet()));
        }
    }
}

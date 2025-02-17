import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clients = new HashSet<>();
    public static final Map<String, ClientHandler> clientMap = new HashMap<>();
    private static final String HISTORY_FILE = "chat_history.txt";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is running...");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcastMessage(String message, ClientHandler sender) {
        String timestamp = getTimestamp();
        String timestampedMessage = timestamp + " " + sender.clientName + ": " + message;
        saveChatHistory(timestampedMessage);

        if (message.startsWith("@private ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length >= 3) {
                String recipientName = parts[1].trim();
                String privateMessage = parts[2].trim();

                ClientHandler recipient = clientMap.get(recipientName);
                if (recipient != null) {
                    String privateTimestampedMessage = timestamp + " [PRIVATE] " + sender.clientName + ": " + privateMessage;
                    recipient.sendMessage(privateTimestampedMessage);
                    sender.sendMessage(timestamp + " [PRIVATE to " + recipientName + "] " + privateMessage);
                } else {
                    sender.sendMessage(timestamp + " [ERROR] User " + recipientName + " not found.");
                }
                return;
            }
        }

        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(timestampedMessage);
            }
        }
    }

    static void privateMessage(String recipient, String message, ClientHandler sender) {
        if (clientMap.containsKey(recipient)) {
            ClientHandler recipientClient = clientMap.get(recipient);
            recipientClient.sendMessage(getTimestamp() + " (Private) " + message);
            sender.sendMessage(getTimestamp() + " (Private to " + recipient + "): " + message);
        } else {
            sender.sendMessage("User " + recipient + " not found.");
        }
    }

    static void updateUserList() {
        String userList = "@users " + String.join(",", clientMap.keySet());
        for (ClientHandler client : clients) {
            client.sendMessage(userList);
        }
    }

    static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        clientMap.remove(clientHandler.clientName);
        updateUserList();
    }

    public static String getTimestamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
    }

    private static void saveChatHistory(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine().trim();
            System.out.println(clientName + " joined the chat.");
            ChatServer.clientMap.put(clientName, this);
            ChatServer.updateUserList();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("@private ")) {
                    // Send private message
                    ChatServer.broadcastMessage(message, this);
                } else {
                    // Normal public message
                    ChatServer.broadcastMessage(message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    void sendMessage(String message) {
        out.println(message);
    }
}

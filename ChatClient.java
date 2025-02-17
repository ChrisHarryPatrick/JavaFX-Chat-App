import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends Application {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    private TextArea chatArea;
    private TextField messageField;
    private ListView<String> userList;
    private ObservableList<String> users = FXCollections.observableArrayList();
    private String selectedUser = "All";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setOnAction(e -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());  // Fix for Send button not working

        userList = new ListView<>(users);
        userList.setPrefWidth(120);
        userList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> selectedUser = newVal);

        VBox rightPane = new VBox(new Label("Online Users"), userList);
        rightPane.setPadding(new Insets(10));

        HBox bottomPane = new HBox(10, messageField, sendButton);
        bottomPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setRight(rightPane);
        root.setCenter(chatArea);
        root.setBottom(bottomPane);

        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("Chat Client");
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Login");
            dialog.setHeaderText("Enter your name:");
            dialog.setContentText("Name:");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().trim().isEmpty()) {
                clientName = result.get().trim();
            } else {
                clientName = "User" + new Random().nextInt(1000);
            }

            out.println(clientName);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        final String finalMsg = msg;
                        Platform.runLater(() -> {
                            if (finalMsg.startsWith("@users ")) {
                                updateUsersList(finalMsg.substring(7));
                            } else {
                                chatArea.appendText(finalMsg + "\n");
                            }
                        });
                    }
                } catch (IOException ignored) {
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUsersList(String userListData) {
        Platform.runLater(() -> users.setAll(userListData.split(",")));
    }

    private String getTimestamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            String timestamp = getTimestamp();
            chatArea.appendText(timestamp + " [Me] " + message + "\n"); // Timestamp for sender
            messageField.clear();
        }
    }
}




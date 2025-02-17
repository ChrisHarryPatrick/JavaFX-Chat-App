JavaFX Chat Application

A real-time JavaFX-based Chat Application that allows users to send messages to multiple users with a graphical user interface (GUI). The application supports private messaging, timestamps, and an online user list.

---

🚀 Features
✔ Real-time chat between multiple users  
✔ Graphical User Interface (GUI) using JavaFX  
✔ Private messaging (`@private username message`)  
✔ Live online user list  
✔ Timestamps for messages 
✔ Message history storage 
✔ Auto-updating user list when users join/leave 

---

🛠 Setup & Installation

1️⃣ Install Java & JavaFX
Make sure you have Java 11+ installed.
To check your Java version, run:

java -version

Download JavaFX SDK from (https://jdk.java.net/), then extract it to `/opt/javafx`.

2️⃣ Clone This Repository

git clone https://github.com/ChrisHarryPatrick/JavaFX-Chat-App.git
cd JavaFX-Chat-App


3️⃣ Compile the Java Files
Compile the Server

javac ChatServer.java


Compile the Client (JavaFX Required)

javac --module-path /opt/javafx/openjfx-23.0.2_macos-x64_bin-sdk/lib --add-modules javafx.controls,javafx.fxml ChatClient.java

---

▶️ Running the Chat Application

1️⃣ Start the Chat Server

java ChatServer

✅ The server should start and wait for client connections.

2️⃣ Start the Chat Client (GUI)

java --module-path /opt/javafx/openjfx-23.0.2_macos-x64_bin-sdk/lib --add-modules javafx.controls,javafx.fxml ChatClient


✅ A JavaFX chat window should open, asking for a username.

3️⃣ Open Multiple Clients
To test multiple users, open new terminal windows and run the client command again.

---

📜 Usage Instructions
- Public Messages: Type in the chatbox and press Send.
- Private Messages: Type `@private username message` to send a private message.
- User List: Displays all online users.

---

📌 File Structure

JavaFX-Chat-App/
│── ChatServer.java   # Server-side logic, includes ClientHandler class
│── ChatClient.java   # JavaFX GUI client
│── chat_history.txt   # (Auto-generated) Stores message history
│── .gitignore   # Ignores unnecessary files
│── README.md   # Documentation

---

📌 .gitignore (Prevents Uploading Unnecessary Files)

*.class
chat_history.txt

---

🛠 Future Enhancements
🚀 Dark Mode UI🌙  
🚀 Emoji Support😊🔥  
🚀 File Sharing 📂  
🚀 Notifications for New Messages🔔  

---

🎯 Happy Coding & Chatting! 💬🚀


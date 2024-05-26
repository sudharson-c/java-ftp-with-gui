# File Transfer Application

This is a Java-based File Transfer Application with a graphical user interface (GUI) for both the client and server. The application allows the client to send files to and receive files from the server.

## Features

- **Client**
  - Connect to the server using hostname and port number.
  - Send files from any directory on the client system.
  - Receive files from a specific directory on the server.
  - Progress bar to indicate file transfer progress.
  
- **Server**
  - Accepts connections from multiple clients.
  - Allows clients to send and receive files.
  - Logs connection and file transfer activities.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Swing library (included in JDK)

## Getting Started

### Running the Server

1. **Clone the repository**:
    ```bash
    git clone https://github.com/sudharson-c/ftp-gui.git
    cd ftp-gui
    ```

2. **Compile and run the server**:
    ```bash
    javac FileTransferServerGUI.java
    java FileTransferServerGUI
    ```

3. **Enter the port number** to start the server.

### Running the Client

1. **Clone the repository** (if not already done):
    ```bash
    git clone https://github.com/sudharson-c/ftp-gui.git
    cd ftp-gui
    ```

2. **Compile and run the client**:
    ```bash
    javac FileTransferClientGUI.java
    java FileTransferClientGUI
    ```

3. **Connect to the server**:
    - Enter the server hostname (e.g., `localhost`) and port number.
    - Click `Connect`.

4. **Send a File**:
    - Click `Choose File` to select a file from any directory on your system.
    - Click `Send File` to transfer the selected file to the server.

5. **Receive a File**:
    - Click `Receive File` and enter the name of the file you want to receive from the server.
    - Choose the directory to save the received file (restricted to the allowed directory on the server).

### Open to more development


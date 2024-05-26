package ftp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServerGUI extends JFrame {
    private JTextArea logArea;
    private ServerSocket serverSocket;

    public FileTransferServerGUI() {
        setTitle("File Transfer Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        add(scrollPane, BorderLayout.CENTER);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        }).start();
    }

    private void startServer() {
        try {
            // Prompt the user to enter a port number
            String portInput = JOptionPane.showInputDialog(this, "Enter port number to start the server:", "Port Input", JOptionPane.QUESTION_MESSAGE);
            if (portInput == null || portInput.isEmpty()) {
                logArea.append("Server start canceled.\n");
                return;
            }
            int port = Integer.parseInt(portInput);

            serverSocket = new ServerSocket(port);
            logArea.append("Server started on port " + port + ". Waiting for connections...\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");
                handleClient(clientSocket);
            }
        } catch (NumberFormatException ex) {
            logArea.append("Invalid port number. Server start canceled.\n");
        } catch (IOException ex) {
            logArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                    String choice = inFromClient.readLine();
                    if ("1".equals(choice)) {
                        receiveFile(clientSocket, inFromClient);
                    } else if ("2".equals(choice)) {
                        sendFile(clientSocket, inFromClient);
                    }
                    clientSocket.close();
                    logArea.append("Client connection closed.\n");
                } catch (IOException ex) {
                    logArea.append("Error: " + ex.getMessage() + "\n");
                }
            }
        }).start();
    }

    private void receiveFile(Socket clientSocket, BufferedReader inFromClient) throws IOException {
        String fileName = inFromClient.readLine();
        logArea.append("Receiving file: " + fileName + "\n");
        FileOutputStream fos = new FileOutputStream(fileName);
        InputStream is = clientSocket.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        logArea.append("File received successfully.\n");
    }

    private void sendFile(Socket clientSocket, BufferedReader inFromClient) throws IOException {
        String fileName = inFromClient.readLine();
        File file = new File(fileName);
        if (!file.exists()) {
            logArea.append("File does not exist: " + fileName + "\n");
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            outToClient.println("File not found");
            return;
        }
        logArea.append("Sending file: " + fileName + "\n");
        FileInputStream fis = new FileInputStream(file);
        OutputStream os = clientSocket.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        fis.close();
        logArea.append("File sent successfully.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileTransferServerGUI().setVisible(true);
            }
        });
    }
}

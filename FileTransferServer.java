import java.io.*;
import java.net.*;

public class FileTransferServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Server socket listening on port 12345

            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming connection from client
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostName());

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientMessage = inFromClient.readLine(); // Read client's choice

                if (clientMessage.equals("1")) {
                    receiveFile(clientSocket);
                } else if (clientMessage.equals("2")) {
                    sendFile(clientSocket);
                } else {
                    System.out.println("Invalid choice. Closing connection.");
                }

                clientSocket.close(); // Close connection
                System.out.println("Connection closed with client.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(Socket clientSocket) throws IOException {
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String fileName = inFromClient.readLine(); // Receive file name from client
        System.out.println("Receiving file: " + fileName);

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int bytesRead;

        InputStream inputStream = clientSocket.getInputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        System.out.println("File received successfully.");
    }

    private static void sendFile(Socket clientSocket) throws IOException {
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String fileName = inFromClient.readLine(); // Receive file name from client
        File fileToSend = new File(fileName);

        if (!fileToSend.exists()) {
            System.out.println("File does not exist.");
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            outToClient.println("File not found");
            return;
        }

        System.out.println("Sending file: " + fileName);
        FileInputStream fileInputStream = new FileInputStream(fileToSend);
        OutputStream outputStream = clientSocket.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        System.out.println("File sent successfully.");
    }
}

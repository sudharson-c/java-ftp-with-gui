import java.io.*;
import java.net.*;

public class FileTransferClient {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 12345); // Connect to server

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server.");

            displayMenu(); // Display menu options

            String choice = inFromUser.readLine();
            outToServer.println(choice);

            switch (choice) {
                case "1":
                    System.out.print("Enter file name to send: ");
                    String sendFileName = inFromUser.readLine();
                    outToServer.println(sendFileName);
                    sendFile(clientSocket, sendFileName);
                    break;
                case "2":
                    System.out.print("Enter file name to receive: ");
                    String receiveFileName = inFromUser.readLine();
                    outToServer.println(receiveFileName);
                    receiveFile(clientSocket, receiveFileName);
                    break;
                default:
                    System.out.println("Invalid choice. Closing connection.");
                    break;
            }

            clientSocket.close(); // Close connection
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("Menu:");
        System.out.println("1. Send a file to the server");
        System.out.println("2. Receive a file from the server");
        System.out.print("Enter your choice: ");
    }

    private static void sendFile(Socket clientSocket, String fileName) throws IOException {
        File fileToSend = new File(fileName);
        if (!fileToSend.exists()) {
            System.out.println("File does not exist.");
            return;
        }

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

    private static void receiveFile(Socket clientSocket, String fileName) throws IOException {
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String response = inFromServer.readLine();
        if (response.equals("File not found")) {
            System.out.println("Server could not find the requested file.");
            return;
        }

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
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FileTransferClientGUI extends JFrame {
    private JTextField serverField, portField;
    private JButton connectButton, sendButton, receiveButton, chooseFileButton;
    private JTextArea logArea;
    private JLabel chosenFileLabel;
    private JProgressBar progressBar;

    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private File chosenFile;
    private File allowedDirectory;

    public FileTransferClientGUI() {
        setTitle("File Transfer Client");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        allowedDirectory = new File("ftp:/"); // Define the allowed directory
        if (!allowedDirectory.exists()) {
            allowedDirectory.mkdirs();
        }

        JPanel panel = new JPanel(new GridLayout(7, 2));

        panel.add(new JLabel("Server:"));
        serverField = new JTextField("localhost");
        panel.add(serverField);

        panel.add(new JLabel("Port:"));
        portField = new JTextField("12345");
        panel.add(portField);

        panel.add(new JLabel("Chosen File:"));
        chosenFileLabel = new JLabel("No file chosen");
        panel.add(chosenFileLabel);

        chooseFileButton = new JButton("Choose File");
        panel.add(chooseFileButton);

        connectButton = new JButton("Connect");
        panel.add(connectButton);

        sendButton = new JButton("Send File");
        sendButton.setEnabled(false);
        panel.add(sendButton);

        receiveButton = new JButton("Receive File");
        receiveButton.setEnabled(false);
        panel.add(receiveButton);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        add(panel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendFile();
                    }
                }).start();
            }
        });

        receiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        receiveFile();
                    }
                }).start();
            }
        });
    }

    private void connect() {
        try {
            String server = serverField.getText();
            int port = Integer.parseInt(portField.getText());
            clientSocket = new Socket(server, port);
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connectButton.setEnabled(false);
            logArea.append("Connected to server.\n");
            sendButton.setEnabled(true);
            receiveButton.setEnabled(true);
        } catch (IOException ex) {
            logArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            chosenFile = fileChooser.getSelectedFile();
            chosenFileLabel.setText(chosenFile.getName());
        }
    }

    private void sendFile() {
        if (chosenFile == null) {
            logArea.append("No file chosen to send.\n");
            return;
        }
        try {
            outToServer.println("1"); // Indicate file send operation
            outToServer.println(chosenFile.getName());

            FileInputStream fis = new FileInputStream(chosenFile);
            OutputStream os = clientSocket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = chosenFile.length();

            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / fileSize);
                progressBar.setValue(progress);
            }
            fis.close();
            progressBar.setValue(100);
            logArea.append("File sent successfully.\n");
        } catch (IOException ex) {
            logArea.append("Error: " + ex.getMessage() + "\n");
        } finally {
            progressBar.setValue(0);
        }
    }

    private void receiveFile() {
        try {
            String fileName = JOptionPane.showInputDialog(this, "Enter the name of the file to receive:");
            if (fileName == null || fileName.isEmpty()) {
                logArea.append("No file name entered.\n");
                return;
            }
            outToServer.println("2"); // Indicate file receive operation
            outToServer.println(fileName);

            File file = new File(allowedDirectory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = inFromServer.readLine().equals("File not found") ? 0 : Long.parseLong(inFromServer.readLine());

            if (fileSize == 0) {
                logArea.append("File not found on server.\n");
                return;
            }

            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / fileSize);
                progressBar.setValue(progress);
            }
            fos.close();
            progressBar.setValue(100);
            logArea.append("File received and saved to " + file.getAbsolutePath() + ".\n");
        } catch (IOException ex) {
            logArea.append("Error: " + ex.getMessage() + "\n");
        } finally {
            progressBar.setValue(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileTransferClientGUI().setVisible(true);
            }
        });
    }
}

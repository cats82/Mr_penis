import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Main {
    private static final String SERVER_ADDRESS = "192.168.1.103";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JTextArea messageArea;
    private JTextField inputField;

    public Main() {
        // Настройка графического интерфейса
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(Color.BLACK);
        messageArea.setForeground(Color.WHITE);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.WHITE);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.getContentPane().add(messageScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputField, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Настройка сети
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Поток для получения сообщений от сервера
            new Thread(new IncomingMessagesHandler()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (message != null && !message.trim().isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    private class IncomingMessagesHandler implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    messageArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}

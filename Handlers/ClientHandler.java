package Handlers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final PrintWriter writer;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String userName = reader.readLine();
            System.out.println("New user: " + userName);

            // Notify all users about the current highest bid
            broadcast("New user " + userName + " joined the auction. Current highest bid: " + highestBid);

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                try {
                    int bid = Integer.parseInt(inputLine);
                    if (bid > highestBid) {
                        highestBid = bid;
                        // Notify all users about the new highest bid
                        broadcast("New highest bid: " + highestBid + " by " + userName);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid bid format from user " + userName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.writer.println(message);
            }
        }
    }
}


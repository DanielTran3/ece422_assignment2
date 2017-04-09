import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket clientSocket;

	public ClientThread(String id, String password) {
		clientID = id;
		clientPassword = password;
	}

	public ClientThread(Socket accept) {
		this.clientSocket = accept;
		
	}

	public void run() {
		int i = 0;
        while (i < 10) {
            System.out.println("Server Connected to Client!");
            PrintWriter writeToClient;
			try {
				writeToClient = new PrintWriter(this.clientSocket.getOutputStream(), true);
	            BufferedReader readFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        	while (i < 10) {
	        		System.out.println("Client: " + readFromClient.readLine());
	        	}
	            writeToClient.println("From Server");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

}

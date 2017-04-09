import java.io.BufferedReader;
import java.io.BufferedWriter;
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
        System.out.println("Server Connected to Client!");
		try {
			PrintWriter writeToClient = new PrintWriter(this.clientSocket.getOutputStream(), true);
            BufferedReader readFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String credentials;
			while ((credentials = readFromClient.readLine()) != null) {
				System.out.println(credentials);
			}
//            String clientUsername = readFromClient.readLine();
//			String clientPassword = readFromClient.readLine();
//			if ((clientUsername == null) || (clientPassword == null)) {
//				System.out.println("Error Occurred in receiving credentials.");
//				System.out.println(clientUsername);
//				System.out.println(clientPassword);
//			}
//			else {
//				System.out.println(clientUsername);
//				System.out.println(clientPassword);
//			}
			
			readFromClient.close();
			writeToClient.close();
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

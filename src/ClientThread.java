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
		
	}

}

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Server {
	private static String shadowFile = "shadow_file.txt";
	private static List<String> passwordList;
	private static List<String> usernameList;
	private static List<String> saltList;
	private static FileIO shadowFileIO;

	public List<String> getPasswordList() {
		return passwordList;
	}

	public boolean inShadowFile(String encryptedPassword) {
		return passwordList.contains(encryptedPassword);
	}

	public static String getShadowFile() {
		return shadowFile;
	}
	
	public static void main (String args[]) {
        if (args.length != 1) {
			System.out.println("Please Enter Only One Inputs: Portnumber");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		passwordList = new ArrayList<String>();
		usernameList = new ArrayList<String>();
		saltList = new ArrayList<String>();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server hostname: " + InetAddress.getLocalHost().getHostName());
			
			shadowFileIO = new FileIO();
			shadowFileIO.loadShadowFile(shadowFile, usernameList, saltList, passwordList);
			for(int i = 0; i < passwordList.size(); i++) {
				System.out.println("Username: " + usernameList.get(i));
				System.out.println("Password: " + passwordList.get(i));
				System.out.println("Salt: " + saltList.get(i));
			}

            while (true) {
                // Create new thread for the client
                new ServerThread(serverSocket.accept()).start();
            	//new CreateAccountThread(serverSocket.accept()).start();
			}
        }
        catch (IOException e) {
            System.out.println("Failure to Open Socket");
            e.printStackTrace();
        }

    }
}

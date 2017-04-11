import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
public class Client {
	
	private static KeyExchange clientKeys;
	
    public static void main (String args[]) {

        if (args.length != 2) {
			System.out.println("Please Enter Only Two Inputs: Portnumber Hostname");
			System.exit(0);
		}
        int port = Integer.parseInt(args[0]);
        String hostname = args[1];

        Console readInput = System.console();

        try {
            System.out.println("Connecting to Computer: " + hostname + " On port: " + port);
            Socket clientSocket = new Socket(hostname, port);
            System.out.println("Connection Successful!");
            PrintWriter writeToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader readFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            if (readInput == null) {
    			System.out.println("Error in reading from console.");
    			System.exit(0);
    		}
    		String username = readInput.readLine("Enter your Username: ");
    		String password = readInput.readLine("Enter your Password: ");

            writeToServer.println(username); 
            writeToServer.println(password);
            
            //----------- After authentication is good, make, encrypt, and send keys
            clientKeys = new KeyExchange();
            clientKeys.generateKeys();
            
            // Encrypt the public key
            // Pass: clientKeys.getPublicKey().getEncoded() into TEA encryption
            clientKeys.setEncryptedPublicKey(clientKeys.encrypt_key(clientKeys.getPublicKey()));
            System.out.println("Encrypted Key: " + clientKeys.getEncryptedPublicKey());
            System.out.println("DecryptedKey: " + clientKeys.decrypt_key(clientKeys.getEncryptedPublicKey()));
            // Send public key
            //writeToServer.println(clientKeys.getEncryptedPublicKey());
            
            // Receive server's encrypted public key
            //String str_encServerKey = readFromServer.readLine();
            //clientKeys.setEncServerKey();
            
			readFromServer.close();
			writeToServer.close();
			clientSocket.close();
        }
        catch (IOException e) {
            System.out.println("Failed to create socket.");
            e.printStackTrace();
        }
    }
}

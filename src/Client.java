import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
public class Client {
	
	private static KeyStorage clientKeys;
	
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
            //PrintWriter writeToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            //BufferedReader readFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            ObjectOutputStream writeToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream readFromServer = new ObjectInputStream(clientSocket.getInputStream());
            if (readInput == null) {
    			System.out.println("Error in reading from console.");
    			System.exit(0);
    		}
    		String username = readInput.readLine("Enter your Username: ");
    		String password = readInput.readLine("Enter your Password: ");

            writeToServer.writeObject(username);
            writeToServer.flush();
            writeToServer.writeObject(password);
            writeToServer.flush();
            
            //----------- After authentication is good, make, encrypt, and send keys
            clientKeys = new KeyStorage();
            clientKeys.generateKeys();
            
            writeToServer.writeObject(clientKeys.getPublicKey());
            writeToServer.flush();
            PublicKey serverPubKey = (PublicKey) readFromServer.readObject();
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(clientKeys.getPrivateKey());
			ka.doPhase(serverPubKey, true);
			byte[] secretKey = ka.generateSecret();
			System.out.println(Arrays.toString(secretKey));
            // Encrypt the public key
            // Pass: clientKeys.getPublicKey().getEncoded() into TEA encryption
            //clientKeys.setEncryptedPublicKey(clientKeys.encrypt_key(clientKeys.getPublicKey()));
            //System.out.println("Encrypted Key: " + clientKeys.getEncryptedPublicKey());
            //System.out.println("DecryptedKey: " + clientKeys.decrypt_key(clientKeys.getEncryptedPublicKey()));
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
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

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
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";

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

            //----------- After authentication is good, make, encrypt, and send keys
            clientKeys = new KeyStorage();
            clientKeys.generateKeys();

            writeToServer.writeObject(clientKeys.getPublicKey());
            writeToServer.flush();
            PublicKey serverPubKey = (PublicKey) readFromServer.readObject();
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(clientKeys.getPrivateKey());
			ka.doPhase(serverPubKey, true);
			clientKeys.setSecretKey(ka.generateSecret());
			System.out.println("Secret Key: " + Arrays.toString(clientKeys.getSecretKey()));

			writeToServer.writeObject(clientKeys.encrypt_message_String(username.getBytes()));
            writeToServer.flush();
            writeToServer.writeObject(clientKeys.encrypt_message_String(password.getBytes()));
            writeToServer.flush();

            String file_input = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
            String ack;
            String fileFromServer;
            while(!file_input.equals("exit")) {
            	file_input = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
                writeToServer.writeObject(clientKeys.encrypt_message_String(file_input.getBytes()));
                ack = (String) readFromServer.readObject();
                if (ack.equals(fileNotFound)) {
                	System.out.println(ack);
                	continue;
                }
                if (ack.equals(fileFound)) {
                	System.out.println("File Found! Displaying...");
                	fileFromServer = clientKeys.decrypt_message_String((byte[]) readFromServer.readObject());
                }
            }

            writeToServer.writeObject(clientKeys.encrypt_message("finished".getBytes()));
			readFromServer.close();
			writeToServer.close();
			clientSocket.close();
        }
        catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Failed to create socket.");
            e.printStackTrace();
        }
    }
}

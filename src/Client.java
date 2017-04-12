import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;
public class Client {

	private static KeyStorage clientKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private static final String ACCESS_DENIED = "Access-Denied";
	private static final String ACCESS_GRANTED = "Access-Granted";

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

            ObjectOutputStream writeToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream readFromServer = new ObjectInputStream(clientSocket.getInputStream());
            FileIO fileIO = new FileIO();
            
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
			
			writeToServer.writeObject(clientKeys.encrypt_message(username.getBytes()));
            writeToServer.flush();
		
            writeToServer.writeObject(clientKeys.encrypt_message(password.getBytes()));
            writeToServer.flush();

            if (clientKeys.decrypt_message_String((int[]) readFromServer.readObject()).equals(ACCESS_DENIED)) {
            	System.out.println(ACCESS_DENIED);
            }
            else {
            	System.out.println(ACCESS_GRANTED);
				String directory = System.getProperty("user.dir") + '/' + username + "_Downloads"; 
            	
				if (Files.notExists(Paths.get(directory), LinkOption.NOFOLLOW_LINKS)) {
            		File dir = new File(directory);
            		dir.mkdir();
            	}
            	
	            String file_request = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
	            String ack;
	            int[] intFromServer;
	            
	            while(true) {
					if (file_request.equals("exit")) {
						System.out.println("Ending Session...");
	            		writeToServer.writeObject(clientKeys.encrypt_message("finished".getBytes()));
						writeToServer.flush();
						break;
					}
	                writeToServer.writeObject(clientKeys.encrypt_message(file_request.getBytes()));
					writeToServer.flush();
					System.out.println("Completed Send.");
	                intFromServer = (int[]) readFromServer.readObject();
	                ack = clientKeys.decrypt_message_String(intFromServer);              
					
					if (ack.equals(fileNotFound)) {
	                	System.out.println(ack);
						file_request = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
	                	continue;
	                }
	                if (ack.equals(fileFound)) {
	                	System.out.println("File Found! Displaying...");
	                	String readFile = clientKeys.decrypt_message_String((int[]) readFromServer.readObject());
	                	System.out.println("________________________________________________________________");
						System.out.println("File: " + readFile);
						System.out.println("________________________________________________________________");
						fileIO.saveToFile(directory, file_request, readFile);
	                }
					file_request = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
	            }
            }
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

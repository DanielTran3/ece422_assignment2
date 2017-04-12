import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.nio.file.Files;
import javax.crypto.KeyAgreement;

public class ServerThread extends Thread {

	private Socket serverSocket;
	private KeyStorage serverKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private static final String ACCESS_DENIED = "Access-Denied";
	private static final String ACCESS_GRANTED = "Access-Granted";
	private Hashing hashing;
	private String directory = System.getProperty("user.dir");

	public ServerThread(Socket accept) {
		this.serverSocket = accept;
		hashing = new Hashing();
	}

	public void run() {
        System.out.println("Server Connected to Client!");
		System.out.println("Thread Number: " + Thread.currentThread().getId());
		System.out.println("Currently in folder: " + directory);
		try {
			ObjectOutputStream writeToClient = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream readFromClient = new ObjectInputStream(serverSocket.getInputStream());
			
			serverKeys = new KeyStorage();
			serverKeys.generateKeys();

			PublicKey clientPubKey = (PublicKey) readFromClient.readObject();
            writeToClient.writeObject(serverKeys.getPublicKey());
            writeToClient.flush();

            KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(serverKeys.getPrivateKey());
			ka.doPhase(clientPubKey, true);
			serverKeys.setSecretKey(ka.generateSecret());

			String username = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
			String password = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
			
			int usernameIndex = Server.getUsernameIndex(username);
			if (usernameIndex == -1) {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_DENIED.getBytes()));
			}
			
			byte[] hashedPassword = hashing.sha256Hash(Server.getSalt(usernameIndex), password);
			if (!hashing.hashToHex(hashedPassword).equals(Server.getPassword(usernameIndex))) {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_DENIED.getBytes()));
			}
			else {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_GRANTED.getBytes()));
				
				String clientFilename;
				int[] int_filename;
				byte[] fileReadIn;
				
				while(true) {
					System.out.println("Waiting for Client Input.");
					int_filename = (int[]) readFromClient.readObject();			
	            	clientFilename = serverKeys.decrypt_message_String(int_filename);			
	            	if (clientFilename.equals("finished")) {
	        			break;
	            	}
	
					System.out.println("Looking for Client File: " + clientFilename);
	            	File file = new File(clientFilename);
	            	if(file.exists() && !file.isDirectory()) {
						System.out.println("Sending File...");
	            		// Acknowledgement
	            	    writeToClient.writeObject(serverKeys.encrypt_message(fileFound.getBytes()));
						writeToClient.flush();
	            	    // Read in File
	            	    fileReadIn = Files.readAllBytes(file.toPath());
	            	    // Write encrypted file to client
	            	    writeToClient.writeObject(serverKeys.encrypt_message(fileReadIn));
						writeToClient.flush();            									
					}
	            	else {
						System.out.println("Failed to find file...");            	
						writeToClient.writeObject(serverKeys.encrypt_message(fileNotFound.getBytes()));
						writeToClient.flush();
	            	}
				}
			}
			System.out.println("Session with Client " + Thread.currentThread().getId() + " has Ended...");
			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

}

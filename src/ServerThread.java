import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.nio.file.Files;
import javax.crypto.KeyAgreement;

public class ServerThread extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket serverSocket;
	private KeyStorage serverKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private static final String ACCESS_DENIED = "Access-Denied";
	private static final String ACCESS_GRANTED = "Access-Granted";
	private FileIO fileIO;
	private Hashing hashing;
	private String directory = System.getProperty("user.dir");

	public ServerThread(Socket accept) {
		this.serverSocket = accept;
		fileIO = new FileIO();
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
			System.out.println("Secret Key: " + Arrays.toString(serverKeys.getSecretKey()));

			String username;
			String password;
			username = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
			password = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
			
			int usernameIndex = Server.getUsernameIndex(username);
			if (usernameIndex == -1) {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_DENIED.getBytes()));
			}
			byte[] hashedPassword = hashing.sha256Hash(Server.getSalt(usernameIndex), password);
			
			if (hashing.hashToHex(hashedPassword) != Server.getPassword(usernameIndex)) {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_DENIED.getBytes()));
			}
			else {
				writeToClient.writeObject(serverKeys.encrypt_message(ACCESS_GRANTED.getBytes()));
				String clientFilename;
				int[] int_filename;
				byte[] test_filename;
				byte[] fileReadIn;
				// Fix while loop
				while(true) {
					System.out.println("Waiting for Client Input.");
					int_filename = (int[]) readFromClient.readObject();			
	            	clientFilename = serverKeys.decrypt_message_String(int_filename);			
	
	            	if (clientFilename.equals("finished")) {
						System.out.println("Session with Client " + Thread.currentThread().getId() + " has Ended...");
	        			break;
	            	}
	
					System.out.println("Looking for Client File: " + clientFilename);
	            	File file = new File(clientFilename);
					System.out.println("File Path: " + file.toPath());
					System.out.println(fileIO.fileExists(clientFilename));
	
	            	if(file.exists() && !file.isDirectory()) {
						System.out.println("Sending File...");
	            		// Acknowledgement
	            	    writeToClient.writeObject(serverKeys.encrypt_message(fileFound.getBytes()));
						writeToClient.flush();
						System.out.println("Acknowledgement Sent.");
	            	    // Read in File
	            	    fileReadIn = Files.readAllBytes(file.toPath());
						System.out.println("Finished Reading File.");
	            	    // Write encrypted file to client
	            	    writeToClient.writeObject(serverKeys.encrypt_message(fileReadIn));
						writeToClient.flush();            	
						System.out.println("Finished Sending File.");									
					}
	            	else {
						System.out.println("Failed to find file...");            	
						writeToClient.writeObject(serverKeys.encrypt_message(fileNotFound.getBytes()));
						writeToClient.flush();
	            	}
				}
			}
			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

}

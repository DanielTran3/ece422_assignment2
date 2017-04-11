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

import javax.crypto.KeyAgreement;

public class ServerThread extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket serverSocket;
	private KeyStorage serverKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private FileIO fileIO;

	public ServerThread(Socket accept) {
		this.serverSocket = accept;
		fileIO = new FileIO(accept);
	}

	public void run() {
        System.out.println("Server Connected to Client!");
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

			int[] credentials;
			int count = 0;
			while (count != 2) {
				credentials = (int[]) readFromClient.readObject();
				System.out.println(serverKeys.decrypt_message_String(credentials));
				count++;
			}

			String clientFilename;
			int[] int_filename;
			byte[] test_filename;
			byte[] fileReadIn;
			// Fix while loop
			while(true) {
				System.out.println("waiting");
				int_filename = (int[]) readFromClient.readObject();
				System.out.println("Client Filename Before decryption: " + Arrays.toString(int_filename));				
            	clientFilename = serverKeys.decrypt_message_String(int_filename);
				System.out.println("Client Filename: " + clientFilename);
            	if (clientFilename.equals("finished")) {
        			break;
            	}

            	File file = new File(clientFilename);
            	if(file.exists() && !file.isDirectory()) {
            		// Acknowledgement
            	    writeToClient.writeObject(serverKeys.encrypt_message(fileFound.getBytes()));
					writeToClient.flush();
            	    // Read in File
            	    fileReadIn = fileIO.readFile(file);
            	    // Write encrypted file to client
            	    writeToClient.writeObject(serverKeys.encrypt_message(fileReadIn));
					writeToClient.flush();            	
				}
            	else {
            		writeToClient.writeObject(serverKeys.encrypt_message(fileNotFound.getBytes()));
					writeToClient.flush();
            	}
				break;            
			}

			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

}

//String clientUsername = readFromClient.readLine();
//String clientPassword = readFromClient.readLine();
//if ((clientUsername == null) || (clientPassword == null)) {
//	System.out.println("Error Occurred in receiving credentials.");
//	System.out.println(clientUsername);
//	System.out.println(clientPassword);
//}
//else {
//	System.out.println(clientUsername);
//	System.out.println(clientPassword);
//}

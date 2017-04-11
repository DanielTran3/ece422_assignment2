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
			//PrintWriter writeToClient = new PrintWriter(this.serverSocket.getOutputStream(), true);
            //BufferedReader readFromClient = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
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

			String credentials;
			int count = 0;
			while (count != 2) {
				credentials = (String) readFromClient.readObject();
				System.out.println(serverKeys.decrypt_message_String(credentials.getBytes()));
				count++;
			}

			String clientFilename;
			byte[] fileReadIn;
			// Fix while loop
//			while(serverSocket.isClosed()) {
			while(true) {
				System.out.println("waiting");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	clientFilename = serverKeys.decrypt_message_String(((String) readFromClient.readObject()).getBytes());
            	if (clientFilename.equals("finished")) {
        			break;
            	}

            	File file = new File(clientFilename);
            	if(file.exists() && !file.isDirectory()) {
            		// Acknowledgement
            	    writeToClient.writeObject(serverKeys.encrypt_message(fileFound.getBytes()));
            	    // Read in File
            	    fileReadIn = fileIO.readFile(file);
            	    // Write encrypted file to client
            	    writeToClient.writeObject(serverKeys.encrypt_message(fileReadIn));
            	}
            	else {
            		writeToClient.writeObject(serverKeys.encrypt_message(fileNotFound.getBytes()));
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

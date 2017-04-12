import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

    public FileIO() { }
	public void loadShadowFile(String filename, List<String> userList, List<String> saltList, List<String> passList) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				String[] userSaltPass = line.split("\t");
				String[] saltPass = userSaltPass[1].split("\\$");			
				userList.add(userSaltPass[0]);
				saltList.add(saltPass[0]);
				passList.add(saltPass[1]);
				line = br.readLine();
			}
			br.close();
		}

		catch (IOException e) {
			System.out.println("Could not read data!");
			e.printStackTrace();
		}
	}

	public void bulkWriteShadowFile(String filename, List<String> list_of_passwords) {
		try {
			PrintWriter writer = new PrintWriter(filename);

			for (String value : list_of_passwords) {
				writer.println(value);
			}
			writer.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't write to File!");
			e.printStackTrace();
		}
	}

	public void writeShadowFile(String filename, String salt, String username, String password) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
			writer.write(username + '\t' + salt + "$" + password);
			writer.newLine();
			writer.flush();
			writer.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't write to File!");
			e.printStackTrace();
		}
	}
	
	public boolean fileExists(String filename) {
		File file = new File(filename);
		System.out.println(filename.length());
		return file.exists();
	}
	
	public void saveToFile(String path, String filename, String file) {
		
		try {
			String fullPath = path + '/' + filename;
			PrintWriter saveToFile = new PrintWriter(fullPath);
			saveToFile.write(file);
			saveToFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

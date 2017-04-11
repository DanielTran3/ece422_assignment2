import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
	public List<String> readShadowFile(String filename) {
		List<String> password_list = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				password_list.add(line);
				line = br.readLine();
			}
			br.close();
		}

		catch (IOException e) {
			System.out.println("Could not read data!");
			e.printStackTrace();
		}

		return password_list;
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
			PrintWriter writer = new PrintWriter(filename);
			writer.println(username + '\t' + salt + "$" + password);
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

}

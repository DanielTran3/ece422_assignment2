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
import java.util.ArrayList;
import java.util.List;

public class FileIO {
	
	OutputStream writeTo;
    InputStream readFrom;
	
    public FileIO(Socket sock) {
    	try {
			this.writeTo = sock.getOutputStream();
	    	this.readFrom = sock.getInputStream();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
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
	
	public void writeShadowFile(String filename, String password) {
		try {
			PrintWriter writer = new PrintWriter(filename);
			writer.println(password);
			writer.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't write to File!");
			e.printStackTrace();
		}
	}
	
	public byte[] readFile(File file) {
		byte[] read_data = new byte[(int) file.length()];
        try {
        	int count;
			while ((count = readFrom.read(read_data)) > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return read_data;
	}
	
}

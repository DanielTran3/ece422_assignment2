import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.List;
import java.io.FileOutputStream;

public class FileIO {

    public FileIO() { }
    // Load the usernames, passwords, and salts from the shadowfile (used by Server)
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

    // Write a new entry into the shadow table
    // Username and tab separating the salt$password
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

    // Check if a file exists
	public boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists();
	}

    // Check if directory exists. If not, create it.
    public void dirExists(String directory) {
        if (Files.notExists(Paths.get(directory), LinkOption.NOFOLLOW_LINKS)) {
            File dir = new File(directory);
            dir.mkdir();
        }
    }

    // Save a file to the specified directory with specified name
	public void saveToFile(String path, String filename, byte[] file) {
		try {
			String fullPath = path + '/' + filename;
			FileOutputStream saveToFile = new FileOutputStream(fullPath);
			saveToFile.write(file);
			saveToFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

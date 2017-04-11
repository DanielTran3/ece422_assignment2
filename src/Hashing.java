import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hashing {
	SecureRandom random;
	MessageDigest digest;
	
	public Hashing() {
		this.random = new SecureRandom();
		try {
			this.digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String sha256Hash(byte[] salt, String text) {
		String concatText = salt + text;
		byte[] hash = digest.digest(concatText.getBytes(StandardCharsets.UTF_8));
		return hash.toString();
	}
	
	public byte[] generateSalt() {
		byte[] saltBytes = new byte[16];
		random.nextBytes(saltBytes);
		return saltBytes;
	}
}

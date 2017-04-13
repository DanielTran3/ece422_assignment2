import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hashing {
	SecureRandom random;
	MessageDigest digest;

	public Hashing() {
		try {
			// Initialize SecureRandom and MessageDigest
			this.random = SecureRandom.getInstance("SHA1PRNG");
			this.digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	// Compute a SHA-256 hash using a salt and the password
	public byte[] sha256Hash(String salt, String text) {
		String concatText = salt + text;
		byte[] hash = digest.digest(concatText.getBytes(StandardCharsets.UTF_8));
		return hash;
	}

	// Function to generate a salt using SecureRandom
	public byte[] generateSalt() {
		byte[] saltBytes = new byte[16];
		random.nextBytes(saltBytes);
		return saltBytes;
	}

	// Convert a hashed password to hex form to store in Shadow File
	public String hashToHex(byte[] hashedText) {
		StringBuffer hexHash = new StringBuffer();
		for (int i = 0; i < hashedText.length; i++) {
			hexHash.append(Integer.toHexString(0xFF & hashedText[i]));
		}
		return hexHash.toString();
	}
}

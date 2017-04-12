import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Hashing {
	SecureRandom random;
	MessageDigest digest;
	
	public Hashing() {
		try {
			this.random = SecureRandom.getInstance("SHA1PRNG");
			this.digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] sha256Hash(String salt, String text) {
		String concatText = salt + text;
		byte[] hash = digest.digest(concatText.getBytes(StandardCharsets.UTF_8));
		return hash;
	}
	
	public byte[] generateSalt() {
		byte[] saltBytes = new byte[16];
		random.nextBytes(saltBytes);
		System.out.println("Salt: " + Arrays.toString(saltBytes));
		return saltBytes;
	}

	public String hashToHex(byte[] hashedText) {
		StringBuffer hexHash = new StringBuffer();		
		for (int i = 0; i < hashedText.length; i++) {
			hexHash.append(Integer.toHexString(0xFF & hashedText[i]));
		}
		System.out.println("Hexed: " + hexHash.toString());
		return hexHash.toString();
	}

}

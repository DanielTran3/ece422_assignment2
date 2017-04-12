import java.io.File;
import java.util.Arrays;

class Test {
	public static void main(String[] args) {
		KeyStorage a = new KeyStorage();
		String b = "Hello";
		System.out.println(Arrays.toString(b.getBytes()));
		int[] c = a.byteToIntArray(b.getBytes());
		System.out.println(Arrays.toString(c));
		byte[] d = a.intToByteArray(c);
		System.out.println(Arrays.toString(d));
		String e = new String(d).replaceAll("\0", "");
		byte[] f = e.getBytes();
		System.out.println(Arrays.toString(f));
		System.out.println(e.equals(b));
	}
}

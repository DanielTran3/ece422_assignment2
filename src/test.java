import java.io.File;

class Test {
	public static void main(String[] args) {

		String directory = System.getProperty("user.dir");
		System.out.println(directory);
		System.out.println(args[0]);
		File file = new File(args[0]);
		System.out.println(file.getPath());
		System.out.println(file.exists());
		file = new File(directory + '/' + args[0]);
		System.out.println(file.getPath());		
		System.out.println(file.exists());
	}
}

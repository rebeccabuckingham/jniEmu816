/*
 * this was a driver class for the jni test project
 * it will be deleted soon
 */

public class HelloWorld {
	public native void print();

	static {
		System.loadLibrary("lib65816");
	}

	public static void main(String[] args) {
		new HelloWorld().print();
	}
}

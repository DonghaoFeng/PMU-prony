package jni;

public class ReadTB {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		ReadTableJNI.readTable(args[0], args[1], args[2], args[3]);
		System.out.println(System.currentTimeMillis() - time);
	}
}

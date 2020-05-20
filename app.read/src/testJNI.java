public class testJNI {

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		ReadTableJNI.readTable("realtime", "scada", "psdb", "substation");
		System.out.println(System.currentTimeMillis());
	}
}

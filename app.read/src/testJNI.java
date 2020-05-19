

public class testJNI {

	public static void main(String[] args) {
		System.out.println(ReadTableJNI.readTable("realtime", "scada", "psdb", "substation"));
	}
}

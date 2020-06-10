package jni;

import java.util.ArrayList;

public class ReadDBNew {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		ArrayList list = (ArrayList) ReadTableJNI.readTableN("realtime", "wams_fes", "psdb", "wams_his_data1");
//		System.out.println(list.get(0).toString());
//		System.out.println("size:" + list.size());
		System.out.println(System.currentTimeMillis() - time);
//		System.out.println();
//		System.out.println(ReadTableJNI.readTable("realtime", "wams_fes", "psdb", "wams_his_data1"));
		
	}
}

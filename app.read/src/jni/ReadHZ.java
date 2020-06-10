package jni;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import app.read.collection.DataHelper;
import app.read.collection.FixSizeLinkedList;

public class ReadHZ {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress("11.11.22.77:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
		
//		client.getSet("wams_index_table").stream().sorted(new Comparator<Object>() {
//			@Override
//			public int compare(Object r1, Object r2) {
//				return ((String)r1).compareTo((String)r2);
//			}
//		}).forEach(o -> System.out.println(o));
		IMap<Object, Object> map = client.getMap("device_pmu");
		FixSizeLinkedList list = (FixSizeLinkedList) client.getMap("device_pmu").get(4L);
		long time = System.currentTimeMillis();
		double[] yArray =new DataHelper(list).getData(120);
//		DrawMath.add(yArray);
//		DrawMath.draw();
		System.out.println(System.currentTimeMillis() - time);
		client.shutdown();
		
	}
}

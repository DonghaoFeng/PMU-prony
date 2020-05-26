package jni;

import java.util.Comparator;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class ReadHZ {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress("11.11.22.77:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
		long time = System.currentTimeMillis();
		client.getSet("wams_index_table").stream().sorted(new Comparator<Object>() {
			@Override
			public int compare(Object r1, Object r2) {
				return ((String)r1).compareTo((String)r2);
			}
		}).forEach(o -> System.out.println(o));
		IMap map= client.getMap("device_pmu");
		client.getMap("device_pmu").values().forEach(o->System.out.println(o));
		System.out.println(System.currentTimeMillis() - time);
		client.shutdown();
		
	}
}

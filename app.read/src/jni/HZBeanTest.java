package jni;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import app.read.bean.Wams_his_dataRecord;
import app.read.collection.WAMSData;

public class HZBeanTest {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress("11.11.22.77:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
		long time = System.currentTimeMillis();
		
		IMap map= client.getMap("device_pmu");
		client.getMap("device_pmu").set("1", new WAMSData("sdadasd", new Wams_his_dataRecord()));
		System.out.println(client.getMap("device_pmu").get("1"));
		System.out.println(System.currentTimeMillis() - time);
		client.shutdown();
		
	}
}

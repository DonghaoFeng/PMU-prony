package jni;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import app.read.reader.PMUReader;

public class ReadDB {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress("11.11.22.77:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
		
//		System.out.println(ReadTableJNI.readTable("realtime", "wams_fes", "psdb", "wams_index_table"));
//		System.out.println(ReadTableJNI.readTable("realtime", "wams_fes", "psdb", "wams_his_data1"));
		
		PMUReader reader = new PMUReader(client);
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		
		
		long time = System.currentTimeMillis();
		reader.readPmuToCache();
		System.out.println(System.currentTimeMillis() - time);
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long time = System.currentTimeMillis();
				reader.readPmuToCache();
				System.out.println(System.currentTimeMillis() - time);
			}
		}, 0, 1, TimeUnit.SECONDS);
		
	}
}

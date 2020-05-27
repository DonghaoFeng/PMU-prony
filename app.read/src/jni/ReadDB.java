package jni;

import java.util.Timer;
import java.util.TimerTask;

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
		Timer timer = new Timer();
		long time = System.currentTimeMillis();
		reader.readPMUN2Cache();
		System.out.println(System.currentTimeMillis() - time);
		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long time = System.currentTimeMillis();
				reader.readPMUN2Cache();
				System.out.println(System.currentTimeMillis() - time);
			}
		},0, 1000);
		
	}
}

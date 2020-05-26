package server;


import java.io.FileNotFoundException;
import java.io.IOException;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

public class Server {
	public static void main(String[] args)
			throws IOException {
		HazelcastInstance hz = startServer(args);

	}

	@SuppressWarnings("unchecked")
	public static HazelcastInstance startServer(String[] args) throws FileNotFoundException {
		Config config = new Config();
		NetworkConfig network = config.getNetworkConfig();
		network.setPublicAddress("11.11.22.77:5701");
		network .getJoin().getMulticastConfig().setEnabled(false);
		HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
		config.getMapConfig("device_pmu").setInMemoryFormat(InMemoryFormat.OBJECT);
		hz.addDistributedObjectListener(new DistributedObjectListener() {
			@Override
			public void distributedObjectCreated(DistributedObjectEvent event) {
				DistributedObject instance = event.getDistributedObject();
				System.out.println("Created " + instance.getName() + ", " + instance.getServiceName());
			}

			@Override
			public void distributedObjectDestroyed(DistributedObjectEvent event) {
				DistributedObject instance = event.getDistributedObject();
				System.out.println("Destroyed " + instance.getName() + ", " + instance.getServiceName());
			}
		});
//		PhysicModelUpdateHelper.readFieldsFile("testData/sys_column_info.txt");
		return hz;
	}

	
	static class MyEntryListener implements EntryAddedListener<String, String>, EntryRemovedListener<String, String>,
			EntryUpdatedListener<String, String>, EntryEvictedListener<String, String> {
		@Override
		public void entryAdded(EntryEvent<String, String> event) {
			System.out.println("Entry Added:" + event);
		}

		@Override
		public void entryRemoved(EntryEvent<String, String> event) {
			System.out.println("Entry Removed:" + event);
		}

		@Override
		public void entryUpdated(EntryEvent<String, String> event) {
			System.out.println("Entry Updated:" + event);
		}

		@Override
		public void entryEvicted(EntryEvent<String, String> event) {
			System.out.println("Entry Evicted:" + event);
		}

		
	}

}
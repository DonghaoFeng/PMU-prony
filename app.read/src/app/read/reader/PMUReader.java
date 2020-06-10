package app.read.reader;

import java.util.ArrayList;
import java.util.Comparator;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;

import app.read.bean.Wams_his_dataRecord;
import app.read.bean.Wams_index_tableRecord;
import app.read.collection.WAMSData;
import app.read.process.AddPMUProcessor;
import jni.ReadTableJNI;

public class PMUReader {

	private HazelcastInstance client;

	public PMUReader(HazelcastInstance client) {
		super();
		this.client = client;
	}

	public void readPMUN2Cache() {
		ArrayList<Wams_index_tableRecord> indexRecrodList = (ArrayList<Wams_index_tableRecord>) ReadTableJNI
				.readTableN("realtime", "wams_fes", "psdb", "wams_index_table");
		ISet<Object> timeSet = client.getSet("wams_index_table");
		IMap<Object, Object> pmuMap = client.getMap("device_pmu");

		indexRecrodList.stream().filter(index -> index.getValid() == 1)
				.filter(index -> !timeSet.contains(index.getTime())).sorted(new Comparator<Wams_index_tableRecord>() {
					@Override
					public int compare(Wams_index_tableRecord r1, Wams_index_tableRecord r2) {
						return r1.getTime().compareTo(r2.getTime());
					}
				}).forEach(index -> {
					ArrayList<Wams_his_dataRecord> wamsDataTable = (ArrayList<Wams_his_dataRecord>) ReadTableJNI
							.readTableN("realtime", "wams_fes", "psdb", index.getTable_name());
					wamsDataTable.parallelStream().forEach(dataRecord -> {
						pmuMap.executeOnKey(dataRecord.getRecord_id(),
								new AddPMUProcessor(new WAMSData(index.getTime(), dataRecord)));
					});
					System.out.println("New time:" + index.getTime());
					timeSet.add(index.getTime());
				});

	}
	
	

}

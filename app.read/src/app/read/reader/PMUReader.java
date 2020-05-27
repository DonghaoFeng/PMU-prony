package app.read.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;

import app.read.bean.WAMSDataRecord;
import app.read.bean.WAMSIndexRecord;
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
		String wamsIndexTable = ReadTableJNI.readTable("realtime", "wams_fes", "psdb", "wams_index_table");
		if (wamsIndexTable.length() > 10) {
			String[] indexStrArray = wamsIndexTable.split("\\n");
			ArrayList<WAMSIndexRecord> indexRecrodList = new ArrayList<WAMSIndexRecord>();
			for (int i = 0; i < indexStrArray.length; i++) {
				indexRecrodList.add(new WAMSIndexRecord(indexStrArray[i]));
			}
			ISet<Object> timeSet = client.getSet("wams_index_table");
			IMap<Object, Object> pmuMap = client.getMap("device_pmu");

			indexRecrodList.stream().filter(index -> index.getValid()==1).filter(index -> !timeSet.contains(index.getTime()))
					.sorted(new Comparator<WAMSIndexRecord>() {
						@Override
						public int compare(WAMSIndexRecord r1, WAMSIndexRecord r2) {
							return r1.getTime().compareTo(r2.getTime());
						}
					}).forEach(index -> {
						String wamsDataTable = ReadTableJNI.readTable("realtime", "wams_fes", "psdb",
								index.getTable_name());
						if (wamsDataTable.length() > 10) {
							String[] wamsDataStrArray = wamsDataTable.split("\\n");
							Arrays.asList(wamsDataStrArray).parallelStream().forEach(wamsDataStr->{
								WAMSDataRecord dataRecord = new WAMSDataRecord(wamsDataStr);
								pmuMap.executeOnKey(dataRecord.getRecord_id(), new AddPMUProcessor(new WAMSData(index.getTime(), dataRecord)));	
							});
							System.out.println("New time:"+index.getTime());
							timeSet.add(index.getTime());
						} else {
							System.err.println("data table error");
						}
					});
		} else {
			System.err.println("index table error");
		}
	}

}

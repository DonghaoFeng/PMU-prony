package app.read.process;

import java.io.Serializable;
import java.util.Map.Entry;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;

import app.read.collection.FixSizeLinkedList;
import app.read.collection.WAMSData;

/**
* @Author Donghao Feng
*/
public class AddPMUProcessor implements EntryProcessor, Serializable {

	private  WAMSData data;

	public AddPMUProcessor() {
		super();
	}

	public AddPMUProcessor(WAMSData data) {
		super();
		this.data = data;
	}

	@Override
	public EntryBackupProcessor getBackupProcessor() {
		return null;
	}

	@Override
	public Object process(Entry entry) {
		Object list =  entry.getValue();
		if (list == null) {
			list =new FixSizeLinkedList<WAMSData>(120);
		}
		((FixSizeLinkedList<WAMSData>) list)
				.add(this.data);
		entry.setValue(list);
		return null;
	}

}

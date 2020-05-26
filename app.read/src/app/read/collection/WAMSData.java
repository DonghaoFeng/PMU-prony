package app.read.collection;

import app.read.bean.BaseJSONBean;
import app.read.bean.WAMSDataRecord;

public class WAMSData extends BaseJSONBean{
	private String time;
	private WAMSDataRecord record;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public WAMSDataRecord getRecord() {
		return record;
	}
	public void setRecord(WAMSDataRecord record) {
		this.record = record;
	}
	public WAMSData(String time, WAMSDataRecord record) {
		super();
		this.time = time;
		this.record = record;
	}

}

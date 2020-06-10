package app.read.collection;

import app.read.bean.BaseJSONBean;
import app.read.bean.Wams_his_dataRecord;

public class WAMSData extends BaseJSONBean{
	private String time;
	private Wams_his_dataRecord record;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Wams_his_dataRecord getRecord() {
		return record;
	}
	public void setRecord(Wams_his_dataRecord record) {
		this.record = record;
	}
	public WAMSData(String time, Wams_his_dataRecord record) {
		super();
		this.time = time;
		this.record = record;
	}

}

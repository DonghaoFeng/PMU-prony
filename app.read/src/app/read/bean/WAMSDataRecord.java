package app.read.bean;

import java.util.ArrayList;

public class WAMSDataRecord extends BaseJSONBean {

	public WAMSDataRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	public WAMSDataRecord(String dataStr) {
		String[] attributeStr = dataStr.split("\\|");
//		for (int i = 0; i < attributeStr.length; i++) {
//			System.out.print(attributeStr[i]+",");
//		}
		this.record_id = Long.valueOf(attributeStr[0]);
		this.psid = Long.valueOf(attributeStr[1].split(",")[0] + attributeStr[1].split(",")[1]);
		for (int i = 0; i < 50; i++) {
			this.pointList.add(new PointRecord(Double.valueOf(attributeStr[2 * (i + 1)]),
					Integer.valueOf(attributeStr[2 * (i + 1) + 1])));
		}
	}
	private long record_id;
	private long psid;
	private ArrayList<PointRecord> pointList  = new ArrayList<PointRecord>();
	
	public long getRecord_id() {
		return record_id;
	}
	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}
	public long getPsid() {
		return psid;
	}
	public void setPsid(long psid) {
		this.psid = psid;
	}
	public ArrayList<PointRecord> getPointList() {
		return pointList;
	}
	public void setPointList(ArrayList<PointRecord> pointList) {
		this.pointList = pointList;
	}
	
}

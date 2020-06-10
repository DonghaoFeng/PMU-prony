package app.read.bean;

import java.util.ArrayList;

public class Wams_his_dataRecord extends BaseJSONBean {

	public Wams_his_dataRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Wams_his_dataRecord(ArrayList<PointRecord> pointList) {
		super();
		this.pointList = pointList;
	}


	public Wams_his_dataRecord(String dataStr) {
		String[] attributeStr = dataStr.split("\\|");
//		for (int i = 0; i < attributeStr.length; i++) {
//			System.out.print(attributeStr[i]+",");
//		}
		this.record_id = Integer.valueOf(attributeStr[0]);
		this.psid = Long.valueOf(attributeStr[1].split(",")[0] + attributeStr[1].split(",")[1]);
		for (int i = 0; i < 50; i++) {
			this.pointList.add(new PointRecord(Double.valueOf(attributeStr[2 * (i + 1)]),
					Integer.valueOf(attributeStr[2 * (i + 1) + 1])));
		}
	}
	public int record_id;
	private long psid;
	private ArrayList<PointRecord> pointList  = new ArrayList<PointRecord>();
	private int status1;
	private int status2;
	private int status3;
	private int status4;
	private int status5;
	private int status6;
	private int status7;
	private int status8;
	private int status9;
	private int status10;
	private int status11;
	private int status12;
	private int status13;
	private int status14;
	private int status15;
	private int status16;
	private int status17;
	private int status18;
	private int status19;
	private int status20;
	private int status21;
	private int status22;
	private int status23;
	private int status24;
	private int status25;
	private int status26;
	private int status27;
	private int status28;
	private int status29;
	private int status30;
	private int status31;
	private int status32;
	private int status33;
	private int status34;
	private int status35;
	private int status36;
	private int status37;
	private int status38;
	private int status39;
	private int status40;
	private int status41;
	private int status42;
	private int status43;
	private int status44;
	private int status45;
	private int status46;
	private int status47;
	private int status48;
	private int status49;
	private int status50;
	
	private float value1;
	private float value2;
	private float value3;
	private float value4;
	private float value5;
	private float value6;
	private float value7;
	private float value8;
	private float value9;
	private float value10;
	private float value11;
	private float value12;
	private float value13;
	private float value14;
	private float value15;
	private float value16;
	private float value17;
	private float value18;
	private float value19;
	private float value20;
	private float value21;
	private float value22;
	private float value23;
	private float value24;
	private float value25;
	private float value26;
	private float value27;
	private float value28;
	private float value29;
	private float value30;
	private float value31;
	private float value32;
	private float value33;
	private float value34;
	private float value35;
	private float value36;
	private float value37;
	private float value38;
	private float value39;
	private float value40;
	private float value41;
	private float value42;
	private float value43;
	private float value44;
	private float value45;
	private float value46;
	private float value47;
	private float value48;
	private float value49;
	private float value50;
	
	
	public long getRecord_id() {
		return record_id;
	}
	public void setRecord_id(int record_id) {
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
	
	public void ini() {
		if (this.pointList.size() == 0) {
			for (int i = 1; i <= 50; i++) {
				try {
					this.pointList.add(new PointRecord(this.getClass().getDeclaredField("value" + i).getDouble(this),
							this.getClass().getDeclaredField("status" + i).getInt(this)));
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

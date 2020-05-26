package app.read.bean;

public class WAMSIndexRecord extends BaseJSONBean {

	public WAMSIndexRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WAMSIndexRecord(String indexStr) {
		String[] attributeStr = indexStr.split("\\|");
		this.keyid = Long.valueOf(attributeStr[0]);
		this.table_name = attributeStr[1];
		this.time = attributeStr[2];
		this.valid = Integer.valueOf(attributeStr[3]);
	}
	
	private long keyid;
	private String table_name;
	private String time;
	private int valid;
	public long getKeyid() {
		return keyid;
	}
	public void setKeyid(long keyid) {
		this.keyid = keyid;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	
	
}

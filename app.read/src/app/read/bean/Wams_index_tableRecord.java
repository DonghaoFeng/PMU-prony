package app.read.bean;

public class Wams_index_tableRecord extends BaseJSONBean {
	
	public long keyid;
	public String table_name;
	public String time;
	public int valid;
	
	public Wams_index_tableRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Wams_index_tableRecord(String indexStr) {
		String[] attributeStr = indexStr.split("\\|");
		this.keyid = Long.valueOf(attributeStr[0]);
		this.table_name = attributeStr[1];
		this.time = attributeStr[2];
		this.valid = Integer.valueOf(attributeStr[3]);
	}
	

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

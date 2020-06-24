package app.read.bean;

/**
 * @author Donghao
 *
 */
public class PointRecord extends BaseJSONBean {
	
	private double value;
	private int status;
	
	public PointRecord(double value, int status) {
		this.value = value;
		this.status = status;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	

}

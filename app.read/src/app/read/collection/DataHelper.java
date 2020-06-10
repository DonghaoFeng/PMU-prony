package app.read.collection;

import java.util.List;

import app.read.bean.Wams_his_dataRecord;

public class DataHelper {

	private FixSizeLinkedList<WAMSData> list;

	public DataHelper(FixSizeLinkedList<WAMSData> list) {
		super();
		this.list = list;
	}

	public double[] getData(int duration) {
		List<WAMSData> subList = this.list.subList(this.list.size() - duration, this.list.size());
		double[] yOrigin = new double[duration * 50];
		for (int i = 0; i < duration; i++) {
			for (int j = 0; j < 50; j++) {
				Wams_his_dataRecord record = subList.get(i).getRecord();
				try {
					yOrigin[i * 50 + j] = record.getClass().getDeclaredField("value" + (j + 1)).getDouble(record);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return yOrigin;
	}

}

package app.read.collection;

import java.util.List;

public class DataHelper {
	
	private FixSizeLinkedList<WAMSData> list;

	public DataHelper(FixSizeLinkedList<WAMSData> list) {
		super();
		this.list = list;
	}
	
	public double[] getData(int duration) {
		List<WAMSData> subList = this.list.subList(this.list.size() - duration, this.list.size());
		double[] yOrigin = new double[duration * 50];
		list.parallelStream().forEach(data -> data.getRecord().ini());
		for (int i = 0; i < duration; i++) {
			for (int j = 0; j < 50; j++) {
				yOrigin [i*50+j]=subList.get(i).getRecord().getPointList().get(j).getValue();
			}
		}
		
		return yOrigin;
	}

}

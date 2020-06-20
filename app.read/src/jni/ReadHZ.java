package jni;

import java.util.List;

import com.chinomars.prony.Prony;
import com.chinomars.prony.PronyParameter;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import app.preprocess.PreFilter;
import app.preprocess.test.DrawMath;
import app.read.collection.DataHelper;
import app.read.collection.FixSizeLinkedList;

public class ReadHZ {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress("11.11.22.77:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
		
//		client.getSet("wams_index_table").stream().sorted(new Comparator<Object>() {
//			@Override
//			public int compare(Object r1, Object r2) {
//				return ((String)r1).compareTo((String)r2);
//			}
//		}).forEach(o -> System.out.println(o));
		IMap<Object, Object> map = client.getMap("device_pmu");
		FixSizeLinkedList list = (FixSizeLinkedList) client.getMap("device_pmu").get(2L);
		long time = System.currentTimeMillis();
		double[] values =new DataHelper(list).getData(20);
		
//		DrawMath.add(values);
		DrawMath.draw();
		if (PreFilter.checkOscillation(values, 4)) {
//			DrawMath.add(values);
			long start = System.currentTimeMillis();
			values = PreFilter.prefilter(values, 5);
			DrawMath.add(values);
//			DrawMath.draw();

			Prony prony = new Prony(values, 0.02, 40);
			
//			prony.fit(8);
			List<PronyParameter> pList = prony.getParameterList();
			DrawMath.add(prony.getFitvalues());
		}
		System.out.println(System.currentTimeMillis() - time);
		client.shutdown();
		
	}
}

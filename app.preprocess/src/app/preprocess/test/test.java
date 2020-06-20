package app.preprocess.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.chinomars.prony.Prony;
import com.chinomars.prony.PronyParameter;

import app.preprocess.PreFilter;

public class test {
	int point = 5 * 50;

	private double frequency = 2;
	private double T = 0.02;
	private double phase = 0.1;
	private double damping = -0.2;
	private double amlitude = 2;

	private double error = 0.1;

	@Test
	public void test() {
		double[] t = new double[point];
		for (int k = 0; k < t.length; k++) {
			t[k] = T * k;
		}
		double[] values = new double[point * 2];
		for (int k = 0; k < point; k++) {
			if (k < point - 60) {
				values[k] = 2 + (Math.random() - 0.5) * 0.2;
			} else if (k < point - 40) {
				values[k] = 2 + (Math.random() - 0.5) * 0.2 - (k - (point - 60)) * 0.4;
			} else if (k < point - 20) {
				values[k] = -6 + (Math.random() - 0.5) * 0.2;
			} else {
				values[k] = -6 + (Math.random() - 0.5) * 0.2 + (k - (point - 20)) * 0.4;
			}
			values[k + point] = 2 + Math.cos(2 * Math.PI * 10 * frequency * t[k] + phase) * 0.1
					+ amlitude * Math.cos(2 * Math.PI * frequency * t[k] + phase) * Math.exp(t[k] * damping)
					+ 0.2 * amlitude * Math.cos(2 * Math.PI * 1.5 * frequency * t[k] + 2 * phase)
							* Math.exp(t[k] * 0.7 * damping);
		}
		if (PreFilter.checkOscillation(values, 4)) {
//			DrawMath.add(values);
			long start = System.currentTimeMillis();
			values = PreFilter.prefilter(values, 5);
//			DrawMath.add(values);
//			DrawMath.draw();

			Prony prony = new Prony(values, T, 40);
//			prony.fit(8);
			List<PronyParameter> list = prony.getParameterList();
			DrawMath.add(prony.getFitvalues());
//			System.out.println("parameter size:" + prony.getRealParameterList().size());
			PronyParameter p = list.get(0);
			assertTrue(Math.abs((p.getAmlitude() - amlitude) / amlitude) < error);
			assertTrue(Math.abs((p.getDamping() - damping) / damping) < error);
			assertTrue(Math.abs((p.getFrequency() - frequency) / frequency) < error);
			System.out.println("Toal time(ms):" + (System.currentTimeMillis() - start));
		}
	}
}

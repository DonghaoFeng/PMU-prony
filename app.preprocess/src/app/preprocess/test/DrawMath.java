package app.preprocess.test;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

 
/**
 * @author Donghao
 *
 */
public class DrawMath {
	static XYSeriesCollection dataset = new XYSeriesCollection();
	public static void add(double[]values) {
	XYSeries series = new XYSeries("xySeries");
	for (int i = 0; i <  values.length;i++) {
		series.add(i,  values[i]);
	}
	
	dataset.addSeries(series);
	}
	
	public static void draw() {
		
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"y", // chart title
				"x", // x axis label
				"y", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL,
				false, // include legend
				false, // tooltips
				false // urls
				);
 
		ChartFrame frame = new ChartFrame("my picture", chart);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
 
}

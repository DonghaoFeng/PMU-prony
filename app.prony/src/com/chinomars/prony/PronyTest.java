package com.chinomars.prony;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class PronyTest {
	int point = 100;
	
	private double frequency = 4;
	private double T = 0.02;
	private double phase = 0.1;
	private double damping = -0.1;
	private double amlitude = 2;
	
	private double error = 0.0001;
	
	@Test
	public void test() {
		double[] t = new double[point];
		double[] time = new double[point];
		for (int k = 0; k < time.length; k++) {
			t[k] = T * k;
		}
		double[] values = new double[point];
		for (int k = 0; k < time.length; k++) {
			values[k] = amlitude * Math.cos(2 * Math.PI * frequency * t[k] + phase) * Math.exp(t[k]* damping )+  Math.cos(2 * Math.PI * frequency *2* t[k] + phase*2) * Math.exp(t[k]* damping*2 );;
			
		}
		long startTime = System.currentTimeMillis();
		ArrayList<PronyParameter> list = new Prony(values, T,40).getParameterList();
		System.out.println(System.currentTimeMillis()-startTime);
		PronyParameter p = list.get(0);
		assertTrue(Math.abs(p.getAmlitude() - amlitude ) < error);
		assertTrue(Math.abs(p.getDamping() - damping) < error);
		assertTrue(Math.abs(p.getFrequency() - frequency) < error);
		assertTrue(Math.abs(p.getPhase() - phase) < error);
	}
}

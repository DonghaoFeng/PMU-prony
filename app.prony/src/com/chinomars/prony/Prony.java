package com.chinomars.prony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.complex.Complex;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Donghao
 *
 */
final public class Prony {

	private final double T;
	private int rank = 1;
	private final double[] values;
	private final double minSnr;
	private double fitSnr;
	private double[] fitvalues;
	private ArrayList<PronyParameter> parameterList;
	private final HashMap<Integer, Double> mapSnr = new HashMap<Integer, Double>();

	public Prony(double[] values, double t, double minSnr) {
		this.values = values;
		this.T = t;
		this.minSnr = minSnr;
	}

	public double getFitSnr() {
		return fitSnr;
	}

	public int getEstimateNumber() {
		return rank;
	}

	public double[] getFitvalues() {
		return fitvalues;
	}

	public ArrayList<PronyParameter> getParameterList() {
		if (parameterList == null) {
			fit();
		}
		return parameterList;
	}

	public List<PronyParameter> getRealParameterList() {
		if (parameterList == null) {
			fit();
		}
		PronyParameter maxP = parameterList.get(0);
		
		return parameterList.stream().filter(p -> p.getEnergy() / maxP.getEnergy() > 0.1).collect(Collectors.toList());
	}

	/**
	 * Calculate the D matrix
	 */
	private Matrix calDMatrix(double[] yArray, int l) {
		int n = yArray.length;
		Matrix dMatrix = new Matrix(n - l, l);
		int i, j;
		for (i = 0; i < n - l; ++i) {
			for (j = 0; j < l; ++j) {
				dMatrix.set(i, j, yArray[l - j - 1 + i]);
			}
		}
		return dMatrix;
	}

	/**
	 * least squares algorithm to calculate a_i
	 */
	private Matrix calAMatrix(double[] yArray, int l) {
		Matrix DMatrix = calDMatrix(yArray, l);
		int n = yArray.length;

		Matrix dMatrix = new Matrix(n - l, 1);
		int i;
		for (i = 0; i < n - l; ++i) {
			dMatrix.set(i, 0, yArray[l + i]);
		}
		return DMatrix.solve(dMatrix);
	}

	/**
	 * solve the poly
	 * 
	 * @param aArray
	 * @return
	 */
	public EigenvalueDecomposition roots(double[] aArray) {
		int len = aArray.length - 1;
		double[][] genA = new double[len][len];
		int i;
		for (i = 0; i < len; ++i) {
			genA[0][i] = -aArray[i + 1] / aArray[0];
		}

		for (i = 1; i < len; ++i) {
			genA[i][i - 1] = 1;
		}

		Matrix AMat = new Matrix(genA);
//        AMat.print(0,0);

		return AMat.eig();

	}

	private ArrayList<PronyParameter> getParamerterList(EigenvalueDecomposition u, double[] values) {
		SparseEqnComplexFast eqn = new SparseEqnComplexFast(values.length);		
		// u matrix
		int i, j;
		int length = u.getRealEigenvalues().length;
		int valuesLength = values.length;
		double[] realEigenvalues = u.getRealEigenvalues();
		double[] imagEigenvalues = u.getImagEigenvalues();
		Complex complex;
		Complex pow;
		for (j = 0; j < length; j++) {	
			complex = new Complex(realEigenvalues[j], imagEigenvalues[j]);
			pow = new Complex(1, 0).divide(complex);
			for (i = 0; i < valuesLength; i++) {
				pow = pow.multiply(complex);
				eqn.setA(pow.getReal(), pow.getImaginary(),i, j);
			}
		}
		// y matrix
		for (i = 0; i < valuesLength; i++) {
			eqn.setBi(values[i], 0, i);
		}
		// C matrix
		try {
			eqn.solveEqn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getParamerterList(u, eqn);
		
		//old code
		/*ISparseEqnComplex eqn = NumericObjectFactory.createSparseEqnComplex(values.length);
		// u matrix
		int i, j;
		int length = u.getRealEigenvalues().length;
		int values_length = values.length;
		double[] realEigenvalues = u.getRealEigenvalues();
		double[] imagEigenvalues = u.getImagEigenvalues();
		for (j = 0; j < length; j++) {
			Complex complex = new Complex(realEigenvalues[j], imagEigenvalues[j]);
			for (i = 0; i < values_length; i++) {
				eqn.setA(complex.pow(i), i, j);
			}
		}
		// y matrix
		for (i = 0; i < values_length; i++) {
			eqn.setBi(new Complex(values[i], 0), i);
		}
		// C matrix
		try {
			eqn.solveEqn();
		} catch (IpssNumericException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getParamerterList(u, eqn);*/
	}

	private ArrayList<PronyParameter> getParamerterList(EigenvalueDecomposition u, SparseEqnComplexFast eqn) {
		ArrayList<PronyParameter> paramerterList = new ArrayList<PronyParameter>();
		for (int i = 0; i < u.getRealEigenvalues().length; i++) {
			Complex uPole = new Complex(u.getRealEigenvalues()[i], u.getImagEigenvalues()[i]);
			double f = uPole.log().getImaginary() / (2.0 * Math.PI * T);
			if (f > 0) {
				PronyParameter parameter = new PronyParameter(this.values.length,this.T);
				paramerterList.add(parameter);

				Complex cPole = eqn.getX(i);

				parameter.setAmlitude(2 * Math.exp(cPole.log().getReal()));
				parameter.setDamping(uPole.log().getReal() / T);
				parameter.setFrequency(f);
				parameter.setPhase(cPole.log().getImaginary());
			}
		}
		return paramerterList;
	}
	
	//old code
/*	private ArrayList<PronyParameter> getParamerterList(EigenvalueDecomposition u,
			ISparseEqnObject<Complex, Complex> eqn) {
		ArrayList<PronyParameter> paramerterList = new ArrayList<PronyParameter>();
		for (int i = 0; i < u.getRealEigenvalues().length; i++) {
			Complex uPole = new Complex(u.getRealEigenvalues()[i], u.getImagEigenvalues()[i]);
			double f = uPole.log().getImaginary() / (2.0 * Math.PI * T);
			if (f > 0) {
				PronyParameter parameter = new PronyParameter();
				paramerterList.add(parameter);

				Complex cPole = eqn.getX(i);

				parameter.setAmlitude(2 * Math.exp(cPole.log().getReal()));
				parameter.setDamping(uPole.log().getReal() / T);
				parameter.setFrequency(f);
				parameter.setPhase(cPole.log().getImaginary());
			}
		}
		return paramerterList;
	}*/


	private double[] getFitValues(int length) {
		double[] values = new double[length];
		parameterList.forEach(p -> {
			for (int k = 0; k < length; k++) {
				values[k] += p.getFitValue(k);
			}
		});
		return values;
	}
	
	

	private void calSnr() {
		double[] diff = new double[values.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = values[i] - fitvalues[i];
		}

		fitSnr = 20 * Math.log10(rms(values) / rms(diff));
		mapSnr.put(rank, fitSnr);
	}

	private double rms(double[] value) {
		double rms = 0;
		for (int i = 0; i < value.length; i++) {
			rms += value[i] * value[i];
		}

		return Math.sqrt(rms / value.length);
	}

	public void fit() {
		fit(rank);
		while (fitSnr < minSnr && rank <= 50) {
			rank++;
			fit(rank);
		}
		if (rank > 20) {
			Entry<Integer, Double> entry = mapSnr.entrySet().stream().max(new Comparator<Entry<Integer, Double>>() {
				@Override
				public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			}).get();
			fit(entry.getKey());
			rank = entry.getKey();
		}
		parameterList.forEach(p->{
			System.out.println(p);
		});
		
	}

	/**
	 * 
	 * @param number the number of parameter for fitting measure
	 * @return parameters list
	 */
	public void fit(int rank) {
		Matrix aMatrix = calAMatrix(values, rank * 2);
		double[] aArray = aMatrix.getColumnPackedCopy();
		double[] aArrayNew = new double[aArray.length + 1];
		aArrayNew[0] = 1;
		for (int i = 0; i < aArray.length; i++) {
			aArrayNew[i + 1] = -aArray[i];
		}
		EigenvalueDecomposition u = roots(aArrayNew);
		parameterList = getParamerterList(u, values);
		Collections.sort(parameterList);

		fitvalues = getFitValues(values.length);
		this.rank = rank;
		this.calSnr();
		System.err.println("Rank:" + rank + ",Snr :" + fitSnr);
	}
}

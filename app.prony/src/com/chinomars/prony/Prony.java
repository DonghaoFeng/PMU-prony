package com.chinomars.prony;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.complex.Complex;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class Prony {

	private double T;
	private int rank = 1;
	private double[] values;
	private double minSNR;
	private double fitSNR;
	private double[] fitvalues;
	private ArrayList<PronyParameter> parameterList;
	private HashMap<Integer, Double> mapSNR = new HashMap<Integer, Double>();

	public Prony(double[] values, double t, double minSNR) {
		this.values = values;
		this.T = t;
		this.minSNR = minSNR;
	}

	public double getFitSNR() {
		return fitSNR;
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
		List<PronyParameter> list = parameterList.stream().filter(p -> p.getDamping() > -5)
				.collect(Collectors.toList());
		PronyParameter maxP = list.get(0);
		return list.stream().filter(p -> p.getAmlitude() / maxP.getAmlitude() > 0.1).collect(Collectors.toList());
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
		int values_length = values.length;
		double[] realEigenvalues = u.getRealEigenvalues();
		double[] imagEigenvalues = u.getImagEigenvalues();
		Complex complex;
		Complex pow;
		for (j = 0; j < length; j++) {	
			complex = new Complex(realEigenvalues[j], imagEigenvalues[j]);
			pow = new Complex(1, 0).divide(complex);
			for (i = 0; i < values_length; i++) {
				pow = pow.multiply(complex);
				eqn.setA(pow.getReal(), pow.getImaginary(),i, j);
			}
		}
		// y matrix
		for (i = 0; i < values_length; i++) {
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

	private void setT(double T) {
		this.T = T;
	}

	

	private double[] getFitValues(int length) {
		double[] values = new double[length];
		parameterList.forEach(p -> {
			for (int k = 0; k < length; k++) {
				values[k] += p.getAmlitude() * Math.cos(2 * Math.PI * p.getFrequency() * T * k + p.getPhase())
						* Math.exp(T * k * p.getDamping());
			}
		});
		return values;
	}

	private void calSNR() {
		double[] diff = new double[values.length];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = values[i] - fitvalues[i];
		}

		fitSNR = 20 * Math.log10(rms(values) / rms(diff));
		mapSNR.put(rank, fitSNR);
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
		while (fitSNR < minSNR && rank <= 20) {
			rank++;
			fit(rank);
		}
		if (rank > 20) {
			Entry<Integer, Double> entry = mapSNR.entrySet().stream().max(new Comparator<Entry<Integer, Double>>() {
				@Override
				public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			}).get();
			fit(entry.getKey());
			rank = entry.getKey();
		}
		
	}

	/**
	 * 
	 * @param number the number of parameter for fitting measure
	 * @return parameters list
	 */
	public void fit(int rank) {
		this.setT(T);
		Matrix aMatrix = calAMatrix(values, rank * 2);
		double[] aArray = aMatrix.getColumnPackedCopy();
		double[] a_Array = new double[aArray.length + 1];
		a_Array[0] = 1;
		for (int i = 0; i < aArray.length; i++) {
			a_Array[i + 1] = -aArray[i];
		}
		EigenvalueDecomposition u = roots(a_Array);
		parameterList = getParamerterList(u, values);
		parameterList.sort(new Comparator<PronyParameter>() {
			@Override
			public int compare(PronyParameter o1, PronyParameter o2) {
				return -Double.valueOf(o1.getAmlitude()).compareTo(o2.getAmlitude());
			}
		});

		fitvalues = getFitValues(values.length);
		this.rank = rank;
		this.calSNR();
		System.err.println("Rank:" + rank + ",SNR :" + fitSNR);
	}
}

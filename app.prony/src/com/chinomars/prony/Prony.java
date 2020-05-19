package com.chinomars.prony;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.numeric.sparse.base.ISparseEqnObject;

import com.interpss.NumericObjectFactory;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;



public class Prony {

    private static double T;

	/**
     * Calculate the D matrix
     */
    private static Matrix calDMatrix(double[] yArray, int l) {
    	int n = yArray.length;
        Matrix dMatrix = new Matrix(n-l,l);
		for (int i = 0; i < n-l; ++i) {
			for (int j = 0; j < l; ++j) {
				dMatrix.set(i, j,yArray[ l - j - 1 + i]);
			}
		}
        return dMatrix;
    }
    
    /**
     * least squares algorithm to calculate a_i
     */
	private static Matrix calAMatrix(double[] yArray, int l) {
		Matrix DMatrix = calDMatrix(yArray, l);
		int n = yArray.length;

		Matrix dMatrix = new Matrix(n-l, 1);

		for (int i = 0; i < n - l; ++i) {
			dMatrix.set(i, 0, yArray[l + i]);
		}
		return DMatrix.solve(dMatrix);
	}

    /**
     * solve the poly
     * @param aArray
     * @return
     */
    public static EigenvalueDecomposition roots(double[] aArray) {
        int len = aArray.length - 1;
		double[][] genA = new double[len][len];
		for (int i = 0; i < len; ++i) {
			genA[0][i] = -aArray[i + 1] / aArray[0];
		}

        for (int i = 1; i < len; ++i) {
            genA[i][i-1] = 1;
        }

        Matrix AMat = new Matrix(genA);
//        AMat.print(0,0);

        return AMat.eig();

    }
    
	private static ArrayList<PronyParameter> getParamerterList(EigenvalueDecomposition u, double[] values)  {
		ISparseEqnComplex eqn = NumericObjectFactory.createSparseEqnComplex(values.length);
		// u matrix
		for (int i = 0; i < values.length; i++) {

			for (int j = 0; j < u.getRealEigenvalues().length; j++) {
				Complex complex = new Complex(u.getRealEigenvalues()[j], u.getImagEigenvalues()[j]);
				eqn.setA(complex.pow(i), i, j);
			}
		}
		// y matrix
		for (int i = 0; i < values.length; i++) {
			eqn.setBi(new Complex(values[i], 0), i);
		}
		//C matrix
		try {
			eqn.solveEqn();
		} catch (IpssNumericException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getParamerterList(u, eqn);
	}
	
	private static ArrayList<PronyParameter> getParamerterList(EigenvalueDecomposition u, ISparseEqnObject<Complex, Complex> eqn) {
		ArrayList<PronyParameter> paramerterList = new ArrayList<PronyParameter>();
		for (int i = 0; i < u.getRealEigenvalues().length; i++) {
			if (i % 2 == 0) {
				PronyParameter parameter = new PronyParameter();
				paramerterList.add(parameter);
				Complex uPole = new Complex(u.getRealEigenvalues()[i], u.getImagEigenvalues()[i]);
				Complex cPole = eqn.getX(i);

				parameter.setAmlitude(2 * Math.exp(cPole.log().getReal()));
				parameter.setDamping(uPole.log().getReal() / T);
				parameter.setFrequency(uPole.log().getImaginary() / (2.0 * Math.PI * T));
				parameter.setPhase(cPole.log().getImaginary());
			}
		}
		return paramerterList;
		
	}

	
	
	private static void setT(double T) {
		Prony.T = T;
	}
	
	/**
	 * @param values   put all measure values Y(k) here
	 * @param T   sampling period
	 * @return  parameters list
	 */
	public static ArrayList<PronyParameter> getParamerterList(double[] values, double T) {
		return Prony.getParameterList(values, T, 2);
	}
	
	/**
	 * @param values put all measure values Y(k) here
	 * @param T  sampling period
	 * @param number  the number of parameter  for fitting measure
	 * @return parameters list
	 */
	public static ArrayList<PronyParameter> getParameterList(double[] values, double T, int number) {
		Prony.setT(T);
		Matrix aMatrix = Prony.calAMatrix(values, number * 2);
		double[] aArray = aMatrix.getColumnPackedCopy();
		double[] a_Array = new double[aArray.length + 1];
		a_Array[0] = 1;
		for (int i = 0; i < aArray.length; i++) {
			a_Array[i + 1] = -aArray[i];
		}
		EigenvalueDecomposition u = Prony.roots(a_Array);
		return Prony.getParamerterList(u, values);
	}
}














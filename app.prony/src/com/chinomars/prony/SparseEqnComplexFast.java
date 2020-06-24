package com.chinomars.prony;


import org.apache.commons.math3.complex.Complex;


import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcs;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsa;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsn;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcss;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_compress;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_entry;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_ipvec;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_lsolve;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_lu;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_sqr;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_usolve;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_util;

/**
 * @author Donghao
 *
 */
public class SparseEqnComplexFast {
	private static final int CSJ_ORDER = 2;
	private static final double CSJ_TOL = 1.0;
	private int length;
	private boolean factored;
	private DZcs aMatrix;
	private double[][] x;
	private double[][] y;
	private DZcss symbolTable;
	private DZcsn numericTable;
	private double[] bx;
	private double[] by;
	private int total;

	/*
	 * public SparseEqnComplexImpl() { this(0); }
	 */

	public SparseEqnComplexFast(int n) {
		this.length = n;
		this.bx = new double[n];
		this.by = new double[n];
		this.x = new double[n][n];
		this.y = new double[n][n];
	}

	/**
	 * Get the bi element.
	 * 
	 * @param i the element row number
	 * @return the bi element
	 */
	public Complex getX(final int i) {
		return new Complex(this.bx[i], this.by[i]);
	}

	public void setA(final double x, final double y, final int i, final int j) {
		if (j != i) {
			this.total++;
		}
		this.x[i][j] = x;
		this.y[i][j] = y;
	}

	/**
	 * Set bi element.
	 * 
	 * @param bi the bi element
	 * @param i  row number
	 */
	public void setBi(final double x, final double y, final int i) {
		this.bx[i] = x;
		this.by[i] = y;
	}


	public void factorization(final double tolerance) throws Exception  {
		buildAMatrix();

		this.symbolTable = DZcs_sqr.cs_sqr(CSJ_ORDER, this.aMatrix, false); /* ordering and symbolic analysis */
		if (this.symbolTable == null) {
			throw new Exception("Error in CSparseJ ordering and symbolic analysis");
		}
		this.numericTable = DZcs_lu.cs_lu(this.aMatrix, this.symbolTable, CSJ_TOL); /* numeric LU factorization */
		if (this.numericTable == null) {
			throw new Exception("Error in  CSparseJ numeric LU factorization");
		}
		this.factored = true;
	}

	/**
	 * Build the [A] matrix in CSparseJ format.
	 */
	protected void buildAMatrix() {
		// allocate initial [A] matrix memory. It can allow if necessary
		int m = this.getTotalElements();
		this.aMatrix = DZcs_util.cs_spalloc(this.length, this.length, m, true, true);
		// copy data from the Sparse eqn to the CSJ [A] matrix
		int i, j;
		for (i = 0; i < this.length; i++) {
			for (j = 0; j < this.length; j++) {
				if (x[i][j] != 0 || y[i][j] != 0) {
					DZcs_entry.cs_entry(aMatrix, i, j, x[i][j], y[i][j]);
				} else if (i == j) {
					DZcs_entry.cs_entry(aMatrix, i, j, 1, 0);
				}
			}
		}
		if (this.aMatrix.nzmax != m) {
			System.err.println("A-matrix memory dynamically increased");
		}
		if (!DZcs_util.CS_CSC(this.aMatrix)) {
			this.aMatrix = DZcs_compress.cs_compress(this.aMatrix);
		}
	}

	private int getTotalElements() {
		return this.total + this.length;
	}

	/**
	 * Solve the [A]X = B problem
	 * @throws Exception 
	 * 
	 */
	public void solveEqn() throws Exception {
		if (!this.factored) {
			this.factorization(1.0e-10);
		}
		int n = this.length;

		if (!DZcs_util.CS_CSC(aMatrix)) {
			throw new Exception("A-matrix is not compressed");
		}
		DZcsa b = new DZcsa(n);
		for (int i = 0; i < n; i++) {
			b.set(i, bx[i], by[i]);
		}

		if (this.factored && symbolTable != null && numericTable != null) {
			DZcsa x = new DZcsa(n);
			DZcs_ipvec.cs_ipvec(numericTable.pinv, b, x, n); /* x = b(p) */
			DZcs_lsolve.cs_lsolve(numericTable.L, x); /* x = L\x */
			DZcs_usolve.cs_usolve(numericTable.U, x); /* x = U\x */
			DZcs_ipvec.cs_ipvec(symbolTable.q, x, b, n); /* b(q) = x */
		} else {
			throw new Exception("The A-matrix is not LUed");
		}

		for (int i = 0; i < n; i++) {
			double[] c = b.get(i);
			this.setBi(c[0], c[1], i);
		}
	}

}

package app.preprocess;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Donghao
 *
 */
public class Preprocess {

	

	public double[] yArray;

	private double frequencyHig = 2.5;

	private double frequencyLow = 0.1;

	private ArrayList<Integer> pointIndexList;
	private ArrayList<Integer> upperPointIndexList;
	private ArrayList<Integer> lowerPointIndexList;

	public double[] upperEnvelopes;
	public double[] lowerEnvelopes;

	public double[] yOrigin;

	public double[] yResult;

	public Preprocess(double[] yOrigin) {
		super();
		this.yOrigin = yOrigin;
	}

	private void ini() {
		yArray = new double[yOrigin.length];
		for (int i = 0; i < yArray.length; i++) {
			yArray[i] = yOrigin[i];
		}
	}

	public void preprocess() {
		ini();
		int i = 0;
		while (i == 0 || checkPointDistance()) {
			i++;
			findPoint();
			if (upperPointIndexList.size() < 3 || lowerPointIndexList.size() < 3) {
				break;
			}
			interpolateEnvelopes();
			while (checkOutlier()) {
				interpolateEnvelopes();
			}
			calculateTrendEnvelopes();
//			DrawMath.add(this.yArray);
		}
		calculateResult();
	}

	private void calculateResult() {
		yResult = new double[yArray.length];
		for (int i = 0; i < yOrigin.length; i++) {
			yResult[i] = yOrigin[i] - yArray[i];
		}
	}

	private boolean checkPointDistance() {
//		boolean is2Closer = false;
//		for (int i = 0; i < pointIndexList.size() - 1; i++) {
//			int distance = pointIndexList.get(i + 1) - pointIndexList.get(i);
//			if (distance < 1 / frequencyLow) {
//				is2Closer = true;
//			}
//		}
//		return is2Closer;
		//TODO remove lower frequency components belong 0.1hz
		return false;
	}

	private void calculateTrendEnvelopes() {
		for (int i = 0; i < yArray.length; i++) {
			yArray[i] = (upperEnvelopes[i] + lowerEnvelopes[i]) / 2;
		}
	}

	private boolean checkOutlier() {
		boolean haveOutlier = false;
		for (int i = 0; i < lowerEnvelopes.length; i++) {
			if (yArray[i] - lowerEnvelopes[i] < 0) {
				if (checkMin (i)) {
					lowerPointIndexList.add(i);
					pointIndexList.add(i);
					haveOutlier = true;
				}
			}
			if (yArray[i] - upperEnvelopes[i] > 0) {
				if (checkMax(i)) {
					upperPointIndexList.add(i);
					pointIndexList.add(i);
					haveOutlier = true;
				}
			}
			
		}
		return haveOutlier;
	}

	private boolean checkMin(int i) {
		for (int j = 0; j < windowsLength; j++) {
			if (i - j >= 0) {
				if (yArray[i - j] < yArray[i]) {
					return false;
				}
			}
			if (i + j < yArray.length) {
				if (yArray[i + j] < yArray[i]) {
					return false;
				}
			}
			
		}
		return true;
	}



	private boolean checkMax(int i) {
		for (int j = 0; j < windowsLength; j++) {
			if (i - j >= 0) {
				if (yArray[i - j] > yArray[i]) {
					return false;
				}
			}
			if (i + j < yArray.length) {
				if (yArray[i + j] > yArray[i]) {
					return false;
				}
			}
		}
		return true;
	}



	private void interpolateEnvelopes() {
		double[] hx = new double[upperPointIndexList.size()];
		double[] hy = new double[upperPointIndexList.size()];
		double[] lx = new double[lowerPointIndexList.size()];
		double[] ly = new double[lowerPointIndexList.size()];
		
		double[] x0 = new double[this.yArray.length];
		for (int i = 0; i < x0.length; i++) {
			x0[i] = i;
		}
		
		Collections.sort(upperPointIndexList);
		for (int i = 0; i < hx.length; i++) {
			hx[i] = upperPointIndexList.get(i);
			hy[i] = yArray[upperPointIndexList.get(i)];
		}
		
		this.upperEnvelopes = Hermite.interpolate(hx, hy, x0);
		
		Collections.sort(lowerPointIndexList);
		for (int i = 0; i < lx.length; i++) {
			lx[i] = lowerPointIndexList.get(i);
			ly[i] = yArray[lowerPointIndexList.get(i)];
		}
		this.lowerEnvelopes = Hermite.interpolate(lx, ly,  x0);
	}

	int windowsLength = new Double(50 / frequencyHig / 2).intValue();
	private void findPoint() {
		int leftNumber;
		int rightNumber;
		boolean isMaxValue;
		boolean isMinValue;
		upperPointIndexList = new ArrayList<Integer>();
		lowerPointIndexList = new ArrayList<Integer>();
		pointIndexList = new ArrayList<Integer>();
		int allLength = windowsLength * 2;
		int j;
		for (int i = 0; i + allLength < yArray.length; i++) {
			isMaxValue = true;
			for ( j = 0; j < allLength; j++) {
				if (yArray[i + j] > yArray[i + windowsLength]) {
					isMaxValue = false;
				}
			}
			if (isMaxValue) {
				leftNumber = 0;
				for (j = 0; j < windowsLength; j++) {
					if (yArray[i + j] < yArray[i + j + 1]) {
						leftNumber++;
					}
				}

				rightNumber = 0;
				for (j = windowsLength; j < allLength; j++) {
					if (yArray[i + j] > yArray[i + j + 1]) {
						rightNumber++;
					}
				}

				if ((double) leftNumber / windowsLength > 0.8 && (double) rightNumber / windowsLength > 0.8) {

					upperPointIndexList.add(i+ windowsLength);
					pointIndexList.add(i+ windowsLength);
				}
			}

			isMinValue = true;
			for (j = 0; j < allLength; j++) {
				if (yArray[i + j] < yArray[i + windowsLength]) {
					isMinValue = false;
				}
			}
			if (isMinValue) {
				leftNumber = 0;
				for (j = 0; j < windowsLength; j++) {
					if (yArray[i + j] > yArray[i + j + 1]) {
						leftNumber++;
					}
				}

				rightNumber = 0;
				for (j = windowsLength; j < allLength; j++) {
					if (yArray[i + j] < yArray[i + j + 1]) {
						rightNumber++;
					}
				}

				if ((double) leftNumber / windowsLength > 0.8 && (double) rightNumber / windowsLength > 0.8) {
					lowerPointIndexList.add(i+ windowsLength);
					pointIndexList.add(i+ windowsLength);
				}
			}

		}
		
//		for (int i = 0; i <yArray.length; i++) {
//			if (checkMin(i)) {
//				lowerPointIndexList.add(i);
//				pointIndexList.add(i);
//				
//			}
//			if (checkMax(i)) {
//				upperPointIndexList.add(i);
//				pointIndexList.add(i);
//				
//			}
//		}

	}

	
	
}

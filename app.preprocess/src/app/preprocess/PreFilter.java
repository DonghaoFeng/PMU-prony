package app.preprocess;

import app.preprocess.test.DrawMath;

/**
 * @author Donghao
 *
 */
public class PreFilter {
	private static double[] AUDHA = { 1.0, -1.98388104166084, 0.984009917549517 };
	private static double[] AUDHB = { 0.991972739802589, -1.98394547960518, 0.991972739802589 };
	
	/**
	 *  butter(8,0.4,'low');
	 */
	private static double[] AUDLA = {1, -1.5906, 2.0838, -1.5326, 0.86944, -0.31918, 0.082090, -0.012247, 0.00086137 };
	private static double[] AUDLB = { 0.0022718, 0.018175, 0.063612, 0.12722, 0.15903, 0.12722, 0.063612, 0.018175, 0.0022718 };

	public static boolean checkOscillation(double[] signal, double amlitude) {
		int i, j;
		int length = signal.length;
		for (i = 0; i < length; i++) {
			for (j = 0; j < length; j++) {
				if (Math.abs(signal[i] - signal[j]) > amlitude) {
					return true;
				}
			}
		}
		return false;
	}

	public static double[] afterPass(double[] signal, int beginIndex) {
		double[] values = new double[signal.length - beginIndex];
		for (int i = 0; i < values.length; i++) {
			values[i] = signal[i + beginIndex];
		}
		return values;
	}

	public static int checkOscillationBegin(double[] yArray, int windowsLength) {
		int leftNumber;
		int rightNumber;
		boolean isMaxValue;
		boolean isMinValue;
		int allLength = windowsLength * 2;
		int j;
		for (int i = 0; i + allLength < yArray.length; i++) {
			isMaxValue = true;
			for (j = 0; j < allLength; j++) {
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
					return i + windowsLength;
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
					return i + windowsLength;
				}
			}

		}
		return 0;
	}

	public static double[] highPass(double[] signal) {
		return filter(AUDHB, AUDHA, signal);
	}

	public static double[] diffPass(double[] signal) {
		double sum = 0;
		int length = signal.length;
		for (int i = 0; i < length; i++) {
			sum += signal[i];
		}
		sum = sum / length;
		for (int i = 0; i < signal.length; i++) {
			signal[i] -= sum;
		}
		return signal;
	}

	public static double[] lowPass(double[] signal) {
		return filter(AUDLB, AUDLA, signal);
	}

	public static double[] filter(double[] bArr, double[] aArr, double[] xArr) {
		int lenB = bArr.length;
		int lenA = aArr.length;
		int lenX = xArr.length;
		int m = lenB - 1;
		int n = lenA - 1;
		double[] yArr = new double[lenX];
		int i,j;
		
		double yFront;
		double yBehind;
		for (i = 0; i < lenX; i++) {
			yFront = 0;
			for (j = 0; j <= m && j <= i; j++) {
				yFront = yFront + bArr[j] * xArr[i - j];
			}
			yBehind = 0;
			for (j = 1; j <= n && j <= i; j++) {
				yBehind = yBehind + aArr[j] * yArr[i - j];
			}
			yArr[i] = (yFront - yBehind) / aArr[0];
		}
		return yArr;
	}
	
	public static double[] prefilter(double[] values, double f) {
		int windowsLength = new Double(50 / f / 2).intValue();
		int beginIndex = PreFilter.checkOscillationBegin(values, windowsLength);
		
		values = PreFilter.afterPass(values, beginIndex);
//		DrawMath.add(values);
		values = PreFilter.diffPass(values);
//		DrawMath.add(values);
		values = PreFilter.lowPass(values);
//		DrawMath.add(values);
		return values;
	}
}
package app.preprocess;

import java.util.Scanner;

/**
 * @author Donghao
 *
 */
public class Hermite {

	public static double[] interpolate(double x[], double y[], double x0[]) {
		int xLength = x.length;
		int x0Length = x0.length;
		double y0[] = new double[x0Length];
		int xIndex;
		double[] d = pchipslopes(x, y);
		int i;
		int j;
		for (i = 0; i < x0Length; i++) {
			xIndex = -1;
			for (j = 0; j < xLength; j++) {
				if (x0[i] - x[j] >= 0) {
					xIndex = j;
				}	
			}
			if (xIndex < 0) {
				xIndex = 0;
			}
			if (xIndex >= xLength-1) {
				xIndex = xLength - 2;
			}
			
			y0[i] = ((1 + 2 * (x0[i] - x[xIndex]) / (x[xIndex + 1] - x[xIndex])) * y[xIndex]
					+ (x0[i] - x[xIndex]) * d[xIndex])
					* Math.pow((x0[i] - x[xIndex + 1]) / (x[xIndex] - x[xIndex + 1]), 2)
					+ ((1 + 2 * (x0[i] - x[xIndex + 1]) / (x[xIndex] - x[xIndex + 1])) * y[xIndex + 1]
							+ (x0[i] - x[xIndex + 1]) * d[xIndex + 1])
							* Math.pow((x0[i] - x[xIndex]) / (x[xIndex + 1] - x[xIndex]), 2);
		}

		return y0;
	}
	
	
	
//	 yi(i) = ((1+2*(xi(i)-x(xIndex))/(x(xIndex+1)-x(xIndex)))*y(xIndex) + (xi(i)-x(xIndex))*d(xIndex))*((xi(i)-x(xIndex+1))/(x(xIndex)-x(xIndex+1)))^2+...
//    ((1+2*(xi(i)-x(xIndex+1))/(x(xIndex)-x(xIndex+1)))*y(xIndex+1) + (xi(i)-x(xIndex+1))*d(xIndex+1))*((xi(i)-x(xIndex))/(x(xIndex+1)-x(xIndex)))^2;


	public static void main(String[] args) {
		double[] x = { 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90, 1.00 };
		double[] y = { 0.904837, 0.818731, 0.740818, 0.670320, 0.606531, 0.548812, 0.496585, 0.449329, 0.406570,
				0.367879 };
		double[] m = { -0.904837, -0.818731, -0.740818, -0.670320, -0.606531, -0.548812, -0.496585, -0.449329,
				-0.406570, -0.367879 };
		System.out.println("输入插值点个数：");
		Scanner scan = new Scanner(System.in);
		int n = scan.nextInt();
		double x0[] = new double[n];
		System.out.println("输入插值点横坐标：");
		for (int i = 0; i < n; i++) {
			x0[i] = scan.nextDouble();
		}

		double y0[] = interpolate(x, y, x0);
		System.out.println("H插值法求解得:");
		for (int i = 0; i < n; i++) {
			System.out.println(y0[i] + " ");
		}
		System.out.println();

	}
	
	private static double[] pchipslopes(double x[], double y[]) {
		double[] del = new double[x.length - 1];
		double[] h = new double[x.length - 1];

		for (int i = 0; i < del.length; i++) {
			h[i] = x[i + 1] - x[i];
			del[i] = (y[i + 1] - y[i]) / h[i];
		}
		double[] d = new double[x.length];
		for (int i = 0; i < d.length - 2; i++) {
			if (Math.signum(del[i]) * Math.signum(del[i + 1]) < 0) {
				d[i + 1] = 0;
			} else {
				d[i + 1] = 3 * del[i] * del[i + 1] * (h[i] + h[i + 1])
						/ ((2 * h[i + 1] + h[i]) * del[i + 1] + (h[i + 1] + 2 * h[i]) * del[i]);
			}
		}

		d[0] = ((2 * h[0] + h[1]) * del[0] - h[0]*del[1] )/ (h[0] + h[1]);
		int n = d.length - 1;
		d[n] = ((2 * h[n - 1] + h[n - 2]) * del[n - 1] -  h[n - 1] *del[n - 2]) / (h[n - 1] + h[n - 2]);
		
		if (Math.signum(d[0])!= Math.signum(del[0])) {
			d[0] = 0;
		} else if (Math.signum(del[0])!= Math.signum(del[1])&& Math.abs(d[0]) > 3 * Math.abs(del[0])) {
			d[0] = 3 * del[0];
		}
		
		if (Math.signum(d[n]) != Math.signum(del[n - 1])) {
			d[n] = 0;
		} else if (Math.signum(del[n-1])!= Math.signum(del[n-2]) && Math.abs(d[n]) > 3 * Math.abs(del[n-1])) {
			d[n] = 3 * del[n-1];
		}
		
		return d;

	}

}
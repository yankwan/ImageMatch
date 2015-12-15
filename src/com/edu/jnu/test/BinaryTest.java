package com.edu.jnu.test;

import java.util.ArrayList;
import java.util.List;

public class BinaryTest {

	public static void main(String[] args) {

		// int H = 16;
		// int S = 3;
		// int V = 2;
		//
		// int G = H << 4 | S << 2 | V;
		// System.out.println(G);
		//
		// System.out.println(Integer.parseInt("111111111111111111", 2));

//		int R = 94;
//		int G = 94;
//		int B = 86;
//		
//		System.out.println(Math.acos(0.5)/(2*Math.PI));
//		
//		
//		float[] result = new float[3];
//		result = RGB2HIS(R, G, B);
		
		double ratio = 5.0 / 3;
		double a = ratio - 1;
		double b = ratio - (1 - a);
		double c = b - (1 - a);
		
		System.out.println(c);

	}

	public static float[] RGB2HIS(int R, int G, int B) {

		float r = R / 255.0f;
		float g = G / 255.0f;
		float b = B / 255.0f;

		float[] HISArray = new float[3];

		float partA = 0, partB = 0;
		float thea = 0;

		partA = ((r - g) + (r - b)) / 2;
		partB = (float) Math.sqrt((r - g) * (r - g) + (r - b) * (g - b));

		thea = (partB == 0) ? 0 : (float) Math.acos(partA / partB);

		HISArray[0] = (float) ((b <= g) ? (thea/(2*Math.PI)) : (2 * Math.PI - thea)/(2*Math.PI));

		HISArray[1] = (r + g + b == 0) ? 0 : 1 - 3.0f
		      * Math.min(r, Math.min(g, b)) / (r + g + b);

		HISArray[2] = (r + g + b) / 3;

		// System.out.println("H =" + HISArray[0]*360 + " S =" + HISArray[1] +
		// " I =" + HISArray[2]);

		return HISArray;

	}

	public static int getHue(float hueDegree) {
		float percent = 1f / 10;
		int result;
		if (hueDegree == 0)
			return 0;
		if (((hueDegree - percent) / percent) % percent == 0)
			result = (int) (Math.floor((hueDegree - percent) / percent) + 1);

		result = (int) Math.ceil((hueDegree - percent) / percent);

		System.out.print("result : " + result);

		return result;

	}

}

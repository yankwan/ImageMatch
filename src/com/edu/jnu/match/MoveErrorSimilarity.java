package com.edu.jnu.match;

import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;

public class MoveErrorSimilarity implements IMatchStrategy {

	@Override
   public double similarity(List<ColorData> data1, List<ColorData> data2) {
	   // TODO Auto-generated method stub
		int minSize = (data1.size() > data2.size()) ? data2.size() : data1.size();
		
		double resQuantity = 0;
		double resColor = 0;
		for (int i = 1; i < minSize; i++) {
			resQuantity += (data1.get(i).getQuantity() - data1.get(i-1).getQuantity()) / (data2.get(i).getQuantity() - data2.get(i-1).getQuantity());
			
			resColor += getHSVFactorDiff(ColorUtil.HSVtoArray(data1.get(i).getHSV()), ColorUtil.HSVtoArray(data1.get(i-1).getHSV()))
					     / getHSVFactorDiff(ColorUtil.HSVtoArray(data2.get(i).getHSV()), ColorUtil.HSVtoArray(data2.get(i-1).getHSV()));
					      
		
		}
		
		return resQuantity * resColor;
		
   }
	
	static double HFactor = 1f / Config.H_DIMENSION * 0.6;
	static double SFactor = 1f / Config.S_DIMENSION * 0.2;
	static double IFactor = 1f / Config.I_DIMENSION * 0.2;

	private static double getHSVFactorDiff(int[] HSV1, int[] HSV2) {
		return Math.abs(HSV1[0] - HSV2[0]) * HFactor
		      + Math.abs(HSV1[1] - HSV2[1]) * SFactor
		      + Math.abs(HSV1[2] - HSV2[2]) * IFactor;
	}

}

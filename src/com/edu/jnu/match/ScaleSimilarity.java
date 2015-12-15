package com.edu.jnu.match;

import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;

public class ScaleSimilarity implements IMatchStrategy {

	@Override
	public double similarity(List<ColorData> data1, List<ColorData> data2) {
		// TODO Auto-generated method stub
		List<ColorData> maxDataList, minDataList;

		maxDataList = (data1.size() > data2.size()) ? data1 : data2;
		minDataList = (data1.size() > data2.size()) ? data2 : data1;

		double maxSize = maxDataList.size(), minSize = minDataList.size();

		double count = 1;
		int j = 0;
		ColorData maxOne, minOne;
		ColorData minPre, minNext;

		double ratio = maxSize / minSize;
		double inverseRation = 1.0 / ratio;
		double tempRatio = ratio;
		double remain = 0;
		double similarity = 0;

		if (ratio > Config.scaleRatio)
			return 1.0;
		
		if (minSize < maxSize)
			for (int i = 0; i < minDataList.size(); i++)
				minDataList.get(i).setQuantity(minDataList.get(i).getQuantity() * inverseRation);

		for (int i = 0; i < maxDataList.size(); i++) {
			maxOne = maxDataList.get(i);
			minOne = minDataList.get(j);

			if (count < tempRatio + Config.FixError) {
				double q = getSimilarity(maxOne, minOne);
				similarity += q;
				if (maxSize != minSize)
					count++;
				else {
					j++;
					if (j == minSize)
						break;
				}

			} else {
				remain = tempRatio - (count - 1);
				maxOne = maxDataList.get(i);
				minPre = minDataList.get(j);
				minNext = minDataList.get(j + 1);
				
//				double q = getSimilarity2(maxOne, minPre, minNext, remain);
//				similarity += q;
				
				double q1 = getSimilarity(maxOne, minPre);
				double q2 = getSimilarity(maxOne, minNext);
				similarity += remain * q1 + (1 - remain) * q2;
				
				

				if (j < minSize)
					j++;
				count = 1;
				tempRatio = ratio - (1 - remain);

			}
		}

		return similarity;
	}

	private static double getSimilarity(ColorData d1, ColorData d2) {

		int[] HSVArr1 = ColorUtil.HSVtoArray(d1.getHSV());
		int[] HSVArr2 = ColorUtil.HSVtoArray(d2.getHSV());
		double HSVfactor = getHSVFactor(HSVArr1, HSVArr2);

		double quantity1 = d1.getQuantity();
		double quantity2 = d2.getQuantity();
		double quaSimilarity = Math.abs(quantity1 - quantity2);

		return quaSimilarity * HSVfactor;
	}
	
	private static double getSimilarity2(ColorData d1, ColorData d2, ColorData d3, double part) {
		
		int[] HSVArr1 = ColorUtil.HSVtoArray(d1.getHSV());
		int[] HSVArr2 = ColorUtil.HSVtoArray(d2.getHSV());
		int[] HSVArr3 = ColorUtil.HSVtoArray(d3.getHSV());
		double HSVfactor1 = getHSVFactor(HSVArr1, HSVArr2) * part;
		double HSVfactor2 = getHSVFactor(HSVArr1, HSVArr3) * (1-part);
		double colorSimilarity = Math.abs(HSVfactor1 + HSVfactor2);

		double quantity1 = d1.getQuantity();
		double quantity2 = d2.getQuantity();
		double quantity3 = d3.getQuantity();
		double quaSimilarity = Math.abs(quantity1 - (quantity2*part + quantity3*(1-part)));
		

		return quaSimilarity * colorSimilarity;
	}

	static double HFactor = 1f / Config.H_DIMENSION * 0.6;
	static double SFactor = 1f / Config.S_DIMENSION * 0.2;
	static double IFactor = 1f / Config.I_DIMENSION * 0.2;

	private static double getHSVFactor(int[] HSV1, int[] HSV2) {
		return Math.abs(HSV1[0] - HSV2[0]) * HFactor
		      + Math.abs(HSV1[1] - HSV2[1]) * SFactor
		      + Math.abs(HSV1[2] - HSV2[2]) * IFactor;
	}
}

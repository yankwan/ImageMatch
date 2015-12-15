package com.edu.jnu.match;

import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;

public class TanimotoMetric implements IMatchStrategy{

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
		
		double A = 0, B = 0, C = 0;
		double AFactor = 0, BFactor = 0, CFactor = 0;

		if (ratio > Config.scaleRatio)
			return 0;
		
		if (minSize < maxSize)
			for (int i = 0; i < minDataList.size(); i++)
				minDataList.get(i).setQuantity(minDataList.get(i).getQuantity() * inverseRation);

		for (int i = 0; i < maxDataList.size(); i++) {
			maxOne = maxDataList.get(i);
			minOne = minDataList.get(j);

			if (count < tempRatio + Config.FixError) {
				double[] q = getTanimotoDSimilarity(maxOne, minOne);
				A += q[0]; B += q[1]; C += q[2];
				AFactor += q[3]; BFactor += q[4]; CFactor += q[5];
				
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
				
				double[] q1 = getTanimotoDSimilarity(maxOne, minPre);
				double[] q2 = getTanimotoDSimilarity(maxOne, minNext);
				
				A += remain * q1[0] + (1-remain) * q2[0];
				B += remain * q1[1] + (1-remain) * q2[1];
				C += remain * q1[2] + (1-remain) * q2[2];
				
				AFactor += remain * q1[3] + (1-remain) * q2[3];
				BFactor += remain * q1[4] + (1-remain) * q2[4];
				CFactor += remain * q1[5] + (1-remain) * q2[5];

				if (j < minSize)
					j++;
				count = 1;
				tempRatio = ratio - (1 - remain);

			}
		}

		similarity = A / (B + C - A) * AFactor / (BFactor + CFactor - AFactor);
		
		return similarity;
	}
	
	/**
	 * 
	 * @param d1
	 * @param d2
	 * @return res[0-2]:分布量统计部分 res[3-5]:颜色统计部分
	 */
	private static double[] getTanimotoDSimilarity (ColorData d1, ColorData d2) {

		int[] HSVArr1 = ColorUtil.HSVtoArray(d1.getHSV());
		int[] HSVArr2 = ColorUtil.HSVtoArray(d2.getHSV());
		double HSVfactor = getHSVFactor(HSVArr1, HSVArr2);
		double HSVSelfFactor1 = getHSVFactor(HSVArr1, HSVArr1);
		double HSVSelfFactor2 = getHSVFactor(HSVArr2, HSVArr2);

		double quantity1 = d1.getQuantity();
		double quantity2 = d2.getQuantity();
		double quaSimilarity = quantity1 * quantity2;

		double[] res = new double[6];
		res[0] = quaSimilarity;
		res[1] = quantity1 * quantity1;
		res[2] = quantity2 * quantity2;
		
		res[3] = HSVfactor;
		res[4] = HSVSelfFactor1;
		res[5] = HSVSelfFactor2;

		return res;

	}
	
	static double HFactor = 1f / Config.H_DIMENSION * 0.6;
	static double SFactor = 1f / Config.S_DIMENSION * 0.2;
	static double IFactor = 1f / Config.I_DIMENSION * 0.2;

	private static double getHSVFactor(int[] HSV1, int[] HSV2) {
		return HSV1[0] * HSV2[0] * HFactor
		      + HSV1[1] * HSV2[1] * SFactor
		      + HSV1[2] * HSV2[2] * IFactor;
	}

}

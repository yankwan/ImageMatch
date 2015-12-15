package com.edu.jnu.test;

import java.util.ArrayList;
import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;

public class SimilarityTest {
	public static void main(String[] args) {
		
		List<ColorData> minData = new ArrayList<>();
		minData.add(new ColorData(257094, 0.4));
		minData.add(new ColorData(257350, 0.3));
		minData.add(new ColorData(256070, 0.2));

		List<ColorData> maxData = new ArrayList<>();
		maxData.add(new ColorData(256326, 0.3));
		maxData.add(new ColorData(255302, 0.25));
		maxData.add(new ColorData(257606, 0.15));
		maxData.add(new ColorData(256341, 0.12));
		maxData.add(new ColorData(255287, 0.08));
		
//		maxData.add(new ColorData(257094, 0.4));
//		maxData.add(new ColorData(257350, 0.3));
//		maxData.add(new ColorData(256070, 0.2));
		
		double q = similarity(minData, maxData);
		
		System.out.println("distance difference : " + q);

		
	}
	
	// 浮点数相等的误差范围
	public static double FixError = 0.0000001;  
	// 拉伸的限制值
	public static double scaleRatio = 1.0;
	
	/**
	 * 获取两个图像相似度
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static double similarity (List<ColorData> data1, List<ColorData> data2) {
	
		List<ColorData> maxDataList, minDataList;
		
		maxDataList = (data1.size() > data2.size()) ? data1 : data2;
		minDataList = (data1.size() > data2.size()) ? data2 : data1;
		
		double maxSize = maxDataList.size(), minSize = minDataList.size();
		 
		double count = 1;
		int j = 0;
		ColorData maxOne, minOne;
		ColorData minPre, minNext;
		
		double ratio = maxSize * 1.0 / minSize;    
		double inverseRation = 1.0 / ratio;   
		double tempRatio = ratio;
		double remain = 0;
		double similarity = 0;
		
		for (int i = 0; i < maxDataList.size(); i++) {
			maxOne = maxDataList.get(i);
			minOne = minDataList.get(j);
			
			if (ratio > scaleRatio)
				return 1;
			
			if (minSize < maxSize && ratio < scaleRatio) {
				minOne.setQuantity(minOne.getQuantity() * inverseRation);
			}

			if (count < tempRatio + FixError) {
				double q = getSimilarity(maxOne, minOne);
				System.out.println(i + ". " + minOne.getHSV() + " : " + minOne.getQuantity());
				similarity += q;
				if (maxSize != minSize && ratio < scaleRatio)
					count++;
				else {
					j++;
					if (j == minSize) break;
				}
				
			} else {
				remain = tempRatio - (count - 1);
				maxOne = maxDataList.get(i);
				minPre = minDataList.get(j);
				minNext = minDataList.get(j+1);
				
				minPre.setQuantity(minPre.getQuantity() * inverseRation);
				minNext.setQuantity(minNext.getQuantity() * inverseRation);
				
				double q1 = getSimilarity(maxOne, minPre);
				double q2 = getSimilarity(maxOne, minNext);
				
				System.out.println(i + ". " + minPre.getHSV() + " : " + minPre.getQuantity() + ", " + minNext.getHSV() + " : " + minPre.getQuantity());
				
				similarity += remain*q1 + (1-remain)*q2;
				
				if (j < minSize) j++;
				count = 1;
				tempRatio = ratio - (1 - remain);
				
			}
		}		
		
		return similarity / maxSize;
	}
	
	private static double getSimilarity (ColorData d1, ColorData d2) {
		
		int[] HSVArr1 = ColorUtil.HSVtoArray(d1.getHSV());
		int[] HSVArr2 = ColorUtil.HSVtoArray(d2.getHSV());
		double HSVfactor = getHSVFactor(HSVArr1, HSVArr2);
		
		double quantity1 = d1.getQuantity();
		double quantity2 = d2.getQuantity();
		double quaSimilarity = Math.abs(quantity1 - quantity2);
		
		return quaSimilarity * HSVfactor;
	}
	
	

	
	static double HFactor = 1f / Config.H_DIMENSION * 0.6;
	static double SFactor = 1f / Config.S_DIMENSION * 0.2;
	static double IFactor = 1f / Config.I_DIMENSION * 0.2;
	
	private static double getHSVFactor(int[] HSV1, int[] HSV2) {
		return Math.abs(HSV1[0] - HSV2[0])*HFactor + Math.abs(HSV1[1] - HSV2[1])*SFactor + Math.abs(HSV1[2] - HSV2[2])*IFactor;
	}
}

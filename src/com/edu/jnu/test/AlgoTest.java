package com.edu.jnu.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;

public class AlgoTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// float[] query = {20.0f, 15.0f, 10.0f};
		// float[] target = {8.0f, 7.5f, 6.3f, 5.7f, 5.0f, 4.5f, 4.1f, 3.9f};
		// float[] result = AlgoTest.scaleHistogram(query, target);
		//
		// float sum = 0;
		// for (int i = 0; i < result.length; i++) {
		// sum += result[i];
		// System.out.println(result[i]);
		// }
		//
		// System.out.println("Sum: " + sum);
		
		
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
		
		double q = similarity(minData, maxData);
		
		System.out.println("similarity: " + q);

		
	}
	
	public static double FixError = 0.0000001;
	public static double scaleRatio = 2.0;
	
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
			
			if (minSize < maxSize && maxSize / minSize < scaleRatio)
				minOne.setQuantity(minOne.getQuantity() * inverseRation);

			if (count < tempRatio + FixError) {
				double q = getSimilarity(maxOne, minOne);
				System.out.println(minOne.getHSV() + " : " + minOne.getQuantity());
				similarity += q;
				if (maxSize != minSize && maxSize / minSize < scaleRatio)
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
				
				System.out.println(minPre.getHSV() + " : " + minPre.getQuantity() + ", " + minNext.getHSV() + " : " + minPre.getQuantity());
				
				similarity += remain*q1 + (1-remain)*q2;
				
				if (j < minSize) j++;
				count = 1;
				tempRatio = ratio - (1 - remain);
				
			}
		}		
		return similarity/maxDataList.size();
	}
	
	private static double getSimilarity (ColorData d1, ColorData d2) {
		
		int[] HSVArr1 = ColorUtil.HSVtoArray(d1.getHSV());
		int[] HSVArr2 = ColorUtil.HSVtoArray(d2.getHSV());
		double HSVfactor = 1 - getHSVFactor(HSVArr1, HSVArr2);
		
		double quantity1 = d1.getQuantity();
		double quantity2 = d2.getQuantity();
		double quaSimilarity = 1 - Math.abs(quantity1 - quantity2);
		
		return quaSimilarity * HSVfactor;
	}

	
	static double HFactor = 1f / Config.H_DIMENSION;
	static double SFactor = 1f / Config.S_DIMENSION;
	static double IFactor = 1f / Config.I_DIMENSION;
	
	private static double getHSVFactor(int[] HSV1, int[] HSV2) {
		
		double H1 = HSV1[0] * HFactor, S1 = HSV1[1] * SFactor, V1 = HSV1[2] * IFactor;

		double H2 = HSV2[0] * HFactor, S2 = HSV2[1] * SFactor, V2 = HSV2[2] * IFactor;

		return Math.abs(H1 - H2)*0.6f + Math.abs(S1 - S2)*0.2f + Math.abs(V1 - V2)*0.2f;
	}
	
	
	
	
	/**
	 * 针对于List结构的拉伸
	 * @param query
	 * @param target
	 * @return
	 */
	
	public static List<Map<String, Double>> scaleHistogram2(List<Map<String, Double>> query,
	      List<Map<String, Double>> target) {

		List<Map<String, Double>> minArr;
		int maxSize;
		int minSize;

		if (query.size() > target.size()) {
			minArr = target;
			maxSize = query.size();
			minSize = target.size();
		} else {
			minArr = query;
			maxSize = target.size();
			minSize = query.size();
		}

		int temp = minSize;
		int remain = 0;
		int remainder = minSize;
		int count = maxSize;

		int k = 0;

		float total = maxSize / (minSize * 1f);
		List<Map<String, Double>> newList = new ArrayList<>();

		while (newList.size() < maxSize) {

			// 不能整取部分
			if (remainder < minSize) {
				k++; // 分前后两个k取
				Map<String, Double> preMap = minArr.get(k - 1);
				Map<String, Double> tempMap = minArr.get(k);
				
				Map<String, Double> _map = new LinkedHashMap<>();

				String _key;
				double _value;

				for (Map.Entry<String, Double> entry : preMap.entrySet()) {
					_key = entry.getKey();
					_value = entry.getValue();

					_value = (remainder / (minSize * 1f)) * _value / total;

					_map.put(_key, _value);
				}

				for (Map.Entry<String, Double> entry : tempMap.entrySet()) {
					_key = entry.getKey();
					_value = entry.getValue();

					_value = (minSize - remainder) / (minSize * 1f) * _value / total;

					_map.put(_key, _value);
				}
				
				newList.add(_map);
				
				remain = minSize - remainder;
				remainder = maxSize - remain;
				temp = minSize;
				count = remainder;
				continue;
			}

			// 能够整取部分
			while (temp <= count) {
				Map<String, Double> tempMap = minArr.get(k);
				Map<String, Double> _map = new LinkedHashMap<>();
				String _key;
				double _value;

				for (Map.Entry<String, Double> entry : tempMap.entrySet()) {
					_key = entry.getKey();
					_value = entry.getValue() / total;
					_map.put(_key, _value);
				}
				
				newList.add(_map);

				remainder = maxSize - remain - temp;
				temp += temp;
			}
		}

		return newList;
	}

	
	/**
	 * 对于简单数组结构的拉伸
	 * @param query
	 * @param target
	 * @return
	 */
	public static float[] scaleHistogram(float[] query, float[] target) {

		float[] minArr;
		int maxSize;
		int minSize;

		if (query.length > target.length) {
			minArr = target;
			maxSize = query.length;
			minSize = target.length;
		} else {
			minArr = query;
			maxSize = target.length;
			minSize = query.length;
		}

		int temp = minSize;
		int remain = 0;
		int remainder = minSize;
		int count = maxSize;

		int i = 0;
		int k = 0;

		float total = maxSize / (minSize * 1f);
		float[] newArr = new float[maxSize];

		while (i < maxSize) {

			if (remainder < minSize) {
				k++;
				newArr[i++] = ((remainder / (minSize * 1f)) * minArr[k - 1] + ((minSize - remainder) / (minSize * 1f))
				      * minArr[k])
				      / total;
				remain = minSize - remainder;
				remainder = maxSize - remain;
				temp = minSize;
				count = remainder;
				continue;
			}

			while (temp <= count) {
				newArr[i++] = minArr[k] / total;
				remainder = maxSize - remain - temp;
				temp += temp;
			}
		}

		return newArr;
	}
}

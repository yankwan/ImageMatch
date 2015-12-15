package com.edu.jnu.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import com.edu.jnu.util.ColorUtil;

public class ColorTest {

	private static float H_DIMENSION = 1024.0f;
	private static float S_DIMENSION = 10.0f;
	private static float I_DIMENSION = 10.0f;
	

	public static void main(String[] args) throws IOException {
		
//		String path = "ukbench/full/ukbench00667.jpg";
//		String path2 = "ukbench/full/ukbench00665.jpg";
		
		String path = "testimage/1.jpg";
		String path2 = "testimage/2.jpg";
		
		
		File imageFile = new File(path);
		File imageFile2 = new File(path2);
		
		BufferedImage bf = ImageIO.read(imageFile);
		BufferedImage bf2 = ImageIO.read(imageFile2);
		
		double[] hist = ColorUtil.getImageHSV(bf);
		double[] hist2 = ColorUtil.getImageHSV(bf2);
		
		long startTime = System.currentTimeMillis();
		
		Map<String, Double> histMap = new HashMap<String, Double>();
		Map<String, Double> histMap2 = new HashMap<String, Double>();
		
		
		for (int i = 0; i < hist.length; i++) {
			histMap.put(Integer.toString(i), hist[i]);
			histMap2.put(Integer.toString(i), hist2[i]);
		}
		
		
		
		histMap = sortByValue(histMap);
		histMap2 = sortByValue(histMap2);
		
		histMap = getTopNMapData(128, histMap);
		histMap2 = getTopNMapData(128, histMap2);
		
		double total1 = getTotalCount(histMap);
		double total2 = getTotalCount(histMap2);
		
		stdTopMapData(histMap, total1);
		stdTopMapData(histMap2, total2);
		
		
		long sTime = System.currentTimeMillis();
		double  result  = similarity(histMap, histMap2);
		long eTime = System.currentTimeMillis();
		
		System.out.println(result);
		System.out.println("cost time: " + (eTime - sTime));
		
		
//		JSONObject jsonObject = JSONObject.fromObject(histMap);
//		System.out.println("result before: " + jsonObject);
//		Map<String, Double> result = new LinkedHashMap<String, Double>();
//		
//		result = jsonToMap(jsonObject);
//		JSONObject jsonObject2 = JSONObject.fromObject(result);
//		System.out.println("result: " + jsonObject2);
		
		
//		int count = topNCompare(20, 40,  histMap, histMap2);
//		
//		double diff = euclideanMatch(128, histMap, histMap2);
//		long endTime = System.currentTimeMillis();
//		System.out.println("match: " + count);
//		System.out.println("diff:" + diff);
//		System.out.println("cost time: " + (endTime - startTime));
		
//		int count = 0;
//		
//		for(Map.Entry<String, Double> entry : histMap.entrySet()) {
////			if (entry.getValue() > 300) {
//				count++;
//				System.out.println(entry.getKey() + " : " + entry.getValue() + "  " + count);
////			}
//		}
		
//		System.out.println("*******" + count);
//		
//		for(Map.Entry<Integer, Double> entry : histMap2.entrySet()) {
//			if (entry.getValue() > 1000)
//				System.out.println(entry.getKey() + " : " + entry.getValue());
//		}
		
	}
	
	
	public static float similarity (Map<String, Double> source, Map<String, Double> target) {
		
		Iterator<Map.Entry<String, Double>> it1 = source.entrySet().iterator();
		Iterator<Map.Entry<String, Double>> it2 = target.entrySet().iterator();
		
		double count = 0;
		
		while (it1.hasNext() && it2.hasNext()) {
			
			Entry<String, Double> entry1 = it1.next();
			Entry<String, Double> entry2 = it2.next();
			
			int HSV1 = Integer.parseInt(entry1.getKey());
			int HSV2 = Integer.parseInt(entry2.getKey());
			
			double count1 = entry1.getValue();
			double count2 = entry2.getValue();
			
			double temp = Math.abs(count1 - count2);
			
//			double temp = Math.sqrt(count1 * count2);
			
			int[] HSVArr1 = ColorUtil.HSVtoArray(HSV1);
			int[] HSVArr2 = ColorUtil.HSVtoArray(HSV2);
			double HSVfactor = getHSVFactor(HSVArr1, HSVArr2);
			
			temp = temp * HSVfactor;
			
			count += temp;

		}
		
		return (float) count;

	}
	
	public static double getHSVFactor (int[] HSV1, int[] HSV2) {
		
		double H1 = HSV1[0]/H_DIMENSION, S1 = HSV1[1]/S_DIMENSION, V1 = HSV1[2]/I_DIMENSION;
		
		double H2 = HSV2[0]/H_DIMENSION, S2 = HSV2[1]/S_DIMENSION, V2 = HSV2[2]/I_DIMENSION;
		
		return Math.abs(H1 - H2) + Math.abs(S1 - S2) + Math.abs(V1 - V2);
		
//		double diff = Math.sqrt((H1-H2)*(H1-H2) + (S1-S2)*(S1-S2) + (V1-V2)*(V1-V2));
//		double mod1 = Math.sqrt(H1*H1 + S1*S1 + V1*V1);
//		double mod2 = Math.sqrt(H2*H2 + S2*S2 + V2*V2);
//		
//		if (mod1 * mod2 == 0)
//			return 0;
//		
//		return 1-(diff/(mod1 * mod2));
	}
	
	public static double getTotalCount (Map<String, Double> map) {
		
		double count = 0;
		
		for (Map.Entry<String, Double> m : map.entrySet()) {
			count += m.getValue();
		}
		
		return count;
	}
	
	public static Map<String, Double> sortByValue (Map<String, Double> map) {
		ArrayList<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			@Override
         public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
	         // TODO Auto-generated method stub
				if (o2.getValue() - o1.getValue() > 0) return 1;
				if (o2.getValue() - o1.getValue() < 0) return -1;
				else return 0;
         }
			
		});
		
		Map<String, Double> result = new LinkedHashMap<String, Double>();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			result.put((String)entry.getKey(), (Double)entry.getValue());
		}
		
		return result;

	}
	
	
	public static int topNCompare(int N, int deep, Map<String, Double> source, Map<String, Double> target) {
		int flag = 0; 
		int countOut = 0;
		for (Map.Entry<String, Double> sourceEntry : source.entrySet()) {
			countOut++;
			if (countOut > N)
				break;
			int count = 0;
			for (Map.Entry<String, Double> targetEntry : target.entrySet()) {
				// 统计内层比较数目，比较前N个
				System.out.println(sourceEntry.getKey() + " " + targetEntry.getKey() );
				int sourceKey = Integer.parseInt(sourceEntry.getKey());
				int targetKey = Integer.parseInt(targetEntry.getKey());
				if (sourceKey == targetKey) {
					// 标记TopN中有匹配到的数目
					flag++;
					break;
				} else {
					count++;
				}
				// 比较超过N个，退出内循环
				if (count > deep) {
					
					break;
				}
			}
			
		}
		
		return flag;
	}
	
	
	public static double euclideanMatch (int N, Map<String, Double> source, Map<String, Double> target) {
		
		double diff = 0;
		int countOuter = 0; 
		for (Map.Entry<String, Double> sourceEntry : source.entrySet()) {
			int countInner = 0;
			for (Map.Entry<String, Double> targetEntry : target.entrySet()) {
				
				int sourceKey = Integer.parseInt(sourceEntry.getKey());
				int targetKey = Integer.parseInt(targetEntry.getKey());
				if (sourceKey == targetKey) {
					diff += Math.pow((sourceEntry.getValue() - targetEntry.getValue()), 2);
					break;
				}
				
				countInner++;
				
				if (countInner > N)
					diff += Math.pow(sourceEntry.getValue(), 2);			
			}
			
			countOuter ++;
			if (countOuter > N)
				break;	
		}
		
		return Math.sqrt(diff);
		
	}
	
	
	public static Map<String, Double> getTopNMapData(int N, Map<String, Double> map) {
		
		Map<String, Double> result = new LinkedHashMap<String, Double>();
		int count = 0;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			if (count >= N) break;
			result.put(entry.getKey(), entry.getValue());
			count++;
		}
		
		return result;
	}
	
	public static void stdTopMapData(Map<String, Double> map, double totalCount) {

		for (Map.Entry<String, Double> entry : map.entrySet()) {
			entry.setValue(entry.getValue()/totalCount);
		}

	}
	
	
	
	public static Map<String, Double> jsonToMap(Object data) {
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		JSONObject jsonObject = JSONObject.fromObject(data);
		Iterator it = jsonObject.keys();
		while(it.hasNext()) {
			 String key = String.valueOf(it.next());  
          Double value = (Double) jsonObject.get(key);  
          map.put(key, value); 
		}
		
		return map;
	}
	
}

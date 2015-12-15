package com.edu.jnu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.edu.jnu.strategy.impl.ColorData;

public class CompareUtil {

	/**
	 * josn格式转为Map格式
	 * 
	 * @param data
	 * @return
	 */
	public static Map<String, Double> jsonToMap(Object data) {
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		JSONObject jsonObject = JSONObject.fromObject(data);
		Iterator it = jsonObject.keys();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			// System.out.println("key: " + key);
			Object value = jsonObject.get(key);
			// System.out.println("value: " + value);
			if (value.equals(0))
				value = 0d;
			map.put(key, (Double) value);
		}

		return map;
	}
	
	public static List<ColorData> jsonToList (Object data) {
		List<ColorData> list = new ArrayList<ColorData>();
		JSONArray mJsonArray = JSONArray.fromObject(data);
		ColorData color;
		for (int i = 0; i < mJsonArray.size(); i++) {
			color = new ColorData(
					((JSONObject)mJsonArray.get(i)).getInt("HSV"), 
					((JSONObject)mJsonArray.get(i)).getDouble("quantity"));
			list.add(color);
		}
		
		return list;
	}
	
	

	/**
	 * 按value值进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, Double> sortByValue(Map<String, Double> map) {
		ArrayList<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
		      map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				// TODO Auto-generated method stub
				if (o2.getValue() - o1.getValue() > 0)
					return 1;
				if (o2.getValue() - o1.getValue() < 0)
					return -1;
				else
					return 0;
			}

		});

		Map<String, Double> result = new LinkedHashMap<String, Double>();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			result.put((String) entry.getKey(), (Double) entry.getValue());
		}

		return result;

	}
	

	/**
	 * 获取指定百分比的排序数据
	 * 
	 * @param map
	 * @param percent
	 * @return
	 */
	public static Map<String, Double> getSamePercentage(Map<String, Double> map,
	      float percent) {

		float sum = 0;
		Map<String, Double> newMap = new LinkedHashMap<>();
		for (Map.Entry<String, Double> resMap : map.entrySet()) {
			if (sum >= percent)
				break;
			newMap.put(resMap.getKey(), resMap.getValue());
			sum += resMap.getValue();

		}

		return newMap;
	}
	
	public static List<ColorData> getSamePercentage(List<ColorData> list,
	      float percent) {

		float sum = 0;
		List<ColorData> newList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (sum >= percent)
				break;
			ColorData data = list.get(i);
			newList.add(data);
			sum += data.getQuantity();
		}

		list = null;
		
		return newList;
	}

	/**
	 * 对直方图进行拉伸，返回拉伸后的直方图数据
	 * @param query
	 * @param target
	 * @return
	 */
	public static List<Map<String, Double>> scaleHistogram (List<Map<String, Double>> minList,
	      int max, int min) {

		List<Map<String, Double>> minArr = minList;
		int maxSize = max;
		int minSize = min;	

		int temp = minSize;
		int remain = 0;
		int remainder = minSize;
		int count = maxSize;

		int k = 0;

		float total = (float) maxSize / minSize;
		List<Map<String, Double>> newList = new ArrayList<>();

		while (newList.size() < maxSize) {

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

					_map.put(_key + "#"  + remainder + "#" + minSize, _value);
				}

				for (Map.Entry<String, Double> entry : tempMap.entrySet()) {
					_key = entry.getKey();
					_value = entry.getValue();

					_value = (minSize - remainder) / (minSize * 1f) * _value / total;

					_map.put(_key + "#" + (minSize - remainder) + "#" + minSize, _value);
				}
				
				newList.add(_map);
				
				remain = minSize - remainder;
				remainder = maxSize - remain;
				temp = minSize;
				count = remainder;
				continue;
			}

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
				temp += minSize;
			}
		}

		return newList;
	}

	/**
	 * 以deep的深度搜寻targetHist前N个HIV是否有匹配 一般N取20,deep取N*2
	 * 
	 * @param N
	 * @param deep
	 * @param source
	 * @param target
	 * @return
	 */
	public static int topNCompare(int N, int deep, Map<String, Double> source,
	      Map<String, Double> target) {
		int flag = 0;
		int countOut = 0;
		for (Map.Entry<String, Double> sourceEntry : source.entrySet()) {
			countOut++;
			if (countOut > N)
				break;
			int count = 0;
			for (Map.Entry<String, Double> targetEntry : target.entrySet()) {
				// 统计内层比较数目，比较前N个
				int sourceKey = Integer.parseInt(sourceEntry.getKey());
				int targetKey = Integer.parseInt(targetEntry.getKey());
				if (sourceKey == targetKey) {
					// 标记TopN中有匹配到的数目
					flag++;
					break;
				} else {
					count++;
				}
				// 比较超过deep个，退出内循环
				if (count > deep) {

					break;
				}
			}

		}

		return flag;
	}

	/**
	 * 计算两个点对的距离，N为进行匹配的个数
	 * 
	 * @param N
	 * @param source
	 * @param target
	 * @return
	 */
	public static double euclideanMatch(int N, Map<String, Double> source,
	      Map<String, Double> target) {

		double diff = 0;
		int countOuter = 0;
		for (Map.Entry<String, Double> sourceEntry : source.entrySet()) {
			int countInner = 0;
			for (Map.Entry<String, Double> targetEntry : target.entrySet()) {

				int sourceKey = Integer.parseInt(sourceEntry.getKey());
				int targetKey = Integer.parseInt(targetEntry.getKey());
				if (sourceKey == targetKey) {
					diff += Math.pow(
					      (sourceEntry.getValue() - targetEntry.getValue()), 2);
					break;
				}

				countInner++;

				if (countInner >= N)
					diff += Math.pow(sourceEntry.getValue(), 2);
			}

			// countOuter++;
			// if (countOuter >= N)
			// break;
		}

		return Math.sqrt(diff);

	}

	public static Map<String, Double> getTopNMapData(int N,
	      Map<String, Double> map) {
		Map<String, Double> result = new LinkedHashMap<String, Double>();
		int count = 0;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			if (count >= N)
				break;
			result.put(entry.getKey(), entry.getValue());
			count++;
		}

		return result;
	}

}

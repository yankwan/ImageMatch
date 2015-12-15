package com.edu.jnu.strategy.impl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.CompareUtil;
import com.edu.jnu.util.DBHelper;
import com.edu.jnu.util.LBP;

public class HSVWeightStrategy {

	private static final int TOP_N = 128;

	private static final int COMPARE_N = 20;
	private static final int COMPARE_DEEP = 2 * COMPARE_N;

	private static final double SIMILARITY_DISTANCE = 15000;

	public HSVWeightStrategy() {
		// TODO Auto-generated constructor stub
	}

	public List<String> calcSimilarity(String imgPath) {
		// TODO Auto-generated method stub
		FileInputStream imageFile;
		// 记录被选中图片的图片存储路径
		String imageUrl = null;
		double score = 0;
		double similarity = 0;
		List<String> matchUrls = new ArrayList<String>();

		try {

			imageFile = new FileInputStream(imgPath);
			BufferedImage bufferImage;

			bufferImage = ImageIO.read(imageFile);

			// 获取垂直区域划分的HIV数据
			List sourceVerticalList = ColorUtil.getImageVerticalHSV(bufferImage);

			List<Map<String, Double>> sourceHistList = new ArrayList<Map<String, Double>>();

			for (int i = 0; i < sourceVerticalList.size(); i++) {
				double[] hist = (double[]) sourceVerticalList.get(i);
				Map<String, Double> sourceHistMapTemp = new HashMap<String, Double>();
				for (int j = 0; j < hist.length; j++) {
					sourceHistMapTemp.put(Integer.toString(j), hist[j]);
				}
				sourceHistMapTemp = CompareUtil.sortByValue(sourceHistMapTemp);
				sourceHistMapTemp = CompareUtil.getTopNMapData(128,
				      sourceHistMapTemp);

				sourceHistList.add(sourceHistMapTemp);
			}

			Map<String, Double> sourceHistMap1 = sourceHistList.get(0);
			Map<String, Double> sourceHistMap2 = sourceHistList.get(1);
			Map<String, Double> sourceHistMap3 = sourceHistList.get(2);

			// 从DB获取相应区域划分数据
			List<Object> list = DBHelper.fetchALLCloth();

			Map<Double, String> resultMap = new HashMap<Double, String>();

			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) list.get(i);

				Object candidatePath = map.get("path");
				Object candidateDataV_1 = map.get("histogramV_1");
				Object candidateDataV_2 = map.get("histogramV_2");
				Object candidateDataV_3 = map.get("histogramV_3");

				Map<String, Double> candidateHistMap1 = new LinkedHashMap<String, Double>();
				candidateHistMap1 = CompareUtil.jsonToMap(candidateDataV_1);

				Map<String, Double> candidateHistMap2 = new LinkedHashMap<String, Double>();
				candidateHistMap2 = CompareUtil.jsonToMap(candidateDataV_2);

				Map<String, Double> candidateHistMap3 = new LinkedHashMap<String, Double>();
				candidateHistMap3 = CompareUtil.jsonToMap(candidateDataV_3);

				double result;
				// 根据划分区域进行比较
				if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
				      sourceHistMap1, candidateHistMap1) < (COMPARE_N / 2)) {
					// 先对两者的区域1进行比较，若匹配较低，区域1与区域3进行比较
					if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
					      sourceHistMap1, candidateHistMap3) < (COMPARE_N / 2))
						continue;

					double distance1_3 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap1, candidateHistMap3);
					// 计算区域1 区域3相似性 不相似则continue
					if (distance1_3 > SIMILARITY_DISTANCE)
						continue;

					// 匹配区域2
					if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
					      sourceHistMap2, candidateHistMap2) < (COMPARE_N / 2))
						continue;

					// 计算区域2相似性
					double distance2_2 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap2, candidateHistMap2);
					if (distance2_2 > SIMILARITY_DISTANCE)
						continue;

					// 匹配区域3与区域1
					if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
					      sourceHistMap3, candidateHistMap1) < (COMPARE_N / 2))
						continue;

					// 计算区域3与区域1
					double distance3_1 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap3, candidateHistMap1);
					if (distance3_1 > SIMILARITY_DISTANCE)
						continue;

					result = (distance1_3 + distance2_2 + distance3_1) / 3;

				} else {
					// 若区域1匹配

					// 若区域1不相似 执行下一个
					double distance1_1 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap1, candidateHistMap1);
					if (distance1_1 > SIMILARITY_DISTANCE)
						continue;

					// 匹配区域2与区域2
					if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
					      sourceHistMap2, candidateHistMap2) < (COMPARE_N / 2))
						continue;

					// 计算区域2相似性
					double distance2_2 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap2, candidateHistMap2);
					if (distance2_2 > SIMILARITY_DISTANCE)
						continue;

					// 匹配区域3与区域3
					if (CompareUtil.topNCompare(COMPARE_N, COMPARE_DEEP,
					      sourceHistMap3, candidateHistMap3) < (COMPARE_N / 2))
						continue;

					// 计算区域3相似性
					double distance3_3 = CompareUtil.euclideanMatch(TOP_N,
					      sourceHistMap3, candidateHistMap3);
					if (distance3_3 > SIMILARITY_DISTANCE)
						continue;
					
					result = distance1_1 + distance2_2 + distance3_3;

				}

				// 每条匹配结果加入到resultMap中，用于排序选出距离最短的若干项
				resultMap.put(result, (String) candidatePath);

			}

			if (resultMap.size() == 0)
				return null;

			List<Map.Entry<Double, String>> infoIds = new ArrayList<Map.Entry<Double, String>>(
			      resultMap.entrySet());

			Collections.sort(infoIds, new Comparator<Map.Entry<Double, String>>() {
				public int compare(Map.Entry<Double, String> o1,
				      Map.Entry<Double, String> o2) {

					if ((o1.getKey() - o2.getKey()) > 0)
						return 1;
					else if (o1.getKey() - o2.getKey() == 0)
						return 0;
					else
						return -1;
				}
			});

			int length = (infoIds.size() < 4) ? infoIds.size() : 4;
			for (int j = 0; j < length; j++) {
				matchUrls.add(infoIds.get(j).getValue());
				System.out.println((float)(double)infoIds.get(j).getKey() + " : "
				      + infoIds.get(j).getValue());
			}
			System.out.println("-------------------------------");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return matchUrls;
	}

}

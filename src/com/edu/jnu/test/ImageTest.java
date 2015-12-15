package com.edu.jnu.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edu.jnu.match.MoveErrorSimilarity;
import com.edu.jnu.match.NormalSimilarity;
import com.edu.jnu.match.ScaleSimilarity;
import com.edu.jnu.match.StatisticalSimilarity;
import com.edu.jnu.match.TanimotoMetric;
import com.edu.jnu.strategy.impl.HSVSingleHistogram;

public class ImageTest {

	public static void main(String[] args) throws IOException {

		HSVSingleHistogram HSVSingle = new HSVSingleHistogram(new TanimotoMetric());

		Map<String, List<String>> result = new HashMap<String, List<String>>();

		String path = null;

		List<String> matchUrls = new ArrayList<String>();

		// path = "ukbench/full/ukbench00628.jpg";

		// path = "testimage/47.jpg";

//		path = "imagestore/3.jpg";
//
//		HSVSingle.calcSimilarity(path);

		for (int i = 1; i <= 64; i++) {
			path = "../Image File/imagestore/" + i + ".jpg";
			matchUrls = HSVSingle.calcSimilarity(path);
			result.put(String.valueOf(i), matchUrls);
		}

		float searchRate = 0;

		for (Map.Entry<String, List<String>> map : result.entrySet()) {
			int query = Integer.valueOf(map.getKey());
			List<String> url = map.getValue();

			int lower = lowerUpperBound(query)[0];
			int upper = lowerUpperBound(query)[1];

			int hit = 0;

			for (int i = 0; i < url.size(); i++) {
				int target = Integer.parseInt(getImageName(url.get(i)));
				if (lower <= target && target <= upper) {
					hit++;
				}
			}

			searchRate += hit / 4f;
		}

		System.out.println("平均检索率：" + searchRate / result.size());

		// for (int i = 0; i <= 9; i += 4) {
		// path = "ukbench/full/ukbench0000" + i + ".jpg";
		// name = "0000" + i;
		// long startTime = System.currentTimeMillis();
		// matchUrls = HSVSingle.calcSimilarity(path);
		// long endTime = System.currentTimeMillis();
		// System.out.println("cost: " + (endTime - startTime));
		// totalTime += (endTime - startTime);
		// result.put(name, matchUrls);
		// System.out.println();
		// }
		//
		// for (int i = 12; i <= 99; i += 4) {
		// path = "ukbench/full/ukbench000" + i + ".jpg";
		// name = "000" + i;
		// long startTime = System.currentTimeMillis();
		// matchUrls = HSVSingle.calcSimilarity(path);
		// long endTime = System.currentTimeMillis();
		// System.out.println("cost: " + (endTime - startTime));
		// totalTime += (endTime - startTime);
		// result.put(name, matchUrls);
		// System.out.println();
		// }
		//
		// for (int i = 100; i <= 999; i += 4) {
		// path = "ukbench/full/ukbench00" + i + ".jpg";
		// name = "00" + i;
		// long startTime = System.currentTimeMillis();
		// matchUrls = HSVSingle.calcSimilarity(path);
		// long endTime = System.currentTimeMillis();
		// System.out.println("cost: " + (endTime - startTime));
		// totalTime += (endTime - startTime);
		// result.put(name, matchUrls);
		// System.out.println();
		// }
		//
		// System.out.println("***********************");
		// System.out.println("Cost Time: " + totalTime/250);
		//
		// int flag = 0;
		// float sum = 0f;
		// for (Map.Entry<String, List<String>> entry : result.entrySet()) {
		// List<String> urls = entry.getValue();
		// if (urls.size() < 4) flag++;
		// int queryName = Integer.parseInt(entry.getKey());
		// int matchCount = 0;
		// for (String url : urls) {
		// int imageName = Integer.parseInt(getImageName(url));
		// if ( queryName == imageName || queryName + 1 == imageName ||
		// queryName + 2 == imageName || queryName + 3 == imageName ) {
		// matchCount ++;
		// }
		// }
		//
		// sum += matchCount / 4.0f;
		// }
		//
		// System.out.println("*****************");
		// System.out.println("result set size: " + result.size());
		// System.out.println("Sum: " + sum);
		// System.out.println("平均检索率: " + (sum / 250));
		// System.out.println("<4 : " + flag);

		// String resultPath = HSVSingle.calcSimilarity(path);
		// System.out.println(resultPath);

		// ImageTest imageTest = new ImageTest();
		// for (int i = 100; i <= 999; i++) {
		// System.out.println("ukbench00" + i);
		// imageTest.storeCloth2DB("ukbench00" + i, "ukbench/full/ukbench00" + i +
		// ".jpg");
		// }

	}

	public static int[] lowerUpperBound(int n) {

		int[] res = new int[2];

		int range = n / 4;

		if (n % 4 != 0) {
			res[0] = 4 * range + 1;
			res[1] = 4 * (range + 1);
		} else {
			res[0] = 4 * (range - 1) + 1;
			res[1] = 4 * range;
		}

		return res;
	}

	public static String getImageName(String path) {

		return path.substring(path.indexOf("/") + 1, path.indexOf("."));

	}

}

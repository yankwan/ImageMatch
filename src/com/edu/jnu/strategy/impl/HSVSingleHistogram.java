package com.edu.jnu.strategy.impl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.edu.jnu.match.IMatchStrategy;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.CompareUtil;
import com.edu.jnu.util.Config;
import com.edu.jnu.util.DBHelper;

public class HSVSingleHistogram {
	
	private IMatchStrategy matchStrategy;

	public HSVSingleHistogram(IMatchStrategy matchStrategy) {
		// TODO Auto-generated constructor stub
		this.matchStrategy = matchStrategy;
	}

	public List<String> calcSimilarity(String imgPath) {
		// TODO Auto-generated method stub

		FileInputStream imageFile;

		double maxSimilarity = Double.MIN_VALUE;
		double minDistance = Double.MAX_VALUE;

		List<String> matchUrls = new ArrayList<String>();
		TreeMap<Double, String> resMap = new TreeMap<Double, String>();

		try {

			imageFile = new FileInputStream(imgPath);
			BufferedImage bufferImage;

			bufferImage = ImageIO.read(imageFile);
			// 获取HSV量化后的直方图数据 一维向量表示
			double[] sourceHist = ColorUtil.getImageHSV(bufferImage);

			List<ColorData> sourceHistList = new ArrayList<>();

			ColorData sourceColorData;
			for (int i = 0; i < sourceHist.length; i++) {
				sourceColorData = new ColorData(i, sourceHist[i]);
				sourceHistList.add(sourceColorData);
			}

			Collections.sort(sourceHistList);
			sourceHistList = CompareUtil.getSamePercentage(sourceHistList,
			      Config.TopPercentage);

			// 提取数据库数据
			List<Object> list = DBHelper.fetchALLCloth();

			// long startTime = System.currentTimeMillis();

			// 每一条数据库对应的数据
			
			double tempRes = 0;
			for (int i = 0; i < list.size(); i++) {

				Map<String, Object> map = (Map<String, Object>) list.get(i);

				Object candidateData = map.get("histogram");
				Object candidatePath = map.get("path");

				List<ColorData> candidateHistList = new ArrayList<>();
				candidateHistList = CompareUtil.jsonToList(candidateData);
				
				List<ColorData> copySourceList = new ArrayList<>();
				Iterator<ColorData> it = sourceHistList.iterator();
				while(it.hasNext())
					copySourceList.add(it.next().clone());
				
				tempRes = matchStrategy.similarity(copySourceList, candidateHistList);
				
				resMap.put(tempRes, (String) candidatePath);
				
				
				
				if (Config.SIMILARITY) {
					if (tempRes > maxSimilarity)
						maxSimilarity = tempRes;
				} else {
					if (tempRes < minDistance)
						minDistance = tempRes;
				}
			
			}

			NavigableMap<Double, String> resultMap;
			
			if (Config.SIMILARITY) {
				System.out.println("Max Similarity: " + (float) maxSimilarity);
				resultMap = resMap.descendingMap();
			} else { 
				System.out.println("Min Distance : " + (float) minDistance);
				resultMap = resMap;
			}

			int counter = 0;

			for (Map.Entry<Double, String> map : resultMap.entrySet()) {

				if (counter >= 4)
					break;

				matchUrls.add(map.getValue());
				counter++;
				System.out.println(map.getKey() + " " + map.getValue());
			}
			
			System.out.println();

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

package com.edu.jnu.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.CompareUtil;

public class AraeTest {

	public static void main(String[] args) throws IOException {
		String path = "ukbench/full/ukbench00000.jpg";
		String path2 = "ukbench/full/ukbench00001.jpg";
		
		File imageFile = new File(path);
		File imageFile2 = new File(path2);
		
		BufferedImage bf = ImageIO.read(imageFile);
		BufferedImage bf2 = ImageIO.read(imageFile2);
		
		List result1 = new LinkedList();
		List result2 = new LinkedList();
		
		result1 = ColorUtil.getImageVerticalHSV(bf);
		
		for (int i = 0; i < result1.size(); i++) {
			double[] hist = (double[]) result1.get(i);
			Map<String, Double> histMap = new HashMap<String, Double>();
			
			for (int j = 0; j < hist.length; j++) {
				histMap.put(Integer.toString(j), hist[j]);
			}
			
			histMap = CompareUtil.sortByValue(histMap);
			histMap = CompareUtil.getTopNMapData(128, histMap);
			
			JSONObject jsonObject = JSONObject.fromObject(histMap);
			System.out.println(jsonObject);
			
		}
			
		
		result2 = ColorUtil.getImageVerticalHSV(bf2);
		
		
	}
}

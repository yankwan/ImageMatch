package com.edu.jnu.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.CompareUtil;
import com.edu.jnu.util.Config;
import com.edu.jnu.util.DBHelper;

public class DBStoreTest {

	public static void main(String[] args) {

		String path = null;

		for (int i = 1; i <= 64; i++) {
			path = "imageStore/" + i + ".jpg";
			storeToDB2(path);
		}

		// for (int i = 0; i <= 9; i++) {
		//
		// path = "ukbench/full/ukbench0000" + i + ".jpg";
		// storeToDB(path);
		// }
		//
		// for (int i = 10; i <= 99; i++) {
		// path = "ukbench/full/ukbench000" + i + ".jpg";
		// storeToDB(path);
		// }
		//
		// for (int i = 100; i <= 999; i++) {
		// path = "ukbench/full/ukbench00" + i + ".jpg";
		// storeToDB(path);
		// }

	}

	
	public static void storeToDB2(String path) {
		File imageFile = new File(path);

		BufferedImage bf;
		try {
			bf = ImageIO.read(imageFile);
			double[] hist = ColorUtil.getImageHSV(bf);

			List<ColorData> list = new ArrayList<>();

			for (int i = 0; i < hist.length; i++) {
				ColorData data = new ColorData(i, hist[i]);
				list.add(data);
			}
			
			Collections.sort(list);
			list = CompareUtil.getSamePercentage(list, Config.TopPercentage);

			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(list);

			// String sql = "update base_cloth set histogram = ? where path = ?";
			String sql = "insert into " + Config.dbTable + " (path, histogram) values (?, ?)";

			// Object[] data = {jsonObject.toString(), path};
			Object[] data = { path, jsonArray.toString() };

			DBHelper.updateData(sql, data);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void storeToDB(String path) {
		File imageFile = new File(path);

		BufferedImage bf;
		try {
			bf = ImageIO.read(imageFile);
			double[] hist = ColorUtil.getImageHSV(bf);

			Map<String, Double> histMap = new HashMap<String, Double>();

			for (int i = 0; i < hist.length; i++) {
				histMap.put(Integer.toString(i), hist[i]);
			}

			histMap = CompareUtil.sortByValue(histMap);
			histMap = CompareUtil.getSamePercentage(histMap, Config.TopPercentage);

			JSONObject jsonObject = JSONObject.fromObject(histMap);

			// String sql = "update base_cloth set histogram = ? where path = ?";
			String sql = "insert into " + Config.dbTable + " (path, histogram) values (?, ?)";

			// Object[] data = {jsonObject.toString(), path};
			Object[] data = { path, jsonObject.toString() };

			DBHelper.updateData(sql, data);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

package com.edu.jnu.test;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.CompareUtil;

public class JsonTest {

	
	public static void main(String[] args) {
		
		
		ArrayList<ColorData> list = new ArrayList<>();
		
		ColorData data1 = new ColorData(1129, 0.35);
		ColorData data2 = new ColorData(1120, 0.39);

		list.add(data1);
		list.add(data2);
		
		Collections.sort(list);
		
		
		
		JSONArray mJsonArray = (JSONArray) JSONSerializer.toJSON(list);

		JSONArray jsonObject = JSONArray.fromObject(mJsonArray);
		List<ColorData> resList = new ArrayList<ColorData>();
		
		ColorData data;
		
		resList = CompareUtil.jsonToList(jsonObject);
		
		
		
//		for (int i = 0; i < jsonObject.size(); i++) {
//			data = new ColorData(((JSONObject) jsonObject.get(i)).getInt("HSV"), ((JSONObject) jsonObject.get(i)).getInt("quantity"));
//			resList.add(data);
//		}
//	
		for (int i = 0; i < resList.size(); i++)
			System.out.println(resList.get(i).getHSV() + " : " + resList.get(i).getQuantity());
		
	}

	

}

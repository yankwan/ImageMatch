package com.edu.jnu.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.CompareUtil;
import com.edu.jnu.util.DBHelper;

public class CountTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int[] res = getMaxMinSize();
		System.out.println("min size: " + res[0] + " " + "max size: " + res[1]);
	}
	
	
	public static int[] getMaxMinSize () {
		
		int[] res = new int[2];
		
		List<Object> list = DBHelper.fetchALLCloth();
		
		int maxSize = Integer.MIN_VALUE, minSize = Integer.MAX_VALUE;
		
		for (int i = 0; i < list.size(); i++) {

			Map<String, Object> map = (Map<String, Object>) list.get(i);

			Object candidateData = map.get("histogram");
			Object candidatePath = map.get("path");

			List<ColorData> candidateHistList = new ArrayList<>();
			candidateHistList = CompareUtil.jsonToList(candidateData);
			int candidateSize = candidateHistList.size();
			
			System.out.println(candidatePath + "size: " + candidateSize);
			
			if (candidateSize > maxSize)
				maxSize = candidateSize;
			
			if (candidateSize < minSize)
				minSize = candidateSize;
			
			if ((i+1) % 4 == 0) {
				System.out.println("max ratio: " + maxSize * 1.0 / minSize );
				System.out.println();
				maxSize = Integer.MIN_VALUE;
				minSize = Integer.MAX_VALUE;
			}
			
		}
		
		res[0] = minSize;
		res[1] = maxSize;
		
		return res;
	}

}

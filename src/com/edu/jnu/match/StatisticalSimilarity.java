package com.edu.jnu.match;

import java.util.Iterator;
import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.CompareUtil;
import com.edu.jnu.util.Config;

/**
 * 通过相关系数计算相似度
 * 每个对应颜色因子HSVFactor作为调整参数相乘
 * @author liuyanjun
 *
 */

public class StatisticalSimilarity implements IMatchStrategy {

	@Override
   public double similarity(List<ColorData> data1, List<ColorData> data2) {
	   // TODO Auto-generated method stub
		double rQuantity = 0;
		double rColor = 0;
		
		double quantityAvg1 = getQuantityAvg(data1);
		double quantityAvg2 = getQuantityAvg(data2);
		
		Iterator<ColorData> it1 = data1.iterator();
		Iterator<ColorData> it2 = data2.iterator();

		ColorData cd1, cd2;
		double A = 0, B = 0, C = 0;
		int count = 0;
		while (it1.hasNext() && it2.hasNext()) {
			cd1 = it1.next();
			cd2 = it2.next();
			
			rColor += 1 - getHSVFactor(ColorUtil.HSVtoArray(cd1.getHSV()), ColorUtil.HSVtoArray(cd2.getHSV()));
			
			A += (cd1.getQuantity() - quantityAvg1) * (cd2.getQuantity() - quantityAvg2);
			B += Math.pow((cd1.getQuantity() - quantityAvg1), 2);
			C += Math.pow((cd2.getQuantity() - quantityAvg2), 2);

			count++;
		}
		
		if (B != 0 && C != 0) {
			rQuantity = A / (Math.sqrt(B) * Math.sqrt(C));
		}
		
		rColor = rColor / count;
		
	   return rQuantity * rColor;
   }
	
	
	
	private double getQuantityAvg (List<ColorData> data) {
		double res = 0;
		
		for (int i = 0; i < data.size(); i++) {
			res += data.get(i).getQuantity();
		}
		
		return res / data.size();
	}
	
	
	
	
	static double HFactor = 1f / Config.H_DIMENSION * 0.6;
	static double SFactor = 1f / Config.S_DIMENSION * 0.2;
	static double IFactor = 1f / Config.I_DIMENSION * 0.2;

	
	private static double getHSVFactor(int[] HSV1, int[] HSV2) {
		return Math.abs(HSV1[0] - HSV2[0]) * HFactor
		      + Math.abs(HSV1[1] - HSV2[1]) * SFactor
		      + Math.abs(HSV1[2] - HSV2[2]) * IFactor;
	}

}

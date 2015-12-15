package com.edu.jnu.match;

import java.util.Iterator;
import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;
import com.edu.jnu.util.ColorUtil;
import com.edu.jnu.util.Config;


/**
 * 颜色与统计量误差距离相乘
 * @author liuyanjun
 *
 */
public class NormalSimilarity implements IMatchStrategy {

	@Override
	public double similarity(List<ColorData> data1, List<ColorData> data2) {
		// TODO Auto-generated method stub
		Iterator<ColorData> it1 = data1.iterator();
		Iterator<ColorData> it2 = data2.iterator();

		double res = 0.0;
		ColorData cd1, cd2;

		while (it1.hasNext() && it2.hasNext()) {

			cd1 = it1.next();
			cd2 = it2.next();

			double quantity1 = cd1.getQuantity();
			double quantity2 = cd2.getQuantity();
			double absQuantity = Math.abs(quantity1 - quantity2);

			int[] HSVArr1 = ColorUtil.HSVtoArray(cd1.getHSV());
			int[] HSVArr2 = ColorUtil.HSVtoArray(cd2.getHSV());
			double absHSV = getHSVFactor(HSVArr1, HSVArr2);

			res += absQuantity * absHSV;

		}

		return res;
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

package com.edu.jnu.match;

import java.util.List;

import com.edu.jnu.strategy.impl.ColorData;

public interface IMatchStrategy {

	public double similarity(List<ColorData> data1, List<ColorData> data2);
}

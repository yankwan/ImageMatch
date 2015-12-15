package com.edu.jnu.util;


public class Config {
	
	/**
	 * 数据库配置信息
	 */
	
	public static final String dbDriver = "com.mysql.jdbc.Driver";
	public static final String dbUrl = "jdbc:mysql://localhost/clothdb";
	public static final String dbUser = "root";
	public static final String dbPass = "";
	
	public static final String dbTable = "2clothstore90";
	
	
	/**
	 *  图像及颜色配置信息
	 */
	
	public final static int H_DIMENSION = 1024;
	public final static int S_DIMENSION = 10;
	public final static int I_DIMENSION = 10;
	
	public final static int Hbit = (int) Math.ceil(Math.log(H_DIMENSION)/Math.log(2));
	public final static int Sbit = (int) Math.ceil(Math.log(S_DIMENSION)/Math.log(2));
	public final static int Vbit = (int) Math.ceil(Math.log(I_DIMENSION)/Math.log(2));

	public final static int SIZE = (int) Math.pow(2, (Hbit + Sbit + Vbit));
	
	// 直方图排序后取TOP_N的值
	public static final int TopNValueOfHis = 128; 
	
	// 取排序后的白分量
	public static final float TopPercentage = 0.90f;
	
	// 误差调整
	public static final double FixError = 0.00000001;
	
	// 伸缩比例限制
	public static final double scaleRatio = 7;
	
	// 计算相似度
	public static final boolean SIMILARITY = true;
	

}

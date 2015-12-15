package com.edu.jnu.util;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;


public class ColorUtil {

	private static int H_DIMENSION = Config.H_DIMENSION;
	private static int S_DIMENSION = Config.S_DIMENSION;
	private static int I_DIMENSION = Config.I_DIMENSION;
	
	private static int Hbit = Config.Hbit;
	private static int Sbit = Config.Sbit;
	private static int Vbit = Config.Vbit;
	
	
	private static int SIZE = Config.SIZE;//Integer.parseInt("111111111111111111", 2) + 1;
	
	private static int VERTICAL_N = 3; // 纵向分N份
	private static int HORIZONTAL_M = 3; // 横向分M份
	
	private static int GRID_VERTICAL = 2;
	private static int GRID_HORIZONTAL = 2;
	
	
	/**
	 * 获取图像的HSV直方图
	 * @param src 
	 * @return int G[] 直方图一维数组
	 */
	public static double[] getImageHSV (BufferedImage src) {
		
		int width = src.getWidth();
		int height = src.getHeight();
		
		int[] pixels = new int[width * height];
		double[] G = new double[SIZE];
		double total = width * height;
		
		getRGB(src, 0, 0, width, height, pixels);
		getHSVVector(width, height, pixels, G);
		
		// normalize histogram data
		for (int i = 0; i <  G.length; i++)
			G[i] = G[i] / total;
		
		return G;

	}

	
	/**
	 * 获取图像纵向分区直方图
	 * N 为纵向分区大小
	 * @param src
	 * @return
	 */
   public static ArrayList getImageVerticalHSV (BufferedImage src) {
		
		int width = src.getWidth() / VERTICAL_N;
		int lastWidth = src.getWidth() - (VERTICAL_N - 1)*width;
		int height = src.getHeight();
	

		int[] pixels = new int[width * height];
		int[] pixels2 = new int[lastWidth * height];
		
		
		ArrayList result = new ArrayList();

		for (int k = 0; k < VERTICAL_N-1; k++) {
			double[] G = new double[SIZE];
			getRGB(src, k*width, 0, width, height, pixels);
			getHSVVector(width, height, pixels, G);
			result.add(G);
		}
		
		// 对于分块的最后一部分
		double[] G = new double[SIZE];
		getRGB(src, (VERTICAL_N-1)*width, 0, lastWidth, height, pixels2);
		getHSVVector(lastWidth, height, pixels2, G);
		result.add(G);
		
		return result;
	}
	
	/**
	 * 获取图像横向分区直方图
	 * M 为横向分区大小
	 * @param src
	 * @return
	 */
	public static double[] getImageHorizontalHSV (BufferedImage src) {
		
		int width = src.getWidth();
		int height = src.getHeight() / HORIZONTAL_M;

		int[] pixels = new int[width * height];
		double[] G = new double[SIZE];
		double[] totalG = new double[H_DIMENSION * S_DIMENSION * I_DIMENSION * HORIZONTAL_M];
		
		double total = width * height;
		
		for (int k = 0; k < HORIZONTAL_M; k++) {
			getRGB(src, 0, k*height, width, height, pixels);
			G = getHSVVector(width, height, pixels, null);
			
			for (int i = 0; i <  G.length; i++) {
				G[i] = G[i] / total;
				totalG[G.length*k + i] = G[i];
			}	
		}
		
		return totalG;
	}
	
	
	public static double[] getImageGridHSV (BufferedImage src) {
		
		int width = src.getWidth() / GRID_VERTICAL;
		int height = src.getHeight() / GRID_HORIZONTAL;

		int[] pixels = new int[width * height];
		double[] G = new double[SIZE];
		double[] totalG = new double[H_DIMENSION * S_DIMENSION * I_DIMENSION * GRID_VERTICAL * GRID_HORIZONTAL];
		
		double total = width * height;
		
		for (int k = 0; k < GRID_HORIZONTAL; k++) {
			for (int q = 0; q < GRID_VERTICAL; q++) {
				getRGB(src, k*width, q*height, width, height, pixels);
				G = getHSVVector(width, height, pixels, null);
				
				for (int i = 0; i <  G.length; i++) {
					G[i] = G[i] / total;
					totalG[(k*G.length*GRID_VERTICAL + q*G.length) + i] = G[i];
				}	
			}
		}
		
		return totalG;
	}
	
	
	/**
	 * 获取每个分块的HSV特征值向量
	 * @param width
	 * @param height
	 * @param pixels
	 * @param g2
	 * @return
	 */
	public static double[] getHSVVector (int width, int height, int[] pixels, double[] g2) {
		
		int index = 0;
		float[] hsv = new float[3];
		
		if (g2 == null)
			g2 = new double[SIZE];
		
		for (int row = 0; row < height; row++) {
			int r = 0, g = 0, b = 0;
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				r = (pixels[index] >> 16) & 0xff;
				g = (pixels[index] >> 8) & 0xff;
				b = (pixels[index]) & 0xff;
				
				hsv = RGBtoHSV(r, g, b, hsv);
//				hsv = RGB2HIS(r, g, b);
		
				int H = quantificatHue(hsv[0]);
				int S = quantificateSaturation(hsv[1]);
				int V = quantificateIntensity(hsv[2]);
				
				//H(0~1023) S(0~9) V(0~9)
				//H用10bit存储
				//S与V用4bit存储
				int _index = (H<<(Sbit+Vbit)) | (S<<Vbit) | (V);
				g2[ _index]++;
			}
		}
		
		return g2;
	}
	
	
	/**
	 * GRB颜色空间转HSV颜色空间
	 * @param R
	 * @param G
	 * @param B
	 * @param HSV
	 * @return
	 */
	public static float[] RGBtoHSV (int R, int G, int B, float[] HSV) {
		
		// R,G,B in [0, 255]
		float H = 0, S = 0, V = 0;
		float cMax = 255.0f;
		int cHi = Math.max(R, Math.max(G, B));
		int cLo = Math.min(R, Math.min(G, B));
		int cRng = cHi - cLo;
		
		
		// 计算明度V
		V = cHi / cMax;
		
		// 计算饱和度S
		if (cHi > 0)
			S = (float) cRng/cHi;
		
		// 计算色调
		if (cRng > 0) {
			float rr = (float) (cHi - R)/cRng;
			float gg = (float) (cHi - G)/cRng;
			float bb = (float) (cHi - B)/cRng;
			float hh;
			if (R == cHi)
				hh = bb - gg;
			else if (G == cHi)
				hh = rr - bb + 2.0f;
			else
				hh = gg - rr + 4.0f;
			if (hh < 0)
				hh = hh + 6;
			H = hh/6;
		}
		

		if (HSV == null)
			HSV = new float[3];
		
		HSV[0] = H;
		HSV[1] = S;
		HSV[2] = V;

		return HSV;
	}
	

	
	/**
	 * 将RGB颜色空间转为HIS颜色空间
	 * @param R
	 * @param G
	 * @param B
	 * @return float HISArray[] 
	 */
	public static float[] RGB2HIS(int R, int G, int B){
		
		float r = R/255.0f;
		float g = G/255.0f;
		float b = B/255.0f;
		
		
		float[] HISArray = new float[3];
		
		float partA = 0, partB = 0;
		float thea = 0;
		
		partA = ((r - g) + (r - b))/2;
		partB = (float) Math.sqrt((r - g) * (r - g) + (r - b) * (g - b));

		thea = (partB == 0) ? 0 : (float) Math.acos(partA / partB);
		
		
		HISArray[0] = (float) ((b <= g) ? (thea/(2*Math.PI)) : (2 * Math.PI - thea)/(2*Math.PI));
		
		
		HISArray[1] = (r + g + b == 0) ? 0 : 1 - 3.0f* Math.min(r, Math.min(g, b)) / (r + g + b);
		
		
		HISArray[2] = (r + g + b) / 3;
		
		//System.out.println("H =" + HISArray[0]*360 + " S =" + HISArray[1] + " I =" + HISArray[2]);
		                              		                   
		return HISArray;
		
	}
	
	/**
	 * 对色调进行量化
	 * @param hue
	 * @return
	 */
	public static int quantificatHue (float hue) {
		
		float percent = 1f/H_DIMENSION;
		int result;
		
		result = (int) Math.floor( hue / percent );
		
		if (hue == 1f) result--;
		
		return result;
	
	}
	
	/**
	 * 对饱和度进行量化
	 * @param saturation
	 * @return
	 */
	private static int quantificateSaturation (float saturation) {
		
		float percent = 1f/S_DIMENSION;
		int result;
		
		result = (int) Math.floor( saturation / percent );
		
		if (saturation == 1f) result--;
	
		return result;
	}
	
	/**
	 * 对强度进行量化
	 * @param intensity
	 * @return
	 */
	private static int quantificateIntensity (float intensity) {
		
		float percent = 1f/I_DIMENSION;
		int result;
		
		result = (int) Math.floor( intensity / percent );
		
		if (intensity == 1f) result--;
		
		return result;

	}
	
	/**
	 * 获取图片RGB像素点
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pixels
	 * @return
	 */
	private static int[] getRGB (BufferedImage image, int x, int y, int width,
	      int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB
		      || type == BufferedImage.TYPE_INT_RGB)
			return (int[]) image.getRaster().getDataElements(x, y, width, height,
			      pixels);
		return image.getRGB(x, y, width, height, pixels, 0, width);
	}
	
	
	/**
	 * 将整型的HSV值抽取H、S、V存入数组中
	 * @param HSV
	 * @return
	 */
	public static int[] HSVtoArray (int HSV) {
		
		int[] arr = new int[3];
		arr[0] = (HSV >> (Sbit+Vbit)) & 0xff;  //0xff:低八位都是1,其余都是0
		arr[1] = (HSV >> Vbit) & 0xf; 
		arr[2] = HSV & 0xf;
		
		return arr;
		
	}

}

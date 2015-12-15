package com.edu.jnu.util;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LBP {
	final static double PI = 3.14159265358979323846;

	static final int PREDICATE = 1;

	static final int BITS = 8;
	static final int DIFF = PREDICATE << 1;
	public static final int PATSNUM = 58;
	
	// uniform patterns
	static final int UNIPATS[] = { 0, 1, 2, 3, 4, 6, 7, 8, 12, 14, 15, 16, 24,
	      28, 30, 31, 32, 48, 56, 60, 62, 63, 64, 96, 112, 120, 124, 126, 127,
	      128, 129, 131, 135, 143, 159, 191, 192, 193, 195, 199, 207, 223, 224,
	      225, 227, 231, 239, 240, 241, 243, 247, 248, 249, 251, 252, 253, 254,
	      255 };

	class IntegerPoint {
		int x;
		int y;
	}

	class DoublePoint {
		double x;
		double y;
	}

	IntegerPoint[] points;
	DoublePoint[] offsets;

	public LBP() {
		int i;

		offsets = new DoublePoint[BITS];
		points = new IntegerPoint[BITS];

		for (i = 0; i < BITS; i++) {
			points[i] = new IntegerPoint();
			offsets[i] = new DoublePoint();
		}
	}

	void compareNeighbors(int[] value, int center, int neighbor, int shift) {
		if (center < neighbor) {
			value[0] |= 1 << shift;
		} else {
			value[0] |= 0 << shift;
		}
	}

	void calculatePoints() {
		int bit;

		double step = 2 * PI / BITS;
		double tmpX, tmpY;

		for (bit = 0; bit < BITS; bit++) {
			// bit * step 为角度
			tmpX = PREDICATE * Math.cos(bit * step);
			tmpY = PREDICATE * Math.sin(bit * step);

			points[bit].x = (int) tmpX;
			points[bit].y = (int) tmpY;

			offsets[bit].x = tmpX - points[bit].x;
			offsets[bit].y = tmpY - points[bit].y;

			if (offsets[bit].x < 1.0e-10 && offsets[bit].x > -1.0e-10) {
				offsets[bit].x = 0;
			}
			if (offsets[bit].y < 1.0e-10 && offsets[bit].y > -1.0e-10) {
				offsets[bit].y = 0;
			}

			if (tmpX < 0 && offsets[bit].x != 0) {
				points[bit].x -= 1;
				offsets[bit].x += 1;
			}

			if (tmpY < 0 && offsets[bit].y != 0) {
				points[bit].y -= 1;
				offsets[bit].y += 1;
			}
			
//			System.out.println("x: " + points[bit].x + " y: " + points[bit].y);
//			System.out.println(" offset x: " + offsets[bit].x + " offset y: " + offsets[bit].y);
		}
	}

	double interpolate(int ul, int rl, int lb, int rb, int i) {
		double dx = 1 - offsets[i].x;
		double dy = 1 - offsets[i].y;

		return ul * dx * dy + rl * offsets[i].x * dy + lb * dx * offsets[i].y
		      + rb * offsets[i].x * offsets[i].y;
	}

	/**
	 * 
	 * @param data 图像灰度值
	 * @param row 图像高度
	 * @param column 图像宽度
	 * @param hist81 特征值数组
	 */
	void histogram81(int[] data, int row, int column, int[] hist81) {
		int i;
		int r, c;
		int[] value = new int[] { 0 };

		int leap = column * PREDICATE;

		// nb - index of points
		int[] nb = new int[8];

		nb[0] = 0;
		nb[1] = nb[0] + PREDICATE;
		nb[2] = nb[1] + PREDICATE;
		nb[3] = nb[2] + leap;
		nb[4] = nb[3] + leap;
		nb[5] = nb[4] - PREDICATE;
		nb[6] = nb[5] - PREDICATE;
		nb[7] = nb[6] - leap;
		
		

		// center - index of center point
		int center = nb[7] + PREDICATE;

		nb[0] = center + points[5].x + points[5].y * column;
		nb[2] = center + points[7].x + points[7].y * column;
		nb[4] = center + points[1].x + points[1].y * column;
		nb[6] = center + points[3].x + points[3].y * column;
		
		
//		for (int j = 0; j < nb.length; j++)
//			System.out.println(nb[j]);

		for (r = 0; r < row - DIFF; r++) {
			for (c = 0; c < column - DIFF; c++) {
				value[0] = 0;
				
				compareNeighbors(value, data[center], data[nb[1]], 1);
				compareNeighbors(value, data[center], data[nb[3]], 3);
				compareNeighbors(value, data[center], data[nb[5]], 5);
				compareNeighbors(value, data[center], data[nb[7]], 7);

				compareNeighbors(
				      value,
				      data[center],
				      (int) (interpolate(data[nb[0]], data[nb[0] + 1], data[nb[0]
				            + column], data[nb[0] + column + 1], 5) + 0.5), 0);
				compareNeighbors(
				      value,
				      data[center],
				      (int) (interpolate(data[nb[2]], data[nb[2] + 1], data[nb[2]
				            + column], data[nb[2] + column + 1], 7) + 0.5), 2);
				compareNeighbors(
				      value,
				      data[center],
				      (int) (interpolate(data[nb[4]], data[nb[4] + 1], data[nb[4]
				            + column], data[nb[4] + column + 1], 1) + 0.5), 4);
				compareNeighbors(
				      value,
				      data[center],
				      (int) (interpolate(data[nb[6]], data[nb[6] + 1], data[nb[6]
				            + column], data[nb[6] + column + 1], 3) + 0.5), 6);

				for (i = 0; i < 8; i++) {
					nb[i]++;
				}

				center++;

				hist81[value[0]]++;
			}

			for (i = 0; i < 8; i++) {
				nb[i] += DIFF;
			}

			center += DIFF;
		}
	}

	public void lbpFeature(int[] img, int height, int width, double[] feature) {
		int i;
		int sum = 0;
		int sumpats = 0;

		calculatePoints();

		int[] histFull = new int[256];

		for (i = 0; i < 256; i++) {
			histFull[i] = 0;
		}

		histogram81(img, height, width, histFull);
		
//		for (int j = 0; j < histFull.length; j++)
//			System.out.println(histFull[j]);

		for (i = 0; i < PATSNUM; i++) {
			feature[i] = histFull[UNIPATS[i]];
			sumpats += feature[i];
		}

		for (i = 0; i < 256; i++) {
			sum += histFull[i];
		}

		feature[PATSNUM] = sum - sumpats;

		for (i = 0; i < feature.length; i++) {
			feature[i] = feature[i] / (float) sum;
		}
	}

	
	
	public static double[] getLBPFeature(String imgPath) {
		int i, j;

		// LBP feature
		double[] feature = new double[LBP.PATSNUM + 1];

		int height; // Height of Image
		int width; // Width of Image

		int[] data; // Block Data

		BufferedImage img;
      try {
	      img = ImageIO.read(new File(imgPath));
	      height = img.getHeight();
			width = img.getWidth();

			data = new int[height * width];

			LBP lbp = new LBP();

			
			// 彩色图像转换成无彩色的灰度图像Y=0.299*R + 0.578*G + 0.114*B
			for (i = 0; i < width; i++) {
				for (j = 0; j < height; j++) {
					Color c = new Color(img.getRGB(i, j));
					data[j * width + i] = (int) (0.299 * c.getRed() + 0.578
					      * c.getGreen() + 0.114 * c.getBlue());
				}
			}

			// Extract LBP feature
			lbp.lbpFeature(data, height, width, feature);
//			System.out.println(feature.length);
//			System.out.println(Arrays.toString(feature));
      } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }

      return feature;
		
	}
}
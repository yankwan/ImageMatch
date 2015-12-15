package com.edu.jnu.strategy.impl;

public class ColorData implements Comparable<ColorData>, Cloneable{

	private int HSV;
	private double quantity;
	
	
	public ColorData (int HSV, double quantity) {
		this.HSV = HSV;
		this.quantity = quantity;
	}
	
	
	public int getHSV() {
		return HSV;
	}
	public void setHSV(int hSV) {
		HSV = hSV;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public int compareTo(ColorData o) {
	   // TODO Auto-generated method stub
	   if (this.quantity - o.quantity > 0)
	   	return -1;
	   else if (this.quantity - o.quantity < 0)
	   	return 1;
	   else
	   	return 0;
	}
	
	@Override
	protected ColorData clone() {
	   // TODO Auto-generated method stub
		ColorData clone = null;
		try {
	      clone = (ColorData) super.clone();
      } catch (CloneNotSupportedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
	   return clone;
	   
	}
	
	
}

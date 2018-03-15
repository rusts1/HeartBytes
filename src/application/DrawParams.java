package application;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class DrawParams {
	public Point p1;
	public Point p2;
	public Scalar color;
	public int thickness;
	public int radius;
	public boolean isCircle = false;
	
	public DrawParams(Point p1, Point p2, Scalar color, int thickness) {
		this.p1 = p1;
		this.p2 = p2;
		this.color = color;
		this.thickness = thickness;
		isCircle = false;
	}
		
	public DrawParams(Point p1, int radius, Scalar color, int thickness) {
		this.p1 = p1;
		this.radius = radius;
		this.color = color;
		this.thickness = thickness;
		isCircle = true;
	}
	
	
}

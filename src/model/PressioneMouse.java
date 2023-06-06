package model;

import java.awt.Point;

public class PressioneMouse implements Azione<Point>{

	private Point p;
	private int delay;
	
	public PressioneMouse(Point p, int delay) {
		this.p = p;
		this.delay = delay;
	}
	
	@Override
	public int getDelay() {
		return delay;
	}

	@Override
	public Point get() {
		return p;
	}

}

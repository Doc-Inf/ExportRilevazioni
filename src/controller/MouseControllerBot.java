package controller;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import static view.AppLogger.*;

public class MouseControllerBot {
	
	private static final List<Point> clicks = new ArrayList<>();
	private static final List<Integer> delays = new ArrayList<>();
	
	
	public static void add(Point p, int delay) {
		clicks.add(p);
		delays.add(delay);
	}
	
	public static void clear() {
		clicks.clear();
		delays.clear();
	}
	
	public static Point getMousePosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	
	public static void click(int x, int y) {
		Robot bot;
		try {
			bot = new Robot();
			bot.mouseMove(x, y);    
		    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (AWTException e) {
			e.printStackTrace();
		}	    
	}
	
	public static void executeAllClicks() {
		for(int i=0;i<clicks.size();++i) {
			log("Click " + i);
			log("In attesa di " + delays.get(i)*1000);
			try {
				Thread.sleep(delays.get(i)*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log("Coordinate - X: " + (int)clicks.get(i).getX() + "  Y: " + (int)clicks.get(i).getY() );
			click((int)clicks.get(i).getX(),(int)clicks.get(i).getY());
		}
	}
}

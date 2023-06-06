package controller;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import model.Azione;
import model.PressioneMouse;
import model.PressioneTasto;

import static view.AppLogger.*;

public class MouseControllerBot {
	
	@SuppressWarnings("rawtypes")
	private static final List<Azione> azioni = new ArrayList<>();
	private static Robot bot;
	
	static {
		try {
			bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> void add(Azione<T> a) {
		azioni.add(a);		
	}
	
	public static void clear() {
		azioni.clear();
	}
	
	public static Point getMousePosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	
	public static void click(int x, int y) {
		bot.mouseMove(x, y);    
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);			    
	}
	
	public static void mousePressed(int x, int y) {
		bot.mouseMove(x, y);    
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);			    
	}
	
	public static void mouseReleased() {
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);			    
	}
	
	public static void keyPress(int key) {
		bot.keyPress(key);
	}
	
	public static void executeAllClicks() {
		for(int i=0;i<azioni.size();++i) {
			if(azioni.get(i) instanceof PressioneMouse) {
				PressioneMouse mouseClick = (PressioneMouse) azioni.get(i);
				log("Azione " + i + " click del mouse");
				log("In attesa di " +  mouseClick.getDelay()*1000);
				try {
					Thread.sleep( mouseClick.getDelay()*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log("Coordinate - X: " + (int) mouseClick.get().getX() + "  Y: " + (int) mouseClick.get().getY() );
				click((int) mouseClick.get().getX(),(int) mouseClick.get().getY());
			}else {
				if(azioni.get(i) instanceof PressioneTasto) {
					PressioneTasto tastoPremuto = (PressioneTasto) azioni.get(i);
					log("Azione " + i + " tasto Premuto");
					log("In attesa di " +  tastoPremuto.getDelay()*1000);
					try {
						Thread.sleep( tastoPremuto.getDelay()*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					log("Pressione del tasto: " + tastoPremuto.get());
					keyPress(tastoPremuto.get());
				}
			}
			
		}
	}
}

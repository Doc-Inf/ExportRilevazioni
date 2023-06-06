package model;


public class PressioneTasto implements Azione<Integer>{

	private int tasto;
	private int delay;
	
	public PressioneTasto(int tasto, int delay) {
		this.tasto = tasto;
		this.delay = delay;
	}
	
	
	@Override
	public int getDelay() {
		return delay;
	}

	@Override
	public Integer get() {
		return tasto;
	}

}

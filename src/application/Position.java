package application;

public class Position {
	public int lv, x, y;
	
	/*
	 * Constructeur de la position
	 */
	public Position(int lv, int x, int y){
		this.lv = lv;
		this.x = x;
		this.y = y;
	}
	
	/*
	 * Modificateur de la position
	 */
	public void setPosition(int lv, int x, int y){
		this.lv = lv;
		this.x = x;
		this.y = y;
	}
}

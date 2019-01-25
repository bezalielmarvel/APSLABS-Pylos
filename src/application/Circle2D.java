package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Circle2D {
	private Circle circle;
	private int posX;
	private int posY;
	
	/*
	 * Constructeur (pour initialiser toutes les variables)
	 */
	public Circle2D(float radius){
		circle = new Circle();
		circle.setRadius(radius);
		
	}
	
	/*
	 * Methode qui retourne la variable circle
	 */
	public Circle getCircle(){
		return circle;
	}
	
	/*
	 * Methode qui permet de changer la couleur d'une boule 2D
	 */
	public void setColor(Color c){
		circle.setFill(c);
	}
	
	/*
	 * Methode qui retourne la coordonée X d'une boule 2D
	 */
	public int getPosX(){
		return posX;
	}
	
	/*
	 * Methode qui retourne la coordonée Y d'une boule 2D
	 */
	public int getPosY(){
		return posY;
	}
	
	/*
	 * Methode qui permet d'affecter la coordonée X d'une boule 2D
	 */
	public void setPosX(int posX){
		this.posX = posX;
		circle.setCenterX(posX);
	}
	
	/*
	 * Methode qui permet d'affecter la coordonée Y d'une boule 2D
	 */
	public void setPosY(int posY){
		this.posY = posY;
		circle.setCenterY(posY);
	}
	
	/*
	 * Methode qui permet de changer la couleur du bordure de'une boule 2D
	 */
	public void setStroke(Color c, int width){
		circle.setStroke(c);
		circle.setStrokeWidth(width);
	}

	/*
	 * Methode pour affecter circle
	 */
	public void setCircle(Circle c) {
		this.circle = c;
	}
}

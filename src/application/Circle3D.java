package application;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import moleculesampleapp.Xform;

public class Circle3D {
	private Sphere sphere;
	private Xform ball;
	final PhongMaterial whiteMaterial  = new PhongMaterial();
	final PhongMaterial blackMaterial = new PhongMaterial();
	
	/*
	 * Constructeur créant une Circle3D représentant les boules fantômes
	 */
	public Circle3D(){
		PhongMaterial transparent = new PhongMaterial();
		transparent.setDiffuseColor(new Color(1,1,1,0.01));
		sphere = new Sphere(40);
		sphere.setMaterial(transparent);
		ball = new Xform();
		ball.getChildren().add(sphere);
	}
	
	/*
	 * Constructeur créant une Circle3D représentant les pions des joueurs
	 */	
	public Circle3D(double size, Color c1, Color c2){

		whiteMaterial.setDiffuseColor(c2);
		whiteMaterial.setSpecularColor(c2);
		
		blackMaterial.setDiffuseColor(c1);
		blackMaterial.setSpecularColor(c1);
		
		sphere = new Sphere(size);
		sphere.setMaterial(whiteMaterial);
		ball = new Xform();
		ball.getChildren().add(sphere);
	}
	
	public Xform getXformBall(){
		return ball;
	}
	
	public void setXform(Xform x) {
		this.ball = x;
	}
	
	/*
	 * Méthode qui permet de définir la couleur (material) de la sphère
	 */
	public void setBallColor(Color color){
		PhongMaterial col = new PhongMaterial();
		col.setDiffuseColor(color);	
		col.setSpecularColor(color);
		sphere.setMaterial(col);
	}
	
	/*
	 * Méthode qui permet de définir la couleur (material) si les couleurs sont celles
	 * par défaut
	 */	
	public void setBallColor(String color){
		if (color.equals("white"))	
			sphere.setMaterial(whiteMaterial);
		else
			sphere.setMaterial(blackMaterial);
	}
	
	public Color getColor() {
		PhongMaterial col = (PhongMaterial) sphere.getMaterial();
		return col.getDiffuseColor();
	}
	
	
	/*
	 * Méthode qui permet de définir la position dans les axes de la sphère 3D
	 */
	public void setTranslate(String axe, int value){
		if (axe.equals("x"))	
			ball.setTx(value);
		else if (axe.equals("y"))
			ball.setTy(value);
		else
			ball.setTz(value);
	}
	
	public Sphere getSphere() {
		return sphere;
	}
}

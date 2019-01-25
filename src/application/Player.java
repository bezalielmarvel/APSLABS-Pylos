package application;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.paint.Color;


public class Player {
	
	private String name;
	private ArrayList<Boule> myBall;
	private boolean turn;
	private boolean isPlayer1;
	private Color c1,c2;
	
	
	/*
	 * Constructeur du Player qui initialise un joueur et lui attribue 15 Boules
	 */
	public Player(String name, boolean firstPlayer, Color c1, Color c2) {
		myBall = new ArrayList<Boule>();
		for (int i = 0; i < 15; i++) {
			myBall.add(new Boule(firstPlayer, c1, c2));
		}
		turn = firstPlayer;
		isPlayer1 = firstPlayer;
		this.name = name;
		this.c1 = c1;
		this.c2 = c2;
	}

	/*
	 * Retourne si'il s'agit du premier joueur
	 */
	public boolean isPlayer1() {
		return isPlayer1;
	}

	/*
	 * Retourne si c'est à son tour de jouer
	 */
	public boolean isTurn() {
		return turn;
	}

	/*
	 * Méthode qui permet de définir s'il s'agit à son tour de jouer
	 */
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	/*
	 * Retourne si le joueur peut jouer:
	 * 	- c'est bien au tour du joueur
	 * 	- s'il s'agit bien d'une boule qui n'a pas été encore placée
	 */
	public boolean canPlay() {
		if (!turn)
			return false;
		for (Boule ball : myBall) {
			if (!ball.isOnBoard())
				return true;
		}
		return false;
	}

	/*
	 * Retourne si la boule est jouable
	 */
	public Boule playableBall() {
		for (Boule ball : myBall) {
			if (!ball.isOnBoard())
				return ball;
		}
		return null;
	}
	
	/*
	 *  Retourne le nombre de boules disponibles
	 */
	public int availableBalls() {
		int availableBalls = 15;
		
		for (Boule ball : myBall) {
			if (ball.isOnBoard()) {
				availableBalls--;
			}
		}
		
		return availableBalls;
	}

	/*
	 * Place la boule 3D sur le plateau
	 */
	public boolean placeBallOn(Board board, int lv, int x, int y, Group group) {
		if (canPlay() && !board.playableCase(lv, x, y)) {
			return false;
		}
		Boule current = playableBall();
		board.setBall(current, lv, x, y);
		current.getPlace().setPosition(lv, x, y);
		current.place3D(lv, x, y, board, group);
		if (group != null)
			current.placeOnGroup3D(group);
		current.setOnBoard(true);
		return true;
	}
	
	public String getName() {
		return this.name;
	}	
	
	public Color getColor1() {
		return this.c1;
	}
	
	public Color getColor2() {
		return this.c2;
	}
	
	

	
}

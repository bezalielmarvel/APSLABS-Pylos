package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Group;

public class Board {
	
	private ArrayList<Boule[][]> grid;
	
	/*
	 * Constructeur (pour initialiser la grille)
	 */
	public Board() {
		grid = new ArrayList<Boule[][]>();	
		for(int i = 0; i<4; i++) {
			this.grid.add(new Boule[i+1][i+1]);
		}
	}
	
	/*
	 * Cette methode permet de savoir si une case du plateau est jouable.
	 * si elle est jouable, elle retourne TRUE sinon FALSE 
	 */
	public boolean playableCase(int lv, int x, int y) {
		if (lv < 0 || lv > 3)
			return false;
		if (isEmpty(lv, x, y)) {
			if (lv == 3)
				return isEmpty(lv, x, y);
			else
				return (!isEmpty(lv + 1, x, y) && !isEmpty(lv + 1, x + 1, y) && !isEmpty(lv + 1, x, y + 1)
						&& !isEmpty(lv + 1, x + 1, y + 1));

		}
		return false;
	}
	
	/*
	 * Cette methode permet de savoir si une case du plateau est vide.
	 * elle retourne TRUE si elle l'est, sinon FALSE 
	 */
	public boolean isEmpty(int lv, int x, int y) {
		if (getBall(lv, x, y) == null) {
			return true;
		} else
			return !getBall(lv, x, y).isOnBoard();
	}
	
	/*
	 * Cette methode retourne une boule de la grille 
	 */
	public Boule getBall(int lv, int x, int y) {
		return this.grid.get(lv)[x][y];
	}
	
	/*
	 * Cette methode permet de savoir s'il y a une boule au dessus d'une bouille.
	 * elle retourne TRUE s'il y a des boulles sinon FALSE 
	 */
	public boolean hasABallOnTop(int lv, int x, int y) {
		if (lv == 0)
			return false;
		else if (lv == 1)
			return onTop();
		else {
			if (x == 0) {
				if (y == lv)
					return !isEmpty(lv - 1, x, y - 1);
				else if (y == 0)
					return !isEmpty(lv - 1, 0, 0);
				else
					return !isEmpty(lv - 1, x, y) || !isEmpty(lv - 1, x, y - 1);
			} else if (x == lv) {
				if (y == lv)
					return !isEmpty(lv - 1, x - 1, y - 1);
				else if (y == 0)
					return !isEmpty(lv - 1, x - 1, y);
				else
					return !isEmpty(lv - 1, x - 1, y) || !isEmpty(lv - 1, x - 1, y - 1);
			} else if (y == 0) {
				return !isEmpty(lv - 1, x, y) || !isEmpty(lv - 1, x - 1, y);
			} else if (y == lv) {
				return !isEmpty(lv - 1, x, y - 1) || !isEmpty(lv - 1, x - 1, y - 1);
			} else {
				return (!isEmpty(lv - 1, x, y) || !isEmpty(lv - 1, x - 1, y) || !isEmpty(lv - 1, x, y - 1)
						|| !isEmpty(lv - 1, x - 1, y - 1));
			}
		}
	}
	
	/*
	 * Cette methode retourne le sommet.
	 */
	public boolean onTop() {
		return this.getBall(0, 0, 0) != null;
	}
	
	/*
	 * Cette methode permet de savoir si un joueur a fait un carré.
	 * elle retourne TRUE si le joueur a fait un carré sinon FALSE
	 */
	public boolean square(int lv, int x, int y) {
		if (lv == 0)
			return false;
		else {
			if (isEmpty(lv, x, y) || isEmpty(lv, x + 1, y) || isEmpty(lv, x, y + 1) || isEmpty(lv, x + 1, y + 1))
				return false;
			else {
				boolean color = getBall(lv, x, y).isPlayer1();
				return (color == getBall(lv, x + 1, y).isPlayer1() && color == getBall(lv, x, y + 1).isPlayer1()
						&& color == getBall(lv, x + 1, y + 1).isPlayer1());
			}
		}

	}

	/*
	 * Cette methode permet de savoir si un joueur a fait un carré à partir d'une boule.
	 * elle retourne TRUE si le joueur a fait un carré sinon FALSE
	 */
	public boolean squareFull(int lv, int x, int y) {
		if (lv == 0)
			return false;
		else if (lv == 1)
			return square(lv, 0, 0);
		else {
			if (x == 0) {
				if (y == lv)
					return square(lv, x, y - 1);
				else if (y == 0)
					return square(lv, x, y);
				else
					return square(lv, x, y - 1) || square(lv, x, y);
			} else if (x == lv) {
				if (y == lv)
					return square(lv, x - 1, y - 1);
				else if (y == 0)
					return square(lv, x - 1, y);
				else
					return square(lv, x - 1, y - 1) || square(lv, x - 1, y);
			} else if (y == 0) {
				return square(lv, x - 1, y) || square(lv, x, y);
			} else if (y == lv) {
				return square(lv, x - 1, y - 1) || square(lv, x, y - 1);
			} else {
				return square(lv, x - 1, y - 1) || square(lv, x - 1, y) || square(lv, x, y - 1) || square(lv, x, y);
			}
		}
	}
	
	/*
	 * Cette methode permet de retirer une boule.
	 * elle retourne TRUE si c'est réussi sinon FALSE
	 */
	public boolean removeBall(Player player,Board board, Group group, int lv, int x, int y) {
		if (hasABallOnTop(lv, x, y)) {
			System.out.println("il y a une balle au dessus");
			return false;
		} else if (player.isPlayer1() == getBall(lv, x, y).isPlayer1()) {
			Circle3D b = board.getBall(lv, x, y).getBoule3D();
			Platform.runLater(new Thread(new Runnable() {
				@Override
				public void run() {					
					group.getChildren().remove(b.getXformBall());
				}
				
			}));
			
			getBall(lv, x, y).setOnBoard(false);
			setBall(null, lv, x, y);
			return true;
		}
		return false;
	}

	/*
	 * Cette methode permet de placer une boule.
	 * elle retourne TRUE si c'est réussi sinon FALSE
	 */
	public boolean setBall(Boule ball, int lv, int x, int y) {
		if (isEmpty(lv, x, y)) {
			this.grid.get(lv)[x][y] = ball;
			if (ball != null)
				ball.setOnBoard(true);
			return true;
		}
		return false;
	}
	

}

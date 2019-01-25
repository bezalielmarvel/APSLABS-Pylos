package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public final class Emplacements {

	/*
	 * boules -> variable pour la liste des boules
	 */
	private static ArrayList<Boule> boules = new ArrayList<Boule>();

	/*
	 * Cette methode permet d'ajouter une boule dans la liste des boules
	 */
	public static void addBoule(Boule b) {
		boules.add(b);
	}

	/*
	 * Cette methode permet d'afficher les boules fantomes sur les emplacements jouables
	 */
	public static void setAvailablePositions(Board board, Group group) {
		for (Boule b : boules) {
			if (board.playableCase(b.getPlace().lv, b.getPlace().x, b.getPlace().y)) {
				b.place3DEmplacement(b.getPlace().lv, b.getPlace().x, b.getPlace().y);
				b.placeOnGroupEmplacement(group);	
				b.setOnBoard(true);
			}else {
				b.removeOnGroupEmplacement(group);
				b.setOnBoard(false);
			}
		}
	}
	
	/*
	 * Cette methode permet d'afficher les boules qu'un joueur peut retirer
	 */
	public static void setRemovablePositions(Board board, Group group, Player player) {		
		for(Boule b : boules) {
			if (!board.hasABallOnTop(b.getPlace().lv, b.getPlace().x, b.getPlace().y)) {
				if(board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y) != null) {
					if(player.isPlayer1() == board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y).isPlayer1()) {
						Circle2D bb = b.getBoule2D(b.getPlace().lv, b.getPlace().x, b.getPlace().y);
						bb.setColor(Color.DARKVIOLET);
						
						Circle3D boule3D = board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y).getBoule3D();
						boule3D.setBallColor(Boule.c3);
						Platform.runLater(new Thread(new Runnable() {
							@Override
							public void run() {
								group.getChildren().remove(boule3D.getXformBall());
								group.getChildren().add(boule3D.getXformBall());
							}
						}));
					}
				}				
			}
			b.removeOnGroupEmplacement(group);
			b.setOnBoard(false);
		}
	}
	
	/*
	 * Cette methode permet de reinitialiser les boules après le joueur a retiré
	 */
	public static void resetRemovablePositions(Board board, Group group, Player player) {		
		for(Boule b : boules) {
			if (!board.hasABallOnTop(b.getPlace().lv, b.getPlace().x, b.getPlace().y)) {
				if(board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y) != null) {
					if(player.isPlayer1() == board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y).isPlayer1()) {
						Circle2D bb = b.getBoule2D(b.getPlace().lv, b.getPlace().x, b.getPlace().y);
						if(bb.getCircle().getFill() == Color.DARKVIOLET && player.isPlayer1()) {
							bb.setColor(player.getColor1());
						}
						
						if(bb.getCircle().getFill() == Color.DARKVIOLET && !player.isPlayer1()) {
							bb.setColor(player.getColor2());
						}
						
						Circle3D boule3D = board.getBall(b.getPlace().lv, b.getPlace().x, b.getPlace().y).getBoule3D();
						if(boule3D.getColor() == Boule.c3 && player.isPlayer1()) {
							boule3D.setBallColor(player.getColor1());
							Platform.runLater(new Thread(new Runnable() {
								@Override
								public void run() {
									group.getChildren().remove(boule3D.getXformBall());
									group.getChildren().add(boule3D.getXformBall());
								}
							}));
						}
						
						if(boule3D.getColor() == Boule.c3 && !player.isPlayer1()) {
							boule3D.setBallColor(player.getColor2());
							Platform.runLater(new Thread(new Runnable() {
								@Override
								public void run() {
									group.getChildren().remove(boule3D.getXformBall());
									group.getChildren().add(boule3D.getXformBall());
								}
							}));
						}
					}
				}				
			}
		}
	}
}

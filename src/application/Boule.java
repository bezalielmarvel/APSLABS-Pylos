package application;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Boule {

	private Circle2D boule2D;
	private Circle3D boule3D;
	private Circle3D emplacement;
	private Board board;
	private boolean isOnBoard;
	private boolean isPlayer1;
	private Position position;
	private static Player player1, player2;
	public static int remove = 0;
	private Group game3dBox;
	private Color c1;
	private Color c2;
	static Color c3 = new Color(Color.DARKVIOLET.getRed(),Color.DARKVIOLET.getGreen(),Color.DARKVIOLET.getBlue(),0.5);

	private final Integer STARTTIME = 30;
	private Integer seconds = STARTTIME;
	public Timeline timeline1 = new Timeline();
	public Timeline timeline2 = new Timeline();

	/*
	 * Constructeur (pour initialiser toutes les variables)
	 */
	public Boule(String j1, String j2, Board grid, int lv, int x, int y, Group grp3d, Color c1, Color c2) {
		board = grid;
		player1 = new Player(j1, true, c1, c2);
		player2 = new Player(j2, false, c1, c2);
		position = new Position(lv, x, y);
		this.game3dBox = grp3d;
		boule2D = new Circle2D(20.0f);
		boule3D = new Circle3D(40, c1, c2);
		this.c1 = c1;
		this.c2 = c2;
		emplacement = new Circle3D();

		Emplacements.addBoule(this);
		Emplacements.setAvailablePositions(board, game3dBox);
		
	}
	
	/*
	 * Constructeur 
	 */
	public Boule(boolean isfirstPlayer, Color c1, Color c2) {
		this.isPlayer1 = isfirstPlayer;
		boule3D = new Circle3D(40, c1, c2);

		if (isfirstPlayer)
			boule3D.setBallColor("black");
		else
			boule3D.setBallColor("white");

		position = new Position(-1, -1, -1);
	}

	/*
	 * Cette methode retourne la boule 3D
	 */
	public Circle3D getBoule3D() {
		return boule3D;
	}

	/*
	 * Cette methode retourne TRUE si une boule est sur le plateau sinon FALSE
	 */
	public boolean isOnBoard() {
		return this.isOnBoard;
	}

	/*
	 * Cette methode retourne TRUE si cette boule est à joueur 1 sinon FALSE
	 */
	public boolean isPlayer1() {
		return this.isPlayer1;
	}

	/*
	 * Cette methode permet de créer des evenements sur les boules 2D et boules 3D (le mechanisme du jeu est ici)
	 */
	public void setBoule(Circle2D cercle) {
		boule2D = cercle;

		boule2D.getCircle().setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (board.playableCase(position.lv, position.x, position.y))
					boule2D.setColor(Color.CHARTREUSE);
			}

		});
		boule2D.getCircle().setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (board.isEmpty(position.lv, position.x, position.y)) {
					boule2D.setColor(Color.WHITE);
				}
			}
		});
		boule2D.getCircle().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {

				ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
				ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

				if (remove == 0) {
					if (board.playableCase(position.lv, position.x, position.y)) {
						Alert alert = new Alert(null, "Make this move?", yes, no);
						alert.setTitle("Confirmation Moves");
						Optional<ButtonType> result = alert.showAndWait();

						if (result.get() == yes) {

							// JOUEUR 1
							if (player1.canPlay()) {

								if (player1.placeBallOn(board, position.lv, position.x, position.y, game3dBox)) {
									boule2D.setColor(c1);
									Main.txa.appendText("\n");
									Main.txa.appendText(player1.getName() + " has placed a ball at [Lv : " + position.lv
											+ ", X : " + position.x + ", Y : " + position.y + "]");
									updatePion("remove");
									if (board.onTop() == true)
										Gagnant(player1.getName());

									if (board.squareFull(position.lv, position.x, position.y)) {
										remove = remove();
										if(remove != 0) {
											Emplacements.setRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
										}
									}
									if (remove == 0)
										switchPlayerTurn();
								}
							} else {

								if (player2.placeBallOn(board, position.lv, position.x, position.y, game3dBox)) {
									boule2D.setColor(c2);
									Main.txa.appendText("\n");
									Main.txa.appendText(player2.getName() + " has placed a ball at [Lv : " + position.lv
											+ ", X : " + position.x + ", Y : " + position.y + "]");
									updatePion("remove");
									if (board.onTop() == true)
										Gagnant(player2.getName());

									if (board.squareFull(position.lv, position.x, position.y)) {
										remove = remove();
										if(remove != 0) {
											Emplacements.setRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
										}
									}
									if (remove == 0)
										switchPlayerTurn();
								}
							}

							// EGALITE
							if (!player1.isTurn() && player1.playableBall() == null)
								Gagnant(player2.getName());
							else if (!player2.isTurn() && player2.playableBall() == null)
								Gagnant(player1.getName());

						}
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("ATTENTION");
						alert.setHeaderText(null);
						alert.setContentText("THIS CASE IS NOT PLAYABLE");
						alert.show();
					}
				} else {
					Alert alert = new Alert(null, "Remove this ball?", yes, no);
					alert.setTitle("Confirmation Moves");
					Optional<ButtonType> result = alert.showAndWait();

					Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("ATTENTION");
					alert2.setHeaderText(null);

					if (result.get() == yes) {
						if (player1.canPlay()) {
							if (board.removeBall(player1, board, game3dBox, position.lv, position.x, position.y)) {
								updatePion("add");
								remove--;
								if (remove == 0) {
									Emplacements.resetRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
									switchPlayerTurn();
								}
								boule2D.setColor(Color.WHITE);
							} else {
								alert2.setContentText(
										"Erreur ! Veuillez bien vérifier s'il y a une boule au dessus ou si la boule est bien la votre");
								alert2.show();
							}
						} else {
							if (board.removeBall(player2, board, game3dBox, position.lv, position.x, position.y)) {
								updatePion("add");
								remove--;
								if (remove == 0) {
									Emplacements.resetRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
									switchPlayerTurn();
								}
								boule2D.setColor(Color.WHITE);
							} else {
								alert2.setContentText(
										"Erreur ! Veuillez bien vérifier s'il y a une boule au dessus ou si la boule est bien la votre");
								alert2.show();
							}
						}
					}
				}
			}
		});

		/*** 3D ***/
		emplacement.getXformBall().setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Color c = new Color(1, 1, 1, 0.7);
				if (board.playableCase(position.lv, position.x, position.y))
					emplacement.setBallColor(c);
			}

		});

		emplacement.getXformBall().setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Color c = new Color(1, 1, 1, 0.4);
				if (board.isEmpty(position.lv, position.x, position.y))
					emplacement.setBallColor(c);
			}

		});

		emplacement.getXformBall().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
				ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

				if (remove == 0) {
					if (board.playableCase(position.lv, position.x, position.y)) {
						Alert alert = new Alert(null, "Make this move?", yes, no);
						alert.setTitle("Confirmation Moves");
						Optional<ButtonType> result = alert.showAndWait();

						if (result.get() == yes) {

							// JOUEUR 1
							if (player1.canPlay()) {
								if (player1.placeBallOn(board, position.lv, position.x, position.y, game3dBox)) {
									boule2D.setColor(c1);
									Main.txa.appendText("\n");
									Main.txa.appendText(player1.getName() + " has placed a ball at [Lv : " + position.lv
											+ ", X : " + position.x + ", Y : " + position.y + "]");
									updatePion("remove");
									if (board.onTop() == true)
										Gagnant(player1.getName());

									if (board.squareFull(position.lv, position.x, position.y)) {
										remove = remove();
										if(remove != 0) {
											Emplacements.setRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
										}
									}

									if (remove == 0)
										switchPlayerTurn();
								}
							} else {

								if (player2.placeBallOn(board, position.lv, position.x, position.y, game3dBox)) {
									boule2D.setColor(c2);
									Main.txa.appendText("\n");
									Main.txa.appendText(player2.getName() + " has placed a ball at [Lv : " + position.lv
											+ ", X : " + position.x + ", Y : " + position.y + "]");
									updatePion("remove");
									if (board.onTop() == true)
										Gagnant(player2.getName());

									if (board.squareFull(position.lv, position.x, position.y)) {
										remove = remove();
										if(remove != 0) {
											Emplacements.setRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
										}
									}

									if (remove == 0)
										switchPlayerTurn();
								}
							}

							// EGALITE
							if (!player1.isTurn() && player1.playableBall() == null)
								Gagnant(player2.getName());
							else if (!player2.isTurn() && player2.playableBall() == null)
								Gagnant(player1.getName());

						}
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("ATTENTION");
						alert.setHeaderText(null);
						alert.setContentText("THIS CASE IS NOT PLAYABLE");
						alert.show();
					}
				}
			}
		});

	}

	/*
	 * Cette methode permet de créer des evenements sur les boules 3D (le mechanisme "retirer" du jeu)
	 */
	public void setBoule3DRemovable(int lv, int x, int y, Board board, Group game3dBox) {		
		boule3D.getXformBall().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
				ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
				
				if(remove != 0) {
					Alert alert = new Alert(null, "Remove this ball?", yes, no);
					alert.setTitle("Confirmation Moves");
					Optional<ButtonType> result = alert.showAndWait();

					Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("ATTENTION");
					alert2.setHeaderText(null);

					if (result.get() == yes) {
						if (player1.canPlay()) {
							if (board.removeBall(player1, board, game3dBox, position.lv, position.x, position.y)) {
								updatePion("add");
								remove--;
								if (remove == 0) {
									Emplacements.resetRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
									switchPlayerTurn();
								}
								Circle2D c = getBoule2D(position.lv, position.x, position.y);
								c.setColor(Color.WHITE);
							} else {
								alert2.setContentText(
										"Erreur ! Veuillez bien vérifier s'il y a une boule au dessus ou si la boule est bien la votre");
								alert2.show();
							}
						} else {
							if (board.removeBall(player2, board, game3dBox, position.lv, position.x, position.y)) {
								updatePion("add");
								remove--;
								if (remove == 0) {
									Emplacements.resetRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
									switchPlayerTurn();
								}
								Circle2D c = getBoule2D(position.lv, position.x, position.y);
								c.setColor(Color.WHITE);
							} else {
								alert2.setContentText(
										"Erreur ! Veuillez bien vérifier s'il y a une boule au dessus ou si la boule est bien la votre");
								alert2.show();
							}
						}
					}
				}
				
			}

		});
	}

	/*
	 * Cette methode permet de mettre à jour la liste des pions de chaque joueur.
	 */
	public void updatePion(String mode) {

		Group g = (Group) Main.balls_players.getChildren().get(0);
		ObservableList<Node> nodeOut = g.getChildren();

		playSound();

		if (mode == "remove") {
			for (Node node : nodeOut) {
				if (node instanceof Circle) {
					Circle2D c = new Circle2D(20.0f);
					c.setCircle((Circle) node);
					if (mode == "remove") {
						if (player1.canPlay() && c.getCircle().getFill() == Main.c1) {
							c.setColor(Color.WHITE);
							break;
						}

						if (player2.canPlay() && c.getCircle().getFill() == Main.c2) {
							c.setColor(Color.GHOSTWHITE);
							break;
						}
					}
				}
			}
		} else {
			for (int i = nodeOut.size() - 1; i >= 0; i--) {
				Node n = nodeOut.get(i);
				if (n instanceof Circle) {
					Circle2D c = new Circle2D(20.0f);
					c.setCircle((Circle) n);
					if (player1.canPlay() && c.getCircle().getFill() == Color.WHITE) {
						c.setColor(Main.c1);
						break;
					}

					if (player2.canPlay() && c.getCircle().getFill() == Color.GHOSTWHITE) {
						c.setColor(Main.c2);
						break;
					}
				}
			}
		}
	}

	/*
	 * Cette methode permet d'initialiser si cette boule est sur le plateau ou non
	 */
	public void setOnBoard(boolean b) {
		this.isOnBoard = b;
	}

	/*
	 * Cette methode permet de retirer une boule 3D
	 */
	public void removeOnGroup3D(Group group) {
		Circle3D b = this.boule3D;
		Platform.runLater(new Thread(new Runnable() {
			@Override
			public void run() {
				group.getChildren().remove(b.getXformBall());
			}
		}));
	}

	/*
	 * Cette methode permet de placer une boule 3D
	 */
	public void placeOnGroup3D(Group group) {
		Circle3D b = this.boule3D;
		
		if(player1.canPlay() && b.getColor() == Boule.c3) {
			b.setBallColor(Main.c1);
		}
		
		if(!player1.canPlay() && b.getColor() == Boule.c3) {
			b.setBallColor(Main.c2);
		}
		
		Platform.runLater(new Thread(new Runnable() {
			@Override
			public void run() {
				group.getChildren().add(b.getXformBall());
			}
		}));
	}

	/*
	 * Cette methode permet de retourner la position de cette boule.
	 */
	public Position getPlace() {
		return this.position;
	}

	/*
	 * Cette methode permet de placer une boule 3D
	 */
	public void place3D(int lv, int x, int y,Board b, Group g) {
		if (lv == 3)
			setTranslate3D(120 - (80 * y), 0, 120 - (80 * x));
		if (lv == 2)
			setTranslate3D(80 - (80 * y), 40, 80 - (80 * x));
		if (lv == 1)
			setTranslate3D(40 - (80 * y), 80, 40 - (80 * x));
		if (lv == 0)
			setTranslate3D(0, 120, 0);
		
		setBoule3DRemovable(lv,x,y,b,g);
		
	}
	
	/*
	 * Cette methode permet de placer une boule 3D
	 */
	public void setTranslate3D(int x, int y, int z) {
		boule3D.setTranslate("x", x);
		boule3D.setTranslate("y", y);
		boule3D.setTranslate("z", z);
	}

	/*
	 * Cette methode permet d'alterner le joueur, de placer les boules fantomes et de réinitialiser les boules à retirer
	 */
	public void switchPlayerTurn() {
		pauseGame();
		Emplacements.setAvailablePositions(Main.board, Main.game3dBox);
		Emplacements.resetRemovablePositions(Main.board, Main.game3dBox, player1.canPlay() ? player1 : player2);
		seconds = STARTTIME;
		if (player1.canPlay()) {
			if(!Main.modeNormal) {
				startTimerJ2();
			}else {
				Main.timer2.setTextFill(Color.RED);
				Main.timer1.setTextFill(Color.BLACK);
			}
			
			Main.txa.appendText("\n");
			Main.txa.appendText(player2.getName() + "'s turn to place a ball");
			player1.setTurn(false);
			player2.setTurn(true);
		} else {
			if(!Main.modeNormal) {
				startTimerJ1();
			}else {
				Main.timer2.setTextFill(Color.BLACK);
				Main.timer1.setTextFill(Color.RED);
			}
			
			Main.txa.appendText("\n");
			Main.txa.appendText(player1.getName() + "'s turn to place a ball");
			player2.setTurn(false);
			player1.setTurn(true);
		}
	}
	
	/*
	 * Cette methode permet de faire une pause sur le timer
	 */
	public void pauseGame() {
		Main.pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				timeline1.pause();
				timeline2.pause();
				// changer le comportement du bouton
				event.consume();

				// les boutons
				ButtonType yes = new ButtonType("Resume", ButtonBar.ButtonData.OK_DONE);
	
				// show close dialog
				Alert alert = new Alert(null, "Game is currently paused, please click resume to unpause", yes);
				alert.setTitle("GAME PAUSED");


				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == yes) {
					timeline1.play();
					timeline2.play();
				}
			}
		});
	}
	
	/*
	 * Cette methode permet de vérifier et d'afficher si un joueur a gagné
	 */
	public void Gagnant(String player) {
		ButtonType yes = new ButtonType("OK !", ButtonBar.ButtonData.OK_DONE);

		Alert alert = new Alert(null, "Félicitations " + player + ", tu as gagné", yes);
		alert.setTitle("GAGNANT");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yes) {
			System.exit(0);
		}
	}

	/*
	 * Cette methode permet de savoir si combien de boules à retirer (choisi par le joueur)
	 */
	public int remove() {
		ButtonType button0 = new ButtonType("0 boule", ButtonBar.ButtonData.OK_DONE);
		ButtonType button1 = new ButtonType("1 boule", ButtonBar.ButtonData.OK_DONE);
		ButtonType button2 = new ButtonType("2 boules", ButtonBar.ButtonData.OK_DONE);
		int resultat = 0;
		Alert alert = new Alert(null, "Combien de boule(s) voulez-vous retirer?", button0, button1, button2);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == button0)
			return resultat;
		else if (result.get() == button1)
			return resultat = 1;
		else
			return resultat = 2;
	}

	/*
	 * Cette methode permet de commencer le timer de joueur 1
	 */
	public void startTimerJ1() {

		timeline1.setCycleCount(Timeline.INDEFINITE);
		KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Main.timer1.setText(seconds.toString());
				Main.timer1.setTextFill(Color.RED);
				
				if (player2.canPlay()) {
					timeline1.stop();
					Main.timer1.setText(STARTTIME.toString());
					Main.timer1.setTextFill(Color.BLACK);
				}
				if (seconds == 0) {
					actRandomly();
					switchPlayerTurn();
				}
				seconds--;
			}
		});

		timeline1.getKeyFrames().add(frame);
		timeline1.playFromStart();
	}

	/*
	 * Cette methode permet de commencer le timer de joueur 2
	 */
	public void startTimerJ2() {
		
		timeline2.setCycleCount(Timeline.INDEFINITE);
		KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Main.timer2.setText(seconds.toString());
				Main.timer2.setTextFill(Color.RED);
				
				if (player1.canPlay()) {
					timeline2.stop();
					Main.timer2.setText(STARTTIME.toString());
					Main.timer2.setTextFill(Color.BLACK);
				}
				if (seconds == 0) {
					actRandomly();
					switchPlayerTurn();
				}
				--seconds;
			}
		});

		timeline2.getKeyFrames().add(frame);
		timeline2.playFromStart();
	}

	/*
	 * Cette methode permet de jouer le son
	 */
	private void playSound() {
		if (Main.sounds) {
			//String musicFile = "res/sounds/pions.mp3"; // For example

			Media sound = new Media(this.getClass().getResource("pions.mp3").toExternalForm());
			MediaPlayer mediaPlayer = new MediaPlayer(sound);
			mediaPlayer.play();
		}
	}

	/*
	 * Cette methode permet de jouer aléatoirement si le timer est à 0.
	 */
	public void actRandomly() {
		ArrayList<Position> possibleCase = new ArrayList<Position>();

		for (int x = 0; x < 4; x++) {
			for (int i = 0; i <= x; i++) {
				for (int j = 0; j <= x; j++) {
					if (board.playableCase(x, i, j)) {
						possibleCase.add(new Position(x, i, j));
					}
				}
			}
		}

		/* CHOSE RANDOM POSITION FROM POSSIBLE CASE */
		Random rand = new Random();
		int i = rand.nextInt(possibleCase.size());
		Position randomPos = possibleCase.get(i);

		/**/
		
		if(remove==0) {
			if (player1.canPlay()) {
				player1.placeBallOn(board, randomPos.lv, randomPos.x, randomPos.y, game3dBox);
				Main.txa.appendText("\n");
				Main.txa.appendText(player1.getName() + " has RANDOMLY placed a ball at [Lv : " + randomPos.lv
						+ ", X : " + randomPos.x + ", Y : " + randomPos.y + "]");
				updatePion("remove");
				Circle2D c = getBoule2D(randomPos.lv, randomPos.x, randomPos.y);
				c.setColor(c1);
			} else {
				player2.placeBallOn(board, randomPos.lv, randomPos.x, randomPos.y, game3dBox);
				Main.txa.appendText("\n");
				Main.txa.appendText(player2.getName() + " has RANDOMLY placed a ball at [Lv : " + randomPos.lv
						+ ", X : " + randomPos.x + ", Y : " + randomPos.y + "]");
				updatePion("remove");
				Circle2D c = getBoule2D(randomPos.lv, randomPos.x, randomPos.y);
				c.setColor(c2);
			}
		}else {
			remove = 0;
		}
		
		

		/* CHOSE IF SWITCH PLAYER HERE OR IN MAIN */

	}

	/*
	 * Cette methode permet de placer une boule fantome
	 */
	public void placeOnGroupEmplacement(Group group) {
		Circle3D b = this.emplacement;
		Platform.runLater(new Thread(new Runnable() {
			@Override
			public void run() {
				group.getChildren().remove(b.getXformBall());
				group.getChildren().add(b.getXformBall());
			}
		}));
	}
	

	/*
	 * Cette methode permet de placer une boule fantome
	 */
	public void place3DEmplacement(int lv, int x, int y) {
		if (lv == 3)
			setTranslate3DEmplacement(120 - (80 * y), 0, 120 - (80 * x));
		if (lv == 2)
			setTranslate3DEmplacement(80 - (80 * y), 40, 80 - (80 * x));
		if (lv == 1)
			setTranslate3DEmplacement(40 - (80 * y), 80, 40 - (80 * x));
		if (lv == 0)
			setTranslate3DEmplacement(0, 120, 0);
	}

	/*
	 * Cette methode permet de placer une boule fantome
	 */
	public void setTranslate3DEmplacement(int x, int y, int z) {
		emplacement.setTranslate("x", x);
		emplacement.setTranslate("y", y);
		emplacement.setTranslate("z", z);
	}

	/*
	 * Cette methode retourne une boule 2D à partir des coordonées et de niveau
	 */
	public Circle2D getBoule2D(int lv, int x, int y) {
		Group g = (Group) Main.sub2d.getChildren().get(0);
		ObservableList<Node> nodeOut = g.getChildren();

		for (Node node : nodeOut) {
			if (node instanceof Circle) {
				Circle2D c = new Circle2D(20.0f);
				c.setCircle((Circle) node);

				if (lv == 3) {
					if (c.getCircle().getCenterX() == (y * 41) && c.getCircle().getCenterY() == (lv * 113 + x * 41)) {
						return c;
					}
				} else if (lv == 0) {
					if (c.getCircle().getCenterX() == (y * 41) && c.getCircle().getCenterY() == (40))
						return c;

				} else if (c.getCircle().getCenterX() == (y * 41) && c.getCircle().getCenterY() == (lv * 100 + x * 41))
					return c;
			}
		}
		return null;

	}

	/*
	 * Cette methode permet d'enlever une boule fantome
	 */
	public void removeOnGroupEmplacement(Group group) {
		Circle3D b = this.emplacement;
		Platform.runLater(new Thread(new Runnable() {
			@Override
			public void run() {
				group.getChildren().remove(b.getXformBall());
			}
		}));
	}
}

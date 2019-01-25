package application;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.ButtonBar;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import moleculesampleapp.Xform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Effect;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

	static String joueur1;
	static String joueur2;

	static Color c1;
	static Color c2;

	static TextArea txa = new TextArea();

	private final Integer STARTTIME = 30;
	private Integer seconds = STARTTIME;
	Label timerJoueur1 = new Label();
	static Label timer1 = new Label();
	Label timerJoueur2 = new Label();
	static Label timer2 = new Label();


	/* Classic */
	private GridPane gridPane = new GridPane();
	static BorderPane sub2d = new BorderPane();
	private Group sub3d = new Group();
	static Group game3dBox = new Group();
	static BorderPane balls_players = new BorderPane();
	private ScrollPane log_history = new ScrollPane();
	static boolean modeNormal;

	/* CAMERA */
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform1 = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	final double cameraDistance = 1000;
	public static final int CAM_ANGLE = 80;
	public static final double CAM_ROTATEZ = 180.0;
	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;
	private double mouseDeltaX;
	private double mouseDeltaY;

	/* GAME */

	static Board board = new Board();

	/* MUSIC */
	
	static Button pauseBtn;
	static boolean sounds = true;
	String musicFile = "fond.mp3";
	Media sound = new Media(this.getClass().getResource("fond.mp3").toExternalForm());
	MediaPlayer mediaPlayer = new MediaPlayer(sound);


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		try {
			
			// initialiser les "panes" pour le menu principal et le jeu
			BorderPane root = new BorderPane();
			BorderPane rootMenu = new BorderPane();
			rootMenu.setId("rootMenu");
			rootMenu.setCenter(gridPane);
			Scene sceneMenu = new Scene(rootMenu, 900, 700);
			Scene scene = new Scene(root, 1000, 720);

			
			// intialiser le menuBar du jeu (options, theme, help)
			MenuBar menuBar = new MenuBar();
			Menu options = new Menu("Options");
			Menu theme = new Menu("Theme");
			RadioMenuItem dark = new RadioMenuItem("Dark");
			RadioMenuItem light = new RadioMenuItem("Light");
			CheckMenuItem sound = new CheckMenuItem("Sound");
			
			
			// lancer le son lorsqu'on clique sur le menu item Sound ou le desactiver s'il est deja actif
			sound.setOnAction(t -> {
				if(sound.isSelected()) {
					sounds = true;
					handleMusic();
				} else {
					sounds = false;
					handleMusic();
				}
			});
			
			
			// gerer le theme du jeu avec le stylesheet css qu'on importe
			String darkTheme = getClass().getResource("dark.css").toExternalForm();
			String lightTheme = getClass().getResource("light.css").toExternalForm();	
			sceneMenu.getStylesheets().add(lightTheme);
			light.setSelected(true);
			ToggleGroup toggleGroup = new ToggleGroup();
			dark.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			    	sceneMenu.getStylesheets().remove(lightTheme);
					sceneMenu.getStylesheets().add(darkTheme);
			    }
			});
			dark.setToggleGroup(toggleGroup);
			light.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			    	sceneMenu.getStylesheets().remove(darkTheme);
					sceneMenu.getStylesheets().add(lightTheme);
			    }
			});
			light.setToggleGroup(toggleGroup);
			theme.getItems().addAll(dark, light);
			options.getItems().addAll(sound);
			
			

			Menu help = new Menu("Help");
			MenuItem rules = new MenuItem("Rules");
			help.getItems().addAll(rules);
			menuBar.getMenus().addAll(options, theme, help);
			rootMenu.setTop(menuBar);

			
			// configurer le gridPane pour afficher le menu principal du jeu
			gridPane.setAlignment(Pos.CENTER);
			gridPane.setPadding(new Insets(20, 150, 150, 100));
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			ColumnConstraints columnOneConstraints = new ColumnConstraints(150, 150, Double.MAX_VALUE);
			columnOneConstraints.setHalignment(HPos.RIGHT);
			ColumnConstraints columnTwoConstrains = new ColumnConstraints(150, 150, Double.MAX_VALUE);
			columnTwoConstrains.setHgrow(Priority.ALWAYS);
			gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
			
			
			// afficher la regle lorsqu'on clique sur le MenuItem "Rules" dans "Help"
			rules.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent event) {
			    		BorderPane gp = new BorderPane();
			            Stage stage = new Stage();
			            Label headerRules = new Label("rules");
			            headerRules.setFont(Font.font("Avenir", FontWeight.BOLD, 100));
			            headerRules.setTranslateX(120);
			            gp.setTop(headerRules);
			            final String WORDS =
			            		"- Each player starts off with 15 balls. \n" +
			            			    "- Players take turns taking pieces from their reserve pile, and placing \n them on a 4x4 game board made up of 16 indentations. \n" +
			            			    "- When four pieces are placed next to each other in a square, one \n piece can be put on top of the square. \n" +
			            			    "- If the square is completed with all the same color, the player of that \n color may take one of their own pieces from the board (one that is not \n supporting anything) and put it back into their reserve pile. \n" +
			            			    "- If a player makes a square that is composed of mixed pieces, they \n may automatically put one of their pieces on top of the square. \n" +
			            			    "- At the end of the game, the game board should have 4 levels. \n" +
			            			    "- The first level with 16 pieces, the second level with 9 pieces, the \n third level with 4 pieces, and the fourth level with 1 piece. \n" +
			            			    "- A player wins if they put the last piece on the 4th level, or if the other \n player runs out of pieces to play.\n"
			            			    ;
			            Label lblRules = new Label(WORDS);
			            lblRules.setTranslateY(-30);  
			            gp.setCenter(lblRules);
			            stage.setTitle("RULES");
			            stage.setScene(new Scene(gp, 450, 450));
			            stage.show();
			        }
			});
			
			
			// afficher le logo APS LABS dans le menu principal en bas a droite
			Image image = new Image(getClass().getResourceAsStream("apslabs.png"));
			ImageView logo = new ImageView(image);
			logo.setTranslateY(-130);
			logo.setTranslateX(700);
			logo.setFitWidth(100);
	        logo.setPreserveRatio(true);
	        logo.setSmooth(true);
	        logo.setCache(true);
			rootMenu.setBottom(logo);
			
			
			// afficher le titre "PYLOS" dans le menu principal
			Label headerLabel = new Label("pylos");
			headerLabel.setId("title");
			headerLabel.setFont(Font.font("Avenir", FontWeight.BOLD, 150));
			gridPane.add(headerLabel, 1, 0, 1, 1);
			GridPane.setHalignment(headerLabel, HPos.CENTER);
			GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));

			
			// afficher le mot "PLAYER 1" dans le menu principal
			Label nameJoueur1 = new Label("PLAYER 1 :");
			nameJoueur1.setId("player1");
			gridPane.add(nameJoueur1, 0, 1);

			// afficher le champ de texte pour specifier le nom de PLAYER 1
			TextField nameJoueur1Field = new TextField();
			nameJoueur1Field.setId("namePlayer1");
			nameJoueur1Field.setPrefHeight(40);
			gridPane.add(nameJoueur1Field, 1, 1);

			// afficher le mot "PLAYER 2" dans le menu principal
			Label nameJoueur2 = new Label("PLAYER 2 :");
			nameJoueur2.setId("player2");
			gridPane.add(nameJoueur2, 0, 2);

			// afficher le champ de texte pour specifier le nom de PLAYER 2
			TextField nameJoueur2Field = new TextField();
			nameJoueur2Field.setId("namePlayer2");
			nameJoueur2Field.setPrefHeight(40);
			gridPane.add(nameJoueur2Field, 1, 2);

			// afficher le bouton pour choisir le couleur des pions du PLAYER 1 et l'assigner
			ColorPicker colorPicker1 = new ColorPicker();
			colorPicker1.setId("colorPlayer1");
			colorPicker1.getStyleClass().add("button");
			colorPicker1.setPrefWidth(30);
			gridPane.add(colorPicker1, 2, 1);
			colorPicker1.setOnAction(t -> {
				c1 = colorPicker1.getValue();
				System.out.println(c1);
			});

			// afficher le bouton pour choisir le couleur des pions du PLAYER 2 et l'assigner
			ColorPicker colorPicker2 = new ColorPicker();
			colorPicker2.setId("colorPlayer2");
			colorPicker2.getStyleClass().add("button");
			colorPicker2.setPrefWidth(30);
			gridPane.add(colorPicker2, 2, 2);
			colorPicker2.setOnAction(t -> {
				c2 = colorPicker2.getValue();
			});
			
			
			// afficher le bouton quit pour quitter le jeu
			Button quitButton = new Button("QUIT");
			quitButton.setPrefHeight(40);
			quitButton.setPrefWidth(100);
			gridPane.add(quitButton, 1, 4, 2, 1);
			quitButton.setId("quitBtn");
			
			// gerer le fonctionnement du bouton quit
			quitButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					// changer le comportement du bouton
					event.consume();

					// les boutons yes et no
					ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
					ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

					// afficher la dialogue pour fermer
					Alert alert = new Alert(null, "Are you sure?", yes, no);
					alert.setTitle("Confirmation");
					alert.initOwner(stage);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == yes) {
						System.exit(0);
					}
				}
			});
			
			
			// afficher le bouton play pour lancer une partie
			Button playButton = new Button("PLAY");
			playButton.setPrefHeight(40);
			playButton.setPrefWidth(100);
			gridPane.add(playButton, 0, 4, 2, 1);
			GridPane.setHalignment(playButton, HPos.RIGHT);
			GridPane.setMargin(playButton, new Insets(20, 0, 20, 0));
			playButton.setId("playBtn");
			
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					
					// initialiser le nom de 2 joueurs 
					if (nameJoueur1Field.getText().isEmpty()) {
						joueur1 = "joueur1";
					} else {
						joueur1 = nameJoueur1Field.getText();
					}

					if (nameJoueur2Field.getText().isEmpty()) {
						joueur2 = "joueur2";
					} else {
						joueur2 = nameJoueur2Field.getText();
					}

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("ATTENTION");
					alert.setHeaderText(null);
					
					// verifier si le nom a bien suivi la norme
					if (!checkNameIfValide(joueur1) || !checkNameIfValide(joueur2)) {

						// ALERT
						alert.setContentText(
								"Names must not exceed 15 characters and must not contain Special Characters");
						alert.show();

						if (!checkNameIfValide(joueur1)) {
							nameJoueur1Field.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
						} else {
							nameJoueur1Field.setStyle("-fx-text-box-border: grey ; -fx-focus-color: grey ;");
						}
						if (!checkNameIfValide(joueur2)) {
							nameJoueur2Field.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
						} else {
							nameJoueur2Field.setStyle("-fx-text-box-border: grey ; -fx-focus-color: grey ;");
						}

						return;
						
						// verification des couleurs et affectation de couleur pour chaque joueur par defaut
					} else {
						if (c1 == null) {
							c1 = Color.INDIANRED;
						}

						if (c2 == null) {
							c2 = Color.CORNFLOWERBLUE;
						}

						if (c1.toString().equalsIgnoreCase(c2.toString())) {
							alert.setContentText("Colors must not be the same for 2 players");
							alert.show();
							return;
						}
						
						/* END */
						
						// afficher l'historique (log) du jeu
						txa.setPrefColumnCount(40);
						txa.setPrefRowCount(2);
						txa.setEditable(false);
						log_history.setContent(txa);
						txa.textProperty().addListener(new ChangeListener<Object>() {
							@Override
							public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
								txa.setScrollTop(Double.MAX_VALUE); // this will scroll to the bottom
								// use Double.MIN_VALUE to scroll to the top
							}
						});
						txa.appendText("The game has been started.");
						
						root.setTop(menuBar);
						
						// afficher le timer pour les 2 joueurs en bas à droite
						VBox box1 = new VBox();
						timerJoueur1.setText(joueur1);
						timerJoueur1.setTextFill(c1);
						timerJoueur1.setStyle("-fx-font-weight: bold;");
						timer1.setText(seconds.toString());
						timer1.setTextFill(Color.BLACK);
						timer1.setStyle("-fx-font-size: 2em;");
						box1.setAlignment(Pos.CENTER);
						box1.getChildren().addAll(timerJoueur1, timer1);

						VBox box2 = new VBox();
						timerJoueur2.setText(joueur2);
						timerJoueur2.setTextFill(c2);
						timerJoueur2.setStyle("-fx-font-weight: bold;");
						timer2.setText(seconds.toString());
						timer2.setTextFill(Color.BLACK);
						timer2.setStyle("-fx-font-size: 2em;");
						box2.setAlignment(Pos.CENTER);
						box2.getChildren().addAll(timerJoueur2, timer2);

						// initialiser l'affichage 3D du jeu
						setGame3d();
						SubScene game3d = new SubScene(sub3d, 700, 500, true, SceneAntialiasing.BALANCED);
						game3d.setCamera(camera);
						sub3d.getChildren().add(game3dBox);

						// les pions qui sont affiche en bas
						setBalls(c1, c2);

						// HBox en bas
						HBox logTimer = new HBox();
						logTimer.setSpacing(30);
						logTimer.getChildren().addAll(log_history, box1, box2);
						
						// afficher le bouton pour pauser le jeu
						pauseBtn = new Button("PAUSE");
						pauseBtn.setAlignment(Pos.CENTER_RIGHT);
						pauseBtn.setStyle("-fx-background-color: #000000; -fx-font-size: 2em; -fx-text-fill: #ffffff");
						pauseBtn.setMaxSize(500, 40);
						
						VBox box = new VBox();
						
						box.setSpacing(20);
						box.getChildren().add(game3d);
						box.getChildren().add(balls_players);
						box.getChildren().add(logTimer);

						box.setTranslateX(-30);
						root.setRight(box);

						// initialiser l'affichage 2D du jeu
						VBox boxLeft = new VBox();
						setGame2d(balls_players);
						sub2d.setStyle("-fx-border-width: 1; -fx-background-color: darkgray; -fx-padding: 100 0 0 0;");
						SubScene game2d = new SubScene(sub2d, 230, 600, true, SceneAntialiasing.BALANCED);
						
						
						boxLeft.setSpacing(20);
						boxLeft.getChildren().add(game2d);
						boxLeft.getChildren().add(pauseBtn);

						root.setLeft(boxLeft);

						// initialiser le camera et son fonctionnement
						setCamera();
						handleMouse(game3d, game3dBox);
						
						// demander aux joueurs s'ils veulent jouer en mode Normal ou avec le Timer
						ButtonType yes = new ButtonType("Normal Mode", ButtonBar.ButtonData.OK_DONE);
						ButtonType no = new ButtonType("Timed Mode", ButtonBar.ButtonData.CANCEL_CLOSE);
						Alert alert2 = new Alert(null, "Voulez vous jouer en mode Normal? (sans limite du temps)", yes, no);
						alert.setTitle("Mode");
						Optional<ButtonType> result = alert2.showAndWait();
						if(result.get() == yes) {
							modeNormal = true;
							timer1.setText("\u221E");
							timer1.setTextFill(Color.RED);
							timer2.setText("\u221E");
						}else {
							modeNormal = false;
						}

						stage.setScene(scene);
						stage.show();

					}

				}

			});

			stage.setScene(sceneMenu);
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 Cette methode va initialiser l'affichage des pions actuels restants de 2 joueurs
	 */
	private void setBalls(Color c1, Color c2) {
		Group balls_player2 = new Group();
		for (int i = 0; i < 15; i++) {
			Circle2D circle = new Circle2D(20.0f);
			circle.setStroke(Color.BLACK, 1);
			circle.setColor(c1);
			circle.getCircle().setTranslateX(i * 41);
			balls_player2.getChildren().add(circle.getCircle());

			Circle2D circle2 = new Circle2D(20.0f);
			circle2.setStroke(Color.BLACK, 1);
			circle2.setColor(c2);
			circle2.getCircle().setTranslateX(i * 41);
			circle2.getCircle().setTranslateY(50);
			balls_player2.getChildren().add(circle2.getCircle());
		}
		balls_players.setCenter(balls_player2);
		balls_players.setTranslateY(5);
	}

	/*
	 Cette methode va initialiser l'affichage du jeu en 2D, c'est-à-dire la grrile 2D à gauche 
	 */
	public void setGame2d(BorderPane boules) {
		Group circles = new Group();

		circles.setTranslateX(30);

		int i = 0;

		for (int lv = 0; lv < 4; lv++) {
			for (int x = 0; x <= lv; x++) {
				for (int y = 0; y <= lv; y++) {
					Circle player_ball = new Circle(20.0f);
					player_ball.setFill(Color.GREEN);
					player_ball.setTranslateX(45 * i++);

					Circle2D circle = new Circle2D(20.0f);
					circle.setStroke(Color.BLACK, 1);
					circle.setColor(Color.WHITE);

					if (lv == 3) {
						circle.setPosY(lv * 113 + x * 41);
						circle.setPosX(y * 41);
					} else if (lv == 0) {
						circle.setPosY(40);
						circle.setPosX(y * 41);
					}

					else {
						circle.setPosY(lv * 100 + x * 41);
						circle.setPosX(y * 41);
					}

					circles.getChildren().add(circle.getCircle());

					Boule boule = new Boule(joueur1, joueur2, board, lv, x, y, game3dBox, c1, c2);
					boule.setBoule(circle);

				}
			}
		}

		sub2d.setLeft(circles);
	}
	
	/*
	 Cette methode va initialiser le CAMERA que nous utilisons pour voir l'affichage jeu 3D sous differents angles
	 */
	public void setCamera() {
		camera.setNearClip(0.1);
		camera.setFarClip(1500.0);
		camera.setTranslateZ(-cameraDistance);
		cameraXform3.getChildren().add(camera);
		cameraXform3.setRotateZ(CAM_ROTATEZ);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform1.getChildren().add(cameraXform2);
		cameraXform1.rx.setAngle(CAM_ANGLE);
	}

	/*
	 Cette methode va initialiser l'affichage du jeu en 3D
	 */
	public void setGame3d() {
		final PhongMaterial plateau = new PhongMaterial();
		plateau.setDiffuseMap(new Image(getClass().getResourceAsStream("wood.png")));
		// Game board
		Box box = new Box(340, 10, 350);
		box.setMaterial(plateau);

		Xform boxXform = new Xform();
		boxXform.getChildren().add(box);
		boxXform.setTranslateY(-40);

		// Game balls
		game3dBox.getChildren().add(boxXform);
		game3dBox.getChildren().add(cameraXform1);
	}
	
	/*
	 Cette methode va gerer le fonctionnement de souris quand on clique sur le plateau 3D
	 */
	private void handleMouse(SubScene subScene3d, final Node root) {
		subScene3d.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		subScene3d.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;
				double modifierFactor = 0.1;

				if (me.isControlDown()) {
					modifier = 0.1;
				}
				if (me.isShiftDown()) {
					modifier = 10.0;
				}
				if (me.isPrimaryButtonDown()) {
					cameraXform1.ry
							.setAngle(cameraXform1.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0); // +
					cameraXform1.rx
							.setAngle(cameraXform1.rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0); // -
				} else if (me.isSecondaryButtonDown()) {
					double z = camera.getTranslateZ();
					double newZ = z + mouseDeltaX * modifierFactor * modifier;
					camera.setTranslateZ(newZ);
				} else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
				}
			}
		});
	}

	
	/*
	 Cette methode va lancer le son ou l'arreter s'il est deja actif 
	 */
	private void handleMusic() {
		if (sounds) {
			mediaPlayer.play();

		} else {
			mediaPlayer.stop();

		}
	}
	
	
	/*
	 Cette methode va verifier si le nom de joueur 1 et 2 suit la norme
	 */
	public boolean checkNameIfValide(String nom) {
		Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(nom);

		if (nom.length() > 15) {
			return false;
		}

		if (m.find()) {
			return false;
		}
		return true;
	}

}

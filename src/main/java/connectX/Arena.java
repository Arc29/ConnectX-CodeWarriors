package connectX;

import java.util.ResourceBundle;
import java.net.URL;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import java.util.Random;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Scanner;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.input.MouseEvent;
import com.jfoenix.controls.JFXCheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.util.Duration;

public class Arena implements Initializable
{
    Thread gameThread;
    @FXML
    private StackPane gameArea;
    @FXML
    private ImageView back;
    @FXML
    private ImageView avishkarImageView;
    @FXML
    private JFXButton initButton;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXButton pauseButton;
    @FXML
    private JFXButton stopButton;

    @FXML
    private ListView historyListView;
    @FXML
    private JFXListView<String> logListView;
    @FXML
    private GridPane gameGrid;
    private ImageView[][] boardStackPanes;
    @FXML
    private JFXCheckBox gridCheckBox;
    @FXML
    private Canvas gameCanvas;

    public Arena() {

    }

    @FXML
    void slideIn(ActionEvent event) throws IOException{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/welcomeDash.fxml"));
        Parent root= loader.load();
        WelcomeDash dash= loader.getController();
        dash.setInitButton(initButton);
        root.prefHeight(600);
        root.prefWidth(510);
        root.translateXProperty().set(-510);
        root.translateYProperty().set(20);

        gameArea.getChildren().add(root);

        initButton.setDisable(true);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateXProperty(), 55, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);

        timeline.play();
    }



    @FXML
    void start(final ActionEvent event) {
        if (!Players.getPlayers().player1Ready) {
            this.log("P1 isn't ready");
            return;
        }
        if (!Players.getPlayers().player2Ready) {
            this.log("P2 isn't ready");
            return;
        }
        if (Main.paused) {
            Main.paused = !Main.paused;
            this.pauseButton.setDisable(false);
            this.startButton.setDisable(true);
            return;
        }
        if (this.gameThread == null) {
            this.gameThread = new Thread(new Game(this, Constants.Player.P1));
            Main.running = true;
            Main.paused = false;
            this.startButton.setDisable(true);
            this.pauseButton.setDisable(false);
            this.stopButton.setDisable(false);
            this.gameThread.start();
        }
    }

    @FXML
    void pause(final ActionEvent event) {
        if (this.gameThread == null) {
            return;
        }
        Main.paused = !Main.paused;
        if (Main.paused) {
            this.log("Game paused");
            this.pauseButton.setDisable(true);
            this.startButton.setDisable(false);
        }
    }

    @FXML
    void stop(final ActionEvent event) throws InterruptedException {
        if (this.gameThread == null) {
            return;
        }
        Main.running = false;
        Main.paused = false;
        this.gameThread = null;
        Thread.sleep(300L);
        this.clearBoard();
        this.startButton.setDisable(false);
        this.pauseButton.setDisable(true);
        this.stopButton.setDisable(true);
    }

    void log(final String s) {
        Platform.runLater(() -> this.logListView.getItems().add(0, s));
    }
//
//    void move(final String s, final Constants.Player curr) {
//        final Label str = new Label(s);
//        if (curr == Constants.Player.WHITE) {
//            str.setStyle("-fx-text-fill: GREEN");
//        }
//        else {
//            str.setStyle("-fx-text-fill: BLUE");
//        }
//        Platform.runLater(() -> this.historyListView.getItems().add(0, (Object)str));
//    }


    void updateBoard(final int x, final int y, final Constants.State state) {
        if (!Game.inRange(x, y)) {
            return;
        }
        Pane pane=(Pane)(this.gameGrid.getChildren().get(y*9+x));
//        System.out.println(state);
        Platform.runLater(() -> {
            switch (state) {

                case P1: {
                    ((ImageView)(pane.getChildren().get(0))).setImage(new Image(getClass().getResourceAsStream("/images/p1.gif")));
                    break;
                }
                case P2: {
                    ((ImageView)(pane.getChildren().get(0))).setImage(new Image(getClass().getResourceAsStream("/images/p2.gif")));
                    break;
                }
                default:
                    ((ImageView)(pane.getChildren().get(0))).setImage(null);
            }
        });
    }



    @FXML
    void close(final MouseEvent event) {
        final Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    void minimize(final MouseEvent event) {
        final Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.toBack();
    }

    public void initialize(final URL location, final ResourceBundle resources) {
//        Pane pane=(Pane)(this.gameGrid.getChildren().get(9*5+4));
//        ((ImageView)(pane.getChildren().get(0))).setImage(new Image(getClass().getResourceAsStream("/images/p1.gif")));
//        Animation animation=createPathAnimation(0,0,3,1,Color.RED);
//        animation.play();
//        animation.setOnFinished(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                clearCanvas();
//            }
//        });
    }

    void clearCanvas(){
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0,0,gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    void clearBoard() {
        Platform.runLater(() -> {
            for (int i = 0; i < Constants.rows; ++i) {
                for (int j = 0; j < Constants.col; ++j) {
                    this.updateBoard(i, j, Constants.State.EMPTY);
                }
            }
            this.logListView.getItems().clear();
            this.clearCanvas();
        });
    }
    private Animation createPathAnimation(int xi,int yi,int xj,int yj,Color col) {

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // move a node along a path. we want its position
        Circle pen = new Circle(0, 0, 16);

        // create path transition
        TranslateTransition pathTransition = new TranslateTransition(Duration.seconds(3),pen);
        pathTransition.setFromX(62+yi*125);
        pathTransition.setFromY(43+xi*86);
        pathTransition.setToX(62+yj*125);
        pathTransition.setToY(43+xj*86);
        pathTransition.currentTimeProperty().addListener( new ChangeListener<Duration>() {

            Location oldLocation = null;

            /**
             * Draw a line from the old location to the new location
             */
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                // skip starting at 0/0
                if( oldValue == Duration.ZERO)
                    return;

                // get current location
                double x = pen.getTranslateX();
                double y = pen.getTranslateY();

                // initialize the location
                if( oldLocation == null) {
                    oldLocation = new Location();
                    oldLocation.x = x;
                    oldLocation.y = y;
                    return;
                }

                // draw line
                gc.setStroke(col);
                gc.setFill(Color.YELLOW);
                gc.setLineWidth(16);
                gc.strokeLine(oldLocation.x, oldLocation.y, x, y);

                // update old location with current one
                oldLocation.x = x;
                oldLocation.y = y;
            }
        });

        return pathTransition;
    }
    static class Location {
        double x;
        double y;
    }

    public void playerWin(int player,int xi,int yi,int xj,int yj){
        Animation animation=createPathAnimation(xi,yi,xj,yj,(player== 1)?Color.RED:Color.BLUE);
        animation.play();
    }

}

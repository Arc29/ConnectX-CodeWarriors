package connectX;

import java.util.ResourceBundle;
import java.net.URL;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.io.IOException;

import com.jfoenix.controls.JFXCheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ListView;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

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
    private JFXListView<String> logListView;
    @FXML
    private GridPane gameGrid;
    private ImageView[][] boardStackPanes;
    @FXML
    private JFXCheckBox gridCheckBox;
    @FXML
    private Canvas gameCanvas;

    private int round;
    private Pair<Integer,Integer> round1Draw, round2Draw;
    private int p1Score,p2Score;

    public Arena() {

        this.round = 1;
        this.p1Score=this.p2Score=0;
        this.round1Draw=new Pair<>(0,0);
        this.round2Draw=new Pair<>(0,0);
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

    void slideConf(int winStat) throws IOException{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/roundEndDialog.fxml"));
        Parent root= loader.load();
        RoundEndDialog dash= loader.getController();
        dash.setRound(String.valueOf(this.round));
        dash.setP1Score(String.valueOf(this.p1Score));
        dash.setP2Score(String.valueOf(this.p2Score));
        dash.setArena(this);
        if(this.round==1)
            dash.setPromptTxt(winStat,1);
        else{
            if(this.p1Score==this.p2Score) {
                dash.setPromptTxt(winStat, 2);
                dash.setStatus(1);
            }
            else if(this.p1Score>this.p2Score) {
                dash.setPromptTxt(winStat, 3);
                dash.setStatus(2);
            }
            else {
                dash.setPromptTxt(winStat, 4);
                dash.setStatus(2);
            }
        }
        root.prefHeight(600);
        root.prefWidth(510);
        root.translateXProperty().set(-510);
        root.translateYProperty().set(20);

        Platform.runLater(()->{
            gameArea.getChildren().add(root);

            initButton.setDisable(true);

            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 55, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            timeline.getKeyFrames().add(kf);

            timeline.play();
        });


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
            this.round = 1;
            this.p1Score=this.p2Score=0;
            this.gameThread = new Thread(new Game(this, 1));
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
    void stop() throws InterruptedException {
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

    void startNextRound() throws InterruptedException{
        this.round=2;

        Main.running = false;
        Main.paused = false;
        this.gameThread = null;
        Thread.sleep(300L);
        this.clearBoard();

        this.gameThread = new Thread(new Game(this, 2));
        Main.running = true;
        Main.paused = false;
        this.startButton.setDisable(true);
        this.pauseButton.setDisable(false);
        this.stopButton.setDisable(false);
        this.gameThread.start();
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
    void close(final ActionEvent event) {
        final Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    void minimize(final ActionEvent event) {
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
        logListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + "#111111" + ";");
                        } else {
                            setText(item);
                            if(item.startsWith("P1"))
                            setStyle("-fx-control-inner-background: " + "#111111" + ";"+"-fx-text-fill: "+"#d81b60"+";"+"-fx-font-family: 'Press Start 2P Regular';");
                            else
                                setStyle("-fx-control-inner-background: " + "#111111" + ";"+"-fx-text-fill: "+"#3377ee"+";"+"-fx-font-family: 'Press Start 2P Regular';");

                        }
                    }
                };
            }
        });
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

    public void setRound1Draw(Pair<Integer, Integer> round1Draw, int winStat) throws IOException {
        this.round1Draw = round1Draw;
        this.slideConf(winStat);
        System.out.println(round1Draw.getKey()+"-"+round1Draw.getValue());
    }

    public void setRound2Draw(Pair<Integer, Integer> round2Draw,int winStat) throws IOException {
        this.round2Draw = round2Draw;
        this.slideConf(winStat);
        System.out.println(round2Draw.getKey()+"-"+round2Draw.getValue());
    }

    public Pair<Integer,Integer> getDrawResScores(){
        return new Pair<>(round1Draw.getKey()+round2Draw.getKey(),round1Draw.getValue()+round2Draw.getValue());
    }

    static class Location {
        double x;
        double y;
    }

    public void playerWin(int player,int xi,int yi,int xj,int yj){
        if(player==1)
            p1Score++;
        else
            p2Score++;
        Animation animation=createPathAnimation(xi,yi,xj,yj,(player== 1)?Color.RED:Color.BLUE);
        animation.play();
    }

}

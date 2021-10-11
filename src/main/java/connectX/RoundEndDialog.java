package connectX;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class RoundEndDialog implements Initializable {
    final private String[] c1={
            "P1 won this round!",
            "P2 won this round!",
            "It's a draw!"
    } ;
    final private String[] c2={
            "Continue on to next round?",
            "Final score is a draw! Start draw resolution?",
            "P1 won the game!",
            "P2 won the game!"
    } ;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label roundLabel,p1Score,p2Score,promptTxt,drawResScore;
    @FXML
    private JFXButton noBtn;

    private int status=0; //0=Start next round,1=Start draw resolution,2= Disable No

    private Arena arena;

    public void onYes(ActionEvent actionEvent) throws InterruptedException {
        if(this.status==0) {
            this.arena.startNextRound();
            slideOut();
        }
        else if(this.status==1){
            Pair<Integer,Integer> p=this.arena.getDrawResScores();
            drawResScore.setVisible(true);
            String resScore="Draw resolution score: "+p.getKey()+"-"+p.getValue()+". ";
            if(p.getKey()!=p.getValue())
                resScore+="P"+(p.getKey()>p.getValue()?1:2)+" wins!";
            else
                resScore+="Draw again! Manual check required.";
            drawResScore.setText(resScore);
            this.setStatus(2);
        }
        else {
            this.arena.stop();
            slideOut();
        }
    }

    public void onNo(ActionEvent actionEvent) {
        slideOut();
    }
    public void setRound(String txt){
        roundLabel.setText(txt);
    }
    public void setP1Score(String txt){
        p1Score.setText(txt);
    }
    public void setP2Score(String txt){
        p2Score.setText(txt);
    }
    public void setPromptTxt(int choice1,int choice2){
        String txt="";
        switch(choice1){
            case 1: case 2: case 3:
                txt+=c1[choice1-1];
                break;
        }
        txt+="\n";
        switch(choice2){
            case 1: case 2: case 3: case 4:
                txt+=c2[choice2-1];
                break;
        }
        promptTxt.setText(txt);
    }

    public void setStatus(int status) {
        this.status = status;
        if(status==2)
            noBtn.setDisable(true);
        else
            noBtn.setDisable(false);
    }

    public void setArena(Arena arena) {
        this.arena=arena;
    }
    private void slideOut() {

        StackPane parentContainer = (StackPane) rootPane.getParent();

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(rootPane.translateXProperty(), 815, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            parentContainer.getChildren().remove(rootPane);
        });
        timeline.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawResScore.setVisible(false);
    }
}

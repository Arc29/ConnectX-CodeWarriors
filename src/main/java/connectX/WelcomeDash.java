package connectX;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class WelcomeDash implements Initializable {
    @FXML
    private JFXComboBox<String> xSelect;
    @FXML
    private JFXTextField moveTextField;
    @FXML
    private JFXTextField pauseTextField;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField p1File;
    @FXML
    private JFXTextField p2File;

    private JFXButton initButton;
    private JFXSnackbar snackbar;
    private File fileChooser() {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Select source code file or compiled file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("C/C++/Java/Python", "*.c", "*.cpp", "*.out", "*.exe", "*.java", "*.class", "*.py"));
        final File file = chooser.showOpenDialog(this.moveTextField.getScene().getWindow());
        return file;
    }

    private boolean compileFile(final File file, final int playerId) {
        String command = "";
        String compiledFileName = "";
        if (Constants.cFileFilter.accept(file)) {
            command = "gcc " + file.getAbsolutePath() + " -o player" + playerId + ".out";
            compiledFileName = "player" + playerId + ".out";
        }
        else if (Constants.cppFileFilter.accept(file)) {
            command = "g++ " + file.getAbsolutePath() + " -o player" + playerId + ".out";
            compiledFileName = "player" + playerId + ".out";
        }
        else if (Constants.javaFileFilter.accept(file)) {
            command = "javac " + file.getAbsolutePath();
            compiledFileName = file.getName().substring(0, file.getName().indexOf(".java")) + ".class";
            this.updateTime(playerId,false);
        }
        if (!command.equals("")) {
            try {
                final Runtime runtime = Runtime.getRuntime();
                final Process proc = runtime.exec(command, null, new File(new File("").getAbsolutePath()));
                proc.waitFor();
                final Scanner scan = new Scanner(proc.getErrorStream());
                if (scan.hasNext()) {
                    this.log("Compilation Error","RED");
                    return false;
                }
                this.log("Compilation Successful");
                if (playerId == 1) {
                    Players.getPlayers().player1Ready = true;
                    Players.getPlayers().player1File = new File("").getAbsolutePath() + "/" + compiledFileName;
                    p1File.setText(Players.getPlayers().player1File);
                }
                else if (playerId == 0) {
                    Players.getPlayers().player2Ready = true;
                    Players.getPlayers().player2File = new File("").getAbsolutePath() + "/" + compiledFileName;
                    p2File.setText(Players.getPlayers().player2File);
                }
                return true;
            }
            catch (InterruptedException e) {
                this.log("Unable to compile file " + file.getAbsolutePath() + " " + file.getName(),"RED");
            }
            catch (IOException e2) {
                this.log("Error running command to compile file: " + command,"RED");
            }
            return false;
        }
        if (Constants.classFileFilter.accept(file)||Constants.pythonFileFilter.accept(file)) {
            this.updateTime(playerId,Constants.pythonFileFilter.accept(file));
        }
        if (Constants.outFileFilter.accept(file) || Constants.classFileFilter.accept(file) || Constants.pythonFileFilter.accept(file)) {
            if (playerId == 1) {
                Players.getPlayers().player1Ready = true;
                if(Constants.classFileFilter.accept(file))
                    Players.getPlayers().player1Type = 1;
                if(Constants.pythonFileFilter.accept(file))
                    Players.getPlayers().player1Type = 2;
                Players.getPlayers().player1File = file.getAbsolutePath();
                p1File.setText(Players.getPlayers().player1File);
            }
            else if (playerId == 0) {
                Players.getPlayers().player2Ready = true;
                if(Constants.classFileFilter.accept(file))
                    Players.getPlayers().player2Type = 1;
                if(Constants.pythonFileFilter.accept(file))
                    Players.getPlayers().player2Type = 2;
                Players.getPlayers().player2File = file.getAbsolutePath();
                p2File.setText(Players.getPlayers().player2File);
            }
            return true;
        }
        this.log("Linux only supports c, cpp, out, java, class, py","RED");
        return false;
    }

    void updateTime(final int playerId, final boolean isPython) {
        if (playerId == 1) {
            try {
                final int time = Integer.parseInt(this.moveTextField.getText());
                if (time > 0) {
                    if(!isPython)
                    Players.getPlayers().player1AllowedTime = 2 * time;
                    else
                        Players.getPlayers().player1AllowedTime = 3 * time;
                    this.log("Move time Updated for P1");
                }
                else {
                    this.log("Negative/Zero time not allowed","RED");
                }
            }
            catch (NumberFormatException e) {
                this.log("Invalid move time entered","RED");
            }
        }
        else {
            try {
                final int time = Integer.parseInt(this.moveTextField.getText());
                if (time > 0) {
                    if(!isPython)
                        Players.getPlayers().player2AllowedTime = 2 * time;
                    else
                        Players.getPlayers().player2AllowedTime = 3 * time;
                    this.log("Move time Updated for P2");
                }
                else {
                    this.log("Negative/Zero time not allowed","RED");
                }
            }
            catch (NumberFormatException e) {
                this.log("Invalid move time entered","RED");
            }
        }
    }
    @FXML
    void player1Code(final ActionEvent event) {
        final File file = this.fileChooser();
        if (file == null) {
            this.log("No file selected","RED");
            return;
        }
        if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("Mac")) {
            if (!this.compileFile(file, 1)) {
                this.log("Cannot open/compile the file: " + file,"RED");
                return;
            }
            this.log("File Selected for P1");
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            if (Constants.cFileFilter.accept(file) || Constants.cppFileFilter.accept(file) || Constants.javaFileFilter.accept(file)) {
                this.log("Windows doesn't support c, cpp, out, java. Please use compiled form either exe or class","RED");
            }
            else if (Constants.exeFileFilter.accept(file) || Constants.classFileFilter.accept(file) || Constants.pythonFileFilter.accept(file)) {
                if (Constants.classFileFilter.accept(file)) {
                    this.updateTime(1,false);
                    Players.getPlayers().player1Type=1;
                }
                else if (Constants.pythonFileFilter.accept(file)) {
                    this.updateTime(1,true);
                    Players.getPlayers().player1Type=2;
                }
                Players.getPlayers().player1Ready = true;
                try {
                    Players.getPlayers().player1File = file.getCanonicalPath();
                    p1File.setText(Players.getPlayers().player1File);
                    this.log("File Selected for P1");
                }
                catch (IOException e) {
                    this.log("Unable to locate file: " + file.getName(),"RED");
                }
            }
        }
        else {
            this.log("OS not supported.","RED");
        }
    }

    @FXML
    void player2Code(final ActionEvent event) {
        final File file = this.fileChooser();
        if (file == null) {
            this.log("No file selected","RED");
            return;
        }
        if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("Mac")) {
            if (!this.compileFile(file, 0)) {
                this.log("Cannot open/compile the file: " + file,"RED");
                return;
            }
            this.log("File Selected for P2");
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            if (Constants.cFileFilter.accept(file) || Constants.cppFileFilter.accept(file) || Constants.javaFileFilter.accept(file)) {
                this.log("Windows doesn't support c, cpp, out, java. Please use compiled form either exe or class","RED");
            }
            else if (Constants.exeFileFilter.accept(file) || Constants.classFileFilter.accept(file)||Constants.pythonFileFilter.accept(file)) {
                if (Constants.classFileFilter.accept(file)) {
                    this.updateTime(0,false);
                    Players.getPlayers().player2Type=1;
                }
                else if (Constants.pythonFileFilter.accept(file)) {
                    this.updateTime(0,true);
                    Players.getPlayers().player2Type=2;
                }
                Players.getPlayers().player2Ready = true;
                try {
                    Players.getPlayers().player2File = file.getCanonicalPath();
                    p2File.setText(Players.getPlayers().player2File);
                    this.log("File Selected for P2");
                }
                catch (IOException e) {
                    this.log("Unable to locate file" + file.getName(),"RED");
                }
            }
        }
        else {
            this.log("OS not supported.","RED");
        }
    }

    @FXML
    void setMoveTime(final KeyEvent event) {
        if (event != null && event.getCode() != KeyCode.ENTER) {
            return;
        }
        try {
            final int time = Integer.parseInt(this.moveTextField.getText());
            if (time > 0) {

                Players.getPlayers().player1AllowedTime = Players.getPlayers().player2AllowedTime = time;
                if(Players.getPlayers().player1File!=null && Players.getPlayers().player1Type!=0)
                    updateTime(1,Players.getPlayers().player1Type==2);
                if(Players.getPlayers().player2File!=null && Players.getPlayers().player2Type!=0)
                    updateTime(0,Players.getPlayers().player2Type==2);
                this.log("Move time Updated");
            }
            else {
                this.log("Negative/Zero time not allowed","RED");
            }
        }
        catch (NumberFormatException e) {
            this.log("Invalid move time entered","RED");
        }
    }

    @FXML
    void setPauseTime(final KeyEvent event) {
        if (event != null && event.getCode() != KeyCode.ENTER) {
            return;
        }
        try {
            final int time = Integer.parseInt(this.pauseTextField.getText());
            if (time >= 0) {
                Players.getPlayers().sleepTime = time;
                this.log("Pause time Updated");
            }
            else {
                this.log("Negative time not allowed","RED");
            }
        }
        catch (NumberFormatException e) {
            this.log("Invalid pause time entered","RED");
        }
    }

    @FXML
    void setXValue(){
        Players.getPlayers().xValue=Integer.parseInt(xSelect.getValue());
//        System.out.println(xSelect.getValue());
    }

    private void log(String message){
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new ColorSnackbar(message)));
    }

    private void log(String message,String color){
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new ColorSnackbar(message,color)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        snackbar=new JFXSnackbar(rootPane);
        xSelect.getItems().addAll(
                "4",
                "5",
                "6",
                "7"
        );
        xSelect.getSelectionModel().selectFirst();
    }
    @FXML
    private void slideOut(ActionEvent actionEvent) {

        StackPane parentContainer = (StackPane) rootPane.getParent();

        initButton.setDisable(false);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(rootPane.translateXProperty(), 815, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            parentContainer.getChildren().remove(rootPane);
        });
        timeline.play();
    }

    public void setInitButton(JFXButton initButton) {
        this.initButton = initButton;
    }
}

package connectX;

import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.Node;
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

public class Arena implements Initializable
{
    Thread gameThread;
    @FXML
    private ImageView back;
    @FXML
    private ImageView avishkarImageView;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private TextField moveTextField;
    @FXML
    private TextField pauseTextField;
    @FXML
    private ListView historyListView;
    @FXML
    private ListView<String> logListView;
    @FXML
    private GridPane boardGridPane;
    private ImageView[][] boardStackPanes;
    @FXML
    private JFXCheckBox gridCheckBox;
    private String castle1;
    private String castle2;

    public Arena() {
        this.castle1 = "Ship1.png";
        this.castle2 = "Ship2.png";
    }

//    @FXML
//    void grid(final MouseEvent event) {
//        this.boardGridPane.setGridLinesVisible(this.gridCheckBox.isSelected());
//    }

    private File fileChooser() {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Select source code file or compiled file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new ExtensionFilter("C/C++/Java/Python", "*.c", "*.cpp", "*.out", "*.exe", "*.java", "*.class", "*.py"));
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
            this.updateTimeForJava(playerId);
        }
        if (!command.equals("")) {
            try {
                final Runtime runtime = Runtime.getRuntime();
                final Process proc = runtime.exec(command, null, new File(new File("").getAbsolutePath()));
                proc.waitFor();
                final Scanner scan = new Scanner(proc.getErrorStream());
                if (scan.hasNext()) {
                    this.log("Compilation Error");
                    return false;
                }
                this.log("Compilation Successful");
                if (playerId == 1) {
                    Players.getPlayers().player1Ready = true;
                    Players.getPlayers().player1File = new File("").getAbsolutePath() + "/" + compiledFileName;
                }
                else if (playerId == 0) {
                    Players.getPlayers().player2Ready = true;
                    Players.getPlayers().player2File = new File("").getAbsolutePath() + "/" + compiledFileName;
                }
                return true;
            }
            catch (InterruptedException e) {
                this.log("Unable to compile file " + file.getAbsolutePath() + " " + file.getName());
            }
            catch (IOException e2) {
                this.log("Error running command to compile file: " + command);
            }
            return false;
        }
        if (Constants.classFileFilter.accept(file)) {
            this.updateTimeForJava(playerId);
        }
        if (Constants.outFileFilter.accept(file) || Constants.classFileFilter.accept(file)) {
            if (playerId == 1) {
                Players.getPlayers().player1Ready = true;
                Players.getPlayers().player1File = file.getAbsolutePath();
            }
            else if (playerId == 0) {
                Players.getPlayers().player2Ready = true;
                Players.getPlayers().player2File = file.getAbsolutePath();
            }
            return true;
        }
        this.log("Linux only supports c, cpp, out, java, class");
        return false;
    }

    void updateTimeForJava(final int playerId) {
        if (playerId == 1) {
            try {
                final int time = Integer.parseInt(this.moveTextField.getText());
                if (time > 0) {
                    Players.getPlayers().player1AllowedTime = 2 * time;
                    this.log("Move time Updated for Green team");
                }
                else {
                    this.log("Negative/Zero time not allowed");
                }
            }
            catch (NumberFormatException e) {
                this.log("Invalid move time entered");
            }
        }
        else {
            try {
                final int time = Integer.parseInt(this.moveTextField.getText());
                if (time > 0) {
                    Players.getPlayers().player2AllowedTime = 2 * time;
                    this.log("Move time Updated for Blue team");
                }
                else {
                    this.log("Negative/Zero time not allowed");
                }
            }
            catch (NumberFormatException e) {
                this.log("Invalid move time entered");
            }
        }
    }

    @FXML
    void player1Code(final ActionEvent event) {
        final File file = this.fileChooser();
        if (file == null) {
            this.log("No file selected");
            return;
        }
        if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("Mac")) {
            if (!this.compileFile(file, 1)) {
                this.log("Cannot open/compile the file: " + file);
                return;
            }
            this.log("File Selected for Green team");
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            if (Constants.cFileFilter.accept(file) || Constants.cppFileFilter.accept(file) || Constants.javaFileFilter.accept(file)) {
                this.log("Windows doesn't support c, cpp, out, java. Please use compiled form either exe or class");
            }
            else if (Constants.exeFileFilter.accept(file) || Constants.classFileFilter.accept(file)) {
                if (Constants.classFileFilter.accept(file)) {
                    this.updateTimeForJava(1);
                }
                Players.getPlayers().player1Ready = true;
                try {
                    Players.getPlayers().player1File = file.getCanonicalPath();
                    this.log("File Selected for Green team");
                }
                catch (IOException e) {
                    this.log("Unable to locate file: " + file.getName());
                }
            }
        }
        else {
            this.log("OS not supported.");
        }
    }

    @FXML
    void player2Code(final ActionEvent event) {
        final File file = this.fileChooser();
        if (file == null) {
            this.log("Selection Canceled");
            return;
        }
        if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("Mac")) {
            if (!this.compileFile(file, 0)) {
                this.log("Cannot open/compile the file: " + file);
                return;
            }
            this.log("File Selected for Blue team");
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            if (Constants.cFileFilter.accept(file) || Constants.cppFileFilter.accept(file) || Constants.javaFileFilter.accept(file)) {
                this.log("Windows doesn't support c, cpp, out, java. Please use compiled form either exe or class");
            }
            else if (Constants.exeFileFilter.accept(file) || Constants.classFileFilter.accept(file)) {
                if (Constants.classFileFilter.accept(file)) {
                    this.updateTimeForJava(0);
                }
                Players.getPlayers().player2Ready = true;
                try {
                    Players.getPlayers().player2File = file.getCanonicalPath();
                    this.log("File Selected for Blue team");
                }
                catch (IOException e) {
                    this.log("Unable to locate file" + file.getName());
                }
            }
        }
        else {
            this.log("OS not supported.");
        }
    }

//    @FXML
//    void start(final ActionEvent event) {
//        if (!Players.getPlayers().player1Ready) {
//            this.log("Green team isn't ready");
//            return;
//        }
//        if (!Players.getPlayers().player2Ready) {
//            this.log("Blue team isn't ready");
//            return;
//        }
//        if (Main.paused) {
//            Main.paused = !Main.paused;
//            this.pauseButton.setDisable(false);
//            this.startButton.setDisable(true);
//            return;
//        }
//        if (this.gameThread == null) {
//            this.setMoveTime(null);
//            this.setPauseTime(null);
//            this.gameThread = new Thread(new Game(this));
//            Main.running = true;
//            Main.paused = false;
//            this.startButton.setDisable(true);
//            this.pauseButton.setDisable(false);
//            this.stopButton.setDisable(false);
//            this.gameThread.start();
//        }
//    }
//
//    @FXML
//    void pause(final ActionEvent event) {
//        if (this.gameThread == null) {
//            return;
//        }
//        Main.paused = !Main.paused;
//        if (Main.paused) {
//            this.log("Game paused");
//            this.pauseButton.setDisable(true);
//            this.startButton.setDisable(false);
//        }
//    }
//
//    @FXML
//    void stop(final ActionEvent event) throws InterruptedException {
//        if (this.gameThread == null) {
//            return;
//        }
//        Main.running = false;
//        Main.paused = false;
//        this.gameThread = null;
//        Thread.sleep(300L);
//        this.clearBoard();
//        this.startButton.setDisable(false);
//        this.pauseButton.setDisable(true);
//        this.stopButton.setDisable(true);
//    }

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
//
//    private String emptyBlock() {
//        final String[] emptyBlock = { "Empty1.png", "Empty2.png", "Empty3.png", "Empty4.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(emptyBlock.length);
//        return emptyBlock[index];
//    }
//
//    private String blockedBlock() {
//        final String[] blockedBlock = { "Block.png", "Block2.png", "Block3.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(blockedBlock.length);
//        return blockedBlock[index];
//    }
//
//    private String blackMen() {
//        final String[] blockedBlock = { "BlueA1.png", "BlueA1.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(blockedBlock.length);
//        return blockedBlock[index];
//    }
//
//    private String whiteMen() {
//        final String[] blockedBlock = { "GreenB1.png", "GreenB1.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(blockedBlock.length);
//        return blockedBlock[index];
//    }
//
//    private String blackHorse() {
//        final String[] blockedBlock = { "BlueC1.png", "BlueC1.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(blockedBlock.length);
//        return blockedBlock[index];
//    }
//
//    private String whiteHorse() {
//        final String[] blockedBlock = { "GreenC1.png", "GreenC1.png" };
//        final Random random = new Random();
//        final int index = random.nextInt(blockedBlock.length);
//        return blockedBlock[index];
//    }

//    void updateBoard(final int x, final int y, final Constants.State state) {
//        if (!Game.inRange(x, y)) {
//            return;
//        }
//        Platform.runLater(() -> {
//            switch (state) {
//                case EMPTY: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.emptyBlock()));
//                    break;
//                }
//                case BLACKMEN: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.blackMen()));
//                    break;
//                }
//                case BLACKHORSE: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.blackHorse()));
//                    break;
//                }
//                case BLACKCASTLE: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.castle2));
//                    break;
//                }
//                case WHITEMEN: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.whiteMen()));
//                    break;
//                }
//                case WHITEHORSE: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.whiteHorse()));
//                    break;
//                }
//                case WHITECASTLE: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.castle1));
//                    break;
//                }
//                case BLOCKED: {
//                    this.boardStackPanes[x][y].setImage(new Image(this.blockedBlock()));
//                    break;
//                }
//            }
//        });
//    }

//    @FXML
//    void setMoveTime(final KeyEvent event) {
//        if (event != null && event.getCode() != KeyCode.ENTER) {
//            return;
//        }
//        try {
//            final int time = Integer.parseInt(this.moveTextField.getText());
//            if (time > 0) {
//                final Players players = Players.getPlayers();
//                final Players players2 = Players.getPlayers();
//                final int n = time;
//                players2.player2AllowedTime = n;
//                players.player1AllowedTime = n;
//                this.log("Move time Updated");
//            }
//            else {
//                this.log("Negative/Zero time not allowed");
//            }
//        }
//        catch (NumberFormatException e) {
//            this.log("Invalid move time entered");
//        }
//    }
//
//    @FXML
//    void setPauseTime(final KeyEvent event) {
//        if (event != null && event.getCode() != KeyCode.ENTER) {
//            return;
//        }
//        try {
//            final int time = Integer.parseInt(this.pauseTextField.getText());
//            if (time >= 0) {
//                Players.getPlayers().sleepTime = time;
//                this.log("Pause time Updated");
//            }
//            else {
//                this.log("Negative time not allowed");
//            }
//        }
//        catch (NumberFormatException e) {
//            this.log("Invalid pause time entered");
//        }
//    }

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

    }

//    void clearBoard() {
//        int i;
//        int j;
//        Platform.runLater(() -> {
//            for (i = 0; i < 16; ++i) {
//                for (j = 0; j < 12; ++j) {
//                    this.updateBoard(i, j, Constants.State.EMPTY);
//                }
//            }
//            this.updateBoard(0, 0, Constants.State.BLOCKED);
//            this.updateBoard(0, 1, Constants.State.BLOCKED);
//            this.updateBoard(0, 2, Constants.State.BLOCKED);
//            this.updateBoard(0, 3, Constants.State.BLOCKED);
//            this.updateBoard(0, 4, Constants.State.BLOCKED);
//            this.updateBoard(0, 7, Constants.State.BLOCKED);
//            this.updateBoard(0, 8, Constants.State.BLOCKED);
//            this.updateBoard(0, 9, Constants.State.BLOCKED);
//            this.updateBoard(0, 10, Constants.State.BLOCKED);
//            this.updateBoard(0, 11, Constants.State.BLOCKED);
//            this.updateBoard(15, 0, Constants.State.BLOCKED);
//            this.updateBoard(15, 1, Constants.State.BLOCKED);
//            this.updateBoard(15, 2, Constants.State.BLOCKED);
//            this.updateBoard(15, 3, Constants.State.BLOCKED);
//            this.updateBoard(15, 4, Constants.State.BLOCKED);
//            this.updateBoard(15, 7, Constants.State.BLOCKED);
//            this.updateBoard(15, 8, Constants.State.BLOCKED);
//            this.updateBoard(15, 9, Constants.State.BLOCKED);
//            this.updateBoard(15, 10, Constants.State.BLOCKED);
//            this.updateBoard(15, 11, Constants.State.BLOCKED);
//            this.updateBoard(1, 0, Constants.State.BLOCKED);
//            this.updateBoard(1, 1, Constants.State.BLOCKED);
//            this.updateBoard(1, 10, Constants.State.BLOCKED);
//            this.updateBoard(1, 11, Constants.State.BLOCKED);
//            this.updateBoard(14, 0, Constants.State.BLOCKED);
//            this.updateBoard(14, 1, Constants.State.BLOCKED);
//            this.updateBoard(14, 10, Constants.State.BLOCKED);
//            this.updateBoard(14, 11, Constants.State.BLOCKED);
//            this.updateBoard(2, 0, Constants.State.BLOCKED);
//            this.updateBoard(2, 11, Constants.State.BLOCKED);
//            this.updateBoard(13, 0, Constants.State.BLOCKED);
//            this.updateBoard(13, 11, Constants.State.BLOCKED);
//            this.updateBoard(5, 2, Constants.State.WHITEHORSE);
//            this.updateBoard(6, 3, Constants.State.WHITEHORSE);
//            this.updateBoard(5, 9, Constants.State.WHITEHORSE);
//            this.updateBoard(6, 8, Constants.State.WHITEHORSE);
//            this.updateBoard(10, 2, Constants.State.BLACKHORSE);
//            this.updateBoard(9, 3, Constants.State.BLACKHORSE);
//            this.updateBoard(10, 9, Constants.State.BLACKHORSE);
//            this.updateBoard(9, 8, Constants.State.BLACKHORSE);
//            this.updateBoard(5, 3, Constants.State.WHITEMEN);
//            this.updateBoard(5, 4, Constants.State.WHITEMEN);
//            this.updateBoard(5, 5, Constants.State.WHITEMEN);
//            this.updateBoard(5, 6, Constants.State.WHITEMEN);
//            this.updateBoard(5, 7, Constants.State.WHITEMEN);
//            this.updateBoard(5, 8, Constants.State.WHITEMEN);
//            this.updateBoard(6, 4, Constants.State.WHITEMEN);
//            this.updateBoard(6, 5, Constants.State.WHITEMEN);
//            this.updateBoard(6, 6, Constants.State.WHITEMEN);
//            this.updateBoard(6, 7, Constants.State.WHITEMEN);
//            this.updateBoard(10, 3, Constants.State.BLACKMEN);
//            this.updateBoard(10, 4, Constants.State.BLACKMEN);
//            this.updateBoard(10, 5, Constants.State.BLACKMEN);
//            this.updateBoard(10, 6, Constants.State.BLACKMEN);
//            this.updateBoard(10, 7, Constants.State.BLACKMEN);
//            this.updateBoard(10, 8, Constants.State.BLACKMEN);
//            this.updateBoard(9, 4, Constants.State.BLACKMEN);
//            this.updateBoard(9, 5, Constants.State.BLACKMEN);
//            this.updateBoard(9, 6, Constants.State.BLACKMEN);
//            this.updateBoard(9, 7, Constants.State.BLACKMEN);
//            this.updateBoard(0, 5, Constants.State.WHITECASTLE);
//            this.updateBoard(0, 6, Constants.State.WHITECASTLE);
//            this.updateBoard(15, 5, Constants.State.BLACKCASTLE);
//            this.updateBoard(15, 6, Constants.State.BLACKCASTLE);
//            this.historyListView.getItems().clear();
//            this.logListView.getItems().clear();
//        });
//    }
}

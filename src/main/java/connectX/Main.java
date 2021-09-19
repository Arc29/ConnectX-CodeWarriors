package connectX;


import javafx.scene.input.MouseEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application
{
    public static volatile boolean running;
    public static volatile boolean paused;
    private double xOffset;
    private double yOffset;

    public Main() {
        this.xOffset = 0.0;
        this.yOffset = 0.0;
    }

    public static void main(final String[] args) {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        final Screen screen = Screen.getPrimary();
        final Rectangle2D bounds = screen.getVisualBounds();
        final String arena = "/fxml/arena.fxml";
        Font.loadFont(getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf"),14);
        Parent root;
        root = FXMLLoader.load(getClass().getResource(arena));


        root.setOnMousePressed(event -> {
            this.xOffset = primaryStage.getX() - event.getScreenX();
            this.yOffset = primaryStage.getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + this.xOffset);
            primaryStage.setY(event.getScreenY() + this.yOffset);
        });
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        Constants.width = bounds.getWidth();
        Constants.height = bounds.getHeight();

        primaryStage.setTitle("ConnectX");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene=new Scene(root);
        scene.getStylesheets().add("/css/App.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void stop() {
        Main.running = false;
        Main.paused = false;
    }

    static {
        Main.running = false;
        Main.paused = false;
    }
}

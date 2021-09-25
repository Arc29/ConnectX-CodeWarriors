package connectX;


import javafx.scene.input.MouseEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.application.Application;

import java.awt.*;

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
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double width = resolution.getWidth();
        double height = resolution.getHeight();
        double w = width/1920;  // your window width
        double h = height/1040;  // your window height
        Scale scale = new Scale(w, h, 0, 0);
        root.getTransforms().add(scale);
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        Constants.width = bounds.getWidth();
        Constants.height = bounds.getHeight();

        primaryStage.setTitle("ConnectX");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene=new Scene(root);
        scene.setFill(Paint.valueOf("#222222"));
        scene.getStylesheets().add("/css/App.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
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

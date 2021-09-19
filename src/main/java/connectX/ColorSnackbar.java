package connectX;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * JFXSnackbarLayout default layout for snackbar content
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-11-16
 */
public class ColorSnackbar extends BorderPane {

    private Label toast;

    public ColorSnackbar(String message){
        this(message,null);
    }

    public ColorSnackbar(String message,String color) {
        initialize();

        toast = new Label();
        toast.setMinWidth(Control.USE_PREF_SIZE);
        toast.getStyleClass().add("jfx-snackbar-toast");
        if(color!=null){
            toast.setStyle("-fx-text-fill: "+color);
        }
        toast.setWrapText(true);
        toast.setText(message);
        StackPane toastContainer = new StackPane(toast);
        toastContainer.setPadding(new Insets(20));

        toast.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            if (getPrefWidth() == -1) {
                return getPrefWidth();
            }

            return prefWidthProperty().get();
        }, prefWidthProperty()));

        setLeft(toastContainer);


    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-snackbar-layout";

    public String getToast() {
        return toast.getText();
    }

    public void setToast(String toast) {
        this.toast.setText(toast);
    }


    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}
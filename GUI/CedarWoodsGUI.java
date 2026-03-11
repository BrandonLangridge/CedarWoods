package gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.InputStream;

public class CedarWoodsGUI extends Application {

    @Override
    public void start(Stage primaryStage)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CedarWoodsGUIFXML.fxml"));

        primaryStage.setTitle("Cedar Woods System");
        Scene helloworldScene = new Scene(loader.load());

        primaryStage.setScene(helloworldScene);

        InputStream iconStream = getClass().getResourceAsStream("/gui/app_icon.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        } else {
            System.err.println("Icon not found at /gui/app_icon.png");
        }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package gui;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CedarWoodsGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CedarWoodsGUIFXML.fxml"));
        Scene scene = new Scene(loader.load());

        // Load the primer-light theme CSS
//        scene.getStylesheets().add(getClass().getResource("cupertino-light.css").toExternalForm());

        // Load your custom overrides
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());

        primaryStage.setTitle("Cedar Woods System");
        primaryStage.setScene(scene);

        // Set the app icon
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
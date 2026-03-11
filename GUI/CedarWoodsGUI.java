package gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CedarWoodsGUI extends Application
{
    @Override
    public void start(Stage primaryStage)
            throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CedarWoodsGUIFXML.fxml"));
        
        primaryStage.setTitle("Cedar Woods System");
        Scene helloworldScene = new Scene(loader.load());
        
        primaryStage.setScene(helloworldScene);
        primaryStage.show();
    }
    
   public static void main(String[] args)
   {
       launch(args);
   }
}
    
  
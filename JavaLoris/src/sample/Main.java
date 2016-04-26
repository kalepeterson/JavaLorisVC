package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("JavaLoris");
        primaryStage.setScene(new View().getPage());
        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("JavaLorisLog.txt"))));
        } catch(Exception e) {
            System.out.println("Unable to create log file, messages will not be persisted.");
        }
        launch(args);
    }
}

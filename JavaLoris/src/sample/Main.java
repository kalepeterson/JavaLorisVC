package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * This class contains the main method and start method for the application.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX GUI and application logic.
     * @param primaryStage The first stage that JavaFX will display.
     * @throws Exception Thrown when stage cannot be created.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaLoris");
        primaryStage.setScene(new View().getPage());
        primaryStage.show();
    }


    public static void main(String[] args) {
        // Redirect output to a log file
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("JavaLorisLog.txt"))));
        } catch(Exception e) {
            System.out.println("Unable to create log file, messages will not be persisted.");
        }
        // Launch the JavaFX application
        launch(args);
        System.out.close();
    }
}

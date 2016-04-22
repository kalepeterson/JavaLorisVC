package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Created by Kale on 4/13/2016.
 */
public class View {
    private Controller mainCtrl;
    private Scene scene;
    GridPane container;

    public View() {
        container = new GridPane();
        scene = new Scene(container, 400, 300);
        //Setting the view parameters
        for(int i = 0; i < 3; i++){
            container.getColumnConstraints().add(new ColumnConstraints(100));
        }
        mainCtrl = new Controller();
        container.setAlignment(Pos.TOP_CENTER);
        container.setHgap(10);
        container.setVgap(10);
        container.setPadding(new Insets(15, 15, 15, 15));
        //Setting up the title pane
        container.add(mainCtrl.getStatusLabel(), 0, 0, 3, 1);
        //Setting up the IP Address pane
        HBox ippane = new HBox();
        ippane.setPadding(new Insets(0, 75, 0, 0));
        ippane.setSpacing(10);
        ippane.getChildren().addAll(mainCtrl.getNetAddrLabel(), mainCtrl.getNetAddrField());
        container.add(ippane, 0, 2, 3, 1);
        //Setting up the port number pane
        HBox portpane = new HBox();
        portpane.setPadding(new Insets(0, 85, 0, 0));
        portpane.setSpacing(20);
        portpane.getChildren().addAll(mainCtrl.getPortLabel(), mainCtrl.getPortField());
        container.add(portpane, 0, 3, 3, 1);
        //Setting up the action buttons
        HBox actionButtonsPane = new HBox();
        actionButtonsPane.setPadding(new Insets(0, 45, 0, 0));
        actionButtonsPane.setSpacing(10);
        actionButtonsPane.getChildren().addAll(mainCtrl.getAttackButton(), mainCtrl.getPingButton());
        container.add(actionButtonsPane, 0, 4, 3, 1);
        //Setting up connections slider
        container.add(mainCtrl.getNumConnectionsSlider(),0,5,3,1);
        //Setting up connections label
        container.add(mainCtrl.getNumConnectionsLabel(),0,6,3,1);
    }

    public Scene getPage(){
        return scene;
    }
}

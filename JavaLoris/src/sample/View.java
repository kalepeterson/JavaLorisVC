package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Class for creating and configuring the layout of the JavaFX GUI.
 */
public class View {
    private Controller mainCtrl;
    private Scene scene;
    GridPane container;

    /**
     * Constructor that creates a UI that references objects in the Controller class.
     */
    public View() {
        // The GridPane is a simple layout based on rows and columns.
        container = new GridPane();

        // Constructing scene is the purpose of this constructor.
        scene = new Scene(container, 600, 500);

        // Set up the column constraints. I used 3 columns for this view.
        for(int i = 0; i < 3; i++){
            container.getColumnConstraints().add(new ColumnConstraints(100));
        }

        // Create a Controller that will handle the logic of the application.
        mainCtrl = new Controller();

        // Last few general layout items.
        container.setAlignment(Pos.TOP_CENTER);
        container.setHgap(10);
        container.setVgap(10);
        container.setPadding(new Insets(15, 15, 15, 15));

        // Setting up the title pane
        // add method will apply a new row to the GridPane.  Documentation:
        // add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
        container.add(mainCtrl.getStatusLabel(), 0, 0, 3, 1);

        // Setting up the IP Address pane
        HBox ippane = new HBox();
        ippane.setPadding(new Insets(0, 75, 0, 0));
        ippane.setSpacing(10);
        ippane.getChildren().addAll(mainCtrl.getNetAddrLabel(), mainCtrl.getNetAddrField());
        container.add(ippane, 0, 1, 3, 1);

        // Setting up the port number pane
        HBox portpane = new HBox();
        portpane.setPadding(new Insets(0, 85, 0, 0));
        portpane.setSpacing(20);
        portpane.getChildren().addAll(mainCtrl.getPortLabel(), mainCtrl.getPortField());
        container.add(portpane, 0, 2, 3, 1);

        // Setting up the URL Path pane
        HBox pathpane = new HBox();
        pathpane.setPadding(new Insets(0, 85, 0, 0));
        pathpane.setSpacing(20);
        pathpane.getChildren().addAll(mainCtrl.getUrlPathLabel(), mainCtrl.getUrlPathField());
        container.add(pathpane, 0, 3, 3, 1);

        // Setting up the keep alive radio buttons
        HBox keepalivepane = new HBox();
        ToggleGroup keepalivegrp = mainCtrl.getKeepAliveGrp();
        keepalivepane.getChildren().addAll(mainCtrl.getKeepAliveBtn(),
                mainCtrl.getNoKeepBtn()
        );
        // Select the first button in the group by default
        keepalivegrp.selectToggle(keepalivegrp.getToggles().get(0));
        container.add(keepalivepane, 0, 4, 3, 1);

        // Setting up the GET or POST radio buttons
        HBox getpostpane = new HBox();
        ToggleGroup getpostgrp = mainCtrl.getRequestTypeGrp();
        getpostpane.getChildren().addAll(mainCtrl.getGetBtn(),
                mainCtrl.getPostBtn()
        );
        // Select the first button in the group by default
        getpostgrp.selectToggle(getpostgrp.getToggles().get(0));
        container.add(getpostpane, 0, 5, 3, 1);

        // Setting up the ping and attack buttons
        HBox actionButtonsPane = new HBox();
        actionButtonsPane.setPadding(new Insets(0, 45, 0, 0));
        actionButtonsPane.setSpacing(10);
        actionButtonsPane.getChildren().addAll(mainCtrl.getAttackButton(), mainCtrl.getPingButton());
        container.add(actionButtonsPane, 0, 6, 3, 1);

        // Setting up the number of connections slider
        container.add(mainCtrl.getNumConnectionsSlider(),0,7,3,1);

        // Setting up the label for the slider
        container.add(mainCtrl.getNumConnectionsLabel(),0,8,3,1);
    }

    /**
     * Retrieves the completed scene object.
     * @return The scene constructed by this view.
     */
    public Scene getPage(){
        return scene;
    }
}

package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import SlowLoris.*;

/**
 * Contains logic for configuring and executing the SlowLoris attack.
 */
public class Controller {

    // User input objects that will be provided to the View.
    private Button attackButton;
    private Button pingButton;
    private TextField netAddrField;
    private TextField portField;
    private TextField urlPathField;
    private Slider numConnectionsSlider;
    private ToggleGroup keepAliveGrp;
    private RadioButton keepAliveBtn;
    private RadioButton noKeepBtn;
    private ToggleGroup requestTypeGrp;
    private RadioButton requestGetBtn;
    private RadioButton requestPostBtn;

    // Label objects that will be dynamically updated.
    private Label statusLabel;
    private Label numConnectionsLabel;

    // Primitive fields.
    private String status;
    private boolean keepAlive;
    private boolean useGet;

    /**
     * Constructor. Initializes fields and attaches the application logic to
     * the appropriate UI events.
     */
    public Controller() {
        keepAlive = false;
        useGet = false;
        status = "Waiting for command...";

        setNetAddrField();
        setPortField();
        setUrlPathField();

        setAttackButton();
        setPingButton();

        setNumConnectionsSlider();

        statusLabel = new Label();
        setStatusLabel();

        numConnectionsLabel = new Label();
        setNumConnectionsLabel();

        setKeepAliveGrp();
        setRequestTypeGrp();
    }

    /**
     * Initializes the attack button, which creates a SlowLoris object
     * and executes its performAttack method.
     */
    public void setAttackButton() {
        // Create the button.
        attackButton = new Button("Launch Attack");
        attackButton.setPrefWidth(100);

        // Tell the button what to do.
        // This uses Lambda Expressions, a Java 8 feature.
        attackButton.setOnAction((ActionEvent e) -> {
            // First, update the status label
            status = "Attacking...";
            setStatusLabel();

            // Check if a URL or IP address has not been entered
            if(netAddrField.getText().trim().length() == 0) {
                // Stop here if there is not any input
                status = "Please enter a target address";
                setStatusLabel();
                return;
            }

            // Create the SlowLoris object
            SlowLoris sl = createSlowLoris();

            // Update the status label again
            status = "Attempting attack...";
            setStatusLabel();

            // This Task object is a way to implement multithreading in JavaFX.
            // The attack should use a background thread so that the UI is still responsive.
            Task async = new Task<Void>() {
                @Override public Void call() {
                    try {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // This is the actual logic for the attack.
                                        System.out.println("running attack");
                                        sl.performAttack();
                                        status = "Attack completed!";
                                        setStatusLabel();
                                    }
                                }
                        );
                    } catch (Exception e) {
                        // Handle any exceptions that may happen.
                        status = "Attack error: " + e.getMessage();
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    // Technically returning null is necessary here, not sure why.
                    return null;
                }
            };
            // Kick off the thread
            Thread t = new Thread(async);
            t.start();
        });
    }

    /**
     * Initializes the ping button, which sends a legitimate HTTP request when clicked
     * in order to check if the server is responsive.
     */
    public void setPingButton() {
        // Create the button
        pingButton = new Button("Ping Target");
        pingButton.setPrefWidth(100);

        // Tell the button what to do
        pingButton.setOnAction((ActionEvent e) -> {
            status = "Performing ping...";
            setStatusLabel();

            // I placed the ping code in the SlowLoris class for simplicity when creating this controller,
            // so a SlowLoris object will be created to perform the request.
            SlowLoris pingSL = createSlowLoris();
            Task async = new Task<Void>() {
                @Override public Void call() {
                    try {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("running ping");
                                        // Note that this time the pingTarget method is invoked, not the
                                        // performAttack method.
                                        status = pingSL.pingTarget();
                                        setStatusLabel();
                                    }
                                }
                        );
                    } catch (Exception e) {
                        // Handle any errors that might occur
                        status = "Attack error";
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            // Kick off the thread
            Thread t = new Thread(async);
            t.start();
        });
    }

    /**
     * Instantiates a SlowLoris object using the current UI parameters.
     * @return A configured SlowLoris object.
     */
    private SlowLoris createSlowLoris() {
        SlowLoris sl;

        // Check if a port has been specified.
        if(portField.getText().length() > 0) {
            // If so, try to parse it.
            int parsedPort = 80;
            try {
                parsedPort = Integer.parseInt(portField.getText());
            } catch (Exception ethree) {
                // If the parsing fails, parsedPort will still be 80, the default.
                ethree.printStackTrace();
            } finally {
                // Create the SlowLoris object with the specified port included.
                sl = new SlowLoris((int) (Math.floor(numConnectionsSlider.getValue())),
                        netAddrField.getText(), parsedPort);
            }
        } else {
            // Otherwise, just make a SlowLoris object with the default port number.
            sl = new SlowLoris((int) (Math.floor(numConnectionsSlider.getValue())),
                    netAddrField.getText());
        }

        // Configure the SlowLoris object to use complete Keep-Alive requests or not
        sl.setKeepAliveAbuse(keepAlive);

        // Configure the SlowLoris object to use GET requests or not
        sl.setUseGetRequest(useGet);

        // Configure the SlowLoris object to use the URL path specified in the UI
        sl.setUrlPath(urlPathField.getText());

        // Return the configured SlowLoris object.
        return sl;
    }

    /**
     * Instantiates the URL or IP address TextField
     */
    public void setNetAddrField() {
        netAddrField = new TextField();
        netAddrField.setPrefWidth(200);
    }

    /**
     * Instantiates the port TextField
     */
    public void setPortField() {
        portField = new TextField();
        portField.setPrefWidth(100);
    }

    /**
     * Instantiates the URL or IP address TextField
     */
    public void setUrlPathField() {
        urlPathField = new TextField();
        urlPathField.setPrefWidth(200);
    }

    /**
     * Instantiates the number of connections slider
     */
    public void setNumConnectionsSlider() {
        // new Slider(double min, double max, double initialValue)
        numConnectionsSlider = new Slider(1, SlowLoris.MAX_CONNECTIONS, 10);
        numConnectionsSlider.setPrefWidth(300);
        numConnectionsSlider.setBlockIncrement(1);
        numConnectionsSlider.setMajorTickUnit(5);
        numConnectionsSlider.setSnapToTicks(true);

        // Automatically update the label when the slider is dragged.
        numConnectionsSlider.setOnMouseReleased((MouseEvent me) -> {
            setNumConnectionsLabel();
        });
    }

    /**
     * Instantiates the radio button group for keep-alive connections
     */
    private void setKeepAliveGrp() {
        keepAliveGrp = new ToggleGroup();

        // Add code to change boolean keepAlive when a radio button is clicked
        keepAliveGrp.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle oldToggle, Toggle newToggle) {

                // Make sure a radio button is selected
                if (keepAliveGrp.getSelectedToggle() != null) {
                    // If so, check if it is the Use Keep Alive Abuse button
                    if(keepAliveGrp.getSelectedToggle().getUserData().equals("KeepAlive")) {
                        keepAlive = true;
                    } else {
                        keepAlive = false;
                    }
                }
            }
        });
    }

    /**
     * Retrieves the created keep-alive ToggleGroup
     * @return The ToggleGroup for keep-alive or not
     */
    public ToggleGroup getKeepAliveGrp() {
        return keepAliveGrp;
    }

    /**
     * Retrieves the created keep-alive RadioButton
     * @return The RadioButton for keep-alive abuse
     */
    public RadioButton getKeepAliveBtn(){
        // The String passed to the constructor is the label for the UI.
        keepAliveBtn = new RadioButton("Use Keep Alive Abuse");
        // The UserData is what to check for when a radio button is selected.
        keepAliveBtn.setUserData("KeepAlive");
        // Tell the RadioButton which ToggleGroup it belongs to.
        keepAliveBtn.setToggleGroup(keepAliveGrp);
        // Make it look pretty.
        keepAliveBtn.setMinWidth(150);
        return keepAliveBtn;
    }

    /**
     * Retrieves the created no keep-alive RadioButton
     * @return The RadioButton for not using keep-alive connections
     */
    public RadioButton getNoKeepBtn(){
        noKeepBtn = new RadioButton("Use Incomplete Requests");
        noKeepBtn.setUserData("NoKeep");
        noKeepBtn.setToggleGroup(keepAliveGrp);
        noKeepBtn.setMinWidth(150);
        return noKeepBtn;
    }

    /**
     * Instantiates the radio button group for GET or POST requests
     */
    private void setRequestTypeGrp() {
        requestTypeGrp = new ToggleGroup();

        requestTypeGrp.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle oldToggle, Toggle newToggle) {

                // Make sure a radio button is selected
                if (requestTypeGrp.getSelectedToggle() != null) {
                    // If so, check if it is the Use Keep Alive Abuse button
                    if(requestTypeGrp.getSelectedToggle().getUserData().equals("GET")) {
                        useGet = true;
                    } else {
                        useGet = false;
                    }
                }
            }
        });
    }

    /**
     * Retrieves the created request type ToggleGroup
     * @return The ToggleGroup for using GET or POST requests
     */
    public ToggleGroup getRequestTypeGrp() {
        return requestTypeGrp;
    }

    /**
     * Retrieves the created GET request RadioButton
     * @return The RadioButton for using GET requests
     */
    public RadioButton getGetBtn(){
        // The String passed to the constructor is the label for the UI.
        requestGetBtn = new RadioButton("Use GET Requests");
        // The UserData is what to check for when a radio button is selected.
        requestGetBtn.setUserData("GET");
        // Tell the RadioButton which ToggleGroup it belongs to.
        requestGetBtn.setToggleGroup(requestTypeGrp);
        // Make it look pretty.
        requestGetBtn.setMinWidth(150);
        return requestGetBtn;
    }

    /**
     * Retrieves the created POST request RadioButton
     * @return The RadioButton for using POST requests
     */
    public RadioButton getPostBtn(){
        requestPostBtn = new RadioButton("Use POST Requests");
        requestPostBtn.setUserData("POST");
        requestPostBtn.setToggleGroup(requestTypeGrp);
        requestPostBtn.setMinWidth(150);
        return requestPostBtn;
    }

    /**
     * Updates the status label's text.
     */
    public void setStatusLabel() {
        statusLabel.setText(status);
    }

    /**
     * Retrieves the TextField for the target IP or URL
     * @return TextField for IP or URL
     */
    public TextField getNetAddrField() {
        return netAddrField;
    }

    /**
     * Retrieves the TextField for the target port
     * @return TextField for target port
     */
    public TextField getPortField() { return portField; }

    /**
     * Retrieves the TextField for the target URL path
     * @return TextField for target URL path
     */
    public TextField getUrlPathField() { return urlPathField; }

    /**
     * Retrieves the status label
     * @return The status Label
     */
    public Label getStatusLabel() {
        return statusLabel;
    }

    /**
     * Retrieves the label for the IP or URL field
     * @return Label for IP or URL field
     */
    public Label getNetAddrLabel() {
        Label temp = new Label("Target Address:");
        temp.setMinWidth(100);
        return temp;
    }

    /**
     * Retrieves the label for the port field
     * @return Label for the port field
     */
    public Label getPortLabel() { return new Label("Target Port:"); }

    /**
     * Retrieves the label for the URL path field
     * @return Label for the URL path field
     */
    public Label getUrlPathLabel() {
        Label temp = new Label("URL path:");
        temp.setMinWidth(100);
        return temp;
    }

    /**
     * Retrieves the Slider for the number of connections
     * @return Slider for number of connections
     */
    public Slider getNumConnectionsSlider() {
        return numConnectionsSlider;
    }

    /**
     * Retrieves the Button that launches an attack
     * @return Button for attack
     */
    public Button getAttackButton() {
        return attackButton;
    }

    /**
     * Retrieves the Button that pings the target
     * @return Button for ping
     */
    public Button getPingButton() {
        return pingButton;
    }


    /**
     * Instantiates the Label for the number of connections slider
     */
    public void setNumConnectionsLabel() {
        numConnectionsLabel.setText(String.valueOf(Math.floor(numConnectionsSlider.getValue())));
    }

    /**
     * Retrieves the Label for the number of connections slider
     * @return Label for number of connections
     */
    public Label getNumConnectionsLabel() {
        return numConnectionsLabel;
    }
}

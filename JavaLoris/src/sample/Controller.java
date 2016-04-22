package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import SlowLoris.SlowLoris;
import javafx.scene.input.MouseEvent;

public class Controller {

    private Button attackButton;
    private Button pingButton;
    private TextField netAddrField;
    private TextField portField;
    private Slider numConnectionsSlider;

    private Label statusLabel;
    private Label numConnectionsLabel;

    private String status;

    public Controller() {
        status = "Waiting for command...";
        setNetAddrField();
        setPortField();
        setAttackButton();
        setPingButton();
        setNumConnectionsSlider();
        statusLabel = new Label();
        setStatusLabel();
        numConnectionsLabel = new Label();
        setNumConnectionsLabel();
    }

    public void setAttackButton() {
        attackButton = new Button("Launch Attack");
        attackButton.setPrefWidth(100);
        attackButton.setOnAction((ActionEvent e) -> {
            status = "Attacking...";
            setStatusLabel();
            if(netAddrField.getText().length() == 0) {
                status = "Please enter a target address";
                setStatusLabel();
                return;
            }
            SlowLoris sl = createSlowLoris();
            status = "Attempting attack...";
            setStatusLabel();
            Task async = new Task<Void>() {
                @Override public Void call() {
                    try {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("running attack");
                                        sl.performAttack();
                                        status = "Attack completed!";
                                        setStatusLabel();
                                    }
                                }
                        );
                    } catch (Exception e) {
                        status = "Attack error: " + e.getMessage();
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            Thread t = new Thread(async);
            t.start();
        });
    }

    public void setPingButton() {
        pingButton = new Button("Ping Target");
        pingButton.setPrefWidth(100);
        pingButton.setOnAction((ActionEvent e) -> {
            status = "Performing ping...";
            setStatusLabel();
            SlowLoris pingSL = createSlowLoris();
            Task async = new Task<Void>() {
                @Override public Void call() {
                    try {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("running ping");
                                        status = pingSL.pingTarget();
                                        setStatusLabel();
                                    }
                                }
                        );
                    } catch (Exception e) {
                        status = "Attack error";
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            Thread t = new Thread(async);
            t.start();
        });
    }

    private SlowLoris createSlowLoris() {
        SlowLoris sl;
        if(portField.getText().length() > 0) {
            int parsedPort = 80;
            try {
                parsedPort = Integer.parseInt(portField.getText());
            } catch (Exception ethree) {
                ethree.printStackTrace();
            } finally {
                sl = new SlowLoris((int) (Math.floor(numConnectionsSlider.getValue())),
                        netAddrField.getText(), parsedPort);
            }
        } else {
            sl = new SlowLoris((int) (Math.floor(numConnectionsSlider.getValue())),
                    netAddrField.getText());
        }
        return sl;
    }

    public void setNetAddrField() {
        netAddrField = new TextField();
        netAddrField.setPrefWidth(200);
    }

    public void setPortField() {
        portField = new TextField();
        portField.setPrefWidth(100);
    }

    public void setNumConnectionsSlider() {
        numConnectionsSlider = new Slider(1, SlowLoris.MAX_CONNECTIONS, 10);
        numConnectionsSlider.setPrefWidth(300);
        numConnectionsSlider.setBlockIncrement(1);
        numConnectionsSlider.setMajorTickUnit(5);
        numConnectionsSlider.setSnapToTicks(true);
        numConnectionsSlider.setOnMouseReleased((MouseEvent me) -> {
            setNumConnectionsLabel();
        });
    }

    public void setStatusLabel() {
        statusLabel.setText(status);
    }

    public TextField getNetAddrField() {
        return netAddrField;
    }

    public TextField getPortField() { return portField; }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public Label getNetAddrLabel() {
        Label temp = new Label("Target Address:");
        temp.setMinWidth(100);
        return temp;
    }

    public Label getPortLabel() { return new Label("Target Port:"); }

    public Slider getNumConnectionsSlider() {
        return numConnectionsSlider;
    }

    public Button getAttackButton() {
        return attackButton;
    }

    public Button getPingButton() {
        return pingButton;
    }

    public void setNumConnectionsLabel() {
        numConnectionsLabel.setText(String.valueOf(Math.floor(numConnectionsSlider.getValue())));
    }

    public Label getNumConnectionsLabel() {
        return numConnectionsLabel;
    }
}

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
    private TextField ipAddrField;
    private Slider numConnectionsSlider;

    private Label statusLabel;
    private Label numConnectionsLabel;

    private String status;

    private SlowLoris sl;

    public Controller() {
        status = "Waiting for command...";
        setIpAddrField();
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
            System.out.println("button pressed.");
            status = "Attacking...";
            setStatusLabel();
            sl = new SlowLoris((int) Math.floor(numConnectionsSlider.getValue()));
            if(sl.setIPAddr(ipAddrField.getText())) {
                status = "Attempting attack...";
                setStatusLabel();
                Task async = new Task<Void>() {
                    @Override public Void call() {
                        try {
                            Platform.runLater(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            sl.performAttack();
                                            status = "Attack completed!";
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
            } else {
                status = "IP Address Invalid";
                setStatusLabel();
            }
        });
    }

    public void setPingButton() {
        pingButton = new Button("Ping Target");
        pingButton.setPrefWidth(100);
        pingButton.setOnAction((ActionEvent e) -> {
            System.out.println("performing ping");
            status = "Performing ping...";
            try {
                Thread.sleep(1000);
            } catch(Exception ex) {

            }
            status = "Waiting for command...";
        });
    }

    public void setIpAddrField() {
        ipAddrField = new TextField();
        ipAddrField.setPrefWidth(200);
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

    public TextField getIPAddrField() {
        return ipAddrField;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public Label getIPAddrLabel() {
        return new Label("Target IP Address:");
    }

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

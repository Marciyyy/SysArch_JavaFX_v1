package com.dds.demo.sysarch_javafx_v1;

import OpcUaClient.ControlNodes;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;

public class SupervisorController
{

    //region System Buttons
    @FXML
    private Button SupLogInButton;
    //endregion

    //region Door Elements
    @FXML
    private Label SupervisorStatusDoorLabel;
    @FXML
    private AnchorPane SupervisorDoor1Open;
    @FXML
    private AnchorPane SupervisorDoor1Close;
    @FXML
    private AnchorPane SupervisorDoor2Open;
    @FXML
    private AnchorPane SupervisorDoor2Close;
    //endregion

    //region Motor Elements
    @FXML
    private Button SupervisorUp_v1;
    @FXML
    private Button SupervisorUp_v2;
    @FXML
    private Button SupervisorUp_Crawl;
    @FXML
    private Button SupervisorDown_v1;
    @FXML
    private Button SupervisorDown_v2;
    @FXML
    private Button SupervisorDown_Crawl;
    @FXML
    private ComboBox<Integer> SupervisorComboBoxUp;
    @FXML
    private ComboBox<Integer> SupervisorComboBoxDown;
    @FXML
    private Label SupervisorCrawlLabel;
    //endregion

    //region Floor Elements
    @FXML
    private Circle SupervisorStockLED4;
    @FXML
    private Circle SupervisorStockLED3;
    @FXML
    private Circle SupervisorStockLED2;
    @FXML
    private Circle SupervisorStockLED1;
    @FXML
    private Label SupervisorStatusCurrentFloor;
    //endregion

    @FXML
    private Label SupervisorStatusPLC;
    @FXML
    private Label SupervisorStatusSpeedLabel;
    @FXML
    private Label SupervisorStatusMotorState;
    @FXML
    private Button SupervisorResetSimulation;
    @FXML
    private Label SupervisorStatusElevatorID;
    @FXML
    private Label SupervisorStatusElevatorState;

    private int lastDoorstate = 2;
    //1: door open
    //2: door closed

    ControlNodes nodes = HelloApplication.getOpcUaService().getControlNodes();

    private static final Logger logger = LoggerFactory.getLogger(SupervisorController.class);




    public void initialize()
    {
        SupervisorComboBoxUp.getItems().addAll(1, 2, 3, 4, 5);
        SupervisorComboBoxDown.getItems().addAll(-1, -2, -3, -4, -5);


        HmiState state = HelloApplication.getHmiState();

        //Current Floor anzeigen lassen (Integer):
        SupervisorStatusCurrentFloor.textProperty().bind(
                HelloApplication.getHmiState()
                        .currentLevelProperty()
                        .asString()
        );


        //LEDs (Circles) für das aktuelle Stockwerk anzeigen lassen --> siehe method showCurrentFloor
        state.currentLevelProperty().addListener(
                (observable, oldLevel, newLevel) ->
                        showCurrentFloor(newLevel.intValue())
        );
        showCurrentFloor(state.currentLevelProperty().get());


        //Speed und Door state hinschreiben!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        SupervisorStatusSpeedLabel.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbSpeedProperty()
                        .asString()
        );

        //Motor state:
        SupervisorStatusMotorState.textProperty().bind(
                HelloApplication.getHmiState()
                        .motorReadyProperty()
                        .asString()
        );

        //PLC Cycle anzeige:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        SupervisorStatusPLC.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbCyclesProperty()
                        .asString()
        );

        SupervisorStatusElevatorID.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbAufzugIdProperty()
                        .asString()
        );

        SupervisorStatusElevatorState.textProperty().bind(
                HelloApplication.getHmiState()
                        .elevatorStateProperty()
        );


        //Door state zu Beginn setzten, falls User Scene etwas geändert hat
        if(HelloApplication.getHmiState().doorOpenProperty().get())
        {
            lastDoorstate = 1;
        }
        else if (HelloApplication.getHmiState().doorClosedProperty().get())
        {
            lastDoorstate = 2;
        }

        //Door state label:
        SupervisorStatusDoorLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            if (HelloApplication.getHmiState().doorOpenProperty().get())
                            {
                                DoorAnimationAndLastState(1);
                                return "Open";
                            }
                            else if (HelloApplication.getHmiState().doorClosedProperty().get())
                            {
                                DoorAnimationAndLastState(2);
                                return "Closed";
                            } else
                            {
                                if(lastDoorstate ==  1)
                                {
                                    return "Closing";
                                }
                                else if(lastDoorstate == 2)
                                {
                                    return "Opening";
                                }

                                return "---";
                            }
                        },
                        HelloApplication.getHmiState().doorOpenProperty(),
                        HelloApplication.getHmiState().doorClosedProperty()
                )
        );





        //Die Speed buttons setzten die OPC variablen auf true wenn gedrückt (setOnMousePressed) und auf false wenn losgelassen wird (setOnMouseReleased)
        bindHoldButton(SupervisorUp_v1, nodes.supervisorV1Up);
        bindHoldButton(SupervisorDown_v1, nodes.supervisorV1Down);

        bindHoldButton(SupervisorUp_v2, nodes.supervisorV2Up);
        bindHoldButton(SupervisorDown_v2, nodes.supervisorV2Down);

        //Für Crawl Button verknüpfte Logik mit jeweiliger ComboBox nötig
        bindCrawlHoldButton(SupervisorUp_Crawl, SupervisorComboBoxUp);
        bindCrawlHoldButton(SupervisorDown_Crawl, SupervisorComboBoxDown);


    }

    public void DoorAnimationAndLastState(int i)
    {
        if(i ==1)
        {
            lastDoorstate = 1;

            SupervisorDoor1Open.setOpacity(1);
            SupervisorDoor2Open.setOpacity(1);

            SupervisorDoor1Close.setOpacity(0);
            SupervisorDoor2Close.setOpacity(0);
        }
        else if (i == 2)
        {
            lastDoorstate = 2;
            SupervisorDoor1Open.setOpacity(0);
            SupervisorDoor2Open.setOpacity(0);

            SupervisorDoor1Close.setOpacity(1);
            SupervisorDoor2Close.setOpacity(1);

        }

    }


    private void showCurrentFloor(int currentLevel) {
        // Zuerst alle Kreise blau setzen.
        SupervisorStockLED1.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED2.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED3.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED4.setFill(javafx.scene.paint.Color.BLUE);

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();

        // Den Kreis des aktuellen Stockwerks grün setzen, wenn current Level passt und speed == 0.
        if (currentSpeed == 0)
        {
            switch (currentLevel)
            {
                case 1 -> SupervisorStockLED1.setFill(javafx.scene.paint.Color.GREEN);
                case 2 -> SupervisorStockLED2.setFill(javafx.scene.paint.Color.GREEN);
                case 3 -> SupervisorStockLED3.setFill(javafx.scene.paint.Color.GREEN);
                case 4 -> SupervisorStockLED4.setFill(javafx.scene.paint.Color.GREEN);

                default ->
                {
                    System.err.println("Invalid FloorLevel has been sent by the OPC UA Server: " + currentLevel);
                    logger.warn("Invalid FloorLevel has been sent by the OPC UA Server: {}", currentLevel);
                }
            }
        }
    }





    @FXML
    private void SupLogInButtonClick() throws IOException
    {
        logger.info("Supervisor Return to LogIn Button clicked");

        HelloApplication.getOpcUaService().write(nodes.supervisor, false).thenRun(() ->
                        logger.info("False Supervisor Signal has been send succesfully"))
                .exceptionally(error -> {
                    logger.error("False Supervisor signal sending has failed");
                    return null;
                });


        try {
            SceneManager.switchScene("LogIn.fxml");
        } catch (Exception exception) {
            logger.error("Switching to Scene LogIn.fxml has failed", exception);
        }
    }


    @FXML
    private void SupervisorResetSimulation_Click() throws IOException
    {
        logger.info("Supervisor Reset Simulation Button clicked");

        HelloApplication.getOpcUaService().write(nodes.reset, true).thenRun(() ->
                        logger.info("Reset Simulation signal sent successfully"))
                .exceptionally(error -> {
                    logger.error("Sending the reset Simulation signal failed");
                    return null;
                });

    }




    //Hilfs funktion für die V1, V2 Buttons
    private void bindHoldButton(@NonNull Button button, NodeId nodeId)
    {
        button.setOnMousePressed(event ->
        {
            HelloApplication.getOpcUaService().write(nodeId, true)
                    .thenRun(() -> logger.info("Speed command sent successfully for Node= {}", nodeId))
                    .exceptionally(error -> {
                        logger.error("OPC-UA write speeds failed. Node={}, value= true", nodeId, error);
                        return null;
                    });
        });

        button.setOnMouseReleased(event ->
        {
            HelloApplication.getOpcUaService().write(nodeId, false)
                    .thenRun(() -> logger.info("Stopping Speed command sent successfully for Node= {}", nodeId))
                    .exceptionally(error -> {
                        logger.error("OPC-UA write speeds failed. Node={}, value= false", nodeId, error);
                        return null;
                    });
        });
    }


    //Für den Crawl Button:
    private void bindCrawlHoldButton(@NonNull Button button, ComboBox<Integer> comboBox)
    {
        //Wenn gedrückt, dann soll die Crwal speed mit der speed aus der comboBox übertragen werden
        button.setOnMousePressed(event ->
        {
            Integer crawlSpeed = comboBox.getValue();

            if (crawlSpeed == null)
            {
                SupervisorCrawlLabel.setVisible(true);
                return;
            }

            SupervisorCrawlLabel.setVisible(false);

            HelloApplication.getOpcUaService()
                    .write(nodes.supervisorCrawl, crawlSpeed)
                    .thenRun(() -> logger.info("Crawl command sent successfully with speed {}", crawlSpeed))
                    .exceptionally(error -> {
                        logger.error("Crawl send with speed {} failed", crawlSpeed, error);
                        return null;
                    });
        });

        //Wenn losgelassen wird wird wert 0 übertragen
        button.setOnMouseReleased(event ->
        {
            HelloApplication.getOpcUaService()
                    .write(nodes.supervisorCrawl, 0)
                    .thenRun(() -> logger.info("Crawl command sent successfully with speed 0"))
                    .exceptionally(error -> {
                        logger.error("Crawl command send with speed 0 failed", error);
                        return null;
                    });



        });
    }











    //Unnütz weils darum geht das die buttons gedrückt bleiben und nicht nur einmal gedrückt werden:
    /*

    @FXML
    private void SupervisorUp_v1_Clicked() throws IOException
    {
        logger.info("Supervisor Up V1 Button clicked");


        HelloApplication.getOpcUaService().write(nodes.supervisorV1Up, true).thenRun(() ->
                        logger.info("Supervisor V1 Up signal sent succesfully"))
                .exceptionally(error -> {
                    logger.error("Sending the Supervisor V1 Up signal failed");
                    return null;
                });

    }

    @FXML
    private void SupervisorUp_v2_Clicked() throws IOException
    {
        logger.info("Supervisor Up V2 Button clicked");

        try {
            HelloApplication.getOpcUaService()
                    .write(nodes.supervisorV2Up, true)
                    .exceptionally(error -> {
                        error.printStackTrace();
                        return null;
                    });
            logger.info("Supervisor V2 Up signal sent succesfully");
        } catch (Exception exception) {
            logger.error("Sending the Supervisor V2 Up signal failed", exception);
        }

    }

    @FXML
    private void SupervisorUp_Crawl_Clicked() throws IOException
    {
        logger.info("Supervisor Up Crawl Button clicked");

        Integer crawlValue = SupervisorComboBoxUp.getValue();

        if (crawlValue == null)
        {
            SupervisorCrawlLabel.setVisible(true);
            return;
        }
        SupervisorCrawlLabel.setVisible(false);

        HelloApplication.getOpcUaService().write(nodes.supervisorCrawl, crawlValue).thenRun(() ->
                        logger.info("Supervisor Up Crawl signal sent with speed {}", crawlValue))
                .exceptionally(error -> {
                    logger.error("Sending Supervisor Up Crawl signal with speed {} failed", crawlValue, error);
                    return null;
                });

    }

    @FXML
    private void SupervisorDown_v1_Clicked() throws IOException
    {
        logger.info("Supervisor Down V1 Button clicked");

        try {
            HelloApplication.getOpcUaService()
                    .write(nodes.supervisorV1Down, true)
                    .exceptionally(error -> {
                        error.printStackTrace();
                        return null;
                    });
            logger.info("Supervisor V1 Down signal sent succesfully");
        } catch (Exception exception) {
            logger.error("Sending the Supervisor V1 Down signal failed", exception);
        }

    }

    @FXML
    private void SupervisorDown_v2_Clicked() throws IOException
    {
        logger.info("Supervisor Down V2 Button clicked");

        try {
            HelloApplication.getOpcUaService()
                    .write(nodes.supervisorV2Down, true)
                    .exceptionally(error -> {
                        error.printStackTrace();
                        return null;
                    });
            logger.info("Supervisor V2 Down signal sent succesfully");
        } catch (Exception exception) {
            logger.error("Sending the Supervisor V2 Down signal failed", exception);
        }

    }

    @FXML
    private void SupervisorDown_Crawl_Clicked() throws IOException
    {
        SupervisorCrawlLabel.setVisible(true);
        logger.info("Supervisor Down Crawl Button clicked");

    }

     */




}

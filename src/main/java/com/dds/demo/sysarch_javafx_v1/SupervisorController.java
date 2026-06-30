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
    @FXML
    private Button SupervisorCabinOpen;
    @FXML
    private Button SupervisorCabinClose;
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

        //Elevator Speed anzeige:
        SupervisorStatusSpeedLabel.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbSpeedProperty()
                        .asString()
        );

        //Motor state anzeige:
        SupervisorStatusMotorState.textProperty().bind(
                HelloApplication.getHmiState()
                        .motorReadyProperty()
                        .asString()
        );

        //PLC Cycle anzeige:
        SupervisorStatusPLC.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbCyclesProperty()
                        .asString()
        );

        //Elevator ID Anzeige:
        SupervisorStatusElevatorID.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbAufzugIdProperty()
                        .asString()
        );

        //Elevator State anzeige (Finite State Machine):
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





        //Die Speed buttons setzten die OPC variablen auf true, wenn gedrückt (setOnMousePressed) und auf false, sobald losgelassen wird (setOnMouseReleased)
        bindHoldButton(SupervisorUp_v1, nodes.supervisorV1Up);
        bindHoldButton(SupervisorDown_v1, nodes.supervisorV1Down);

        bindHoldButton(SupervisorUp_v2, nodes.supervisorV2Up);
        bindHoldButton(SupervisorDown_v2, nodes.supervisorV2Down);

        //Für Crawl Button verknüpfte Logik mit jeweiliger ComboBox nötig
        bindCrawlHoldButton(SupervisorUp_Crawl, SupervisorComboBoxUp);
        bindCrawlHoldButton(SupervisorDown_Crawl, SupervisorComboBoxDown);


    }

    //Hilfsfunktion für die Door State anzeige und die Door Animation
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


    //Current Floor LED anzeige. LED wird grün, sobald current floor auf dem floor der LED ist
    private void showCurrentFloor(int currentLevel) {
        // Zuerst alle Kreise blau setzen.
        SupervisorStockLED1.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED2.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED3.setFill(javafx.scene.paint.Color.BLUE);
        SupervisorStockLED4.setFill(javafx.scene.paint.Color.BLUE);



        // Den Kreis des aktuellen Stockwerks grün setzen, wenn current Level passt
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




    //Hilfs funktion für die V1, V2 Buttons (gedrückt halten und wieder loslassen sette/resettet OPC variablen)
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


    @FXML
    private void SupervisorCabinCloseClick () throws IOException
    {
        logger.info("SupervisorCabinClose Button clicked");
        boolean doorOpen = HelloApplication.getHmiState().doorOpenProperty().get();

        boolean doorClosed = HelloApplication.getHmiState().doorClosedProperty().get();

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();

        //Speed auslesen!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        if(doorClosed && (currentSpeed == 0) )
        {
            logger.info("Door couldn't be closed because it's already closed");
            return;
        }

        if(!doorOpen && !doorClosed && (currentSpeed == 0) )
        {
            if(lastDoorstate == 1)
            {
                logger.info("Door is already currently closing");
                return;
            }
            else if (lastDoorstate == 2)
            {
                logger.info("Door couldn't be opend because it's currently opening");
                return;
            }
        }

        if( (currentSpeed == 0) && (doorOpen == true) )
        {
            HelloApplication.getOpcUaService().write(nodes.closeDoor, true).thenRun(() ->
                            logger.info("Close Door signal send via OPC UA"))
                    .exceptionally(error -> {
                        logger.error("Close Door signal couldn't be send");
                        return null;
                    });
        }


    }

    @FXML
    private void SupervisorCabinOpenClick () throws IOException
    {
        logger.info("SupervisorCabinOpen Button clicked");
        boolean doorOpen = HelloApplication.getHmiState().doorOpenProperty().get();

        boolean doorClosed = HelloApplication.getHmiState().doorClosedProperty().get();

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();

        //Speed auslesen!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Schauen ob türe breits offen ist
        if(doorOpen && (currentSpeed == 0) )
        {
            logger.info("Door couldn't be opend because it's already open");
            return;
        }
        //Checken ob sich die tür aktuell öffnet oder schliest:
        if(!doorOpen && !doorClosed)
        {
            if(lastDoorstate == 1)
            {
                logger.info("Door couldn't be opend because it's currently closing");
                return;
            }
            else if (lastDoorstate == 2)
            {
                logger.info("Door is already currently opening");
                return;
            }
        }

        if( (currentSpeed == 0) && (doorClosed == true) )
        {

            HelloApplication.getOpcUaService().write(nodes.openDoor, true).thenRun(() ->
                            logger.info("Open door signal has been send successfully"))
                    .exceptionally(error -> {
                        logger.error("Open Door signal couldn't be send");
                        return null;
                    });
        }

    }




}

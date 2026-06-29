package com.dds.demo.sysarch_javafx_v1;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import com.dds.demo.sysarch_javafx_v1.HelloApplication;
import OpcUaClient.ControlNodes;

import javafx.animation.PauseTransition;
import javafx.util.Duration;


import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class UserController {

    //region System Elements:
    @FXML
    private Button UserReturnToLogInButton;
    @FXML
    private Button UserResetSimulation;
    //endregion

    //region Cabin Elements:
    @FXML
    private Button UserCabinStock1;
    @FXML
    private Button UserCabinStock2;
    @FXML
    private Button UserCabinStock3;
    @FXML
    private Button UserCabinStock4;
    @FXML
    private Button UserCabinOpen;
    @FXML
    private Button UserCabinClose;
    @FXML
    private Button UserCabinStopp;
    //endregion

    //region Floor Elements:
    @FXML
    private Button UserCall4;
    @FXML
    private Button UserCall3Up;
    @FXML
    private Button UserCall3Down;
    @FXML
    private Button UserCall2Up;
    @FXML
    private Button UserCall2Down;
    @FXML
    private Button UserCall1;
    @FXML
    private Circle UserStockLED4;
    @FXML
    private Circle UserStockLED3;
    @FXML
    private Circle UserStockLED2;
    @FXML
    private Circle UserStockLED1;
    //endregion

    //region Door Elements:
    @FXML
    private AnchorPane UserDoor1Open;
    @FXML
    private AnchorPane UserDoor1Close;
    @FXML
    private AnchorPane UserDoor2Open;
    @FXML
    private AnchorPane UserDoor2Close;
    @FXML
    private Label UserStatusDoorLabel;
    //endregion

    //region Other Status Labels:
    @FXML
    private Label UserStatusCurrentFloor;
    @FXML
    private Label UserStatusNextFloor;
    @FXML
    private Label UserStatusShowDirection;
    @FXML
    private Label UserStatusMotorState;
    @FXML
    private Label UserStatusSpeedLabel;

    //endregion

    private boolean isEmergency = false;

    ControlNodes nodes = HelloApplication.getOpcUaService().getControlNodes();

    //Paltzhalter für die echte speed:
    private int speed;

    //TEst
    private boolean a;

    private int lastDoorstate = 2;
    //1 = door open
    //2 = door closed




    private static final Logger logger = LoggerFactory.getLogger(UserController.class);





    @FXML
    public void initialize()
    {
        UserDoor1Open.setOpacity(0);
        UserDoor2Open.setOpacity(0);


        HmiState state = HelloApplication.getHmiState();

        //Current Floor anzeigen lassen (Integer):
        UserStatusCurrentFloor.textProperty().bind(
                HelloApplication.getHmiState()
                        .currentLevelProperty()
                        .asString()
        );



        //Neue method:
        state.currentLevelProperty().addListener(
                (observable, oldLevel, newLevel) -> {
                    showCurrentFloor(newLevel.intValue());
                    resetArrivedFloorButtons();
                }
        );

        //Alt:
        /*
        //LEDs (Circles) für das aktuelle Stockwerk anzeigen lassen --> siehe method showCurrentFloor
        state.currentLevelProperty().addListener(
                (observable, oldLevel, newLevel) ->
                        showCurrentFloor(newLevel.intValue())
        );
         */
        showCurrentFloor(state.currentLevelProperty().get());
        //Neu:
        resetArrivedFloorButtons();

        //Next Floor anzeigen lassen (Integer):
        UserStatusNextFloor.textProperty().bind(
                HelloApplication.getHmiState()
                        .nextLevelProperty()
                        .asString()
        );

        //Current Direction anzeigen lassen (String):
        UserStatusShowDirection.textProperty().bind(
                HelloApplication.getHmiState()
                        .directionProperty()
        );

        //Door state anzeigen: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Geht so nicht!!!! --> Eigene logik einbauen.
        // Wenn state: door open(true) = open
        //Wenn state: door closed(true) = closed
        //Wenn beide OPC Variable false, dann last state merken und dann dementsprechen entweder opening oder closing
        //--> Last state = open --> closing
        // Last state = closed --> opening
        /*
        UserStatusDoorLabel.textProperty().bind(
                HelloApplication.getHmiState()
                        .directionProperty()
        );
        */

        //Speed anzeige: Keine OPC Variable bisher !!!!!!!!!!!!!!!!!!!!!!
        /*

         */

        //Motor state:

        UserStatusMotorState.textProperty().bind(
                HelloApplication.getHmiState()
                        .motorReadyProperty()
                        .asString()
        );

        //Speed:
        UserStatusSpeedLabel.textProperty().bind(
                HelloApplication.getHmiState()
                        .mbSpeedProperty()
                        .asString()
        );


        //Zu Beginn den lastDoorstate definieren, falls die Supervisor scene etwas verändert hat
        if(HelloApplication.getHmiState().doorOpenProperty().get())
        {
            lastDoorstate = 1;
        }
        else if (HelloApplication.getHmiState().doorClosedProperty().get())
        {
            lastDoorstate = 2;
        }

        //Door state Label
        UserStatusDoorLabel.textProperty().bind(
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


        //Hillfe um allgemien verschiedene dinge anzeigen lassen:
        /*
        currentFloorLabel.textProperty().bind(
        HelloApplication.getHmiState()
                .currentLevelProperty()
                .asString()
        );


        elevatorStateLabel.textProperty().bind(
        HelloApplication.getHmiState()
                .elevatorStateProperty()
        );

        doorOpenLabel.textProperty().bind(
        HelloApplication.getHmiState()
                .doorOpenProperty()
                .asString()
        );

         */






    }

    public void DoorAnimationAndLastState(int i)
    {
        if(i ==1)
        {
            lastDoorstate = 1;
            UserDoor1Open.setOpacity(1);
            UserDoor2Open.setOpacity(1);

            UserDoor1Close.setOpacity(0);
            UserDoor2Close.setOpacity(0);
        }
        else if (i == 2)
        {
            lastDoorstate = 2;
            UserDoor1Open.setOpacity(0);
            UserDoor2Open.setOpacity(0);

            UserDoor1Close.setOpacity(1);
            UserDoor2Close.setOpacity(1);

        }

    }

    //Method um die Floor LEDS richtig anzeigen zu lassen:
    private void showCurrentFloor(int currentLevel) {
        // Zuerst alle Kreise blau setzen.
        UserStockLED1.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED2.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED3.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED4.setFill(javafx.scene.paint.Color.BLUE);




        // Den Kreis des aktuellen Stockwerks grün setzen, wenn current Level passt
        switch (currentLevel)
            {
                case 1 -> UserStockLED1.setFill(javafx.scene.paint.Color.GREEN);
                case 2 -> UserStockLED2.setFill(javafx.scene.paint.Color.GREEN);
                case 3 -> UserStockLED3.setFill(javafx.scene.paint.Color.GREEN);
                case 4 -> UserStockLED4.setFill(javafx.scene.paint.Color.GREEN);

                default ->
                {
                    System.err.println("Invalid FloorLevel has been sent by the OPC UA Server: " + currentLevel);
                    logger.warn("Invalid FloorLevel has been sent by the OPC UA Server: {}", currentLevel);
                }
            }


    }

    private void removeFocusAfterOneSecond(Button button)
    {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(1)
        );

        pause.setOnFinished(event -> {
            if (button.getScene() != null) {
                button.getScene()
                        .getRoot()
                        .setFocusTraversable(true);

                button.getScene()
                        .getRoot()
                        .requestFocus();
            }
        });

        pause.play();
    }

    private void resetArrivedFloorButtons() {
        int currentFloor = HelloApplication.getHmiState().currentLevelProperty().get();

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();
        // Während der Fahrt keine Anforderungs-Buttons zurücksetzen.
        if (currentSpeed != 0) {
            return;
        }

        switch (currentFloor) {
            case 1 -> {
                //UserCabinStock1.setStyle("");
                //UserCall1.setStyle("");
                UserCabinStock1.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall1.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

            }

            case 2 -> {
                //UserCabinStock2.setStyle("");
                //UserCall2Up.setStyle("");
                //UserCall2Down.setStyle("");
                UserCabinStock2.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall2Up.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall2Down.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

            }

            case 3 -> {
                UserCabinStock3.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                //UserCall3Up.setStyle("");
                //UserCall3Down.setStyle("");
                UserCall3Up.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall3Down.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

            }

            case 4 -> {
                //UserCabinStock4.setStyle("");
                UserCabinStock4.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall4.setStyle("-fx-background-color: #f4f4f4;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

                //UserCall4.setStyle("");
            }

            default -> logger.warn("Cannot reset buttons: invalid floor {}", currentFloor);
        }
    }



    //region System Button functions
    @FXML
    protected void UserReturnToLoginButtonClick() throws Exception
    {
        logger.info("User Return to LogIn Button clicked");
        try {
            SceneManager.switchScene("LogIn.fxml");
        } catch (Exception exception) {
            logger.error("Switching to Scene LogIn.fxml has failed", exception);
        }
    }
    @FXML
    protected void UserResetSimulationClick () throws Exception
    {
        logger.info("User Reset Simulation Button clicked");

        HelloApplication.getOpcUaService().write(nodes.reset, true).thenRun(() ->
                        logger.info("Reset Simulation signal sent successfully"))
                .exceptionally(error -> {
                    logger.error("Sending the reset Simulation signal failed");
                    return null;
                });



    }

    //endregion

    //region Cabin Buttons
    @FXML
    protected void UserCabinStock1Click() throws Exception
    {
        logger.info("UserCabinStock1 Button clicked");
        FloorCheck(1);
    }

    @FXML
    protected void UserCabinStock2Click() throws Exception
    {
        logger.info("UserCabinStock2 Button clicked");
        FloorCheck(2);
    }

    @FXML
    protected void UserCabinStock3Click() throws Exception
    {
        logger.info("UserCabinStock3 Button clicked");
        FloorCheck(3);
    }

    @FXML
    protected void UserCabinStock4Click() throws Exception
    {
        logger.info("UserCabinStock4 Button clicked");
        FloorCheck(4);
    }

    @FXML
    protected  void UserCabinOpenClick() throws Exception
    {
        logger.info("UserCabinOpen Button clicked");
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

    @FXML
    protected  void UserCabinCloseClick() throws Exception
    {
        logger.info("UserCabinClose Button clicked");
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
    protected  void UserCabinStoppClick() throws Exception
    {
        logger.info("User Emergency Stop Button clicked");
        if(isEmergency)
        {

            HelloApplication.getOpcUaService().write(nodes.emergencyStop, false)
                    .thenRun(() ->
                            {
                                logger.info("Emergency Stop set to false");
                                UserCabinStopp.setStyle("-fx-background-color: white;" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                                isEmergency = false;
                            })
                    .exceptionally(error -> {
                        logger.error("Setting emergency stop to false has failed");
                        return null;
                    });

        }
        else
        {
            HelloApplication.getOpcUaService().write(nodes.emergencyStop, true)
                    .thenRun(() ->
                    {
                        logger.info("Emergency Stop set to true");
                        UserCabinStopp.setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                        isEmergency = true;
                    })
                    .exceptionally(error -> {
                        logger.error("Setting emergency stop to true has failed");
                        return null;
                    });

        }
    }

    //endregion

    //region Floor call methods
    @FXML
    protected void UserCall4Click() throws Exception
    {
        logger.info("UserCall4 Button clicked");
        FloorCheckOutside(4,6);
    }
    @FXML
    protected void UserCall3UpClick() throws Exception
    {
        logger.info("UserCall3Up Button clicked");
        FloorCheckOutside(3,4);
    }
    @FXML
    protected void UserCall3DownClick() throws Exception
    {
        logger.info("UserCall3Down Button clicked");
        FloorCheckOutside(3,5);
    }
    @FXML
    protected void UserCall2UpClick() throws Exception
    {
        logger.info("UserCall2Up Button clicked");
        FloorCheckOutside(2,2);
    }
    @FXML
    protected void UserCall2DownClick() throws Exception
    {
        logger.info("UserCall2Down Button clicked");
        FloorCheckOutside(2,3);
    }
    @FXML
    protected void UserCall1Click() throws Exception
    {
        logger.info("UserCall1 Button clicked");
        FloorCheckOutside(1,1);
    }
    //endregion



    private void FloorCheck(int FloorLevel)
    {
        //Einmal speed und current level auslesen
        int currentFloor = HelloApplication.getHmiState().currentLevelProperty().get();

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();



        //Wenn aufzug gerade an diesem stockwerk steht, dann nix machen:
        if ( (currentFloor == FloorLevel ) && (currentSpeed == 0 ))
        {
            logger.info("FloorLevel request denied. Elevator already at Level: {}", FloorLevel);
            return;
        }

        //Wenn aufzug entweeder nicht auf dem stock ist oder zwar noch der stock angezeigt wird aber er sich schon wieder bewegt:
        if( (currentFloor != FloorLevel) || ( (currentFloor == FloorLevel) && (currentSpeed != 0) ) )
        {
            //Richtige Variable setzten:
            switch (FloorLevel)
            {
                case 1 ->
                        {
                            HelloApplication.getOpcUaService().write(nodes.insideLevel1, true).thenRun(() ->
                                            logger.info("Cabin request send for FloorLevel {}", FloorLevel))
                                    .exceptionally(error -> {
                                        logger.error("Cabin request for Level 1 failed");
                                        return null;
                                    });
                            UserCabinStock1.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                            removeFocusAfterOneSecond(UserCabinStock1);
                        }
                case 2 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.insideLevel2, true).thenRun(() ->
                                    logger.info("Cabin request send for FloorLevel {}", FloorLevel))
                            .exceptionally(error -> {
                                logger.error("Cabin request for Level 2 failed");
                                return null;
                            });
                    UserCabinStock2.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" +  "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCabinStock2);
                }
                case 3 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.insideLevel3, true).thenRun(() ->
                                    logger.info("Cabin request send for FloorLevel {}", FloorLevel))
                            .exceptionally(error -> {
                                logger.error("Cabin request for Level 3 failed");
                                return null;
                            });
                    UserCabinStock3.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCabinStock3);
                }
                case 4 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.insideLevel4, true).thenRun(() ->
                                    logger.info("Cabin request send for FloorLevel {}", FloorLevel))
                            .exceptionally(error -> {
                                logger.error("Cabin request for Level 4 failed");
                                return null;
                            });
                    UserCabinStock4.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCabinStock4);
                }

            }


        }

    }



    private void FloorCheckOutside(int FloorLevel, int UpDown)
    {
        //Einmal speed und einmal current level auslesen
        int currentFloor = HelloApplication.getHmiState().currentLevelProperty().get();

        int currentSpeed = HelloApplication.getHmiState().mbSpeedProperty().get();




        //Wenn aufzug gerade an diesem stockwerk steht, dann nix machen:
        if ( (currentFloor == FloorLevel) && (currentSpeed == 0) )
        {
            logger.info("Floor request denied. Elevator already at Floor: {}", FloorLevel);
            return;
        }

        //Wenn aufzug entweeder nicht auf dem stock ist oder zwar noch der stock angezeigt wird aber er sich shcon wieder bewegt:
        if( (currentFloor != FloorLevel) || ( (currentFloor == FloorLevel) && (currentSpeed != 0) ) )
        {
            //Richtige Variable setzten:
            switch (UpDown)
            {
                case 1 ->
                {
                        HelloApplication.getOpcUaService().write(nodes.outsideLevel1Up, true).thenRun(() ->
                                        logger.info("Outside request send for Level 1 Up "))
                                .exceptionally(error -> {
                                    logger.error("Outside request for Level 1 Up failed");
                                    return null;
                                });
                        UserCall1.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                        removeFocusAfterOneSecond(UserCall1);

                }
                case 2 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.outsideLevel2Up, true).thenRun(() ->
                                    logger.info("Outside request send for Level 2 Up "))
                            .exceptionally(error -> {
                                logger.error("Outside request for Level 2 Up failed");
                                return null;
                            });
                    UserCall2Up.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

                    removeFocusAfterOneSecond(UserCall2Up);
                }
                case 3 ->
                {

                    HelloApplication.getOpcUaService().write(nodes.outsideLevel2Down, true).thenRun(() ->
                                    logger.info("Outside request send for Level 2 Down "))
                            .exceptionally(error -> {
                                logger.error("Outside request for Level 2 Down failed");
                                return null;
                            });
                    UserCall2Down.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCall2Down);
                }
                case 4 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.outsideLevel3Up, true).thenRun(() ->
                                    logger.info("Outside request send for Level 3 Up "))
                            .exceptionally(error -> {
                                logger.error("Outside request for Level 3 Up failed");
                                return null;
                            });
                    UserCall3Up.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCall3Up);

                }
                case 5 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.outsideLevel3Down, true).thenRun(() ->
                                    logger.info("Outside request send for Level 3 Down "))
                            .exceptionally(error -> {
                                logger.error("Outside request for Level 3 Down failed");
                                return null;
                            });
                    UserCall3Down.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCall3Down);
                }
                case 6 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.outsideLevel4Down, true).thenRun(() ->
                                    logger.info("Outside request send for Level 4 Down "))
                            .exceptionally(error -> {
                                logger.error("Outside request for Level 4 Down failed");
                                return null;
                            });
                    UserCall4.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                    removeFocusAfterOneSecond(UserCall4);
                }

            }


        }

    }







}

package com.dds.demo.sysarch_javafx_v1;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import com.dds.demo.sysarch_javafx_v1.HelloApplication;
import OpcUaClient.ControlNodes;




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

        //LEDs (Circles) für das aktuelle Stockwerk anzeigen lassen --> siehe method showCurrentFloor
        state.currentLevelProperty().addListener(
                (observable, oldLevel, newLevel) ->
                        showCurrentFloor(newLevel.intValue())
        );
        showCurrentFloor(state.currentLevelProperty().get());

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


        //Neu:
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

    //Method um die Floor LEDS richtig anzeigen zu lassen:
    private void showCurrentFloor(int currentLevel) {
        // Zuerst alle Kreise blau setzen.
        UserStockLED1.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED2.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED3.setFill(javafx.scene.paint.Color.BLUE);
        UserStockLED4.setFill(javafx.scene.paint.Color.BLUE);

        // Den Kreis des aktuellen Stockwerks grün setzen.
        switch (currentLevel)
        {
            case 1 -> UserStockLED1.setFill(javafx.scene.paint.Color.GREEN);
            case 2 -> UserStockLED2.setFill(javafx.scene.paint.Color.GREEN);
            case 3 -> UserStockLED3.setFill(javafx.scene.paint.Color.GREEN);
            case 4 -> UserStockLED4.setFill(javafx.scene.paint.Color.GREEN);

            default ->
            {
                System.err.println("Invalid FloorLevel has been send by the OPC UA Server: " + currentLevel);
                logger.warn("Invalid FloorLevel has been send by the OPC UA Server");
            }
        }

    //Button Farben zurücksetzten:
        switch (currentLevel)
        {
            case 1 ->
            {
                UserCabinStock1.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall1.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
            }
            case 2 ->
            {
                UserCall2Up.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall2Down.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCabinStock2.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
            }
            case 3 ->
            {
                UserCall3Up.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCall3Down.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCabinStock3.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
            }
            case 4 ->
            {
                UserCall4.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
                UserCabinStock4.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
            }

            default -> System.err.println("Ungültiges Stockwerk vom OPC-UA-Server: " + currentLevel);
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
        boolean doorOpen = HelloApplication.getHmiState()
                .doorOpenProperty()
                .get();

        boolean doorClosed = HelloApplication.getHmiState()
                .doorClosedProperty()
                .get();

        //Speed auslesen!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        if(doorOpen)
        {
            logger.info("Door couldn't be opend because it's already open");
            return;
        }

        if( (speed == 0) && (doorClosed == true) )
        {

            HelloApplication.getOpcUaService().write(nodes.openDoor, true).thenRun(() ->
                            logger.info("Open door signal has been send successfully"))
                    .exceptionally(error -> {
                        logger.error("Open Door signal couldn't be send");
                        return null;
                    });

            //Animation:
            try {
                UserDoor1Open.setOpacity(1);
                UserDoor2Open.setOpacity(1);

                UserDoor1Close.setOpacity(0);
                UserDoor2Close.setOpacity(0);
                logger.info("Opening Door animation successful");
            } catch (Exception exception) {
                logger.error("Opening Door animation failed", exception);
            }

        }

    }

    @FXML
    protected  void UserCabinCloseClick() throws Exception
    {
        logger.info("UserCabinClose Button clicked");
        boolean doorOpen = HelloApplication.getHmiState()
                .doorOpenProperty()
                .get();

        boolean doorClosed = HelloApplication.getHmiState()
                .doorClosedProperty()
                .get();

        //Speed auslesen!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        if(doorClosed)
        {
            logger.info("Door couldn't be closed because it's already closed");
            return;
        }

        if( (speed == 0) && (doorOpen == true) )
        {

            HelloApplication.getOpcUaService().write(nodes.closeDoor, true).thenRun(() ->
                            logger.info("Close Door signal send via OPC UA"))
                    .exceptionally(error -> {
                        logger.error("Close Door signal couldn't be send");
                        return null;
                    });


            //Animation:
            try {
                UserDoor1Open.setOpacity(0);
                UserDoor2Open.setOpacity(0);

                UserDoor1Close.setOpacity(1);
                UserDoor2Close.setOpacity(1);

                logger.info("Closing Door animation successful");
            } catch (Exception exception) {
                logger.error("Closing Door animation failed", exception);
            }

        }

    }

    @FXML
    protected  void UserCabinStoppClick() throws Exception
    {
        logger.info("User Emergency Stop Button clicked");
        if(isEmergency)
        {

            HelloApplication.getOpcUaService().write(nodes.emergencyStop, false).thenRun(() ->
                            logger.info("Emergency Stop set to false"))
                    .exceptionally(error -> {
                        logger.error("Setting emergency stop to false has failed");
                        return null;
                    });
            UserCabinStopp.setStyle("-fx-background-color: white;" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

        }
        else
        {

            HelloApplication.getOpcUaService().write(nodes.emergencyStop, true).thenRun(() ->
                            logger.info("Emergency Stop set to true"))
                    .exceptionally(error -> {
                        logger.error("Setting emergency stop to true has failed");
                        return null;
                    });
            UserCabinStopp.setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

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
        //EInmal speed un einmal current level auslesen
        int currentFloor = HelloApplication.getHmiState()
                .currentLevelProperty()
                .get();

        //hier noch speed auslesen:


        //Wenn aufzug gerade an diesem stockwerk steht, dann nix machen:
        if (currentFloor == FloorLevel && speed == 0)
        {
            logger.info("FloorLevel request denied. Elevator already at Level: {}", FloorLevel);
            return;
        }

        //Wenn aufzug entweeder nicht auf dem stock ist oder zwar noch der stock angezeigt wird aber er sich shcon wieder bewegt:
        if( (currentFloor != FloorLevel) || (currentFloor == FloorLevel && speed != 0) )
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

                        }
                case 2 ->
                {
                    HelloApplication.getOpcUaService().write(nodes.insideLevel2, true).thenRun(() ->
                                    logger.info("Cabin request send for FloorLevel {}", FloorLevel))
                            .exceptionally(error -> {
                                logger.error("Cabin request for Level 2 failed");
                                return null;
                            });
                    UserCabinStock2.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

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

                }

            }


        }

    }



    private void FloorCheckOutside(int FloorLevel, int UpDown)
    {
        //EInmal speed un einmal current level auslesen
        int currentFloor = HelloApplication.getHmiState()
                .currentLevelProperty()
                .get();

        //hier noch speed auslesen:


        //Wenn aufzug gerade an diesem stockwerk steht, dann nix machen:
        if (currentFloor == FloorLevel && speed == 0)
        {
            logger.info("Floor request denied. Elevator already at Floor: {}", FloorLevel);
            return;
        }

        //Wenn aufzug entweeder nicht auf dem stock ist oder zwar noch der stock angezeigt wird aber er sich shcon wieder bewegt:
        if( (currentFloor != FloorLevel) || (currentFloor == FloorLevel && speed != 0) )
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

                }

            }


        }

    }







}

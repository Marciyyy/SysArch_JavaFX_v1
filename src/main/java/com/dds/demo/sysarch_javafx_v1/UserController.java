package com.dds.demo.sysarch_javafx_v1;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import com.dds.demo.sysarch_javafx_v1.HelloApplication;
import OpcUaClient.ControlNodes;


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





    @FXML
    public void initialize()
    {
        UserDoor1Open.setOpacity(0);
        UserDoor2Open.setOpacity(0);
    }



    //region System Button functions
    @FXML
    protected void UserReturnToLoginButtonClick() throws Exception
    {
        SceneManager.switchScene("LogIn.fxml");
    }
    @FXML
    protected void UserResetSimulationClick () throws Exception
    {

    }

    //endregion

    //region Cabin Buttons
    @FXML
    protected void UserCabinStock1Click() throws Exception
    {
        UserCabinStock1.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

        HelloApplication.getOpcUaService()
                .write(ControlNodes.CMD_START, true)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return null;
                });
    }

    @FXML
    protected void UserCabinStock2Click() throws Exception
    {
        UserCabinStock2.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

    }

    @FXML
    protected void UserCabinStock3Click() throws Exception
    {
        UserCabinStock3.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

    }

    @FXML
    protected void UserCabinStock4Click() throws Exception
    {
        UserCabinStock4.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3);" + "-fx-border-color: black;" + "-fx-border-width: 1.5;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");

    }

    @FXML
    protected  void UserCabinOpenClick() throws Exception
    {
        UserDoor1Open.setOpacity(1);
        UserDoor2Open.setOpacity(1);

        UserDoor1Close.setOpacity(0);
        UserDoor2Close.setOpacity(0);

    }

    @FXML
    protected  void UserCabinCloseClick() throws Exception
    {
        UserStockLED1.setFill(javafx.scene.paint.Color.RED);

        UserDoor1Open.setOpacity(0);
        UserDoor2Open.setOpacity(0);

        UserDoor1Close.setOpacity(1);
        UserDoor2Close.setOpacity(1);
    }

    @FXML
    protected  void UserCabinStoppClick() throws Exception
    {
        if(isEmergency)
        {
            isEmergency = false;
            UserCabinStopp.setStyle("-fx-background-color: white;" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
        }
        else
        {
            isEmergency = true;
            UserCabinStopp.setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);" + "-fx-border-color: red;" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5");
        }
    }

    //endregion

    //region Floor call methods
    @FXML
    protected void UserCall4Click() throws Exception
    {

    }
    @FXML
    protected void UserCall3UpClick() throws Exception
    {

    }
    @FXML
    protected void UserCall3DownClick() throws Exception
    {

    }
    @FXML
    protected void UserCall2UpClick() throws Exception
    {

    }
    @FXML
    protected void UserCall2DownClick() throws Exception
    {

    }
    @FXML
    protected void UserCall1Click() throws Exception
    {

    }
    //endregion





}

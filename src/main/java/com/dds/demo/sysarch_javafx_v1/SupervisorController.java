package com.dds.demo.sysarch_javafx_v1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    //endregion

    @FXML
    private Label SupervisorStatusPLC;
    @FXML
    private Label SupervisorStatusSpeedLabel;

    private static final Logger logger = LoggerFactory.getLogger(SupervisorController.class);




    public void initialize()
    {
        SupervisorComboBoxUp.getItems().addAll(1, 2, 3, 4, 5);
        SupervisorComboBoxDown.getItems().addAll(-1, -2, -3, -4, -5);
    }



    @FXML
    private void SupLogInButtonClick() throws IOException
    {
        SceneManager.switchScene("LogIn.fxml");
    }


}

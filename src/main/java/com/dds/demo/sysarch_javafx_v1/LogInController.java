package com.dds.demo.sysarch_javafx_v1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogInController
{

    //UI components:
    @FXML
    private Label LogInHeaderTextField;
    @FXML
    private Button LogInUserButton;
    @FXML
    private TextField LogInUsernameTextField;
    @FXML
    private PasswordField LogInPasswordField;
    @FXML
    private Label LogInErrorTextBox;
    @FXML
    private Button LogInConfirmButton;


    //Normal Attributes
    private static final String Supervisor_UserName = "supervisor";
    private static final String Supervisor_Password = "1234";

    private static final Logger logger = LoggerFactory.getLogger(LogInController.class);


    //initialize wird beim laden der FXML aufgerufen (sozusagen wie ein constructor)
    @FXML
    public void initialize()
    {
        //Damit passwordfield auch auf enter reagiert und nicht nur auf den LogIn Button:
        LogInPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
            {
                try {
                    LogInConfirmButtonClick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }



    @FXML
    private void LogInUserButtonClick() throws IOException
    {
        logger.info("LogIn User Button clicked");
        try {
            SceneManager.switchScene("User.fxml");
        } catch (Exception exception) {
            logger.error("Switching to Scene User.fxml has failed", exception);
        }
    }

    @FXML
    private void LogInConfirmButtonClick () throws IOException
    {
        logger.info("LogIn Confirm Button clicked or pressed Enter");
        String username = LogInUsernameTextField.getText();
        String password = LogInPasswordField.getText();

        if ( username.equals(Supervisor_UserName) && password.equals(Supervisor_Password) )
        {
            logger.info("Username and Password match successfully");
            LogInErrorTextBox.setText("");

            try {
                SceneManager.switchScene("Supervisor.fxml");
            } catch (Exception exception) {
                logger.error("Switching to Scene Supervisor.fxml has failed", exception);
            }

        }
        else
        {
            logger.warn("Username and Password input wrong");
            LogInErrorTextBox.setText("Username or Password wrong!");
        }
    }


}

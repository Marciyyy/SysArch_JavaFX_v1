package com.dds.demo.sysarch_javafx_v1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

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
        SceneManager.switchScene("User.fxml");
    }

    @FXML
    private void LogInConfirmButtonClick () throws IOException
    {
        String username = LogInUsernameTextField.getText();
        String password = LogInPasswordField.getText();

        if ( username.equals(Supervisor_UserName) && password.equals(Supervisor_Password) )
        {
            LogInErrorTextBox.setText("");
            SceneManager.switchScene("Supervisor.fxml");
        }
        else
        {
            LogInErrorTextBox.setText("Username or Password wrong!");
        }
    }


}

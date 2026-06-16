package com.dds.demo.sysarch_javafx_v1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args)
    {
                //Wenn main drinne ist wird zuerst main ausgeführt bius zu dem punkt wo launch(args) kommt.
        launch(args);
        //Nach launch(args) wird in der main nix mehr ausgeführt. Deshlb falls code in der main nötig ist. Diesen vor launch schreiben.
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        SceneManager.init(stage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Elevator-HMI");
        stage.setScene(scene);
        stage.show();

    }
}

/*@FXML
private void onLoginButtonClick() throws IOException
{
    SceneManager.switchScene("user-view.fxml");
}*/

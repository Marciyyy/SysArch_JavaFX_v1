package com.dds.demo.sysarch_javafx_v1;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager
{
    private static Stage stage;

    public static void init(Stage primaryStage)
    {
        stage = primaryStage;
    }

    public static void switchScene(String fxml) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);



    }
}

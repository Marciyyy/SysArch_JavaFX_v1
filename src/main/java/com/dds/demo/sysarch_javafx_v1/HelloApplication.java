package com.dds.demo.sysarch_javafx_v1;

import OpcUaClient.OpcUaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.milo.opcua.stack.core.Identifiers;

import java.io.IOException;
import java.util.Map;

public class HelloApplication extends Application {

    private static OpcUaService opcUaService;

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


        //opcUaService = new OpcUaService("opc.tcp://192.168.1.50:12686/milo", connected -> System.out.println(connected ? "OPC-UA verbunden." : "OPC-UA nicht verbunden."));

        opcUaService = new OpcUaService("opc.tcp://localhost:4840/milo", connected -> System.out.println(connected ? "OPC-UA connected." : "OPC-UA not connected."));



        // Verbindung im Hintergrund starten.
        // Die GUI wird dadurch nicht blockiert.
        opcUaService.connect().thenRun(() -> System.out.println("Verbindung zum OPC-UA-Server erfolgreich.")).exceptionally(error ->
        {   System.err.println("OPC-UA-Verbindung fehlgeschlagen: " + error.getMessage());
            error.printStackTrace();
            return null;
        });


        //Zwei test methods:
        opcUaService.connect().thenCompose(unused -> opcUaService.read(Identifiers.Server_ServerStatus_CurrentTime)).thenAccept(dataValue -> {
                    System.out.println("TEST ERFOLGREICH. Serverzeit: " + dataValue.value().value());
                })
                .exceptionally(error -> {
                    System.err.println("OPC-UA-Test fehlgeschlagen: " + error.getMessage());
                    error.printStackTrace();
                    return null;});

        opcUaService.subscribe(Map.of(Identifiers.Server_ServerStatus_CurrentTime, dataValue ->
                System.out.println("Subscription: " + dataValue.value().value())
        ));






        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Elevator-HMI");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        // Wird beim Schließen des HMI aufgerufen.
        if (opcUaService != null)
        {
            opcUaService.shutdown();
        }
    }

    public static OpcUaService getOpcUaService() {
        if (opcUaService == null)
        {
            throw new IllegalStateException("OpcUaService wurde noch nicht initialisiert.");
        }

        return opcUaService;
    }



}

/*@FXML
private void onLoginButtonClick() throws IOException
{
    SceneManager.switchScene("user-view.fxml");
}*/

package com.dds.demo.sysarch_javafx_v1;

import OpcUaClient.ControlNodes;
import OpcUaClient.OpcUaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;


import OpcUaClient.OpcUaService;






public class HelloApplication extends Application {

    private static OpcUaService opcUaService;

    private static final HmiState hmiState = new HmiState();

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

        opcUaService = new OpcUaService("opc.tcp://localhost:4840/milo", connected -> {
            hmiState.setConnected(connected);
            System.out.println(connected ? "OPC-UA connected." : "OPC-UA not connected.");
        });



        // Verbindung im Hintergrund starten.
        // Die GUI wird dadurch nicht blockiert.
        /*
        opcUaService.connect().thenRun(() -> System.out.println("Verbindung zum OPC-UA-Server erfolgreich.")).exceptionally(error ->
        {   System.err.println("OPC-UA-Verbindung fehlgeschlagen: " + error.getMessage());
            error.printStackTrace();
            return null;
        });
        */

        //Zwei test methods mit dem docker desktop test milo server:
        /*
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
        */


        opcUaService.connect()
                .thenCompose(unused ->
                        opcUaService.subscribe(Map.of(

                                ControlNodes.CURRENT_FLOOR,
                                dataValue -> updateInteger(
                                        dataValue,
                                        hmiState::setCurrentFloor
                                ),

                                ControlNodes.ELEVATOR_MOVING,
                                dataValue -> updateBoolean(
                                        dataValue,
                                        hmiState::setElevatorMoving
                                ),

                                ControlNodes.DOOR_OPEN,
                                dataValue -> updateBoolean(
                                        dataValue,
                                        hmiState::setDoorOpen
                                ),

                                ControlNodes.ERROR_TEXT,
                                dataValue -> updateString(
                                        dataValue,
                                        hmiState::setErrorText
                                )
                        ))
                )
                .thenRun(() ->
                        System.out.println(
                                "OPC-UA verbunden und Subscriptions aktiv."
                        )
                )
                .exceptionally(error -> {
                    System.err.println(
                            "OPC-UA-Fehler: " + error.getMessage()
                    );
                    error.printStackTrace();
                    return null;
                });







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


    public static HmiState getHmiState() {
        return hmiState;
    }



    private static void updateBoolean(DataValue dataValue, Consumer<Boolean> setter)
    {
        if (!dataValue.statusCode().isGood())
        {
            System.err.println("Ungültiger OPC-UA-Wert: " + dataValue.statusCode());
            return;
        }

        Object value = dataValue.value().value();

        if (value instanceof Boolean booleanValue)
        {
            setter.accept(booleanValue);
        }
    }

    private static void updateInteger(DataValue dataValue, Consumer<Integer> setter)
    {
        if (!dataValue.statusCode().isGood())
        {
            System.err.println("Ungültiger OPC-UA-Wert: " + dataValue.statusCode());
            return;
        }

        Object value = dataValue.value().value();

        if (value instanceof Number number)
        {
            setter.accept(number.intValue());
        }
    }

    private static void updateString(DataValue dataValue, Consumer<String> setter)
    {
        if (!dataValue.statusCode().isGood())
        {
            System.err.println("Ungültiger OPC-UA-Wert: " + dataValue.statusCode());
            return;
        }

        Object value = dataValue.value().value();

        if (value instanceof String text)
        {
            setter.accept(text);
        }
    }






}

/*@FXML
private void onLoginButtonClick() throws IOException
{
    SceneManager.switchScene("user-view.fxml");
}*/

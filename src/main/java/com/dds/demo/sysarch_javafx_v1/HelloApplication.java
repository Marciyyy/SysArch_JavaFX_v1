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

        //!!!!!!Hier später statt /milo: /opcua/process!!!!!!; Da beide programme später über ssh eapc165 laufen passt das, sonst müsste man hier noch riuchtigee IP statt localhost angeben
        opcUaService = new OpcUaService("opc.tcp://localhost:4840/opcua/process", connected -> {
            hmiState.setConnected(connected);
            System.out.println(connected ? "OPC-UA connected." : "OPC-UA not connected.");
        });



        // Verbindung im Hintergrund starten.
        // Die GUI wird dadurch nicht blockiert.



        //Richtige connect Mehtod für die spätere implementierung

        opcUaService.connect()
                .thenCompose(unused -> {
                    ControlNodes nodes = opcUaService.getControlNodes();

                    return opcUaService.subscribe(Map.of(
                            nodes.currentLevel,
                            dataValue -> updateInteger(
                                    dataValue,
                                    hmiState::setCurrentLevel
                            ),

                            nodes.nextLevel,
                            dataValue -> updateInteger(
                                    dataValue,
                                    hmiState::setNextLevel
                            ),

                            nodes.elevatorState,
                            dataValue -> updateString(
                                    dataValue,
                                    hmiState::setElevatorState
                            ),

                            nodes.direction,
                            dataValue -> updateString(
                                    dataValue,
                                    hmiState::setDirection
                            ),

                            nodes.doorOpen,
                            dataValue -> updateBoolean(
                                    dataValue,
                                    hmiState::setDoorOpen
                            ),

                            nodes.doorClosed,
                            dataValue -> updateBoolean(
                                    dataValue,
                                    hmiState::setDoorClosed
                            ),

                            nodes.motorReady,
                            dataValue -> updateBoolean(
                                    dataValue,
                                    hmiState::setMotorReady
                            )
                    ));
                })
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

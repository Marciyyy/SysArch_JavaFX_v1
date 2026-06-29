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
import javafx.application.Platform;


import OpcUaClient.OpcUaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    private static OpcUaService opcUaService;

    private static final HmiState hmiState = new HmiState();

    public static void main(String[] args)
    {
        logger.info("HMI application started");
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
       // VErsion ohne logging:
        /*
        opcUaService = new OpcUaService("opc.tcp://localhost:4840/opcua/process", connected -> {
            hmiState.setConnected(connected);
            System.out.println(connected ? "OPC-UA connected." : "OPC-UA not connected.");
        });
         */
        //Version mit logging statt println:
        opcUaService = new OpcUaService("opc.tcp://localhost:4840/opcua/process", connected -> {
        //opcUaService = new OpcUaService("opc.tcp://PC-Marcel:53530/OPCUA/SimulationServer", connected -> {            //Für lokalen prosys testserver
                    hmiState.setConnected(connected);
                    if (connected)
                    {
                        logger.info("OPC-UA-Connection has been established succesfully");
                    } else
                    {
                        logger.error("OPC-UA-Connection establishment has failed");
                    }
                }
        );



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
                            ),

                            nodes.mbCycles,
                            dataValue -> updateInteger(
                                    dataValue,
                                    hmiState::setMbCycles
                            ),

                            nodes.mbSpeed,
                            dataValue -> updateInteger(
                                    dataValue,
                                    hmiState::setMbSpeed
                            ),

                            nodes.mbAufzugId,
                            dataValue -> updateInteger(
                                    dataValue,
                                    hmiState::setMbAufzugId
                            )
                    ));
                })
                .thenRun(() ->
                {
                    System.out.println("OPC-UA connected and subscriptions activ");
                    logger.info("OPC-UA connected and subscriptions activ und ");

                    // GUI darf nur auf dem JavaFX-Thread geändert werden.
                    Platform.runLater(() ->
                    {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));

                            Scene scene = new Scene(loader.load());

                            stage.setTitle("Elevator-HMI");
                            stage.setScene(scene);
                            stage.show();

                            logger.info("LogIn scene loaded.");

                        } catch (IOException error)
                        {
                            logger.error("Loading LogIn.fxml failed.", error);
                        }
                    });
                })
                .exceptionally(error ->
                {
                    System.err.println("OPC-UA-Error: " + error.getMessage());
                    error.printStackTrace();
                    logger.error("OPC-UA-Connection or subscription failed", error);
                    return null;
                });





        //Geht so nicht, weil zuerst die verbindung aufgebaut sein sollte. Alle Controller nutzen nämlich
        //getControlNodes() aber wenn noch keine Verbindung dann problem
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Elevator-HMI");
        stage.setScene(scene);
        stage.show();
        */

    }

    @Override
    public void stop()
    {
        // Wird beim Schließen des HMI aufgerufen.
        if (opcUaService != null)
        {
            logger.info("Shutdown of the OpcUaService");
            opcUaService.shutdown();
        }
        logger.info("Stopped the application");
    }

    public static OpcUaService getOpcUaService()
    {
        if (opcUaService == null)
        {
            logger.info("OpcUaService hasn't been initialized yet");
            throw new IllegalStateException("OpcUaService hasn't been initialized yet");
        }

        return opcUaService;
    }


    public static HmiState getHmiState()
    {
        return hmiState;
    }



    private static void updateBoolean(DataValue dataValue, Consumer<Boolean> setter)
    {
        if (!dataValue.statusCode().isGood())
        {
            logger.warn("Invalid OPC-UA Value has been transferred during an subscription transfer: {}; Should be Boolean", dataValue.statusCode() );
            System.err.println("Invalid OPC-UA Value has been transferred during an subscription transfer: " + dataValue.statusCode()+" ;Should be Boolean");
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
            logger.warn("Invalid OPC-UA Value has been transferred during an subscription transfer: {}; Should be Integer", dataValue.statusCode() );
            System.err.println("Invalid OPC-UA Value has been transferred during an subscription transfer: " + dataValue.statusCode() + " ;Should be Integer");
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
            logger.warn("Invalid OPC-UA Value has been transferred during an subscription transfer: {}; Should be String", dataValue.statusCode() );
            System.err.println("Invalid OPC-UA Value has been transferred during an subscription transfer: " + dataValue.statusCode() +" ;Should be String");
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

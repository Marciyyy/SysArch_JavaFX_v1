package com.dds.demo.sysarch_javafx_v1;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HmiState {

    // Lokaler Verbindungsstatus des HMI
    private final BooleanProperty connected = new SimpleBooleanProperty(false);

    // Statuswerte vom OPC-UA-Server
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);

    private final IntegerProperty nextLevel = new SimpleIntegerProperty(1);

    private final StringProperty elevatorState = new SimpleStringProperty("STOPPED");

    private final StringProperty direction = new SimpleStringProperty("DontCare");

    private final BooleanProperty doorOpen = new SimpleBooleanProperty(false);

    private final BooleanProperty doorClosed = new SimpleBooleanProperty(true);

    private final BooleanProperty motorReady = new SimpleBooleanProperty(false);

    private final IntegerProperty mbCycles = new SimpleIntegerProperty(1);

    private final IntegerProperty mbAufzugId = new SimpleIntegerProperty(1);

    private final IntegerProperty mbSpeed = new SimpleIntegerProperty(1);



    // ----------------------------------------------------------
    // Properties für JavaFX-Bindings
    // ----------------------------------------------------------

    public BooleanProperty connectedProperty()
    {
        return connected;
    }

    public IntegerProperty currentLevelProperty()
    {
        return currentLevel;
    }

    public IntegerProperty nextLevelProperty()
    {
        return nextLevel;
    }

    public StringProperty elevatorStateProperty()
    {
        return elevatorState;
    }

    public StringProperty directionProperty()
    {
        return direction;
    }

    public BooleanProperty doorOpenProperty()
    {
        return doorOpen;
    }

    public BooleanProperty doorClosedProperty()
    {
        return doorClosed;
    }

    public BooleanProperty motorReadyProperty()
    {
        return motorReady;
    }

    public IntegerProperty mbCyclesProperty()
    {
        return mbCycles;
    }

    public IntegerProperty mbAufzugIdProperty()
    {
        return mbAufzugId;
    }

    public IntegerProperty mbSpeedProperty()
    {
        return mbSpeed;
    }

    // ----------------------------------------------------------
    // Setter: werden durch OPC-UA-Subscriptions aufgerufen
    // ----------------------------------------------------------

    public void setConnected(boolean value) {
        connected.set(value);
    }

    public void setCurrentLevel(int value) {
        currentLevel.set(value);
    }

    public void setNextLevel(int value) {
        nextLevel.set(value);
    }

    public void setElevatorState(String value) {
        elevatorState.set(value);
    }

    public void setDirection(String value) {
        direction.set(value);
    }

    public void setDoorOpen(boolean value) {
        doorOpen.set(value);
    }

    public void setDoorClosed(boolean value) {
        doorClosed.set(value);
    }

    public void setMotorReady(boolean value) {
        motorReady.set(value);
    }


    public void setMbCycles(int value)
    {
        mbCycles.set(value);
    }

    public void setMbAufzugId(int value)
    {
        mbAufzugId.set(value);
    }

    public void setMbSpeed(int value)
    {
        mbSpeed.set(value);
    }


}
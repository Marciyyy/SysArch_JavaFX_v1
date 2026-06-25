package com.dds.demo.sysarch_javafx_v1;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



//Hier alle Variablen hinzufügen, die später in den Scenes angezeigt werden sollen (speed, stockwerk etc.)
public class HmiState {

    private final BooleanProperty connected =
            new SimpleBooleanProperty(false);

    private final IntegerProperty currentFloor =
            new SimpleIntegerProperty(0);

    private final BooleanProperty elevatorMoving =
            new SimpleBooleanProperty(false);

    private final BooleanProperty doorOpen =
            new SimpleBooleanProperty(false);

    private final StringProperty errorText =
            new SimpleStringProperty("");

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public IntegerProperty currentFloorProperty() {
        return currentFloor;
    }

    public BooleanProperty elevatorMovingProperty() {
        return elevatorMoving;
    }

    public BooleanProperty doorOpenProperty() {
        return doorOpen;
    }

    public StringProperty errorTextProperty() {
        return errorText;
    }

    public void setConnected(boolean value) {
        connected.set(value);
    }

    public void setCurrentFloor(int value) {
        currentFloor.set(value);
    }

    public void setElevatorMoving(boolean value) {
        elevatorMoving.set(value);
    }

    public void setDoorOpen(boolean value) {
        doorOpen.set(value);
    }

    public void setErrorText(String value) {
        errorText.set(value);
    }
}
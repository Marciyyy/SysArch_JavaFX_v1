package OpcUaClient;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class ControlNodes
{
    private ControlNodes() {
    }

    // Schreibbare Befehle

    //Test variable vom Docker server:
    public static final NodeId TEST_INT32 =
            new NodeId(2, "Demo.Variants.Scalar.Int32");







    public static final NodeId CMD_START =
            NodeId.parse("ns=2;s=Control.Commands.Start");

    public static final NodeId CMD_STOP =
            NodeId.parse("ns=2;s=Control.Commands.Stop");

    public static final NodeId SETPOINT_SPEED =
            NodeId.parse("ns=2;s=Control.Parameters.SpeedSetpoint");

    // Werte vom Control-Programm
    public static final NodeId MACHINE_RUNNING =
            NodeId.parse("ns=2;s=Control.Status.MachineRunning");

    public static final NodeId ACTUAL_SPEED =
            NodeId.parse("ns=2;s=Control.Status.ActualSpeed");

    public static final NodeId ERROR_CODE =
            NodeId.parse("ns=2;s=Control.Status.ErrorCode");


    public static final NodeId CURRENT_FLOOR =
            new NodeId(2, "Elevator.Status.CurrentFloor");

    public static final NodeId ELEVATOR_MOVING =
            new NodeId(2, "Elevator.Status.Moving");

    public static final NodeId DOOR_OPEN =
            new NodeId(2, "Elevator.Status.DoorOpen");

    public static final NodeId ERROR_TEXT =
            new NodeId(2, "Elevator.Status.ErrorText");

    // Befehle vom HMI zum Control-Programm
    public static final NodeId COMMAND_UP =
            new NodeId(2, "Elevator.Command.Up");

    public static final NodeId COMMAND_DOWN =
            new NodeId(2, "Elevator.Command.Down");

    public static final NodeId COMMAND_OPEN_DOOR =
            new NodeId(2, "Elevator.Command.OpenDoor");
}

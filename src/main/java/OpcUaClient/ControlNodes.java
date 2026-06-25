package OpcUaClient;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class ControlNodes
{
    private ControlNodes() {
    }

    // Schreibbare Befehle
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
}

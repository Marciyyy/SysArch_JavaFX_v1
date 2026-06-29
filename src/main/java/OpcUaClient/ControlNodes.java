package OpcUaClient;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

public final class ControlNodes {

    public static final String NAMESPACE_URI =
            //"urn:example:opcua:process-namespace";    //alter name
            "urn:example:opcua:process-namespace";

    private final int namespaceIndex;

    // Client -> Server: schreibbare Variablen
    public final NodeId insideLevel1;
    public final NodeId insideLevel2;
    public final NodeId insideLevel3;
    public final NodeId insideLevel4;

    public final NodeId outsideLevel1Up;
    public final NodeId outsideLevel2Up;
    public final NodeId outsideLevel2Down;
    public final NodeId outsideLevel3Up;
    public final NodeId outsideLevel3Down;
    public final NodeId outsideLevel4Down;

    public final NodeId openDoor;
    public final NodeId closeDoor;
    public final NodeId emergencyStop;
    public final NodeId reset;
    public final NodeId supervisor;

    public final NodeId supervisorV1Up;
    public final NodeId supervisorV1Down;
    public final NodeId supervisorV2Up;
    public final NodeId supervisorV2Down;
    public final NodeId supervisorCrawl;

    // Server -> Client: lesbare Variablen
    public final NodeId currentLevel;
    public final NodeId nextLevel;
    public final NodeId elevatorState;
    public final NodeId direction;
    public final NodeId doorOpen;
    public final NodeId doorClosed;
    public final NodeId motorReady;

    public final NodeId mbCycles;
    public final NodeId mbAufzugId;
    public final NodeId mbSpeed;

    private ControlNodes(int namespaceIndex) {
        this.namespaceIndex = namespaceIndex;

        insideLevel1 = new NodeId(namespaceIndex, "InsideLevel1");
        insideLevel2 = new NodeId(namespaceIndex, "InsideLevel2");
        insideLevel3 = new NodeId(namespaceIndex, "InsideLevel3");
        insideLevel4 = new NodeId(namespaceIndex, "InsideLevel4");

        outsideLevel1Up = new NodeId(namespaceIndex, "OutsideLevel1Up");
        outsideLevel2Up = new NodeId(namespaceIndex, "OutsideLevel2Up");
        outsideLevel2Down = new NodeId(namespaceIndex, "OutsideLevel2Down");
        outsideLevel3Up = new NodeId(namespaceIndex, "OutsideLevel3Up");
        outsideLevel3Down = new NodeId(namespaceIndex, "OutsideLevel3Down");
        outsideLevel4Down = new NodeId(namespaceIndex, "OutsideLevel4Down");

        openDoor = new NodeId(namespaceIndex, "OpenDoor");
        closeDoor = new NodeId(namespaceIndex, "CloseDoor");
        emergencyStop = new NodeId(namespaceIndex, "EmergencyStop");
        reset = new NodeId(namespaceIndex, "Reset");
        supervisor = new NodeId(namespaceIndex, "Supervisor");

        supervisorV1Up = new NodeId(namespaceIndex, "SupervisorV1Up");
        supervisorV1Down = new NodeId(namespaceIndex, "SupervisorV1Down");
        supervisorV2Up = new NodeId(namespaceIndex, "SupervisorV2Up");
        supervisorV2Down = new NodeId(namespaceIndex, "SupervisorV2Down");
        supervisorCrawl = new NodeId(namespaceIndex, "SupervisorCrawl");

        currentLevel = new NodeId(namespaceIndex, "CurrentLevel");
        nextLevel = new NodeId(namespaceIndex, "NextLevel");
        elevatorState = new NodeId(namespaceIndex, "ElevatorState");
        direction = new NodeId(namespaceIndex, "Direction");
        doorOpen = new NodeId(namespaceIndex, "DoorOpen");
        doorClosed = new NodeId(namespaceIndex, "DoorClosed");
        motorReady = new NodeId(namespaceIndex, "MotorReady");

        mbCycles = new NodeId(namespaceIndex, "Cycles");
        mbAufzugId = new NodeId(namespaceIndex, "AufzugId");
        mbSpeed = new NodeId(namespaceIndex, "Speed");
    }

    public static ControlNodes create(OpcUaClient client) throws UaException {
        NamespaceTable namespaceTable = client.readNamespaceTable();

        UShort namespaceIndex =
                namespaceTable.getIndex(NAMESPACE_URI);

        if (namespaceIndex == null) {
            throw new IllegalStateException(
                    "Namespace nicht gefunden: " + NAMESPACE_URI
            );
        }

        return new ControlNodes(namespaceIndex.intValue());
    }

    public int getNamespaceIndex() {
        return namespaceIndex;
    }
}
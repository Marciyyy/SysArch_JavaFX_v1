package OpcUaClient;

import javafx.application.Platform;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.subscriptions.MonitoredItemSynchronizationException;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;


public final class OpcUaService {

    private final String endpointUrl;
    private final Consumer<Boolean> connectionStateConsumer;

    // Alle blockierenden OPC-UA-Aufrufe laufen nur auf diesem Hintergrundthread.
    private final ExecutorService opcExecutor =
            Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "opc-ua-client-thread");
                thread.setDaemon(true);
                return thread;
            });

    private volatile OpcUaClient client;
    private volatile OpcUaSubscription subscription;
    private volatile boolean connected;

    public OpcUaService(String endpointUrl, Consumer<Boolean> connectionStateConsumer)
    {
        this.endpointUrl = endpointUrl;
        this.connectionStateConsumer = connectionStateConsumer;
    }

    public CompletableFuture<Void> connect()
    {
        return CompletableFuture.runAsync(() -> {
            if (connected)
            {
                return;
            }

            try {
                client = createClient();
                client.connect();

                connected = true;
                updateConnectionState(true);

            } catch (Exception e) {
                connected = false;
                updateConnectionState(false);

                throw new CompletionException(e);
            }
        }, opcExecutor);
    }

    public CompletableFuture<Void> write(NodeId nodeId, Object value)
    {
        return CompletableFuture.runAsync(() -> {
            requireConnected();

            // Variant.of(...) prüft, ob der Java-Typ ein gültiger OPC-UA-Typ ist.
            DataValue dataValue = DataValue.valueOnly(Variant.of(value));

            List<StatusCode> result = null;
            try {
                result = client.writeValues(
                        List.of(nodeId),
                        List.of(dataValue)
                );
            } catch (UaException e) {
                throw new RuntimeException(e);
            }

            StatusCode statusCode = result.get(0);

            if (!statusCode.isGood())
            {
                throw new IllegalStateException("OPC-UA-Schreiben fehlgeschlagen. NodeId=" + nodeId + ", Status=" + statusCode);
            }
        }, opcExecutor);
    }

    public CompletableFuture<Void> subscribe(Map<NodeId, Consumer<DataValue>> valueConsumers)
    {
        return CompletableFuture.runAsync(() -> {
            requireConnected();

            if (subscription != null)
            {
                throw new IllegalStateException("Eine Subscription wurde bereits erstellt.");
            }

            OpcUaSubscription newSubscription = new OpcUaSubscription(client);

            // Subscription auf dem OPC-UA-Server erzeugen.
            try {
                newSubscription.create();
            } catch (UaException e) {
                throw new RuntimeException(e);
            }

            for (Map.Entry<NodeId, Consumer<DataValue>> entry : valueConsumers.entrySet())
            {

                NodeId nodeId = entry.getKey();
                Consumer<DataValue> consumer = entry.getValue();

                OpcUaMonitoredItem monitoredItem =
                        OpcUaMonitoredItem.newDataItem(nodeId);

                monitoredItem.setDataValueListener((item, dataValue) -> {
                    // Milo-Callback ist kein JavaFX-Thread.
                    Platform.runLater(() -> consumer.accept(dataValue));
                });

                newSubscription.addMonitoredItem(monitoredItem);
            }

            try {
                // Erst hier werden alle Monitored Items am Server angelegt.
                newSubscription.synchronizeMonitoredItems();

                subscription = newSubscription;

            } catch (MonitoredItemSynchronizationException e) {
                try {
                    newSubscription.delete();
                } catch (Exception ignored) {
                    // Ursprünglichen Fehler nicht verdecken.
                }

                throw new CompletionException("Mindestens eine OPC-UA-Subscription konnte " + "nicht erstellt werden.", e);
            }
        }, opcExecutor);
    }

    public CompletableFuture<Void> disconnect()
    {
        return CompletableFuture.runAsync(() -> {
            try {
                if (subscription != null)
                {
                    try {
                        subscription.delete();
                    } catch (UaException e) {
                        throw new RuntimeException(e);
                    }
                    subscription = null;
                }

                if (client != null)
                {
                    client.disconnectAsync().join();
                    client = null;
                }

            } finally {
                connected = false;
                updateConnectionState(false);
            }
        }, opcExecutor);
    }

    public void shutdown()
    {
        disconnect().whenComplete((unused, error) -> opcExecutor.shutdownNow());
    }

    public boolean isConnected()
    {
        return connected;
    }

    private OpcUaClient createClient() throws Exception
    {
        return OpcUaClient.create(endpointUrl, endpoints -> endpoints.stream().filter(endpoint -> SecurityPolicy.None.getUri().equals(endpoint.getSecurityPolicyUri())).findFirst(),

                transportConfigBuilder -> {
                    // Für den ersten Test keine Spezialkonfiguration nötig.
                },

                clientConfigBuilder -> clientConfigBuilder.setApplicationName(LocalizedText.english("HMI OPC UA Client")).setApplicationUri("urn:dds:sysarch-javafx:hmi-client").setIdentityProvider(new AnonymousProvider()));
    }

    private void requireConnected()
    {
        if (!connected || client == null)
        {
            throw new IllegalStateException("OPC-UA-Client ist nicht verbunden.");
        }
    }

    private void updateConnectionState(boolean value)
    {
        if (connectionStateConsumer != null)
        {
            Platform.runLater(() -> connectionStateConsumer.accept(value));
        }
    }

    public CompletableFuture<DataValue> read(NodeId nodeId) {
        return CompletableFuture.supplyAsync(() -> {
            requireConnected();

            try {
                return client.readValues(0.0, TimestampsToReturn.Both, List.of(nodeId)
                ).get(0);
            } catch (UaException e) {
                throw new RuntimeException(e);
            }
        }, opcExecutor);
    }
}
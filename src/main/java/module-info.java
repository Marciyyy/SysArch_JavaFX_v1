module com.dds.demo.sysarch_javafx_v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires org.eclipse.milo.opcua.sdk.client;
    requires org.eclipse.milo.opcua.stack.core;
    requires org.eclipse.milo.opcua.stack.transport;


    opens com.dds.demo.sysarch_javafx_v1 to javafx.fxml;
    exports com.dds.demo.sysarch_javafx_v1;
}
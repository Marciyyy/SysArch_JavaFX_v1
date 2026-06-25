module com.dds.demo.sysarch_javafx_v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    opens com.dds.demo.sysarch_javafx_v1 to javafx.fxml;
    exports com.dds.demo.sysarch_javafx_v1;
}
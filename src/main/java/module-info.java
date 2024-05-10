module com.example.tars01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires java.desktop;
    requires jdk.compiler;
    requires jbcrypt;


    opens com.example.tars01 to javafx.fxml;
    exports com.example.tars01;
    exports com.example.tars01.Database;
    opens com.example.tars01.Database to javafx.fxml;
}
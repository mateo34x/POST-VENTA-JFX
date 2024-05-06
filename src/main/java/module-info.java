module com.example.tars01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.tars01 to javafx.fxml;
    exports com.example.tars01;
}
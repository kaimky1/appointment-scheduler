module com.example.helloworldjfxtemplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.software2 to javafx.fxml;
    exports com.example.software2;

    exports com.example.software2.controller;
    opens com.example.software2.controller to javafx.fxml;

    exports com.example.software2.model;
    opens com.example.software2.model to javafx.base;

}
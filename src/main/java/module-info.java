module com.yourcompany.ems {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;
    requires org.json;

    opens com.yourcompany.ems to javafx.fxml;
    opens com.yourcompany.ems.controllers to javafx.fxml;
    opens com.yourcompany.ems.models to javafx.base;  // This is the critical addition

    exports com.yourcompany.ems;
    exports com.yourcompany.ems.controllers;
}
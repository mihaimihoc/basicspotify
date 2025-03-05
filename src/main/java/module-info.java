module org.example.final_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jaudiotagger;
    requires javafx.media;

    opens org.example.final_project to javafx.fxml;
    exports org.example.final_project;
}
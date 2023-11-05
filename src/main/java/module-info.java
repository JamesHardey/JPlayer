
module com.example.jplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    requires javafx.graphics;
    requires java.desktop;

    requires javafx.media;
    requires de.jensd.fx.glyphs.fontawesome;


    opens com.example.jplayer to javafx.fxml;
    exports com.example.jplayer;
    exports com.example.jplayer.controller;
    opens com.example.jplayer.controller to javafx.fxml;
}
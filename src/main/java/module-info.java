module Lab4 {
    requires javafx.controls;
    requires javafx.fxml;

    // Export your package to JavaFX
    exports sekiro to javafx.graphics, javafx.base;

    // If you have FXML files, also add:
    opens sekiro to javafx.fxml;
}
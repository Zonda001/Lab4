package sekiro;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OwlEditDialog {

    public static void display(Owl owl) throws IOException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редагування сови: " + owl.name);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Поля вводу з поточними значеннями
        Label nameLabel = new Label("Ім'я сови:");
        TextField nameField = new TextField(owl.name);

        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Сова", "Великий Сова", "
package sekiro;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateOwlDialog {
    public static Stage window = null;
    public static Scene scene;

    public static void display() throws IOException {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Створити нову сову");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Назва сови
        Label nameLabel = new Label("Назва сови:");
        TextField nameField = new TextField();
        nameField.setText("Нова Сова");

        // Тип сови
        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови");
        typeCombo.setValue("Сова");

        // Чи має спеціальну здібність
        Label abilityLabel = new Label("Має спеціальну здібність:");
        CheckBox abilityCheck = new CheckBox();
        abilityCheck.setSelected(false);

        // Назва здібності
        Label abilityNameLabel = new Label("Назва здібності:");
        TextField abilityNameField = new TextField();
        abilityNameField.setText("Немає");
        abilityNameField.setDisable(true);

        // Радіокнопки для кольору здібності (додатковий параметр)
        Label colorLabel = new Label("Колір здібності:");
        ToggleGroup colorGroup = new ToggleGroup();
        RadioButton redRadio = new RadioButton("Червоний");
        RadioButton blueRadio = new RadioButton("Синій");
        RadioButton greenRadio = new RadioButton("Зелений");

        redRadio.setToggleGroup(colorGroup);
        blueRadio.setToggleGroup(colorGroup);
        greenRadio.setToggleGroup(colorGroup);
        redRadio.setSelected(true);

        // Обробка зміни checkbox
        abilityCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            abilityNameField.setDisable(!newVal);
            if (!newVal) {
                abilityNameField.setText("Немає");
            } else {
                abilityNameField.setText("Нова здібність");
            }
        });

        // Кнопки
        Button okButton = new Button("Створити");
        Button cancelButton = new Button("Скасувати");

        okButton.setOnAction(e -> {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Помилка");
                alert.setHeaderText("Введіть назву сови!");
                alert.showAndWait();
                return;
            }

            String type = typeCombo.getValue();
            boolean hasAbility = abilityCheck.isSelected();
            String abilityName = abilityNameField.getText();

            Main.addNewOwl(name, type, hasAbility, abilityName);
            window.close();
        });

        cancelButton.setOnAction(e -> window.close());

        // Розміщення елементів
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1, 2, 1);

        grid.add(abilityLabel, 0, 2);
        grid.add(abilityCheck, 1, 2);

        grid.add(abilityNameLabel, 0, 3);
        grid.add(abilityNameField, 1, 3, 2, 1);

        grid.add(colorLabel, 0, 4);
        grid.add(redRadio, 1, 4);
        grid.add(blueRadio, 1, 5);
        grid.add(greenRadio, 1, 6);

        grid.add(okButton, 0, 7);
        grid.add(cancelButton, 1, 7);

        scene = new Scene(grid, 400, 350);
        window.setScene(scene);
        window.showAndWait();
    }
}
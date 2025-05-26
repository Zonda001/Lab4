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

public class OwlCreationDialog {

    public static void display() throws IOException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Створення нової сови");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Поля вводу
        Label nameLabel = new Label("Ім'я сови:");
        TextField nameField = new TextField();
        nameField.setText("Нова Сова");

        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови");
        typeCombo.setValue("Сова");

        Label positionLabel = new Label("Позиція:");
        TextField xField = new TextField();
        xField.setText("200");
        xField.setPromptText("X координата");

        TextField yField = new TextField();
        yField.setText("300");
        yField.setPromptText("Y координата");

        CheckBox featureCheck = new CheckBox("Має особливу властивість");
        featureCheck.setSelected(true);

        Label featureLabel = new Label("Опис властивості:");
        TextField featureField = new TextField();
        featureField.setText("Особлива здібність");

        // Радіокнопки для вибору замку
        Label castleLabel = new Label("Призначити до замку:");
        ToggleGroup castleGroup = new ToggleGroup();

        RadioButton noCastleRadio = new RadioButton("Без замку");
        noCastleRadio.setToggleGroup(castleGroup);
        noCastleRadio.setSelected(true);

        RadioButton asinaRadio = new RadioButton("Замок Асіна");
        asinaRadio.setToggleGroup(castleGroup);

        RadioButton hiruRadio = new RadioButton("Хіру-ден");
        hiruRadio.setToggleGroup(castleGroup);

        RadioButton towerRadio = new RadioButton("Верхній Баштовий Додзьо");
        towerRadio.setToggleGroup(castleGroup);

        // Кнопки
        Button okButton = new Button("Створити");
        Button cancelButton = new Button("Скасувати");

        // Обробники подій
        featureCheck.setOnAction(e -> featureField.setDisable(!featureCheck.isSelected()));

        okButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showAlert("Помилка", "Введіть ім'я сови!");
                    return;
                }

                String type = typeCombo.getValue();
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                boolean hasFeature = featureCheck.isSelected();
                String featureDesc = hasFeature ? featureField.getText() : "";

                // Створюємо сову
                Owl newOwl = new Owl(name, type, x, y, hasFeature, featureDesc);
                Main.owls.add(newOwl);

                // Призначаємо до замку, якщо вибрано
                RadioButton selected = (RadioButton) castleGroup.getSelectedToggle();
                if (selected != noCastleRadio) {
                    Castle selectedCastle = null;
                    String castleName = selected.getText();

                    for (Castle castle : Main.castles) {
                        if (castle.getName().equals(castleName)) {
                            selectedCastle = castle;
                            break;
                        }
                    }

                    if (selectedCastle != null) {
                        newOwl.setBelongsToCastle(selectedCastle);
                    }
                }

                window.close();

            } catch (NumberFormatException ex) {
                showAlert("Помилка", "Некоректні координати!");
            }
        });

        cancelButton.setOnAction(e -> window.close());

        // Розміщення елементів
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1, 2, 1);

        grid.add(positionLabel, 0, 2);
        HBox posBox = new HBox(5);
        posBox.getChildren().addAll(xField, yField);
        grid.add(posBox, 1, 2, 2, 1);

        grid.add(featureCheck, 0, 3, 3, 1);
        grid.add(featureLabel, 0, 4);
        grid.add(featureField, 1, 4, 2, 1);

        grid.add(castleLabel, 0, 5);
        grid.add(noCastleRadio, 1, 5);
        grid.add(asinaRadio, 1, 6);
        grid.add(hiruRadio, 1, 7);
        grid.add(towerRadio, 1, 8);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(okButton, cancelButton);
        grid.add(buttonBox, 0, 9, 3, 1);

        Scene scene = new Scene(grid, 450, 400);
        window.setScene(scene);
        window.showAndWait();
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
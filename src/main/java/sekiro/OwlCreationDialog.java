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
    private static boolean result = false;
    private static String owlName;
    private static String owlType;
    private static boolean hasShinobiTechniques;
    private static String skillLevel;
    private static double positionX;
    private static double positionY;

    public static void display() throws IOException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Створення нової сови");
        window.setMinWidth(400);
        window.setMinHeight(350);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Поле для імені
        Label nameLabel = new Label("Ім'я сови:");
        TextField nameField = new TextField();
        nameField.setPromptText("Введіть ім'я сови");
        nameField.setText("Нова Сова");

        // Поле для типу
        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(
                "Великий Сова",
                "Сова",
                "Нащадок Сови",
                "Демон Сова",
                "Ворон-ніндзя"
        );
        typeComboBox.setValue("Сова");

        // Чекбокс для технік шінобі
        CheckBox shinobiCheckBox = new CheckBox("Володіє техніками шінобі");
        shinobiCheckBox.setSelected(false);

        // Поле для рівня майстерності
        Label skillLabel = new Label("Рівень майстерності:");
        ComboBox<String> skillComboBox = new ComboBox<>();
        skillComboBox.getItems().addAll(
                "Новачок",
                "Учень",
                "Адепт",
                "Експерт",
                "Майстер",
                "Легенда"
        );
        skillComboBox.setValue("Новачок");

        // Поля для позиції
        Label posXLabel = new Label("Позиція X:");
        TextField posXField = new TextField();
        posXField.setText(String.valueOf(Main.rnd.nextInt(1000) + 100));

        Label posYLabel = new Label("Позиція Y:");
        TextField posYField = new TextField();
        posYField.setText(String.valueOf(Main.rnd.nextInt(600) + 100));

        // Кнопки
        Button createButton = new Button("Створити");
        createButton.setOnAction(e -> {
            try {
                owlName = nameField.getText().trim();
                owlType = typeComboBox.getValue();
                hasShinobiTechniques = shinobiCheckBox.isSelected();
                skillLevel = skillComboBox.getValue();
                positionX = Double.parseDouble(posXField.getText());
                positionY = Double.parseDouble(posYField.getText());

                if (owlName.isEmpty()) {
                    showAlert("Помилка", "Ім'я сови не може бути порожнім!");
                    return;
                }

                if (positionX < 0 || positionX > 1150 || positionY < 0 || positionY > 750) {
                    showAlert("Помилка", "Позиція повинна бути в межах сцени (0-1150, 0-750)!");
                    return;
                }

                // Створюємо нову сову
                Main.addNewOwl(owlName, owlType, hasShinobiTechniques, skillLevel, positionX, positionY);
                result = true;
                window.close();

            } catch (NumberFormatException ex) {
                showAlert("Помилка", "Неправильний формат числа для позиції!");
            }
        });

        Button cancelButton = new Button("Скасувати");
        cancelButton.setOnAction(e -> {
            result = false;
            window.close();
        });

        // Випадкова позиція
        Button randomPosButton = new Button("Випадкова позиція");
        randomPosButton.setOnAction(e -> {
            posXField.setText(String.valueOf(Main.rnd.nextInt(1000) + 100));
            posYField.setText(String.valueOf(Main.rnd.nextInt(600) + 100));
        });

        // Розміщення елементів
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(typeLabel, 0, 1);
        grid.add(typeComboBox, 1, 1);

        grid.add(shinobiCheckBox, 0, 2, 2, 1);

        grid.add(skillLabel, 0, 3);
        grid.add(skillComboBox, 1, 3);

        grid.add(posXLabel, 0, 4);
        grid.add(posXField, 1, 4);

        grid.add(posYLabel, 0, 5);
        grid.add(posYField, 1, 5);

        HBox posButtonBox = new HBox(10);
        posButtonBox.getChildren().add(randomPosButton);
        grid.add(posButtonBox, 1, 6);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(createButton, cancelButton);
        grid.add(buttonBox, 0, 7, 2, 1);

        Scene scene = new Scene(grid);
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

    public static boolean getResult() {
        return result;
    }
}
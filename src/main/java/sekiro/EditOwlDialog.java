package sekiro;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EditOwlDialog {
    public static Stage window = null;
    public static Scene scene;

    public static void display(Owl owl) throws IOException {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редагувати сову: " + owl.name);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Назва сови
        Label nameLabel = new Label("Назва сови:");
        TextField nameField = new TextField();
        nameField.setText(owl.name);

        // Тип сови
        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови");
        typeCombo.setValue(owl.type);

        // Чи має спеціальну здібність
        Label abilityLabel = new Label("Має спеціальну здібність:");
        CheckBox abilityCheck = new CheckBox();
        abilityCheck.setSelected(owl.hasSpecialAbility);

        // Назва здібності
        Label abilityNameLabel = new Label("Назва здібності:");
        TextField abilityNameField = new TextField();
        abilityNameField.setText(owl.abilityName);
        abilityNameField.setDisable(!owl.hasSpecialAbility);

        // Радіокнопки для стану
        Label statusLabel = new Label("Стан сови:");
        ToggleGroup statusGroup = new ToggleGroup();
        RadioButton activeRadio = new RadioButton("Активна");
        RadioButton inactiveRadio = new RadioButton("Неактивна");
        RadioButton sleepingRadio = new RadioButton("Спить");

        activeRadio.setToggleGroup(statusGroup);
        inactiveRadio.setToggleGroup(statusGroup);
        sleepingRadio.setToggleGroup(statusGroup);

        if (owl.isActive()) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        // Інформація про приналежність до макрооб'єкта
        Label belongsLabel = new Label("Належить до:");
        Label belongsValue = new Label(owl.belongsTo != null ? owl.belongsTo.name : "Немає");

        // Список доступних макрооб'єктів
        Label macroLabel = new Label("Перемістити до:");
        ComboBox<String> macroCombo = new ComboBox<>();
        macroCombo.getItems().add("Немає");
        for (MacroObject macro : Main.macroObjects) {
            macroCombo.getItems().add(macro.name);
        }
        macroCombo.setValue(owl.belongsTo != null ? owl.belongsTo.name : "Немає");

        // Обробка зміни checkbox
        abilityCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            abilityNameField.setDisable(!newVal);
            if (!newVal) {
                abilityNameField.setText("Немає");
            }
        });

        // Кнопки
        Button okButton = new Button("Зберегти");
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

            // Оновлення параметрів сови
            owl.name = name;
            owl.type = typeCombo.getValue();
            owl.hasSpecialAbility = abilityCheck.isSelected();
            owl.abilityName = abilityNameField.getText();

            // Оновлення активності
            boolean shouldBeActive = activeRadio.isSelected();
            if (shouldBeActive != owl.isActive()) {
                owl.toggleActive();
            }

            // Оновлення приналежності до макрооб'єкта
            String selectedMacro = macroCombo.getValue();
            if ("Немає".equals(selectedMacro)) {
                if (owl.belongsTo != null) {
                    owl.belongsTo.removeOwl(owl);
                }
            } else {
                MacroObject targetMacro = null;
                for (MacroObject macro : Main.macroObjects) {
                    if (macro.name.equals(selectedMacro)) {
                        targetMacro = macro;
                        break;
                    }
                }
                if (targetMacro != null) {
                    if (owl.belongsTo != null) {
                        owl.belongsTo.removeOwl(owl);
                    }
                    targetMacro.addOwl(owl);
                }
            }

            // Оновлення відображення
            owl.canvas.getGraphicsContext2D().clearRect(0, 0, owl.canvas.getWidth(), owl.canvas.getHeight());
            Owl.drawOwl(owl.canvas, owl.name, owl.type, owl.hasSpecialAbility, owl.abilityName);
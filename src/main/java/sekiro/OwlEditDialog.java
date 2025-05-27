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
        typeCombo.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови");
        typeCombo.setValue(owl.owlType);

        Label locationLabel = new Label("Позиція X:");
        TextField xField = new TextField(String.valueOf((int)owl.canvas.getLayoutX()));

        Label yLabel = new Label("Позиція Y:");
        TextField yField = new TextField(String.valueOf((int)owl.canvas.getLayoutY()));

        Label shinobyLabel = new Label("Володіє Синобі техніками:");
        CheckBox shinobyCheck = new CheckBox();
        shinobyCheck.setSelected(owl.hasShinobiTechniques);

        Label skillLabel = new Label("Рівень майстерності:");
        ToggleGroup skillGroup = new ToggleGroup();
        RadioButton beginnerRadio = new RadioButton("Новачок");
        RadioButton expertRadio = new RadioButton("Експерт");
        RadioButton masterRadio = new RadioButton("Майстер");

        beginnerRadio.setToggleGroup(skillGroup);
        expertRadio.setToggleGroup(skillGroup);
        masterRadio.setToggleGroup(skillGroup);

        // Встановлюємо поточний рівень
        switch(owl.skillLevel) {
            case "Новачок":
                beginnerRadio.setSelected(true);
                break;
            case "Експерт":
                expertRadio.setSelected(true);
                break;
            case "Майстер":
                masterRadio.setSelected(true);
                break;
        }

        // Розміщення елементів
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(typeLabel, 0, 1);
        grid.add(typeCombo, 1, 1, 2, 1);

        grid.add(locationLabel, 0, 2);
        grid.add(xField, 1, 2);

        grid.add(yLabel, 0, 3);
        grid.add(yField, 1, 3);

        grid.add(shinobyLabel, 0, 4);
        grid.add(shinobyCheck, 1, 4);

        grid.add(new Label("Рівень майстерності:"), 0, 5);
        grid.add(beginnerRadio, 1, 5);
        grid.add(expertRadio, 1, 6);
        grid.add(masterRadio, 1, 7);

        // Кнопки
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Скасувати");

        okButton.setOnAction(e -> {
            try {
                String newName = nameField.getText().trim();
                if (newName.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Помилка");
                    alert.setHeaderText("Введіть ім'я сови!");
                    alert.showAndWait();
                    return;
                }

                String newType = typeCombo.getValue();
                if (newType == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Помилка");
                    alert.setHeaderText("Оберіть тип сови!");
                    alert.showAndWait();
                    return;
                }

                double newX = Double.parseDouble(xField.getText());
                double newY = Double.parseDouble(yField.getText());
                boolean newShinoby = shinobyCheck.isSelected();

                RadioButton selectedSkill = (RadioButton) skillGroup.getSelectedToggle();
                String newSkillLevel = selectedSkill != null ? selectedSkill.getText() : "Новачок";

                // Оновлюємо сову
                owl.name = newName;
                owl.owlType = newType;
                owl.hasShinobiTechniques = newShinoby;
                owl.skillLevel = newSkillLevel;

                // Переміщуємо сову
                owl.move(newX - owl.canvas.getLayoutX(), newY - owl.canvas.getLayoutY());

                // Перемальовуємо сову з новими параметрами
                owl.redraw();

                window.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("Невірний формат координат!");
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> window.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(okButton, cancelButton);

        grid.add(buttonBox, 0, 8, 3, 1);

        Scene scene = new Scene(grid, 400, 350);
        window.setScene(scene);
        window.showAndWait();
    }
}
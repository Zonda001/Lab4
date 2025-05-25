package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    // Елементи для створення нової сови
    public TextField txtName;
    public ComboBox<String> cmbType;
    public ComboBox<String> cmbColor;
    public CheckBox chkHasFeather;
    public ComboBox<String> cmbFeatherColor;
    public ToggleGroup tgSize;
    public RadioButton rbSmall;
    public RadioButton rbMedium;
    public RadioButton rbLarge;

    // Елементи для редагування існуючої сови
    public TextField txtEditName;
    public ComboBox<String> cmbEditColor;
    public CheckBox chkEditHasFeather;
    public ComboBox<String> cmbEditFeatherColor;
    public ToggleGroup tgEditSize;
    public RadioButton rbEditSmall;
    public RadioButton rbEditMedium;
    public RadioButton rbEditLarge;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ініціалізація для створення нової сови
        if (txtName != null) {
            initializeNewOwlDialog();
        }

        // Ініціалізація для редагування сови
        if (txtEditName != null) {
            initializeEditOwlDialog();
        }
    }

    private void initializeNewOwlDialog() {
        txtName.setText("Нова Сова");

        cmbType.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови");
        cmbType.setValue("Сова");

        cmbColor.getItems().addAll("BROWN", "DARKBROWN", "LIGHTBROWN", "GRAY", "WHITE", "BLACK");
        cmbColor.setValue("BROWN");

        cmbFeatherColor.getItems().addAll("GOLD", "SILVER", "RED", "BLUE", "GREEN", "PURPLE");
        cmbFeatherColor.setValue("GOLD");

        chkHasFeather.setSelected(true);
        rbMedium.setSelected(true);
    }

    private void initializeEditOwlDialog() {
        if (DialogEditOwl.currentOwl != null) {
            Owl owl = DialogEditOwl.currentOwl;

            txtEditName.setText(owl.name);

            cmbEditColor.getItems().addAll("BROWN", "DARKBROWN", "LIGHTBROWN", "GRAY", "WHITE", "BLACK");
            cmbEditColor.setValue(owl.color);

            cmbEditFeatherColor.getItems().addAll("GOLD", "SILVER", "RED", "BLUE", "GREEN", "PURPLE");
            cmbEditFeatherColor.setValue(owl.featherColor);

            chkEditHasFeather.setSelected(owl.hasFeather);

            // Встановлюємо розмір
            if (owl.size <= 60) {
                rbEditSmall.setSelected(true);
            } else if (owl.size <= 100) {
                rbEditMedium.setSelected(true);
            } else {
                rbEditLarge.setSelected(true);
            }
        }
    }

    // Обробник для створення нової сови
    public void pressOK(ActionEvent actionEvent) {
        String name = txtName.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Помилка", "Введіть ім'я сови!");
            return;
        }

        String type = cmbType.getValue();
        if (type == null) {
            showAlert("Помилка", "Оберіть тип сови!");
            return;
        }

        String color = cmbColor.getValue();
        if (color == null) {
            showAlert("Помилка", "Оберіть колір!");
            return;
        }

        String featherColor = cmbFeatherColor.getValue();
        boolean hasFeather = chkHasFeather.isSelected();

        RadioButton selectedSize = (RadioButton) tgSize.getSelectedToggle();
        if (selectedSize == null) {
            showAlert("Помилка", "Оберіть розмір!");
            return;
        }

        double size = 80; // Середній розмір за замовчуванням
        if (selectedSize == rbSmall) {
            size = 50;
        } else if (selectedSize == rbMedium) {
            size = 80;
        } else if (selectedSize == rbLarge) {
            size = 120;
        }

        // Створюємо нову сову
        switch (type) {
            case "Сова":
                Main.addNewOwl(name, color, hasFeather, featherColor, size);
                break;
            case "Великий Сова":
                Main.addNewBigOwl(name, color, hasFeather, featherColor, size);
                break;
            case "Нащадок Сови":
                Main.addNewOwlChild(name, color, hasFeather, featherColor, size);
                break;
        }

        DialogNewOwl.window.close();
    }

    // Обробник для редагування сови
    public void pressEditOK(ActionEvent actionEvent) {
        if (DialogEditOwl.currentOwl == null) return;

        String name = txtEditName.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Помилка", "Введіть ім'я сови!");
            return;
        }

        String color = cmbEditColor.getValue();
        if (color == null) {
            showAlert("Помилка", "Оберіть колір!");
            return;
        }

        String featherColor = cmbEditFeatherColor.getValue();
        boolean hasFeather = chkEditHasFeather.isSelected();

        RadioButton selectedSize = (RadioButton) tgEditSize.getSelectedToggle();
        if (selectedSize == null) {
            showAlert("Помилка", "Оберіть розмір!");
            return;
        }

        double size = 80;
        if (selectedSize == rbEditSmall) {
            size = 50;
        } else if (selectedSize == rbEditMedium) {
            size = 80;
        } else if (selectedSize == rbEditLarge) {
            size = 120;
        }

        // Оновлюємо параметри сови
        Owl owl = DialogEditOwl.currentOwl;
        owl.name = name;
        owl.color = color;
        owl.hasFeather = hasFeather;
        owl.featherColor = featherColor;
        owl.size = size;

        // Перемальовуємо сову з новими параметрами
        owl.redraw();

        DialogEditOwl.window.close();
    }

    public void pressCancel(ActionEvent actionEvent) {
        if (DialogNewOwl.window != null) {
            DialogNewOwl.window.close();
        }
        if (DialogEditOwl.window != null) {
            DialogEditOwl.window.close();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
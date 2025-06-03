package sekiro;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OwlEditDialog {

    public static void display(Owl owl) throws IOException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редагування сови: " + owl.name);
        window.setMinWidth(500);
        window.setMinHeight(600);

        // Основний контейнер з вкладками
        TabPane tabPane = new TabPane();

        // Вкладка основних параметрів
        Tab basicTab = new Tab("Основні параметри");
        basicTab.setClosable(false);
        VBox basicContent = createBasicParametersTab(owl);
        basicTab.setContent(basicContent);

        // Вкладка технік
        Tab techniquesTab = new Tab("Техніки");
        techniquesTab.setClosable(false);
        VBox techniquesContent = createTechniquesTab(owl);
        techniquesTab.setContent(techniquesContent);

        tabPane.getTabs().addAll(basicTab, techniquesTab);

        // Кнопки діалогу
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button saveButton = new Button("Зберегти зміни");
        Button cancelButton = new Button("Скасувати");
        Button previewButton = new Button("Попередній перегляд");

        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        previewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(saveButton, previewButton, cancelButton);

        // Обробники подій
        saveButton.setOnAction(e -> {
            if (saveChanges(owl, basicContent)) {
                window.close();
            }
        });

        previewButton.setOnAction(e -> showPreview(owl, basicContent));

        cancelButton.setOnAction(e -> window.close());

        // Основний макет
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(tabPane, buttonBox);

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);
        window.showAndWait();
    }

    private static VBox createBasicParametersTab(Owl owl) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Заголовок
        Label titleLabel = new Label("Основні параметри сови");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Форма з полями
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        // Поле імені
        Label nameLabel = new Label("Ім'я сови:");
        TextField nameField = new TextField(owl.name);
        nameField.setPromptText("Введіть ім'я сови");
        nameField.setId("nameField"); // Для пошуку

        // Поле типу
        Label typeLabel = new Label("Тип сови:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Сова", "Великий Сова", "Нащадок Сови", "Мудра Сова", "Бойова Сова");
        typeCombo.setValue(owl.type);
        typeCombo.setId("typeCombo");

        // Поле рівня майстерності
        Label skillLabel = new Label("Рівень майстерності:");
        ComboBox<String> skillCombo = new ComboBox<>();
        skillCombo.getItems().addAll("Новачок", "Учень", "Досвідчений", "Експерт", "Майстер", "Гранд-майстер");
        skillCombo.setValue(owl.skillLevel);
        skillCombo.setId("skillCombo");

        // Поле shinobi технік
        Label shinobiLabel = new Label("Shinobi техніки:");
        CheckBox shinobiCheck = new CheckBox("Володіє техніками Shinobi");
        shinobiCheck.setSelected(owl.hasShinobiTechniques);
        shinobiCheck.setId("shinobiCheck");

        // Поля позиції
        Label posLabel = new Label("Позиція:");
        HBox posBox = new HBox(5);
        Label xLabel = new Label("X:");
        TextField xField = new TextField(String.valueOf((int)owl.canvas.getLayoutX()));
        xField.setPrefWidth(80);
        xField.setId("xField");
        Label yLabel = new Label("Y:");
        TextField yField = new TextField(String.valueOf((int)owl.canvas.getLayoutY()));
        yField.setPrefWidth(80);
        yField.setId("yField");
        posBox.getChildren().addAll(xLabel, xField, yLabel, yField);

        // Додаємо елементи до форми
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(typeLabel, 0, 1);
        form.add(typeCombo, 1, 1);
        form.add(skillLabel, 0, 2);
        form.add(skillCombo, 1, 2);
        form.add(shinobiLabel, 0, 3);
        form.add(shinobiCheck, 1, 3);
        form.add(posLabel, 0, 4);
        form.add(posBox, 1, 4);

        // Інформаційна панель
        Label infoLabel = new Label("Поточна інформація:");
        infoLabel.setStyle("-fx-font-weight: bold;");

        TextArea infoArea = new TextArea();
        infoArea.setPrefRowCount(4);
        infoArea.setEditable(false);
        infoArea.setText(getOwlInfoText(owl));
        infoArea.setStyle("-fx-font-family: monospace;");

        content.getChildren().addAll(titleLabel, form, infoLabel, infoArea);
        return content;
    }

    private static VBox createTechniquesTab(Owl owl) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Заголовок
        Label titleLabel = new Label("Техніки сови");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Список технік
        ListView<String> techniquesList = new ListView<>();
        techniquesList.setPrefHeight(200);
        updateTechniquesList(techniquesList, owl);
        techniquesList.setId("techniquesList");

        // Кнопки управління техніками
        HBox techButtons = new HBox(5);
        Button addTechButton = new Button("Додати техніку");
        Button removeTechButton = new Button("Видалити");
        Button regenerateTechButton = new Button("Перегенерувати всі");

        addTechButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        removeTechButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        regenerateTechButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        techButtons.getChildren().addAll(addTechButton, removeTechButton, regenerateTechButton);

        // Детальна інформація про вибрану техніку
        Label detailLabel = new Label("Детальна інформація про техніку:");
        detailLabel.setStyle("-fx-font-weight: bold;");

        TextArea detailArea = new TextArea();
        detailArea.setPrefRowCount(6);
        detailArea.setEditable(false);
        detailArea.setStyle("-fx-font-family: monospace;");

        // Обробники подій для кнопок технік
        addTechButton.setOnAction(e -> {
            // Створюємо просту форму для додавання техніки
            showAddTechniqueDialog(owl, techniquesList);
        });

        removeTechButton.setOnAction(e -> {
            int selectedIndex = techniquesList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < owl.techniques.size()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Підтвердження видалення");
                confirm.setHeaderText("Видалення техніки");
                confirm.setContentText("Ви дійсно хочете видалити техніку \"" +
                        owl.techniques.get(selectedIndex).name + "\"?");

                if (confirm.showAndWait().get() == ButtonType.OK) {
                    owl.removeTechniqueByIndex(selectedIndex);
                    updateTechniquesList(techniquesList, owl);
                    detailArea.clear();
                }
            } else {
                showAlert("Попередження", "Будь ласка, оберіть техніку для видалення", Alert.AlertType.WARNING);
            }
        });

        regenerateTechButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Підтвердження перегенерації");
            confirm.setHeaderText("Перегенерація технік");
            confirm.setContentText("Це видалить всі поточні техніки і створить нові випадкові. Продовжити?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                owl.techniques.clear();
                owl.techniques.addAll(Technique.generateRandomTechniques(
                        owl.hasShinobiTechniques, owl.skillLevel, Main.rnd));
                updateTechniquesList(techniquesList, owl);
                detailArea.clear();
                owl.drawOwl(); // Оновлюємо відображення сови
            }
        });

        // Обробник вибору техніки зі списку
        techniquesList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < owl.techniques.size()) {
                Technique technique = owl.techniques.get(index);
                detailArea.setText(technique.getDetailedInfo());
            } else {
                detailArea.clear();
            }
        });

        content.getChildren().addAll(titleLabel, techniquesList, techButtons, detailLabel, detailArea);
        return content;
    }

    private static void updateTechniquesList(ListView<String> listView, Owl owl) {
        listView.getItems().clear();
        for (int i = 0; i < owl.techniques.size(); i++) {
            Technique tech = owl.techniques.get(i);
            String item = String.format("%d. %s [%s] - Сила: %d",
                    i + 1, tech.name, tech.element, tech.power);
            listView.getItems().add(item);
        }
    }

    private static String getOwlInfoText(Owl owl) {
        StringBuilder sb = new StringBuilder();
        sb.append("Поточні параметри:\n");
        sb.append("Ім'я: ").append(owl.name).append("\n");
        sb.append("Тип: ").append(owl.type).append("\n");
        sb.append("Рівень: ").append(owl.skillLevel).append("\n");
        sb.append("Shinobi техніки: ").append(owl.hasShinobiTechniques ? "Так" : "Ні").append("\n");
        sb.append("Кількість технік: ").append(owl.techniques.size()).append("\n");
        sb.append("Загальна сила: ").append(owl.getTotalPower()).append("\n");
        sb.append("Позиція: (").append((int)owl.canvas.getLayoutX())
                .append(", ").append((int)owl.canvas.getLayoutY()).append(")");
        return sb.toString();
    }

    private static boolean saveChanges(Owl owl, VBox basicContent) {
        try {
            // Знаходимо поля з основної вкладки
            TextField nameField = (TextField) basicContent.lookup("#nameField");
            ComboBox<String> typeCombo = (ComboBox<String>) basicContent.lookup("#typeCombo");
            ComboBox<String> skillCombo = (ComboBox<String>) basicContent.lookup("#skillCombo");
            CheckBox shinobiCheck = (CheckBox) basicContent.lookup("#shinobiCheck");
            TextField xField = (TextField) basicContent.lookup("#xField");
            TextField yField = (TextField) basicContent.lookup("#yField");

            // Валідація
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                showAlert("Помилка", "Введіть ім'я сови!", Alert.AlertType.WARNING);
                return false;
            }

            String newType = typeCombo.getValue();
            if (newType == null) {
                showAlert("Помилка", "Оберіть тип сови!", Alert.AlertType.WARNING);
                return false;
            }

            double newX = Double.parseDouble(xField.getText());
            double newY = Double.parseDouble(yField.getText());

            // Перевіряємо межі
            if (newX < 0 || newX > Main.WINDOW_WIDTH - 80 ||
                    newY < 0 || newY > Main.WINDOW_HEIGHT - 80) {
                showAlert("Помилка", "Координати виходять за межі вікна!", Alert.AlertType.WARNING);
                return false;
            }

            // Оновлюємо сову
            owl.name = newName;
            owl.type = newType;
            owl.hasShinobiTechniques = shinobiCheck.isSelected();
            owl.skillLevel = skillCombo.getValue();

            // Переміщуємо сову
            owl.move(newX - owl.canvas.getLayoutX(), newY - owl.canvas.getLayoutY());

            // Якщо змінились shinobi техніки, можливо потрібно оновити техніки
            if (shinobiCheck.isSelected() != owl.hasShinobiTechniques) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Оновлення технік");
                confirm.setHeaderText("Зміна Shinobi статусу");
                confirm.setContentText("Бажаєте перегенерувати техніки відповідно до нового статусу?");

                if (confirm.showAndWait().get() == ButtonType.OK) {
                    owl.techniques.clear();
                    owl.techniques.addAll(Technique.generateRandomTechniques(
                            owl.hasShinobiTechniques, owl.skillLevel, Main.rnd));
                }
            }

            // Перемальовуємо сову з новими параметрами
            owl.drawOwl();

            showAlert("Успіх", "Зміни збережено успішно!", Alert.AlertType.INFORMATION);
            return true;

        } catch (NumberFormatException ex) {
            showAlert("Помилка", "Невірний формат координат!", Alert.AlertType.ERROR);
            return false;
        } catch (Exception ex) {
            showAlert("Помилка", "Помилка при збереженні: " + ex.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private static void showPreview(Owl owl, VBox basicContent) {
        try {
            TextField nameField = (TextField) basicContent.lookup("#nameField");
            ComboBox<String> typeCombo = (ComboBox<String>) basicContent.lookup("#typeCombo");
            ComboBox<String> skillCombo = (ComboBox<String>) basicContent.lookup("#skillCombo");
            CheckBox shinobiCheck = (CheckBox) basicContent.lookup("#shinobiCheck");

            StringBuilder preview = new StringBuilder();
            preview.append("Попередній перегляд змін:\n\n");
            preview.append("Поточне ім'я: ").append(owl.name).append(" → Нове: ").append(nameField.getText()).append("\n");
            preview.append("Поточний тип: ").append(owl.type).append(" → Новий: ").append(typeCombo.getValue()).append("\n");
            preview.append("Поточний рівень: ").append(owl.skillLevel).append(" → Новий: ").append(skillCombo.getValue()).append("\n");
            preview.append("Поточні Shinobi техніки: ").append(owl.hasShinobiTechniques ? "Так" : "Ні")
                    .append(" → Нові: ").append(shinobiCheck.isSelected() ? "Так" : "Ні").append("\n");

            Alert preview_alert = new Alert(Alert.AlertType.INFORMATION);
            preview_alert.setTitle("Попередній перегляд");
            preview_alert.setHeaderText("Зміни, які будуть застосовані:");
            preview_alert.setContentText(preview.toString());
            preview_alert.showAndWait();

        } catch (Exception ex) {
            showAlert("Помилка", "Помилка попереднього перегляду: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private static void showAddTechniqueDialog(Owl owl, ListView<String> techniquesList) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Додати нову техніку");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Поля для нової техніки
        Label nameLabel = new Label("Назва техніки:");
        TextField nameField = new TextField();

        Label elementLabel = new Label("Елемент:");
        ComboBox<String> elementCombo = new ComboBox<>();
        elementCombo.getItems().addAll("Вогонь", "Вода", "Земля", "Повітря", "Тінь", "Світло");
        elementCombo.setValue("Вогонь");

        Label powerLabel = new Label("Сила (1-10):");
        TextField powerField = new TextField("5");

        Label descLabel = new Label("Опис:");
        TextField descField = new TextField();

        // Розміщення
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(elementLabel, 0, 1);
        grid.add(elementCombo, 1, 1);
        grid.add(powerLabel, 0, 2);
        grid.add(powerField, 1, 2);
        grid.add(descLabel, 0, 3);
        grid.add(descField, 1, 3);

        // Кнопки
        HBox buttons = new HBox(10);
        Button addButton = new Button("Додати");
        Button cancelButton = new Button("Скасувати");
        buttons.getChildren().addAll(addButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showAlert("Помилка", "Введіть назву техніки!", Alert.AlertType.WARNING);
                    return;
                }

                int power = Integer.parseInt(powerField.getText());
                if (power < 1 || power > 10) {
                    showAlert("Помилка", "Сила має бути від 1 до 10!", Alert.AlertType.WARNING);
                    return;
                }

                // Створюємо нову техніку з правильним конструктором
                String element = elementCombo.getValue();
                String description = descField.getText().trim().isEmpty() ?
                        "Техніка " + name + " елементу " + element :
                        descField.getText().trim();
                String type = owl.hasShinobiTechniques ? "Shinobi" : "Основна";

                Technique newTech = new Technique(name, type, description, power, element);

                owl.addTechnique(newTech);
                updateTechniquesList(techniquesList, owl);
                dialog.close();

            } catch (NumberFormatException ex) {
                showAlert("Помилка", "Невірний формат сили техніки!", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        grid.add(buttons, 0, 4, 2, 1);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Допоміжний метод для показу повідомлень
    private static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
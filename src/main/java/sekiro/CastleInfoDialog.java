package sekiro;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CastleInfoDialog {

    public static void display(Castle castle) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Інформація про замок: " + castle.name);
        window.setMinWidth(400);
        window.setMinHeight(300);

        // Головний контейнер
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Заголовок
        Label titleLabel = new Label(castle.name);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Інформація про замок
        Label typeLabel = new Label("Тип: " + castle.castleType);
        typeLabel.setFont(Font.font("Arial", 14));

        Label countLabel = new Label("Кількість сов: " + castle.getOwlCount());
        countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        countLabel.setTextFill(Color.DARKGREEN);

        // Список сов
        Label owlsTitle = new Label("Сови у замку:");
        owlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextArea owlsTextArea = new TextArea();
        owlsTextArea.setEditable(false);
        owlsTextArea.setPrefHeight(150);
        owlsTextArea.setWrapText(true);

        if (castle.getOwlCount() == 0) {
            owlsTextArea.setText("У цьому замку немає сов");
        } else {
            owlsTextArea.setText(castle.getDetailedOwlsList());
        }

        // Кнопки управління
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button removeOwlButton = new Button("Видалити сову");
        removeOwlButton.setDisable(castle.getOwlCount() == 0);
        removeOwlButton.setOnAction(e -> {
            if (castle.getOwlCount() > 0) {
                showRemoveOwlDialog(castle, window, owlsTextArea, countLabel, removeOwlButton);
            }
        });

        Button closeButton = new Button("Закрити");
        closeButton.setOnAction(e -> window.close());

        buttonBox.getChildren().addAll(removeOwlButton, closeButton);

        // Розміщення елементів
        mainLayout.getChildren().addAll(
                titleLabel,
                new Separator(),
                typeLabel,
                countLabel,
                new Separator(),
                owlsTitle,
                owlsTextArea,
                buttonBox
        );

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);
        window.showAndWait();
    }

    private static void showRemoveOwlDialog(Castle castle, Stage parentWindow,
                                            TextArea owlsTextArea, Label countLabel,
                                            Button removeOwlButton) {
        if (castle.getOwlCount() == 0) {
            showAlert("Помилка", "У замку немає сов для видалення", Alert.AlertType.WARNING);
            return;
        }

        Stage removeWindow = new Stage();
        removeWindow.initModality(Modality.APPLICATION_MODAL);
        removeWindow.setTitle("Видалення сови з замку");
        removeWindow.setMinWidth(350);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Оберіть сову для видалення з замку:");
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        // Список сов для вибору
        ListView<String> owlListView = new ListView<>();
        String[] owlNames = castle.getOwlNamesArray();
        owlListView.getItems().addAll(owlNames);
        owlListView.setPrefHeight(150);
        owlListView.getSelectionModel().selectFirst();

        // Кнопки
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("Видалити");
        confirmButton.setOnAction(e -> {
            int selectedIndex = owlListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Owl owlToRemove = castle.getOwlByIndex(selectedIndex);
                if (owlToRemove != null) {
                    castle.removeOwl(owlToRemove);

                    // Оновлюємо головне вікно
                    countLabel.setText("Кількість сов: " + castle.getOwlCount());
                    if (castle.getOwlCount() == 0) {
                        owlsTextArea.setText("У цьому замку немає сов");
                        removeOwlButton.setDisable(true);
                    } else {
                        owlsTextArea.setText(castle.getDetailedOwlsList());
                    }

                    // Оновлюємо головну сцену
                    Main.updateStatus();

                    showAlert("Успіх", "Сову '" + owlToRemove.name + "' видалено з замку",
                            Alert.AlertType.INFORMATION);
                    removeWindow.close();
                }
            } else {
                showAlert("Помилка", "Оберіть сову для видалення", Alert.AlertType.WARNING);
            }
        });

        Button cancelButton = new Button("Скасувати");
        cancelButton.setOnAction(e -> removeWindow.close());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        layout.getChildren().addAll(
                instructionLabel,
                owlListView,
                buttonBox
        );

        Scene scene = new Scene(layout);
        removeWindow.setScene(scene);
        removeWindow.showAndWait();
    }

    private static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
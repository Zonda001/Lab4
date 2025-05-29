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

import java.io.IOException;

public class OwlContextMenu {

    public static void display(Owl owl, double x, double y) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Меню сови: " + owl.name);
        window.setMinWidth(200);
        window.setResizable(false);

        VBox layout = new VBox(5);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Заголовок
        Label titleLabel = new Label(owl.name);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Кнопки меню
        Button editButton = new Button("Редагувати сову");
        editButton.setPrefWidth(180);
        editButton.setOnAction(e -> {
            window.close();
            try {
                OwlEditDialog.display(owl);
            } catch (IOException ex) {
                showAlert("Помилка", "Не вдалося відкрити діалог редагування", Alert.AlertType.ERROR);
            }
        });

        Button techniquesButton = new Button("Переглянути техніки");
        techniquesButton.setPrefWidth(180);
        techniquesButton.setOnAction(e -> {
            window.close();
            showTechniquesDialog(owl); // ВИПРАВЛЕНО: завершено обрізаний код
        });

        Button copyButton = new Button("Копіювати сову");
        copyButton.setPrefWidth(180);
        copyButton.setOnAction(e -> {
            window.close();
            copyOwl(owl);
        });

        Button deleteButton = new Button("Видалити сову");
        deleteButton.setPrefWidth(180);
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            window.close();
            deleteOwl(owl);
        });

        // Додаємо всі елементи до layout
        layout.getChildren().addAll(titleLabel,
                new Separator(),
                editButton,
                techniquesButton,
                copyButton,
                new Separator(),
                deleteButton);

        Scene scene = new Scene(layout);
        window.setScene(scene);

        // Позиціонуємо вікно біля курсора
        window.setX(x);
        window.setY(y);

        window.show();
    }

    // ДОДАНО: метод showAlert
    private static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ДОДАНО: метод для показу технік
    private static void showTechniquesDialog(Owl owl) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Техніки сови: " + owl.name);
        window.setWidth(500);
        window.setHeight(400);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label titleLabel = new Label("Техніки сови: " + owl.name);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea techniquesArea = new TextArea();
        techniquesArea.setText(owl.getDetailedTechniquesInfo());
        techniquesArea.setEditable(false);
        techniquesArea.setWrapText(true);

        Button closeButton = new Button("Закрити");
        closeButton.setOnAction(e -> window.close());

        layout.getChildren().addAll(titleLabel, techniquesArea, closeButton);
        VBox.setVgrow(techniquesArea, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    // ДОДАНО: метод для копіювання сови
    private static void copyOwl(Owl owl) {
        TextInputDialog dialog = new TextInputDialog(owl.name + "_копія");
        dialog.setTitle("Копіювання сови");
        dialog.setHeaderText("Створення копії сови");
        dialog.setContentText("Введіть ім'я для копії:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                // Створюємо копію поруч з оригіналом
                double newX = owl.canvas.getLayoutX() + 100;
                double newY = owl.canvas.getLayoutY() + 50;

                Owl copy = owl.createCopy(newName.trim(), newX, newY);
                showAlert("Успіх", "Сову успішно скопійовано!", Alert.AlertType.INFORMATION);
            }
        });
    }

    // ДОДАНО: метод для видалення сови
    private static void deleteOwl(Owl owl) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Підтвердження видалення");
        confirmAlert.setHeaderText("Видалення сови");
        confirmAlert.setContentText("Ви впевнені, що хочете видалити сову \"" + owl.name + "\"?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                owl.removeFromScene();
                showAlert("Успіх", "Сову успішно видалено!", Alert.AlertType.INFORMATION);
            }
        });
    }
}
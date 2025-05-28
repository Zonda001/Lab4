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
            showT
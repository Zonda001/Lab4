package sekiro;
Aboba
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    // Адаптивні розміри для ноутбуків
    public static final int WINDOW_WIDTH = 1200;  // Зменшено з 1200
    public static final int WINDOW_HEIGHT = 650;  // Зменшено з 800

    public static Stage primaryStage;
    public static Scene scene;
    public static Random rnd = new Random();
    public static Group group;
    public static ArrayList<Owl> owls = new ArrayList<>();
    public static ArrayList<Castle> castles = new ArrayList<>();
    public static Label statusLabel;
    public static Owl currentGeneral = null; // Поточний генерал
    private static int leftFormationPosition = 0;
    private static int rightTopFormationPosition = 0;
    private static int rightBottomFormationPosition = 0;
    private static int leftBottomFormationPosition = 0;


    // Фонова текстура
    private static ImageView backgroundImageView;
    private static Rectangle backgroundRectangle;

    public static void addNewOwl(String name, String type, boolean hasShinobiTechniques,
                                 String skillLevel, double x, double y) {
        Owl newOwl = new Owl(name, type, hasShinobiTechniques, skillLevel, x, y);
        owls.add(newOwl);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro");

        // Встановлення мінімального розміру вікна
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        group = new Group();

        // Завантажуємо фонову текстуру
        loadBackgroundTexture();

        // Створюємо макрооб'єкти (замки) з адаптованими координатами
        castles.add(new Castle("Замок Асіна", "Замок Асіна", 50, 50));
        castles.add(new Castle("Хіру-ден", "Хіру-ден", 350, 250));  // Адаптовано для меншого екрану
        castles.add(new Castle("Баштовий Додзьо", "Верхній Баштовий Додзьо", 650, 100));

        // Створюємо початкових сов з адаптованими координатами
        addNewOwl("Великий Сова", "Великий Сова", true, "Майстер", 250, 100);
        addNewOwl("Нащадок Сови", "Нащадок Сови", false, "Новачок", 450, 180);
        addNewOwl("Сова", "Сова", true, "Експерт", 600, 280);

        // Додаємо деяких сов до замків
        castles.get(0).addOwl(owls.get(0));
        castles.get(1).addOwl(owls.get(1));

        // Статус лейбл - розміщуємо внизу з відступом
        statusLabel = new Label("Натисніть Tab для створення нової сови (Insert не працює на всіх ноутбуках)");
        statusLabel.setFont(new Font("Arial", 12));  // Трохи менший шрифт
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(WINDOW_HEIGHT - 40);  // Адаптивна позиція
        statusLabel.setMaxWidth(WINDOW_WIDTH - 20);
        statusLabel.setWrapText(true);  // Дозволяємо перенос тексту

        // Додаємо тінь для кращої читабельності тексту на фоні
        statusLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 2, 0.5, 1, 1);");

        group.getChildren().add(statusLabel);

        scene = new Scene(group, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);

        // Обробка подій клавіатури
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handleKeyPress(event);
            }
        });

        // Обробка подій миші
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseClick(event);
            }
        });

        primaryStage.show();
        updateStatus();
    }

    private void loadBackgroundTexture() {
        try {
            // Спробуємо завантажити фонову текстуру
            InputStream stream = getClass().getResourceAsStream("/textures/background.png");

            if (stream != null) {
                Image backgroundImage = new Image(stream);

                if (!backgroundImage.isError()) {
                    // Якщо текстура завантажилась успішно
                    backgroundImageView = new ImageView(backgroundImage);
                    backgroundImageView.setFitWidth(WINDOW_WIDTH);
                    backgroundImageView.setFitHeight(WINDOW_HEIGHT);
                    backgroundImageView.setPreserveRatio(false); // Розтягуємо на весь екран
                    group.getChildren().add(backgroundImageView);

                    System.out.println("Фонова текстура завантажена успішно");
                    stream.close();
                    return;
                } else {
                    System.err.println("Помилка при завантаженні фонової текстури");
                    stream.close();
                }
            } else {
                System.out.println("Файл фонової текстури не знайдено: /textures/backgrounds/main_background.png");
            }
        } catch (Exception e) {
            System.err.println("Помилка завантаження фонової текстури: " + e.getMessage());
        }

        // Якщо текстура не завантажилась, використовуємо однотонний фон
        createFallbackBackground();
    }

    private void createFallbackBackground() {
        // Створюємо градієнтний фон як альтернативу
        backgroundRectangle = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Встановлюємо градієнт або однотонний колір
        backgroundRectangle.setFill(Color.DARKGREEN.darker());

        // Додаємо фоновий прямокутник
        group.getChildren().add(backgroundRectangle);

        System.out.println("Використовується резервний фон");
    }

    // Метод для зміни фону (можна викликати з меню або кнопок)
    public static void changeBackgroundTexture(String texturePath) {
        try {
            InputStream stream = Main.class.getResourceAsStream(texturePath);

            if (stream != null) {
                Image newBackgroundImage = new Image(stream);

                if (!newBackgroundImage.isError()) {
                    if (backgroundImageView != null) {
                        backgroundImageView.setImage(newBackgroundImage);
                    } else {
                        // Створюємо новий ImageView
                        backgroundImageView = new ImageView(newBackgroundImage);
                        backgroundImageView.setFitWidth(WINDOW_WIDTH);
                        backgroundImageView.setFitHeight(WINDOW_HEIGHT);
                        backgroundImageView.setPreserveRatio(false);

                        // Видаляємо старий фон
                        if (backgroundRectangle != null) {
                            group.getChildren().remove(backgroundRectangle);
                            backgroundRectangle = null;
                        }

                        // Додаємо новий фон на перше місце
                        group.getChildren().add(0, backgroundImageView);
                    }

                    System.out.println("Фон змінено на: " + texturePath);
                    stream.close();
                } else {
                    System.err.println("Помилка при завантаженні нової фонової текстури");
                    stream.close();
                }
            } else {
                System.out.println("Файл фонової текстури не знайдено: " + texturePath);
            }
        } catch (Exception e) {
            System.err.println("Помилка зміни фонової текстури: " + e.getMessage());
        }
    }

    private void handleKeyPress(KeyEvent event) {
        double delta = 10.0;
        if (event.isShiftDown()) delta *= 3.0;

        switch (event.getCode()) {
            case TAB:
            case INSERT:  // Залишаємо обидва варіанти
                try {
                    OwlCreationDialog.display();
                    updateStatus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case DELETE:
            case BACK_SPACE:  // Додаємо Backspace як альтернативу Delete
                deleteActiveOwls();
                updateStatus();
                break;

            case ESCAPE:
                deactivateAllOwls();
                updateStatus();
                break;

            case C:
                if (event.isControlDown()) {
                    copyActiveOwl();
                    updateStatus();
                }
                break;

            case UP:
                moveActiveOwlsOrGeneral(0, -delta);
                break;
            case DOWN:
                moveActiveOwlsOrGeneral(0, delta);
                break;
            case LEFT:
                moveActiveOwlsOrGeneral(-delta, 0);
                break;
            case RIGHT:
                moveActiveOwlsOrGeneral(delta, 0);
                break;
            case V:
                handleGeneralAssignment();
                updateStatus();
                break;

            // Додаткові команди для роботи з замками
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
                assignOwlToCastle(event.getCode());
                updateStatus();
                break;

            case R:
                removeOwlFromAllCastles();
                updateStatus();
                break;

            // Додаємо команди для зміни фону (опціонально)
            case B:
                if (event.isControlDown()) {
                    // Ctrl+B - змінити фон на альтернативний
                    changeBackgroundTexture("/textures/backgrounds/alternative_background.png");
                }
                break;
        }
    }

    private void showOwlContextMenu(Owl owl, double x, double y) {
        try {
            // Створюємо кастомне діалогове вікно
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Меню сови");
            window.setMinWidth(400);
            window.setMinHeight(500);
            window.setResizable(true);

            // Основний контейнер
            VBox mainLayout = new VBox(10);
            mainLayout.setPadding(new Insets(15));

            // Заголовок з назвою сови
            Label titleLabel = new Label("Сова: " + owl.name);
            titleLabel.setFont(new Font("Arial", 16));
            titleLabel.setStyle("-fx-font-weight: bold;");

            // Панель з кнопками дій
            HBox buttonPanel = new HBox(10);
            buttonPanel.setAlignment(Pos.CENTER);

            Button editButton = new Button("Редагувати");
            editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 16;");
            editButton.setOnAction(e -> {
                window.close();
                try {
                    OwlEditDialog.display(owl);
                } catch (IOException ex) {
                    showAlert("Помилка", "Не вдалося відкрити діалог редагування", Alert.AlertType.ERROR);
                }
            });

            Button copyButton = new Button("Копіювати");
            copyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 16;");
            copyButton.setOnAction(e -> {
                window.close();
                copyOwlWithDialog(owl);
            });

            Button deleteButton = new Button("Видалити");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 16;");
            deleteButton.setOnAction(e -> {
                window.close();
                deleteOwlWithConfirmation(owl);
            });

            Button techniquesButton = new Button("Техніки");
            techniquesButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 8 16;");
            techniquesButton.setOnAction(e -> {
                window.close();
                showTechniquesWindow(owl);
            });

            buttonPanel.getChildren().addAll(editButton, copyButton, deleteButton, techniquesButton);

            // Розділювач
            Separator separator = new Separator();

            // Інформаційна панель (ScrollPane для довгого тексту)
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(300);

            TextArea infoArea = new TextArea();
            infoArea.setEditable(false);
            infoArea.setWrapText(true);
            infoArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 12px;");

            // Формуємо текст з інформацією про сову
            StringBuilder content = new StringBuilder();
            content.append("Тип: ").append(owl.type).append("\n");
            content.append("Рівень: ").append(owl.skillLevel).append("\n");
            content.append("Техніки Shinobi: ").append(owl.hasShinobiTechniques ? "Так" : "Ні").append("\n");
            content.append("Кількість технік: ").append(owl.techniques.size()).append("\n");
            content.append("Загальна сила: ").append(owl.getTotalPower()).append("\n\n");

            if (owl.getBelongsToCastle() != null) {
                content.append("Належить замку: ").append(owl.getBelongsToCastle().name).append("\n");
            } else {
                content.append("Не належить жодному замку\n");
            }

            content.append("\nТехніки:\n");
            content.append(owl.getTechniquesInfo());
            content.append("\n\nЗагальна сила: ").append(owl.getTotalPower());

            infoArea.setText(content.toString());
            scrollPane.setContent(infoArea);

            // Кнопка закриття
            Button closeButton = new Button("Закрити");
            closeButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-padding: 8 20;");
            closeButton.setOnAction(e -> window.close());

            HBox closePanel = new HBox();
            closePanel.setAlignment(Pos.CENTER);
            closePanel.getChildren().add(closeButton);

            // Додаємо всі елементи до основного контейнера
            mainLayout.getChildren().addAll(
                    titleLabel,
                    buttonPanel,
                    separator,
                    scrollPane,
                    closePanel
            );

            // Налаштування сцени та показ вікна
            Scene scene = new Scene(mainLayout);
            window.setScene(scene);

            // Позиціонуємо вікно
            window.setX(Math.max(0, x));
            window.setY(Math.max(0, y));

            window.show();

        } catch (Exception e) {
            System.err.println("Помилка відображення контекстного меню: " + e.getMessage());
            e.printStackTrace();

            // Показуємо простий Alert якщо щось пішло не так
            showSimpleOwlInfo(owl);
        }
    }

    private void handleGeneralAssignment(){
        // Знаходимо активну сову
        Owl activeOwl = null;
        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeOwl = owl;
                break;
            }
        }

        if (activeOwl != null) {
            // Якщо вже є генерал, деактивуємо його
            if (currentGeneral != null) {
                currentGeneral.setGeneral(false);
                currentGeneral.setActive(false);
            }

            // Призначаємо нового генерала
            currentGeneral = activeOwl;
            currentGeneral.setGeneral(true);
            currentGeneral.setActive(true);

            // Скидаємо позиції в шерензі
            resetAllFormationPositions();

            System.out.println(activeOwl.name + " призначено генералом!");
        } else {
            System.out.println("Немає активної сови для призначення генералом");
        }
    }

    private void resetAllFormationPositions() {
        leftFormationPosition = 0;
        rightTopFormationPosition = 0;
        rightBottomFormationPosition = 0;
        leftBottomFormationPosition = 0;
    }

    private void deactivateGeneral(){
        if (currentGeneral != null){
            currentGeneral.setGeneral(false);
            currentGeneral.setActive(false);
            currentGeneral = null;
            resetAllFormationPositions();
            System.out.println("Генерал деактивований");
        }
    }

    private void moveActiveOwlsOrGeneral(double dx, double dy) {
        if (currentGeneral != null) {
            // Рухаємо генерала
            double newX = currentGeneral.canvas.getLayoutX() + dx;
            double newY = currentGeneral.canvas.getLayoutY() + dy;

            // Перевіряємо межі екрану
            if (newX >= 0 && newX + currentGeneral.canvas.getWidth() <= WINDOW_WIDTH &&
                    newY >= 0 && newY + currentGeneral.canvas.getHeight() <= WINDOW_HEIGHT - 50) {

                currentGeneral.move(dx, dy);

                // ВИПРАВЛЕНО: Перевіряємо дотик ПІСЛЯ руху
                checkGeneralTouchingOwls();
                updateStatus(); // Оновлюємо статус після руху
            }
        } else {
            // Звичайний рух активних сов
            moveActiveOwls(dx, dy);
        }
    }

    private void checkGeneralTouchingOwls() {
        if (currentGeneral == null) return;

        for (Owl owl : owls) {
            if (owl != currentGeneral && currentGeneral.isTouching(owl)) {
                // Перевіряємо чи сова ще не в формації
                if (!isOwlInFormation(owl)) {
                    // Визначаємо до якого замку належить сова
                    Castle owlCastle = owl.getBelongsToCastle();

                    if (owlCastle != null) {
                        // Сова належить замку - телепортуємо в залежності від замку
                        String formationType = getFormationTypeForCastle(owlCastle);
                        teleportOwlToSpecificFormation(owl, formationType);

                        // Телепортуємо всіх інших сов з того ж замку
                        teleportCastleOwlsToFormation(owlCastle, formationType);

                        System.out.println("Генерал " + currentGeneral.name + " торкнувся " + owl.name +
                                " з замку " + owlCastle.name + " - телепортація в " + formationType + "!");
                    } else {
                        // Сова не належить жодному замку - телепортуємо вліво зверху
                        teleportOwlToSpecificFormation(owl, "left_top");
                        System.out.println("Генерал " + currentGeneral.name + " торкнувся " + owl.name +
                                " (вільна сова) - телепортація вліво зверху!");
                    }
                }
            }
        }
    }
    private void teleportOwlToSpecificFormation(Owl owl, String formationType) {
        switch (formationType) {
            case "right_top":
                owl.teleportToFormation("right_top", rightTopFormationPosition);
                rightTopFormationPosition++;
                break;
            case "right_bottom":
                owl.teleportToFormation("right_bottom", rightBottomFormationPosition);
                rightBottomFormationPosition++;
                break;
            case "left_bottom":
                owl.teleportToFormation("left_bottom", leftBottomFormationPosition);
                leftBottomFormationPosition++;
                break;
            case "left_top":
            default:
                owl.teleportToFormation("left_top", leftFormationPosition);
                leftFormationPosition++;
                break;
        }
    }

    private String getFormationTypeForCastle(Castle castle) {
        switch (castle.name) {
            case "Замок Асіна":
                return "right_top";      // Справа зверху
            case "Хіру-ден":
                return "right_bottom";   // Справа знизу
            case "Баштовий Додзьо":
                return "left_bottom";    // Зліва знизу
            default:
                return "left_top";       // За замовчуванням зліва зверху
        }
    }

    private void teleportCastleOwlsToFormation(Castle castle, String formationType) {
        if (castle == null) return;

        ArrayList<Owl> castleOwls = castle.getOwls();

        for (Owl owl : castleOwls) {
            if (owl != currentGeneral && !isOwlInFormation(owl)) {
                teleportOwlToSpecificFormation(owl, formationType);
                System.out.println(owl.name + " (з замку " + castle.name + ") автоматично телепортовано в " + formationType + " шеренгу");
            }
        }
    }

    private boolean isOwlInFormation(Owl owl) {
        double x = owl.canvas.getLayoutX();
        double y = owl.canvas.getLayoutY();

        // Перевіряємо чи сова знаходиться в будь-якій із шеренг
        boolean inLeftTopFormation = (x <= 30 && y <= WINDOW_HEIGHT / 2); // Ліва верхня
        boolean inRightTopFormation = (x >= WINDOW_WIDTH - 120 && y <= WINDOW_HEIGHT / 2); // Права верхня
        boolean inRightBottomFormation = (x >= WINDOW_WIDTH - 120 && y > WINDOW_HEIGHT / 2); // Права нижня
        boolean inLeftBottomFormation = (x <= 30 && y > WINDOW_HEIGHT / 2); // Ліва нижня

        return inLeftTopFormation || inRightTopFormation || inRightBottomFormation || inLeftBottomFormation;
    }

    private void copyOwlWithDialog(Owl owl) {
        TextInputDialog dialog = new TextInputDialog(owl.name + "_копія");
        dialog.setTitle("Копіювання сови");
        dialog.setHeaderText("Створення копії сови");
        dialog.setContentText("Введіть ім'я для копії:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                // Знаходимо вільне місце для копії
                double newX = owl.canvas.getLayoutX() + 100;
                double newY = owl.canvas.getLayoutY() + 50;

                // Перевіряємо межі екрану
                if (newX + 80 > WINDOW_WIDTH) newX = owl.canvas.getLayoutX() - 100;
                if (newY + 80 > WINDOW_HEIGHT) newY = owl.canvas.getLayoutY() - 50;

                addNewOwl(newName.trim(), owl.type, owl.hasShinobiTechniques, owl.skillLevel, newX, newY);
                updateStatus();
                showAlert("Успіх", "Сову успішно скопійовано!", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void deleteOwlWithConfirmation(Owl owl) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Підтвердження видалення");
        confirmAlert.setHeaderText("Видалення сови");
        confirmAlert.setContentText("Ви впевнені, що хочете видалити сову \"" + owl.name + "\"?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Видаляємо з усіх замків
                for (Castle castle : castles) {
                    castle.removeOwl(owl);
                }
                owl.removeFromScene();
                owls.remove(owl);
                updateStatus();
                showAlert("Успіх", "Сову успішно видалено!", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void showTechniquesWindow(Owl owl) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Техніки сови: " + owl.name);
        window.setWidth(600);
        window.setHeight(500);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label titleLabel = new Label("Детальна інформація про техніки");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        TextArea techniquesArea = new TextArea();
        techniquesArea.setText(getDetailedTechniquesInfo(owl));
        techniquesArea.setEditable(false);
        techniquesArea.setWrapText(true);
        techniquesArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace;");

        Button closeButton = new Button("Закрити");
        closeButton.setOnAction(e -> window.close());

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);

        layout.getChildren().addAll(titleLabel, techniquesArea, buttonBox);
        VBox.setVgrow(techniquesArea, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private String getDetailedTechniquesInfo(Owl owl) {
        StringBuilder sb = new StringBuilder();
        sb.append("Техніки сови ").append(owl.name).append(":\n\n");

        for (int i = 0; i < owl.techniques.size(); i++) {
            Technique tech = owl.techniques.get(i);
            sb.append(String.format("%d. %s [%s, %s, Сила: %d]\n",
                    i + 1, tech.name, tech.type, tech.element, tech.power));
            sb.append("   Опис: ").append(tech.description).append("\n\n");
        }

        sb.append("Загальна сила всіх технік: ").append(owl.getTotalPower());
        return sb.toString();
    }

    private void showSimpleOwlInfo(Owl owl) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація про сову");
        alert.setHeaderText("Сова: " + owl.name);
        alert.setContentText("Тип: " + owl.type + "\nРівень: " + owl.skillLevel +
                "\nТехніки: " + owl.techniques.size() +
                "\nЗагальна сила: " + owl.getTotalPower());
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            // Лівий клік - активація/деактивація сови
            boolean owlClicked = false;
            for (Owl owl : owls) {
                if (owl.contains(event.getX(), event.getY())) {
                    owl.setActive(!owl.isActive());
                    owlClicked = true;
                    break;
                }
            }
            if (!owlClicked) {
                // Якщо не клікнули на сову, перевіряємо замки
                for (Castle castle : castles) {
                    if (castle.containsPoint(event.getX(), event.getY())) {
                        showCastleInfo(castle);
                        break;
                    }
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            // Правий клік - меню з опціями (редагування сови або перегляд технік)
            for (Owl owl : owls) {
                if (owl.contains(event.getX(), event.getY())) {
                    showOwlContextMenu(owl, event.getX(), event.getY());
                    break;
                }
            }
        }
        updateStatus();
    }

    private void deleteActiveOwls() {
        for (int i = owls.size() - 1; i >= 0; i--) {
            Owl owl = owls.get(i);
            if (owl.isActive()) {
                // Видаляємо з усіх замків
                for (Castle castle : castles) {
                    castle.removeOwl(owl);
                }
                owl.removeFromScene();
                owls.remove(i);
            }
        }
    }

    private void deactivateAllOwls() {
        for (Owl owl : owls) {
            owl.setActive(false);
        }
    }

    private void copyActiveOwl() {
        for (Owl owl : owls) {
            if (owl.isActive()) {
                try {
                    // Використовуємо існуючий метод createCopy()
                    double newX = owl.canvas.getLayoutX() + 50;
                    double newY = owl.canvas.getLayoutY() + 50;

                    // Перевіряємо межі екрану
                    if (newX + 80 > WINDOW_WIDTH) newX = owl.canvas.getLayoutX() - 50;
                    if (newY + 80 > WINDOW_HEIGHT - 50) newY = owl.canvas.getLayoutY() - 50;

                    Owl clonedOwl = owl.createCopy(owl.name + " (копія)", newX, newY);
                    owls.add(clonedOwl);

                    System.out.println("Створено копію сови з тими ж техніками: " + clonedOwl.name);
                    break;

                } catch (Exception e) {
                    System.err.println("Помилка при копіюванні сови: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveActiveOwls(double dx, double dy) {
        for (Owl owl : owls) {
            if (owl.isActive()) {
                // Перевіряємо межі екрану
                double newX = owl.canvas.getLayoutX() + dx;
                double newY = owl.canvas.getLayoutY() + dy;

                // Обмежуємо рух межами вікна
                if (newX >= 0 && newX + owl.canvas.getWidth() <= WINDOW_WIDTH &&
                        newY >= 0 && newY + owl.canvas.getHeight() <= WINDOW_HEIGHT - 50) {
                    owl.move(dx, dy);
                }
            }
        }
    }

    private void assignOwlToCastle(KeyCode keyCode) {
        int castleIndex = -1;
        switch (keyCode) {
            case DIGIT1: castleIndex = 0; break;
            case DIGIT2: castleIndex = 1; break;
            case DIGIT3: castleIndex = 2; break;
        }

        if (castleIndex >= 0 && castleIndex < castles.size()) {
            Castle castle = castles.get(castleIndex);
            for (Owl owl : owls) {
                if (owl.isActive()) {
                    // Спочатку видаляємо з інших замків
                    for (Castle c : castles) {
                        c.removeOwl(owl);
                    }
                    // Потім додаємо до обраного замку
                    castle.addOwl(owl);
                }
            }
        }
    }

    private void removeOwlFromAllCastles() {
        for (Owl owl : owls) {
            if (owl.isActive()) {
                for (Castle castle : castles) {
                    castle.removeOwl(owl);
                }
            }
        }
    }

    private void showCastleInfo(Castle castle) {
        try {
            CastleInfoDialog.display(castle);
            updateStatus();
        } catch (Exception e) {
            // Якщо діалог не вдалося відкрити, показуємо простий Alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Інформація про замок");
            alert.setHeaderText(castle.name);
            alert.setContentText("Кількість сов: " + castle.getOwlCount() +
                    "\n\nСписок сов:\n" + castle.getOwlsList());
            alert.showAndWait();
        }
    }

    public static void updateStatus() {
        int activeCount = 0;
        StringBuilder activeOwls = new StringBuilder();


        for (Owl owl : owls) {
            if (owl.isActive() && !owl.isGeneral()) { // Виключаємо генерала з підрахунку звичайних активних
                activeCount++;
                if (activeOwls.length() > 0) {
                    activeOwls.append(", ");
                }
                activeOwls.append(owl.name);
            }
        }

        String statusText;

        if (currentGeneral != null) {
            // Якщо є генерал
            statusText = "★ ГЕНЕРАЛ: " + currentGeneral.name +
                    " | Стрілки - рух генерала для формування шеренг | V - змінити генерала | ESC - деактивувати";

            if (activeCount > 0) {
                statusText += " | Активних сов: " + activeCount + " (" + activeOwls.toString() + ")";
            }
        } else if (activeCount == 0) {
            statusText = "Немає активних сов. Tab - створити, клік - активувати, V - призначити генерала, Ctrl+B - змінити фон";
        } else if (activeCount == 1) {
            statusText = "Активна сова: " + activeOwls.toString() +
                    " | V - зробити генералом | Стрілки - рух, Del/Backspace - видалити, Ctrl+C - копіювати, 1/2/3 - до замку, R - з замку";
        } else {
            statusText = "Активних сов: " + activeCount + " (" + activeOwls.toString() +
                    ") | Стрілки - рух, Del/Backspace - видалити, ESC - деактивувати";
        }

        statusLabel.setText(statusText);

        // Оновлюємо відображення замків
        for (Castle castle : castles) {
            castle.updateDisplay();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
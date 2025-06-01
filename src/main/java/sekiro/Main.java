package sekiro;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
                moveActiveOwls(0, -delta);
                break;
            case DOWN:
                moveActiveOwls(0, delta);
                break;
            case LEFT:
                moveActiveOwls(-delta, 0);
                break;
            case RIGHT:
                moveActiveOwls(delta, 0);
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
            // Create a simple context menu using Alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Меню сови");
            alert.setHeaderText("Сова: " + owl.name);

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

            content.append("\nТехніки:\n").append(owl.getTechniquesInfo());

            alert.setContentText(content.toString());
            alert.showAndWait();

        } catch (Exception e) {
            System.err.println("Помилка відображення контекстного меню: " + e.getMessage());
            e.printStackTrace();
        }
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
                    // Створюємо копію сови
                    Owl clonedOwl = new Owl(
                            owl.name + " (копія)",
                            owl.type,
                            owl.hasShinobiTechniques,
                            owl.skillLevel,
                            owl.canvas.getLayoutX() + 50,
                            owl.canvas.getLayoutY() + 50
                    );

                    // Перевіряємо межі екрану і коригуємо позицію якщо потрібно
                    double newX = clonedOwl.canvas.getLayoutX();
                    double newY = clonedOwl.canvas.getLayoutY();

                    if (newX + clonedOwl.canvas.getWidth() > WINDOW_WIDTH) {
                        newX = owl.canvas.getLayoutX() - 50;
                    }
                    if (newY + clonedOwl.canvas.getHeight() > WINDOW_HEIGHT - 50) {
                        newY = owl.canvas.getLayoutY() - 50;
                    }

                    // Встановлюємо скориговану позицію
                    clonedOwl.canvas.setLayoutX(newX);
                    clonedOwl.canvas.setLayoutY(newY);
                    clonedOwl.updatePosition();

                    // Додаємо до списку сов
                    owls.add(clonedOwl);

                    System.out.println("Створено копію сови: " + clonedOwl.name +
                            " на позиції (" + newX + ", " + newY + ")");
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
            if (owl.isActive()) {
                activeCount++;
                if (activeOwls.length() > 0) {
                    activeOwls.append(", ");
                }
                activeOwls.append(owl.name);
            }
        }

        String statusText;
        if (activeCount == 0) {
            statusText = "Немає активних сов. Tab - створити, клік - активувати, Ctrl+B - змінити фон";
        } else if (activeCount == 1) {
            statusText = "Активна сова: " + activeOwls.toString() +
                    " | Стрілки - рух, Del/Backspace - видалити, Ctrl+C - копіювати, 1/2/3 - до замку, R - з замку";
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
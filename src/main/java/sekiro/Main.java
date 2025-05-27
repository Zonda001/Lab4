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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    public static Stage primaryStage;
    public static Scene scene;
    public static Random rnd = new Random();
    public static Group group;
    public static ArrayList<Owl> owls = new ArrayList<>();
    public static ArrayList<Castle> castles = new ArrayList<>();
    public static Label statusLabel;

    public static void addNewOwl(String name, String type, boolean hasShinobiTechniques,
                                 String skillLevel, double x, double y) {
        Owl newOwl = new Owl(name, type, hasShinobiTechniques, skillLevel, x, y);
        owls.add(newOwl);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro - Сови та Замки");

        group = new Group();
        Rectangle background = new Rectangle(1200, 800, Color.DARKGREEN.darker());
        group.getChildren().add(background);

        // Створюємо макрооб'єкти (замки)
        castles.add(new Castle("Замок Асіна", "Замок Асіна", 50, 50));
        castles.add(new Castle("Хіру-ден", "Хіру-ден", 400, 300));
        castles.add(new Castle("Баштовий Додзьо", "Верхній Баштовий Додзьо", 750, 100));

        // Створюємо початкових сов
        addNewOwl("Отець Сова", "Великий Сова", true, "Майстер", 300, 100);
        addNewOwl("Молода Сова", "Нащадок Сови", false, "Новачок", 500, 200);
        addNewOwl("Стража", "Сова", true, "Експерт", 700, 300);

        // Додаємо деяких сов до замків
        castles.get(0).addOwl(owls.get(0));
        castles.get(1).addOwl(owls.get(1));

        // Статус лейбл
        statusLabel = new Label("Натисніть Insert для створення нової сови");
        statusLabel.setFont(new Font("Arial", 14));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(750);
        group.getChildren().add(statusLabel);

        scene = new Scene(group, 1200, 800);
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

    private void handleKeyPress(KeyEvent event) {
        double delta = 10.0;
        if (event.isShiftDown()) delta *= 3.0;

        switch (event.getCode()) {
            case TAB:
                try {
                    OwlCreationDialog.display();
                    updateStatus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case DELETE:
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
            // Правий клік - редагування сови
            for (Owl owl : owls) {
                if (owl.contains(event.getX(), event.getY())) {
                    try {
                        OwlEditDialog.display(owl);
                        updateStatus();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
                    Owl clonedOwl = owl.clone();
                    clonedOwl.name = owl.name + " (копія)";
                    clonedOwl.canvas.setLayoutX(owl.canvas.getLayoutX() + 50);
                    clonedOwl.canvas.setLayoutY(owl.canvas.getLayoutY() + 50);

                    // Додаємо клонований об'єкт до сцени
                    Main.group.getChildren().add(clonedOwl.canvas);
                    Main.group.getChildren().add(clonedOwl.rectActive);
                    if (clonedOwl.hasShinobiTechniques) {
                        Main.group.getChildren().add(clonedOwl.shinobiRect);
                    }

                    clonedOwl.updatePosition();
                    owls.add(clonedOwl);
                    break;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void moveActiveOwls(double dx, double dy) {
        for (Owl owl : owls) {
            if (owl.isActive()) {
                owl.move(dx, dy);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація про замок");
        alert.setHeaderText(castle.name);
        alert.setContentText("Кількість сов: " + castle.getOwlCount() +
                "\nСписок сов: " + castle.getOwlsList());
        alert.showAndWait();
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
            statusText = "Немає активних сов. Insert - створити, клік - активувати";
        } else if (activeCount == 1) {
            statusText = "Активна сова: " + activeOwls.toString() +
                    " | Стрілки - рух, Delete - видалити, Ctrl+C - копіювати, 1/2/3 - до замку, R - з замку";
        } else {
            statusText = "Активних сов: " + activeCount + " (" + activeOwls.toString() +
                    ") | Стрілки - рух, Delete - видалити, ESC - деактивувати";
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
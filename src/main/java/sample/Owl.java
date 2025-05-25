package sample;

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
import java.util.List;
import java.util.Random;

public class Main extends Application {
    public static Stage primaryStage;
    public static Scene scene;
    public static Group group;
    public static Random rnd = new Random();

    public static List<Owl> owls = new ArrayList<>();
    public static List<MacroObject> macroObjects = new ArrayList<>();

    private Label statusLabel;
    private Rectangle statusBackground;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro: Світ Сов і Замків");

        // Створюємо групу для всіх об'єктів
        group = new Group();

        // Фон
        Rectangle background = new Rectangle(1000, 700, Color.DARKBLUE);
        group.getChildren().add(background);

        // Створюємо макрооб'єкти
        createMacroObjects();

        // Створюємо початкових сов
        createInitialOwls();

        // Створюємо рядок статуса
        createStatusBar();

        // Створюємо сцену
        scene = new Scene(group, 1000, 700);
        primaryStage.setScene(scene);

        // Обробники подій
        scene.setOnKeyPressed(new KeyPressedHandler());
        scene.setOnMouseClicked(new MouseClickHandler());

        updateStatusBar();
        primaryStage.show();
    }

    private void createMacroObjects() {
        // Створюємо три макрооб'єкти
        macroObjects.add(new AshigaCastle(50, 50));
        macroObjects.add(new Hirudoen(300, 100));
        macroObjects.add(new UpperTowerDojo(600, 30));
    }

    private void createInitialOwls() {
        // Створюємо початкових сов
        owls.add(new Owl("Сова-Наставник", "Сова",
                100, 250, 150, true,
                new OwlWeapon("Катана", 9)));

        owls.add(new Owl("Великий Хозо", "Великий Сова",
                300, 300, 200, false,
                new OwlWeapon("Кинджал", 7)));

        owls.add(new Owl("Молодий Учень", "Нащадок Сови",
                500, 350, 25, true,
                new OwlWeapon("Сюрікен", 5)));
    }

    private void createStatusBar() {
        statusBackground = new Rectangle(0, 670, 1000, 30);
        statusBackground.setFill(Color.LIGHTGRAY);
        group.getChildren().add(statusBackground);

        statusLabel = new Label("Натисніть Insert для створення нової сови");
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(675);
        statusLabel.setFont(new Font("Arial", 12));
        group.getChildren().add(statusLabel);
    }

    private void updateStatusBar() {
        List<Owl> activeOwls = getActiveOwls();
        if (activeOwls.isEmpty()) {
            statusLabel.setText("Активних сов немає. Натисніть на сову для активації.");
        } else if (activeOwls.size() == 1) {
            Owl owl = activeOwls.get(0);
            String macroInfo = owl.belongsToMacroObject() ?
                    " | Належить до: " + owl.getMacroObject().getName() : " | Вільна";
            statusLabel.setText("Активна: " + owl.toString() + macroInfo);
        } else {
            statusLabel.setText("Активних сов: " + activeOwls.size() +
                    " | Використовуйте стрілки для руху, Delete для видалення");
        }
    }

    public static void addNewOwl(String name, String type, int age,
                                 boolean hasScroll, OwlWeapon weapon) {
        double x = rnd.nextInt(800);
        double y = rnd.nextInt(500) + 100;
        owls.add(new Owl(name, type, x, y, age, hasScroll, weapon));
    }

    private List<Owl> getActiveOwls() {
        List<Owl> activeOwls = new ArrayList<>();
        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeOwls.add(owl);
            }
        }
        return activeOwls;
    }

    private void deactivateAllOwls() {
        for (Owl owl : owls) {
            owl.setActive(false);
        }
        updateStatusBar();
    }

    private void deleteActiveOwls() {
        List<Owl> toRemove = new ArrayList<>();
        for (Owl owl : owls) {
            if (owl.isActive()) {
                owl.removeFromScene();
                toRemove.add(owl);
            }
        }
        owls.removeAll(toRemove);
        updateStatusBar();
    }

    private void moveActiveOwls(double dx, double dy) {
        boolean moved = false;
        for (Owl owl : owls) {
            if (owl.isActive()) {
                owl.move(dx, dy);
                moved = true;
            }
        }
        if (moved) {
            updateStatusBar();
        }
    }

    private void copyActiveOwl() {
        List<Owl> activeOwls = getActiveOwls();
        if (activeOwls.size() == 1) {
            Owl original = activeOwls.get(0);
            Owl copy = original.clone();
            copy.move(20, 20); // Зміщуємо копію
            owls.add(copy);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Копіювання");
            alert.setHeaderText("Сову скопійовано!");
            alert.setContentText("Створено копію: " + copy.getName());
            alert.showAndWait();
        } else if (activeOwls.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Помилка копіювання");
            alert.setHeaderText("Забагато активних сов!");
            alert.setContentText("Для копіювання виберіть тільки одну сову.");
            alert.showAndWait();
        }
    }

    private void handleOwlToMacroObjectAssignment(Owl owl) {
        if (macroObjects.isEmpty()) return;

        // Простий алгоритм: знайти найближчий макрооб'єкт
        MacroObject closest = null;
        double minDistance = Double.MAX_VALUE;

        for (MacroObject macro : macroObjects) {
            double distance = Math.sqrt(Math.pow(owl.getX() - macro.getX(), 2) +
                    Math.pow(owl.getY() - macro.getY(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                closest = macro;
            }
        }

        if (closest != null) {
            if (owl.belongsToMacroObject()) {
                // Якщо сова вже належить комусь, видаляємо її
                owl.getMacroObject().removeOwl(owl);
            }
            closest.addOwl(owl);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Призначення сови");
            alert.setHeaderText("Сову призначено!");
            alert.setContentText(owl.getName() + " тепер належить до " + closest.getName());
            alert.showAndWait();
        }
    }

    private void removeOwlFromMacroObjects(Owl owl) {
        if (owl.belongsToMacroObject()) {
            MacroObject macro = owl.getMacroObject();
            macro.removeOwl(owl);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Звільнення сови");
            alert.setHeaderText("Сову звільнено!");
            alert.setContentText(owl.getName() + " більше не належить до " + macro.getName());
            alert.showAndWait();
        }
    }

    private class KeyPressedHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            double delta = event.isShiftDown() ? 20.0 : 5.0;

            switch (event.getCode()) {
                case INSERT:
                    try {
                        DialogNewOwl.display();
                        updateStatusBar();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case DELETE:
                    deleteActiveOwls();
                    break;

                case ESCAPE:
                    deactivateAllOwls();
                    break;

                case C:
                    if (event.isControlDown()) {
                        copyActiveOwl();
                    }
                    break;

                case UP:
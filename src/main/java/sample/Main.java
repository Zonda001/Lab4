package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import java.util.Iterator;
import java.util.Random;

// Клас для макрооб'єктів
class MacroObject {
    String name;
    Canvas canvas;
    int owlCount;
    Label countLabel;

    public MacroObject(String name, double x, double y) {
        this.name = name;
        this.owlCount = 0;
        canvas = new Canvas(200, 150);
        drawMacroObject(canvas, name);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);

        // Створюємо лейбл для відображення кількості сов
        countLabel = new Label("Сови: " + owlCount);
        countLabel.setFont(new Font("Arial", 14));
        countLabel.setTextFill(Color.WHITE);
        countLabel.setLayoutX(x + 10);
        countLabel.setLayoutY(y + 120);

        Main.group.getChildren().addAll(canvas, countLabel);
    }

    public void updateOwlCount() {
        owlCount = 0;
        for (Owl owl : Main.owls) {
            if (owl.belongsToMacro == this) {
                owlCount++;
            }
        }
        countLabel.setText("Сови: " + owlCount);
    }

    public boolean contains(double x, double y) {
        return x >= canvas.getLayoutX() && x <= canvas.getLayoutX() + canvas.getWidth() &&
                y >= canvas.getLayoutY() && y <= canvas.getLayoutY() + canvas.getHeight();
    }

    private void drawMacroObject(Canvas canvas, String name) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (name.equals("Замок Асіна")) {
            // Малюємо замок
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(50, 80, 100, 60);
            gc.setFill(Color.GRAY);
            gc.fillRect(40, 60, 120, 20);
            gc.setFill(Color.DARKRED);
            gc.fillPolygon(new double[]{40, 80, 120, 160}, new double[]{60, 40, 40, 60}, 4);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Замок Асіна", 60, 100);
        } else if (name.equals("Хіру-ден")) {
            // Малюємо храм
            gc.setFill(Color.BROWN);
            gc.fillRect(30, 90, 140, 50);
            gc.setFill(Color.DARKRED);
            gc.fillPolygon(new double[]{20, 100, 180}, new double[]{90, 50, 90}, 3);
            gc.setFill(Color.GOLD);
            gc.fillOval(90, 70, 20, 20);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Хіру-ден", 75, 110);
        } else if (name.equals("Верхній Баштовий Додзьо")) {
            // Малюємо додзьо
            gc.setFill(Color.DARKGREEN);
            gc.fillRect(60, 70, 80, 70);
            gc.setFill(Color.BLACK);
            gc.fillRect(70, 60, 60, 10);
            gc.setFill(Color.YELLOW);
            gc.fillOval(85, 80, 30, 30);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 10));
            gc.fillText("Верхній Додзьо", 65, 120);
        }
    }
}

public class Main extends Application {
    public static Stage primaryStage;
    public static Scene scene;
    public static Random rnd = new Random();
    public static Group group;
    public static ArrayList<Owl> owls = new ArrayList<Owl>();
    public static ArrayList<MacroObject> macroObjects = new ArrayList<MacroObject>();
    public static Label statusLabel;

    public static void addNewOwl(String name, String type, boolean hasWeapon,
                                 String weaponColor, String owlColor, double x, double y) {
        owls.add(new Owl(name, type, hasWeapon, weaponColor, owlColor, x, y));
        updateMacroObjectCounts();
    }

    public static void updateMacroObjectCounts() {
        for (MacroObject macro : macroObjects) {
            macro.updateOwlCount();
        }
    }

    public static void updateStatusLabel() {
        int activeCount = 0;
        StringBuilder activeNames = new StringBuilder();

        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeCount++;
                if (activeNames.length() > 0) {
                    activeNames.append(", ");
                }
                activeNames.append(owl.name);
            }
        }

        if (activeCount == 0) {
            statusLabel.setText("Активних сов: немає");
        } else {
            statusLabel.setText("Активних сов: " + activeCount + " (" + activeNames.toString() + ")");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro: Shadows Die Twice - Сови");

        group = new Group();

        // Створюємо фон
        Rectangle background = new Rectangle(1200, 800, Color.DARKSLATEBLUE);
        group.getChildren().add(background);

        // Створюємо макрооб'єкти
        macroObjects.add(new MacroObject("Замок Асіна", 50, 50));
        macroObjects.add(new MacroObject("Хіру-ден", 350, 50));
        macroObjects.add(new MacroObject("Верхній Баштовий Додзьо", 650, 50));

        // Створюємо початкових сов
        addNewOwl("Сова-Розвідник", "Сова", true, "Silver", "Brown", 100, 300);
        addNewOwl("Великий Страж", "Великий Сова", true, "Gold", "Black", 400, 300);
        addNewOwl("Спадкоємець Тіні", "Нащадок Сови", false, "Red", "White", 700, 300);

        // Створюємо статус лейбл
        statusLabel = new Label("Активних сов: немає");
        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(750);
        group.getChildren().add(statusLabel);

        scene = new Scene(group, 1200, 800);
        primaryStage.setScene(scene);

        // Обробка клавіатури
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                double delta = 10.0;
                if (event.isShiftDown()) delta *= 3.0;

                switch (event.getCode()) {
                    case INSERT:
                        try {
                            DialogNewOwl.display();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DELETE:
                        Iterator<Owl> iterator = owls.iterator();
                        while (iterator.hasNext()) {
                            Owl owl = iterator.next();
                            if (owl.isActive()) {
                                owl.removeFromScene();
                                iterator.remove();
                            }
                        }
                        updateMacroObjectCounts();
                        updateStatusLabel();
                        break;

                    case ESCAPE:
                        for (Owl owl : owls) {
                            owl.setActive(false);
                        }
                        updateStatusLabel();
                        break;

                    case UP:
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.move(0, -delta);
                            }
                        }
                        break;

                    case DOWN:
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.move(0, delta);
                            }
                        }
                        break;

                    case LEFT:
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.move(-delta, 0);
                            }
                        }
                        break;

                    case RIGHT:
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.move(delta, 0);
                            }
                        }
                        break;

                    case C:
                        if (event.isControlDown()) {
                            // Копіюємо активних сов
                            ArrayList<Owl> activeCopies = new ArrayList<>();
                            for (Owl owl : owls) {
                                if (owl.isActive()) {
                                    try {
                                        Owl copy = (Owl) owl.clone();
                                        copy.move(30, 30); // Зміщуємо копію
                                        activeCopies.add(copy);
                                    } catch (CloneNotSupportedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            owls.addAll(activeCopies);
                            updateMacroObjectCounts();
                        }
                        break;

                    case DIGIT1:
                        // Приєднати активних сов до першого макрооб'єкта
                        if (!macroObjects.isEmpty()) {
                            for (Owl owl : owls) {
                                if (owl.isActive()) {
                                    owl.belongsToMacro = macroObjects.get(0);
                                }
                            }
                            updateMacroObjectCounts();
                        }
                        break;

                    case DIGIT2:
                        // Приєднати активних сов до другого макрооб'єкта
                        if (macroObjects.size() > 1) {
                            for (Owl owl : owls) {
                                if (owl.isActive()) {
                                    owl.belongsToMacro = macroObjects.get(1);
                                }
                            }
                            updateMacroObjectCounts();
                        }
                        break;

                    case DIGIT3:
                        // Приєднати активних сов до третього макрооб'єкта
                        if (macroObjects.size() > 2) {
                            for (Owl owl : owls) {
                                if (owl.isActive()) {
                                    owl.belongsToMacro = macroObjects.get(2);
                                }
                            }
                            updateMacroObjectCounts();
                        }
                        break;

                    case DIGIT0:
                        // Відʼєднати активних сов від усіх макрооб'єктів
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.belongsToMacro = null;
                            }
                        }
                        updateMacroObjectCounts();
                        break;
                }
            }
        });

        // Обробка миші
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    // Ліва кнопка - активація/деактивація
                    for (Owl owl : owls) {
                        if (owl.contains(event.getX(), event.getY())) {
                            owl.toggleActive();
                            updateStatusLabel();
                            break;
                        }
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    // Права кнопка - редагування
                    for (Owl owl : owls) {
                        if (owl.contains(event.getX(), event.getY())) {
                            try {
                                DialogEditOwl.display(owl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        });

        primaryStage.show();
        updateStatusLabel();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
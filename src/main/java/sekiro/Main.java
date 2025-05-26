package sekiro;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
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
import java.util.Random;

// Клас мікрооб'єкта Сова
class Owl {
    String name;
    String type; // "Сова", "Великий Сова", "Нащадок Сови"
    Canvas canvas;
    boolean active;
    Rectangle rectActive;
    boolean hasSpecialAbility; // посилальний тип для глибинного копіювання
    String abilityName;
    MacroObject belongsTo; // до якого макрооб'єкта належить

    public Owl(String name, String type, boolean hasSpecialAbility, String abilityName, double x, double y) {
        this.name = name;
        this.type = type;
        this.hasSpecialAbility = hasSpecialAbility;
        this.abilityName = abilityName;
        this.belongsTo = null;

        canvas = new Canvas(150, 120);
        drawOwl(canvas, name, type, hasSpecialAbility, abilityName);
        Main.group.getChildren().add(canvas);

        canvas.setLayoutX(x);
        canvas.setLayoutY(y);

        active = false;
        rectActive = new Rectangle(154, 124);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStrokeWidth(3);
        rectActive.setStroke(Color.BLACK);
        Main.group.getChildren().add(rectActive);
        rectActive.setX(canvas.getLayoutX() - 2);
        rectActive.setY(canvas.getLayoutY() - 2);

        // Обробка натискання миші
        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleActive();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                try {
                    EditOwlDialog.display(this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void toggleActive() {
        active = !active;
        if (active) {
            rectActive.setStroke(Color.YELLOW);
        } else {
            rectActive.setStroke(Color.BLACK);
        }
        Main.updateStatusLabel();
    }

    public void move(double dx, double dy) {
        canvas.setLayoutX(canvas.getLayoutX() + dx);
        canvas.setLayoutY(canvas.getLayoutY() + dy);
        rectActive.setX(rectActive.getX() + dx);
        rectActive.setY(rectActive.getY() + dy);
    }

    public void removeFromScene() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);
    }

    public boolean isActive() {
        return active;
    }

    public void setBelongsTo(MacroObject macro) {
        this.belongsTo = macro;
        redraw();
    }

    private void redraw() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawOwl(canvas, name, type, hasSpecialAbility, abilityName);
    }

    public Owl clone() {
        Owl cloned = new Owl(name + "_copy", type, hasSpecialAbility,
                new String(abilityName), // глибинне копіювання
                canvas.getLayoutX() + 20, canvas.getLayoutY() + 20);
        return cloned;
    }

    public static void drawOwl(Canvas canvas, String name, String type, boolean hasSpecialAbility, String abilityName) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Тіло сови - овал
        gc.setFill(Color.BROWN);
        gc.fillOval(35, 40, 80, 60);

        // Голова сови - коло
        gc.setFill(Color.SADDLEBROWN);
        gc.fillOval(45, 20, 60, 50);

        // Очі
        gc.setFill(Color.YELLOW);
        gc.fillOval(55, 30, 15, 15);
        gc.fillOval(80, 30, 15, 15);
        gc.setFill(Color.BLACK);
        gc.fillOval(60, 35, 5, 5);
        gc.fillOval(85, 35, 5, 5);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] xPoints = {75, 70, 80};
        double[] yPoints = {45, 55, 55};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Крила
        gc.setFill(Color.DARKGOLDENROD);
        gc.fillOval(20, 50, 30, 40);
        gc.fillOval(100, 50, 30, 40);

        // Показник спеціальної здібності
        if (hasSpecialAbility) {
            gc.setFill(Color.RED);
            gc.fillOval(5, 5, 15, 15);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(10));
            gc.fillText("!", 10, 15);
        }

        // Текст назви
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(12));
        gc.fillText(name, 10, 115);

        // Тип сови
        gc.setFont(new Font(10));
        gc.fillText(type, 10, 105);
    }
}

// Клас макрооб'єкта
class MacroObject {
    String name;
    String type;
    Canvas canvas;
    ArrayList<Owl> containedOwls;

    public MacroObject(String name, String type, double x, double y) {
        this.name = name;
        this.type = type;
        this.containedOwls = new ArrayList<>();

        canvas = new Canvas(200, 150);
        drawMacroObject(canvas, name, type, 0);
        Main.group.getChildren().add(canvas);

        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    public void addOwl(Owl owl) {
        if (!containedOwls.contains(owl)) {
            containedOwls.add(owl);
            owl.setBelongsTo(this);
            updateDisplay();
        }
    }

    public void removeOwl(Owl owl) {
        if (containedOwls.contains(owl)) {
            containedOwls.remove(owl);
            owl.setBelongsTo(null);
            updateDisplay();
        }
    }

    public void removeAllOwls() {
        for (Owl owl : new ArrayList<>(containedOwls)) {
            removeOwl(owl);
        }
    }

    private void updateDisplay() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawMacroObject(canvas, name, type, containedOwls.size());
    }

    public boolean contains(Point2D point) {
        return canvas.getBoundsInParent().contains(point);
    }

    public static void drawMacroObject(Canvas canvas, String name, String type, int owlCount) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Основна структура залежно від типу
        switch (type) {
            case "Замок Асіна":
                // Замок - прямокутник з вежами
                gc.setFill(Color.GRAY);
                gc.fillRect(20, 40, 160, 80);
                gc.fillRect(10, 20, 30, 40);
                gc.fillRect(160, 20, 30, 40);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(20, 40, 160, 80);
                gc.strokeRect(10, 20, 30, 40);
                gc.strokeRect(160, 20, 30, 40);
                break;

            case "Хіру-ден":
                // Традиційний японський будинок
                gc.setFill(Color.BURLYWOOD);
                gc.fillRect(30, 60, 140, 60);
                // Дах
                double[] roofX = {20, 100, 180};
                double[] roofY = {60, 30, 60};
                gc.setFill(Color.DARKRED);
                gc.fillPolygon(roofX, roofY, 3);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(30, 60, 140, 60);
                gc.strokePolygon(roofX, roofY, 3);
                break;

            case "Верхній Баштовий Додзьо":
                // Кругла вежа
                gc.setFill(Color.LIGHTGRAY);
                gc.fillOval(50, 20, 100, 100);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(50, 20, 100, 100);
                // Вікна
                gc.setFill(Color.DARKBLUE);
                gc.fillRect(70, 40, 15, 20);
                gc.fillRect(115, 40, 15, 20);
                gc.fillRect(70, 80, 15, 20);
                gc.fillRect(115, 80, 15, 20);
                break;
        }

        // Назва
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));
        gc.fillText(name, 10, 140);

        // Кількість сов
        gc.setFont(new Font(12));
        gc.fillText("Сов: " + owlCount, 120, 140);
    }
}

public class Main extends Application {
    public static Stage primaryStage;
    public static Scene scene;
    public static Random rnd = new Random();
    public static Group group;
    public static Label statusLabel;

    public static ArrayList<Owl> owls = new ArrayList<>();
    public static ArrayList<MacroObject> macroObjects = new ArrayList<>();

    public static void addNewOwl(String name, String type, boolean hasSpecialAbility, String abilityName) {
        owls.add(new Owl(name, type, hasSpecialAbility, abilityName,
                (double)rnd.nextInt(650), (double)rnd.nextInt(400)));
        updateStatusLabel();
    }

    public static void updateStatusLabel() {
        int activeCount = 0;
        StringBuilder activeNames = new StringBuilder();

        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeCount++;
                if (activeNames.length() > 0) activeNames.append(", ");
                activeNames.append(owl.name);
            }
        }

        if (activeCount == 0) {
            statusLabel.setText("Активних сов: 0");
        } else {
            statusLabel.setText("Активних сов: " + activeCount + " (" + activeNames.toString() + ")");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro - Лабораторна робота №4");

        group = new Group();
        Rectangle background = new Rectangle(800, 600, Color.LIGHTBLUE);
        group.getChildren().add(background);

        // Створення макрооб'єктів
        macroObjects.add(new MacroObject("Замок Асіна", "Замок Асіна", 50, 50));
        macroObjects.add(new MacroObject("Хіру-ден", "Хіру-ден", 300, 50));
        macroObjects.add(new MacroObject("Верхній Баштовий Додзьо", "Верхній Баштовий Додзьо", 550, 50));

        // Створення початкових сов
        addNewOwl("Головна Сова", "Сова", true, "Невидимість");
        addNewOwl("Великий Страж", "Великий Сова", true, "Сила");
        addNewOwl("Молода Сова", "Нащадок Сови", false, "Немає");

        // Статус лейбл
        statusLabel = new Label("Активних сов: 0");
        statusLabel.setFont(new Font(16));
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(570);
        statusLabel.setTextFill(Color.BLACK);
        group.getChildren().add(statusLabel);

        scene = new Scene(group, 800, 600);
        primaryStage.setScene(scene);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                double delta = 10.0;
                if (event.isShiftDown()) delta *= 2.0;

                switch (event.getCode()) {
                    case INSERT:
                        try {
                            CreateOwlDialog.display();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DELETE:
                        for (int i = owls.size() - 1; i >= 0; i--) {
                            Owl owl = owls.get(i);
                            if (owl.isActive()) {
                                // Видалити з макрооб'єкта якщо належить
                                if (owl.belongsTo != null) {
                                    owl.belongsTo.removeOwl(owl);
                                }
                                owl.removeFromScene();
                                owls.remove(i);
                            }
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

                    case ESCAPE:
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                owl.toggleActive();
                            }
                        }
                        break;

                    case C:
                        if (event.isControlDown()) {
                            for (Owl owl : owls) {
                                if (owl.isActive()) {
                                    Owl cloned = owl.clone();
                                    owls.add(cloned);
                                    break; // копіюємо тільки першу активну
                                }
                            }
                            updateStatusLabel();
                        }
                        break;

                    case M:
                        // Додати активну сову до макрооб'єкта
                        assignOwlToMacro();
                        break;

                    case R:
                        // Видалити сову з усіх макрооб'єктів
                        removeOwlFromAllMacros();
                        break;
                }
            }
        });

        // Обробка натискання миші для макрооб'єктів
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Point2D clickPoint = new Point2D(event.getX(), event.getY());

                // Перевірка чи клікнули на макрооб'єкт
                for (MacroObject macro : macroObjects) {
                    if (macro.contains(clickPoint)) {
                        // Додати всіх активних сов до цього макрооб'єкта
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                macro.addOwl(owl);
                            }
                        }
                        break;
                    }
                }
            }
        });

        updateStatusLabel();
        primaryStage.show();
    }

    private void assignOwlToMacro() {
        for (Owl owl : owls) {
            if (owl.isActive() && macroObjects.size() > 0) {
                // Додати до першого макрооб'єкта
                macroObjects.get(0).addOwl(owl);
                break;
            }
        }
    }

    private void removeOwlFromAllMacros() {
        for (Owl owl : owls) {
            if (owl.isActive()) {
                for (MacroObject macro : macroObjects) {
                    macro.removeOwl(owl);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
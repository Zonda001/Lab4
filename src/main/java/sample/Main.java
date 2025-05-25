package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
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

class Owl {
    String name;
    String type; // "Сова", "Великий Сова", "Нащадок Сови"
    Canvas canvas;
    boolean active;
    Rectangle rectActive;
    String color;
    int size;
    boolean hasFeather; // додатковий елемент посилального типу для клонування
    String featherColor;
    Rectangle featherRect;
    String belongsToMacro; // до якого макрооб'єкта належить

    public Owl(String name, String type, String color, int size, boolean hasFeather,
               String featherColor, double x, double y) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.size = size;
        this.hasFeather = hasFeather;
        this.featherColor = featherColor;
        this.belongsToMacro = "";

        // Створюємо канвас для малювання сови
        int canvasSize = getCanvasSize();
        canvas = new Canvas(canvasSize, canvasSize);
        drawOwl();
        Main.group.getChildren().add(canvas);

        // Створюємо прямокутник для пера (якщо є)
        if (hasFeather) {
            featherRect = new Rectangle(20, 5);
            featherRect.setFill(Color.valueOf(featherColor));
            Main.group.getChildren().add(featherRect);
            featherRect.setX(x + canvasSize + 10);
            featherRect.setY(y + 20);
        }

        // Створюємо прямокутник для позначення активності
        active = false;
        rectActive = new Rectangle(canvasSize + 4, canvasSize + 4);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStrokeWidth(3);
        rectActive.setStroke(Color.BLACK);
        Main.group.getChildren().add(rectActive);

        // Встановлюємо позицію
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        rectActive.setX(x - 2);
        rectActive.setY(y - 2);

        // Додаємо обробник кліків миші
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    toggleActive();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    try {
                        DialogEditOwl.display(Owl.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private int getCanvasSize() {
        switch (type) {
            case "Великий Сова": return 120;
            case "Нащадок Сови": return 60;
            default: return 80; // Звичайна Сова
        }
    }

    private void drawOwl() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double bodyRadius = size * 0.3;

        // Тіло сови (овал)
        gc.setFill(Color.valueOf(color));
        gc.fillOval(centerX - bodyRadius, centerY - bodyRadius, bodyRadius * 2, bodyRadius * 1.5);

        // Голова (коло)
        double headRadius = bodyRadius * 0.8;
        gc.fillOval(centerX - headRadius, centerY - bodyRadius * 1.5, headRadius * 2, headRadius * 2);

        // Очі
        gc.setFill(Color.YELLOW);
        gc.fillOval(centerX - headRadius * 0.5, centerY - bodyRadius * 1.2, headRadius * 0.4, headRadius * 0.4);
        gc.fillOval(centerX + headRadius * 0.1, centerY - bodyRadius * 1.2, headRadius * 0.4, headRadius * 0.4);

        // Зіниці
        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - headRadius * 0.35, centerY - bodyRadius * 1.05, headRadius * 0.1, headRadius * 0.1);
        gc.fillOval(centerX + headRadius * 0.25, centerY - bodyRadius * 1.05, headRadius * 0.1, headRadius * 0.1);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] xPoints = {centerX, centerX - 5, centerX + 5};
        double[] yPoints = {centerY - bodyRadius * 0.8, centerY - bodyRadius * 0.6, centerY - bodyRadius * 0.6};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Крила
        gc.setFill(Color.valueOf(color).darker());
        gc.fillOval(centerX - bodyRadius * 1.2, centerY - bodyRadius * 0.5, bodyRadius * 0.8, bodyRadius);
        gc.fillOval(centerX + bodyRadius * 0.4, centerY - bodyRadius * 0.5, bodyRadius * 0.8, bodyRadius);

        // Текст з назвою
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(10));
        gc.fillText(name, 5, canvas.getHeight() - 15);
        gc.fillText(type, 5, canvas.getHeight() - 5);
    }

    public void toggleActive() {
        active = !active;
        if (active) {
            rectActive.setStroke(Color.YELLOW);
            rectActive.setStrokeWidth(5);
        } else {
            rectActive.setStroke(Color.BLACK);
            rectActive.setStrokeWidth(3);
        }
        Main.updateStatusInfo();
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            rectActive.setStroke(Color.YELLOW);
            rectActive.setStrokeWidth(5);
        } else {
            rectActive.setStroke(Color.BLACK);
            rectActive.setStrokeWidth(3);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void move(double dx, double dy) {
        if (!active) return;

        canvas.setLayoutX(canvas.getLayoutX() + dx);
        canvas.setLayoutY(canvas.getLayoutY() + dy);
        rectActive.setX(rectActive.getX() + dx);
        rectActive.setY(rectActive.getY() + dy);

        if (hasFeather && featherRect != null) {
            featherRect.setX(featherRect.getX() + dx);
            featherRect.setY(featherRect.getY() + dy);
        }
    }

    public void removeFromScreen() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);
        if (hasFeather && featherRect != null) {
            Main.group.getChildren().remove(featherRect);
        }
    }

    public Owl clone() {
        // Глибинне копіювання через створення нового об'єкта
        return new Owl(name + "_copy", type, color, size, hasFeather,
                featherColor, canvas.getLayoutX() + 20, canvas.getLayoutY() + 20);
    }

    public void setBelongsToMacro(String macroName) {
        this.belongsToMacro = macroName;
        // Змінюємо поведінку в залежності від приналежності
        if (!macroName.isEmpty()) {
            rectActive.setStroke(Color.BLUE);
        }
    }

    public String getBelongsToMacro() {
        return belongsToMacro;
    }

    public void updateParameters(String newName, String newColor, int newSize,
                                 boolean newHasFeather, String newFeatherColor) {
        this.name = newName;
        this.color = newColor;
        this.size = newSize;
        this.hasFeather = newHasFeather;
        this.featherColor = newFeatherColor;

        // Перемальовуємо сову
        drawOwl();

        // Оновлюємо перо
        if (hasFeather) {
            if (featherRect == null) {
                featherRect = new Rectangle(20, 5);
                Main.group.getChildren().add(featherRect);
                featherRect.setX(canvas.getLayoutX() + getCanvasSize() + 10);
                featherRect.setY(canvas.getLayoutY() + 20);
            }
            featherRect.setFill(Color.valueOf(featherColor));
        } else if (featherRect != null) {
            Main.group.getChildren().remove(featherRect);
            featherRect = null;
        }
    }
}

class MacroObject {
    String name;
    Canvas canvas;
    ArrayList<Owl> containedOwls;
    double x, y;

    public MacroObject(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.containedOwls = new ArrayList<>();

        canvas = new Canvas(200, 150);
        drawMacroObject();
        Main.group.getChildren().add(canvas);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    private void drawMacroObject() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Малюємо залежно від типу макрооб'єкта
        switch (name) {
            case "Замок Асіна":
                drawCastle(gc);
                break;
            case "Хіру-ден":
                drawTemple(gc);
                break;
            case "Верхній Баштовий Додзьо":
                drawDojo(gc);
                break;
        }

        // Додаємо текст з назвою
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(12));
        gc.fillText(name, 10, 20);
        gc.fillText("Сов: " + containedOwls.size(), 10, 140);
    }

    private void drawCastle(GraphicsContext gc) {
        // Основа замку (прямокутник)
        gc.setFill(Color.GRAY);
        gc.fillRect(20, 50, 160, 80);

        // Вежі (кола)
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(10, 30, 40, 40);
        gc.fillOval(150, 30, 40, 40);
        gc.fillOval(75, 20, 50, 50);

        // Ворота (арка)
        gc.setFill(Color.BLACK);
        gc.fillRect(85, 90, 30, 40);
        gc.fillOval(85, 85, 30, 20);
    }

    private void drawTemple(GraphicsContext gc) {
        // Основа храму (прямокутник)
        gc.setFill(Color.BROWN);
        gc.fillRect(30, 60, 140, 70);

        // Дах (трикутник)
        gc.setFill(Color.RED);
        double[] xPoints = {20, 100, 180};
        double[] yPoints = {60, 30, 60};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Колони (прямокутники)
        gc.setFill(Color.BEIGE);
        gc.fillRect(40, 60, 10, 70);
        gc.fillRect(95, 60, 10, 70);
        gc.fillRect(150, 60, 10, 70);
    }

    private void drawDojo(GraphicsContext gc) {
        // Основа додзьо (прямокутник)
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(25, 70, 150, 60);

        // Багатоярусний дах
        gc.setFill(Color.DARKRED);
        gc.fillRect(15, 50, 170, 20);
        gc.fillRect(20, 30, 160, 20);

        // Вікна (кола)
        gc.setFill(Color.YELLOW);
        gc.fillOval(50, 85, 15, 15);
        gc.fillOval(100, 85, 15, 15);
        gc.fillOval(135, 85, 15, 15);

        // Вхід
        gc.setFill(Color.BLACK);
        gc.fillRect(85, 105, 30, 25);
    }

    public void addOwl(Owl owl) {
        containedOwls.add(owl);
        owl.setBelongsToMacro(name);
        drawMacroObject(); // Оновлюємо відображення кількості
    }

    public void removeOwl(Owl owl) {
        containedOwls.remove(owl);
        owl.setBelongsToMacro("");
        drawMacroObject(); // Оновлюємо відображення кількості
    }

    public boolean containsOwl(Owl owl) {
        return containedOwls.contains(owl);
    }

    public int getOwlCount() {
        return containedOwls.size();
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

    public static void addNewOwl(String name, String type, String color, int size,
                                 boolean hasFeather, String featherColor) {
        owls.add(new Owl(name, type, color, size, hasFeather, featherColor,
                (double)rnd.nextInt(600), (double)rnd.nextInt(400)));
        updateStatusInfo();
    }

    public static void updateStatusInfo() {
        int activeCount = 0;
        StringBuilder activeNames = new StringBuilder();

        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeCount++;
                if (activeNames.length() > 0) activeNames.append(", ");
                activeNames.append(owl.name);
            }
        }

        String statusText;
        if (activeCount == 0) {
            statusText = "Активних сов немає";
        } else if (activeCount == 1) {
            statusText = "Активна сова: " + activeNames.toString();
        } else {
            statusText = "Активних сов: " + activeCount + " (" + activeNames.toString() + ")";
        }

        statusLabel.setText(statusText);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Лабораторна робота №4 - Сови та Додзьо");

        group = new Group();
        Rectangle background = new Rectangle(1000, 700, Color.LIGHTBLUE);
        group.getChildren().add(background);

        // Створюємо макрооб'єкти
        macroObjects.add(new MacroObject("Замок Асіна", 50, 500));
        macroObjects.add(new MacroObject("Хіру-ден", 350, 500));
        macroObjects.add(new MacroObject("Верхній Баштовий Додзьо", 650, 500));

        // Створюємо кілька сов для початку
        owls.add(new Owl("Мудра Сова", "Сова", "BROWN", 80, true, "GOLD", 100, 100));
        owls.add(new Owl("Великий Страж", "Великий Сова", "DARKBROWN", 120, true, "SILVER", 300, 150));
        owls.add(new Owl("Малеча", "Нащадок Сови", "LIGHTBROWN", 60, false, "", 500, 200));

        // Створюємо статус-лейбл
        statusLabel = new Label("Активних сов немає");
        statusLabel.setFont(new Font(14));
        statusLabel.setTextFill(Color.BLACK);
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(10);
        group.getChildren().add(statusLabel);

        scene = new Scene(group, 1000, 700);
        primaryStage.setScene(scene);

        // Обробка клавіатури
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handleKeyPress(event);
            }
        });

        primaryStage.show();
        updateStatusInfo();
    }

    private void handleKeyPress(KeyEvent event) {
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
                for (int i = owls.size() - 1; i >= 0; --i) {
                    Owl owl = owls.get(i);
                    if (owl.isActive()) {
                        owl.removeFromScreen();
                        // Видаляємо з макрооб'єктів
                        for (MacroObject macro : macroObjects) {
                            macro.removeOwl(owl);
                        }
                        owls.remove(i);
                    }
                }
                updateStatusInfo();
                break;

            case ESCAPE:
                for (Owl owl : owls) {
                    owl.setActive(false);
                }
                updateStatusInfo();
                break;

            case UP:
                for (Owl owl : owls) {
                    owl.move(0, -delta);
                }
                break;

            case DOWN:
                for (Owl owl : owls) {
                    owl.move(0, delta);
                }
                break;

            case LEFT:
                for (Owl owl : owls) {
                    owl.move(-delta, 0);
                }
                break;

            case RIGHT:
                for (Owl owl : owls) {
                    owl.move(delta, 0);
                }
                break;

            case C:
                if (event.isControlDown()) {
                    // Копіювання активних сов
                    ArrayList<Owl> newOwls = new ArrayList<>();
                    for (Owl owl : owls) {
                        if (owl.isActive()) {
                            newOwls.add(owl.clone());
                        }
                    }
                    owls.addAll(newOwls);
                    updateStatusInfo();
                }
                break;

            case DIGIT1:
                // Додати активну сову до Замку Асіна
                assignOwlsToMacro("Замок Асіна");
                break;

            case DIGIT2:
                // Додати активну сову до Хіру-ден
                assignOwlsToMacro("Хіру-ден");
                break;

            case DIGIT3:
                // Додати активну сову до Верхнього Баштового Додзьо
                assignOwlsToMacro("Верхній Баштовий Додзьо");
                break;

            case DIGIT0:
                // Видалити сову з усіх макрооб'єктів
                removeOwlsFromAllMacros();
                break;
        }
    }

    private void assignOwlsToMacro(String macroName) {
        MacroObject targetMacro = null;
        for (MacroObject macro : macroObjects) {
            if (macro.name.equals(macroName)) {
                targetMacro = macro;
                break;
            }
        }

        if (targetMacro != null) {
            for (Owl owl : owls) {
                if (owl.isActive()) {
                    // Видаляємо з інших макрооб'єктів
                    for (MacroObject macro : macroObjects) {
                        macro.removeOwl(owl);
                    }
                    // Додаємо до цільового
                    targetMacro.addOwl(owl);
                }
            }
        }
    }

    private void removeOwlsFromAllMacros() {
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
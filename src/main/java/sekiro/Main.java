package sekiro;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

// Клас мікрооб'єкта - Сова
class Owl {
    String name;
    String type; // "Сова", "Великий Сова", "Нащадок Сови"
    Canvas canvas;
    boolean active;
    Rectangle rectActive;
    boolean hasSpecialFeature; // елемент посилального типу для глибинного копіювання
    String featureDescription;

    // Координати
    double x, y;

    // Макрооб'єкт, якому належить сова (може бути null)
    Castle belongsToCastle;

    // Спрайти
    static Image owlSprite;
    static Image bigOwlSprite;
    static Image descendantOwlSprite;

    static {
        // Завантаження спрайтів (для демонстрації використовуємо заглушки)
        try {
            // В реальному проекті тут були б справжні шляхи до спрайтів
            owlSprite = new Image("file:owl.png", 60, 60, true, true);
            bigOwlSprite = new Image("file:big_owl.png", 80, 80, true, true);
            descendantOwlSprite = new Image("file:descendant_owl.png", 50, 50, true, true);
        } catch (Exception e) {
            // Якщо спрайти не завантажились, будемо малювати примітивами
            System.out.println("Спрайти не завантажені, використовуємо примітиви");
        }
    }

    public Owl(String name, String type, double x, double y, boolean hasSpecialFeature, String featureDescription) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.hasSpecialFeature = hasSpecialFeature;
        this.featureDescription = featureDescription;
        this.active = false;
        this.belongsToCastle = null;

        canvas = new Canvas(100, 100);
        drawOwl();

        Main.group.getChildren().add(canvas);

        // Активний прямокутник
        rectActive = new Rectangle(104, 104);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStrokeWidth(3);
        rectActive.setStroke(Color.BLACK);
        Main.group.getChildren().add(rectActive);

        updatePosition();
    }

    private void drawOwl() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 100, 100);

        // Спробуємо використати спрайт, якщо не вийде - малюємо примітивами
        boolean spriteDrawn = false;

        try {
            Image sprite = null;
            switch (type) {
                case "Сова":
                    sprite = owlSprite;
                    break;
                case "Великий Сова":
                    sprite = bigOwlSprite;
                    break;
                case "Нащадок Сови":
                    sprite = descendantOwlSprite;
                    break;
            }

            if (sprite != null && !sprite.isError()) {
                gc.drawImage(sprite, 20, 10);
                spriteDrawn = true;
            }
        } catch (Exception e) {
            spriteDrawn = false;
        }

        // Якщо спрайт не вдалося намалювати, використовуємо примітиви
        if (!spriteDrawn) {
            drawOwlWithPrimitives(gc);
        }

        // Обов'язковий текст
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(10));
        gc.fillText(name, 5, 95);

        // Показуємо елемент посилального типу
        if (hasSpecialFeature) {
            gc.setFill(Color.GOLD);
            gc.fillRect(75, 5, 20, 10);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(8));
            gc.fillText("Ф", 82, 13);
        }

        // Показуємо приналежність до замку
        if (belongsToCastle != null) {
            gc.setStroke(belongsToCastle.getColor());
            gc.setLineWidth(2);
            gc.strokeRect(2, 2, 96, 96);
        }
    }

    private void drawOwlWithPrimitives(GraphicsContext gc) {
        // Тіло сови - еліпс
        gc.setFill(getOwlColor());
        gc.fillOval(25, 30, 50, 40);

        // Голова сови - коло
        gc.fillOval(35, 15, 30, 30);

        // Очі
        gc.setFill(Color.WHITE);
        gc.fillOval(40, 20, 8, 8);
        gc.fillOval(52, 20, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(42, 22, 4, 4);
        gc.fillOval(54, 22, 4, 4);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] xPoints = {50, 45, 55};
        double[] yPoints = {35, 40, 40};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Крила
        gc.setFill(getOwlColor().darker());
        gc.fillOval(15, 35, 20, 25);
        gc.fillOval(65, 35, 20, 25);

        // Розмір залежить від типу
        if (type.equals("Великий Сова")) {
            gc.scale(1.2, 1.2);
        } else if (type.equals("Нащадок Сови")) {
            gc.scale(0.8, 0.8);
        }
    }

    private Color getOwlColor() {
        switch (type) {
            case "Великий Сова": return Color.DARKGRAY;
            case "Нащадок Сови": return Color.LIGHTGRAY;
            default: return Color.BROWN;
        }
    }

    public void updatePosition() {
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        rectActive.setX(x - 2);
        rectActive.setY(y - 2);
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            rectActive.setStroke(Color.YELLOW);
        } else {
            rectActive.setStroke(Color.BLACK);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;

        // Обмеження по краях екрану
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > Main.SCENE_WIDTH - 100) x = Main.SCENE_WIDTH - 100;
        if (y > Main.SCENE_HEIGHT - 100) y = Main.SCENE_HEIGHT - 100;

        updatePosition();

        // Перевіряємо чи сова потрапила в замок
        checkCastleMembership();
    }

    private void checkCastleMembership() {
        Castle newCastle = null;
        for (Castle castle : Main.castles) {
            if (castle.containsPoint(x + 50, y + 50)) {
                newCastle = castle;
                break;
            }
        }

        if (newCastle != belongsToCastle) {
            if (belongsToCastle != null) {
                belongsToCastle.removeOwl(this);
            }

            belongsToCastle = newCastle;

            if (belongsToCastle != null) {
                belongsToCastle.addOwl(this);
            }

            drawOwl(); // Перемалювати з новою приналежністю
        }
    }

    public boolean containsPoint(double px, double py) {
        return px >= x && px <= x + 100 && py >= y && py <= y + 100;
    }

    public void removeFromScene() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);

        if (belongsToCastle != null) {
            belongsToCastle.removeOwl(this);
        }
    }

    // Глибинне копіювання
    public Owl deepCopy() {
        String newFeatureDescription = hasSpecialFeature ? new String(featureDescription) : null;
        Owl copy = new Owl(name + "_copy", type, x + 20, y + 20, hasSpecialFeature, newFeatureDescription);
        return copy;
    }

    public String getInfo() {
        String castleInfo = belongsToCastle != null ? belongsToCastle.getName() : "Немає";
        return String.format("%s (%s) - Замок: %s", name, type, castleInfo);
    }

    public void setBelongsToCastle(Castle castle) {
        if (belongsToCastle != null) {
            belongsToCastle.removeOwl(this);
        }
        belongsToCastle = castle;
        if (castle != null) {
            castle.addOwl(this);
        }
        drawOwl();
    }

    public Castle getBelongsToCastle() {
        return belongsToCastle;
    }
}

// Клас макрооб'єкта - Замок
class Castle {
    String name;
    double x, y, width, height;
    Color color;
    ArrayList<Owl> owls;
    Canvas canvas;
    Label countLabel;

    public Castle(String name, double x, double y, double width, double height, Color color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.owls = new ArrayList<>();

        canvas = new Canvas(width, height);
        drawCastle();

        countLabel = new Label("Сови: 0");
        countLabel.setFont(new Font(12));
        countLabel.setTextFill(Color.WHITE);

        Main.group.getChildren().add(canvas);
        Main.group.getChildren().add(countLabel);

        updatePosition();
    }

    private void drawCastle() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Основа замку - прямокутник
        gc.setFill(color);
        gc.fillRect(10, height - 60, width - 20, 60);

        // Башти - кола
        gc.fillOval(5, height - 80, 30, 30);
        gc.fillOval(width - 35, height - 80, 30, 30);
        gc.fillOval(width/2 - 15, height - 90, 30, 30);

        // Ворота - арка
        gc.setFill(Color.BLACK);
        gc.fillRect(width/2 - 10, height - 40, 20, 40);
        gc.fillOval(width/2 - 10, height - 50, 20, 20);

        // Назва замку - текст
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(14));
        gc.fillText(name, 10, 20);

        // Декоративні елементи
        gc.setStroke(color.darker());
        gc.setLineWidth(2);
        gc.strokeRect(10, height - 60, width - 20, 60);
    }

    private void updatePosition() {
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        countLabel.setLayoutX(x + 10);
        countLabel.setLayoutY(y + height - 15);
    }

    public void addOwl(Owl owl) {
        if (!owls.contains(owl)) {
            owls.add(owl);
            updateCountLabel();
        }
    }

    public void removeOwl(Owl owl) {
        owls.remove(owl);
        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText("Сови: " + owls.size());
    }

    public boolean containsPoint(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getOwlCount() {
        return owls.size();
    }
}

public class Main extends Application {
    public static final int SCENE_WIDTH = 1200;
    public static final int SCENE_HEIGHT = 800;

    public static Stage primaryStage;
    public static Scene scene;
    public static Group group;
    public static Random rnd = new Random();

    public static ArrayList<Owl> owls = new ArrayList<>();
    public static ArrayList<Castle> castles = new ArrayList<>();

    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sekiro: Світ Сов та Замків");

        group = new Group();

        // Фон
        Rectangle background = new Rectangle(SCENE_WIDTH, SCENE_HEIGHT, Color.DARKGREEN.darker());
        group.getChildren().add(background);

        // Створюємо замки
        createCastles();

        // Створюємо початкових сов
        createInitialOwls();

        // Статус рядок
        statusLabel = new Label("Натисніть Insert для створення нової сови");
        statusLabel.setFont(new Font(14));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(SCENE_HEIGHT - 30);
        group.getChildren().add(statusLabel);

        scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);

        // Обробка подій
        scene.setOnKeyPressed(new KeyPressedHandler());
        scene.setOnMouseClicked(new MouseClickHandler());

        primaryStage.setScene(scene);
        primaryStage.show();

        // Фокус для обробки клавіатури
        scene.getRoot().requestFocus();
    }

    private void createCastles() {
        castles.add(new Castle("Замок Асіна", 100, 100, 200, 150, Color.DARKRED));
        castles.add(new Castle("Хіру-ден", 400, 200, 180, 130, Color.DARKBLUE));
        castles.add(new Castle("Верхній Баштовий Додзьо", 700, 150, 220, 160, Color.DARKMAGENTA));
    }

    private void createInitialOwls() {
        owls.add(new Owl("Мудра Сова", "Сова", 50, 300, true, "Мудрість предків"));
        owls.add(new Owl("Лорд Сова", "Великий Сова", 350, 400, true, "Сила лідера"));
        owls.add(new Owl("Молодий Спадкоємець", "Нащадок Сови", 650, 350, false, ""));
    }

    private void updateStatusLabel() {
        ArrayList<Owl> activeOwls = new ArrayList<>();
        for (Owl owl : owls) {
            if (owl.isActive()) {
                activeOwls.add(owl);
            }
        }

        if (activeOwls.isEmpty()) {
            statusLabel.setText("Немає активних сов. Натисніть Insert для створення нової сови");
        } else if (activeOwls.size() == 1) {
            statusLabel.setText("Активна сова: " + activeOwls.get(0).getInfo());
        } else {
            statusLabel.setText("Активних сов: " + activeOwls.size());
        }
    }

    private class KeyPressedHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            double delta = 10.0;
            if (event.isShiftDown()) delta *= 2.0;

            switch (event.getCode()) {
                case INSERT:
                    try {
                        OwlCreationDialog.display();
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
                        for (Owl owl : owls) {
                            if (owl.isActive()) {
                                Owl copy = owl.deepCopy();
                                owls.add(copy);
                                break; // Копіюємо тільки першу активну сову
                            }
                        }
                    }
                    break;
            }
            updateStatusLabel();
        }
    }

    private class MouseClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                // Ліва кнопка - активація/деактивація
                for (Owl owl : owls) {
                    if (owl.containsPoint(event.getX(), event.getY())) {
                        owl.setActive(!owl.isActive());
                        break;
                    }
                }
                updateStatusLabel();
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Права кнопка - редагування
                for (Owl owl : owls) {
                    if (owl.containsPoint(event.getX(), event.getY())) {
                        try {
                            OwlEditDialog.display(owl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
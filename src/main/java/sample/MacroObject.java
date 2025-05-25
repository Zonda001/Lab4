package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

// Базовий клас для макрооб'єктів
abstract class MacroObject {
    protected String name;
    protected double x, y, width, height;
    protected Canvas canvas;
    protected List<Owl> owls;

    public MacroObject(String name, double x, double y, double width, double height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.owls = new ArrayList<>();

        createCanvas();
        draw();
    }

    protected void createCanvas() {
        canvas = new Canvas(width, height);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        Main.group.getChildren().add(canvas);
    }

    protected abstract void draw();

    public void addOwl(Owl owl) {
        if (!owls.contains(owl)) {
            owls.add(owl);
            owl.setMacroObject(this);
            updateDisplay();
        }
    }

    public void removeOwl(Owl owl) {
        if (owls.contains(owl)) {
            owls.remove(owl);
            owl.setMacroObject(null);
            updateDisplay();
        }
    }

    public boolean containsOwl(Owl owl) {
        return owls.contains(owl);
    }

    public int getOwlCount() {
        return owls.size();
    }

    public List<Owl> getOwls() {
        return new ArrayList<>(owls);
    }

    public void updateDisplay() {
        draw();
    }

    protected void drawOwlCount(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Сов: " + owls.size(), 10, height - 10);
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public String getName() {
        return name;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void removeFromScene() {
        Main.group.getChildren().remove(canvas);
    }
}

// Замок Асіна
class AshigaCastle extends MacroObject {
    public AshigaCastle(double x, double y) {
        super("Замок Асіна", x, y, 200, 150);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Основа замку - прямокутник
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(20, 80, 160, 60);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(20, 80, 160, 60);

        // Башта замку - трикутник
        double[] xPoints = {100, 50, 150};
        double[] yPoints = {20, 80, 80};
        gc.setFill(Color.GRAY);
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Вхід - арка
        gc.setFill(Color.BLACK);
        gc.fillOval(85, 100, 30, 30);

        // Назва замку
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 14));
        gc.fillText(name, 60, 50);

        // Кількість сов
        drawOwlCount(gc);
    }
}

// Хіру-ден
class Hirudoen extends MacroObject {
    public Hirudoen(double x, double y) {
        super("Хіру-ден", x, y, 180, 120);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Основна будівля - прямокутник
        gc.setFill(Color.BROWN);
        gc.fillRect(30, 60, 120, 50);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(30, 60, 120, 50);

        // Дах - коло (частково)
        gc.setFill(Color.DARKRED);
        gc.fillOval(20, 30, 140, 40);
        gc.strokeOval(20, 30, 140, 40);

        // Стовпи
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(40, 80, 10, 30);
        gc.fillRect(130, 80, 10, 30);
        gc.strokeRect(40, 80, 10, 30);
        gc.strokeRect(130, 80, 10, 30);

        // Назва
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(name, 65, 25);

        // Кількість сов
        drawOwlCount(gc);
    }
}

// Верхній Баштовий Додзьо
class UpperTowerDojo extends MacroObject {
    public UpperTowerDojo(double x, double y) {
        super("Верхній Баштовий Додзьо", x, y, 160, 180);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Башта - прямокутник
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(50, 40, 60, 120);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(50, 40, 60, 120);

        // Верх башти - трикутник
        double[] xPoints = {80, 40, 120};
        double[] yPoints = {20, 40, 40};
        gc.setFill(Color.DARKGRAY);
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Вікна башти
        gc.setFill(Color.YELLOW);
        gc.fillRect(65, 60, 15, 15);
        gc.fillRect(85, 60, 15, 15);
        gc.fillRect(65, 90, 15, 15);
        gc.fillRect(85, 90, 15, 15);
        gc.fillRect(65, 120, 15, 15);
        gc.fillRect(85, 120, 15, 15);

        // Двері
        gc.setFill(Color.web("#8B4513")); // SADDLEBROWN equivalent
        gc.fillRect(70, 140, 20, 20);
        gc.strokeRect(70, 140, 20, 20);

        // Назва
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 10));
        gc.fillText("Верхній Баштовий", 30, 15);
        gc.fillText("Додзьо", 60, 175);

        // Кількість сов
        drawOwlCount(gc);
    }
}
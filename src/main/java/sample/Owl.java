package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.*;

// Клас для елемента посилального типу (для глибинного копіювання)
class OwlFeather implements Cloneable {
    public String color;
    public boolean isVisible;

    public OwlFeather(String color, boolean isVisible) {
        this.color = color;
        this.isVisible = isVisible;
    }

    @Override
    public OwlFeather clone() {
        try {
            return (OwlFeather) super.clone();
        } catch (CloneNotSupportedException e) {
            return new OwlFeather(this.color, this.isVisible);
        }
    }
}

// Базовий клас сови (мікрооб'єкт)
class Owl implements Cloneable {
    public String name;
    public Canvas canvas;
    public String color;
    public double size;
    public boolean active;
    public Rectangle activeRect;
    public OwlFeather feather; // Елемент посилального типу
    public boolean hasFeather;
    public String featherColor;
    public double x, y;

    public Owl(String name, String color, boolean hasFeather, String featherColor, double size, double x, double y) {
        this.name = name;
        this.color = color;
        this.hasFeather = hasFeather;
        this.featherColor = featherColor;
        this.size = size;
        this.x = x;
        this.y = y;
        this.active = false;

        // Створюємо елемент посилального типу
        this.feather = new OwlFeather(featherColor, hasFeather);

        createCanvas();
        createActiveRect();
        draw();
    }

    protected void createCanvas() {
        canvas = new Canvas(size + 40, size + 40);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        Main.group.getChildren().add(canvas);

        // Додаємо обробник миші
        canvas.setOnMouseClicked(this::handleMouseClick);
    }

    protected void createActiveRect() {
        activeRect = new Rectangle(size + 44, size + 44);
        activeRect.setFill(Color.TRANSPARENT);
        activeRect.setStrokeWidth(3);
        activeRect.setStroke(Color.TRANSPARENT);
        activeRect.setX(x - 2);
        activeRect.setY(y - 2);
        Main.group.getChildren().add(activeRect);
    }

    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = (canvas.getWidth()) / 2;
        double centerY = (canvas.getHeight()) / 2;
        double radius = size / 2;

        // Тіло сови (коло)
        gc.setFill(Color.valueOf(color));
        gc.fillOval(centerX - radius, centerY - radius, size, size);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, size, size);

        // Очі
        double eyeSize = size / 6;
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - radius/2 - eyeSize/2, centerY - radius/3, eyeSize, eyeSize);
        gc.fillOval(centerX + radius/2 - eyeSize/2, centerY - radius/3, eyeSize, eyeSize);

        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - radius/2 - eyeSize/4, centerY - radius/3 + eyeSize/4, eyeSize/2, eyeSize/2);
        gc.fillOval(centerX + radius/2 - eyeSize/4, centerY - radius/3 + eyeSize/4, eyeSize/2, eyeSize/2);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] xPoints = {centerX, centerX - 5, centerX + 5};
        double[] yPoints = {centerY, centerY + 10, centerY + 10};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Перо (якщо є)
        if (feather.isVisible) {
            gc.setFill(Color.valueOf(feather.color));
            gc.fillRect(centerX + radius - 10, centerY - radius - 5, 8, 15);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(centerX + radius - 10, centerY - radius - 5, 8, 15);
        }

        // Текст з ім'ям
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(12));
        gc.fillText(name, centerX - name.length() * 3, centerY + radius + 15);
    }

    public void handleMouseClick(MouseEvent event) {
        setActive(!active);
        Main.updateStatus();
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            activeRect.setStroke(Color.YELLOW);
        } else {
            activeRect.setStroke(Color.TRANSPARENT);
        }
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        activeRect.setX(x - 2);
        activeRect.setY(y - 2);
    }

    public void removeFromScreen() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(activeRect);
    }

    public void redraw() {
        draw();
    }

    @Override
    public Owl clone() {
        try {
            Owl cloned = (Owl) super.clone();
            // Глибинне копіювання елемента посилального типу
            cloned.feather = this.feather.clone();
            // Створюємо нові графічні елементи
            cloned.createCanvas();
            cloned.createActiveRect();
            cloned.draw();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return new Owl(this.name + "_copy", this.color, this.hasFeather, this.featherColor, this.size, this.x + 20, this.y + 20);
        }
    }

    public boolean isActive() {
        return active;
    }

    public String getInfo() {
        return String.format("%s (%.0f)", name, size);
    }
}

// Нащадок сови - Великий Сова
class BigOwl extends Owl {
    public BigOwl(String name, String color, boolean hasFeather, String featherColor, double size, double x, double y) {
        super(name, color, hasFeather, featherColor, size * 1.5, x, y);
    }

    @Override
    protected void draw() {
        super.draw();

        // Додаткові елементи для Великого Сова
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Корона
        gc.setFill(Color.GOLD);
        double[] xPoints = {centerX - 15, centerX, centerX + 15, centerX + 10, centerX, centerX - 10};
        double[] yPoints = {centerY - size/2 - 10, centerY - size/2 - 20, centerY - size/2 - 10, centerY - size/2 - 5, centerY - size/2 - 15, centerY - size/2 - 5};
        gc.fillPolygon(xPoints, yPoints, 6);
    }

    @Override
    public BigOwl clone() {
        try {
            BigOwl cloned = (BigOwl) super.clone();
            cloned.feather = this.feather.clone();
            cloned.createCanvas();
            cloned.createActiveRect();
            cloned.draw();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return new BigOwl(this.name + "_copy", this.color, this.hasFeather, this.featherColor, this.size, this.x + 20, this.y + 20);
        }
    }
}

// Ще один нащадок - Нащадок Сови
class OwlChild extends Owl {
    public OwlChild(String name, String color, boolean hasFeather, String featherColor, double size, double x, double y) {
        super(name, color, hasFeather, featherColor, size * 0.7, x, y);
    }

    @Override
    protected void draw() {
        super.draw();

        // Додаткові елементи для Нащадка Сови
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Маленькі крила
        gc.setFill(Color.valueOf(color).darker());
        gc.fillOval(centerX - size/2 - 10, centerY - 5, 15, 10);
        gc.fillOval(centerX + size/2 - 5, centerY - 5, 15, 10);
    }

    @Override
    public OwlChild clone() {
        try {
            OwlChild cloned = (OwlChild) super.clone();
            cloned.feather = this.feather.clone();
            cloned.createCanvas();
            cloned.createActiveRect();
            cloned.draw();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return new OwlChild(this.name + "_copy", this.color, this.hasFeather, this.featherColor, this.size, this.x + 20, this.y + 20);
        }
    }
}
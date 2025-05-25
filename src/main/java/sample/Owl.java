package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Owl implements Cloneable {
    protected String name;
    protected String type;
    protected boolean hasWeapon; // Змінено з hasFeather на hasWeapon відповідно до Main.java
    protected String weaponColor; // Змінено з featherColor на weaponColor
    protected String color;
    protected double size;
    protected double x, y;
    protected boolean active;
    protected MacroObject macroObject;
    protected Canvas canvas;
    protected Circle boundingCircle;

    public Owl(String name, String type, boolean hasWeapon, String weaponColor, String color, double x, double y) {
        this.name = name;
        this.type = type;
        this.hasWeapon = hasWeapon;
        this.weaponColor = weaponColor;
        this.color = color;
        this.size = 80; // Default size
        this.x = x;
        this.y = y;
        this.active = false;
        this.macroObject = null;

        createCanvas();
        draw();
    }

    protected void createCanvas() {
        canvas = new Canvas(size + 20, size + 40); // Extra space for name
        canvas.setLayoutX(x - (size + 20) / 2);
        canvas.setLayoutY(y - (size + 40) / 2);

        // Create bounding circle for mouse detection
        boundingCircle = new Circle(x, y, size / 2);
        boundingCircle.setVisible(false);

        Main.group.getChildren().addAll(canvas, boundingCircle);
        draw();
    }

    protected void draw() {
        if (canvas == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2 - 10; // Leave space for name

        // Draw owl body
        Color owlColor = getColorFromString(color);
        gc.setFill(owlColor);
        gc.fillOval(centerX - size/3, centerY - size/4, size/1.5, size/2);

        // Draw owl head
        gc.fillOval(centerX - size/4, centerY - size/2, size/2, size/2);

        // Draw eyes
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - size/6, centerY - size/3, size/8, size/8);
        gc.fillOval(centerX + size/12, centerY - size/3, size/8, size/8);

        // Draw pupils
        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - size/8, centerY - size/4, size/16, size/16);
        gc.fillOval(centerX + size/8, centerY - size/4, size/16, size/16);

        // Draw beak
        gc.setFill(Color.ORANGE);
        gc.fillPolygon(
                new double[]{centerX - size/16, centerX + size/16, centerX},
                new double[]{centerY - size/8, centerY - size/8, centerY},
                3
        );

        // Draw wings
        gc.setFill(owlColor.darker());
        gc.fillOval(centerX - size/2, centerY - size/6, size/4, size/3);
        gc.fillOval(centerX + size/4, centerY - size/6, size/4, size/3);

        // Draw weapon if owl has one
        if (hasWeapon) {
            Color wColor = getColorFromString(weaponColor);
            gc.setFill(wColor);
            gc.setLineWidth(3);
            gc.setStroke(wColor);

            // Draw katana
            gc.strokeLine(centerX + size/3, centerY - size/4, centerX + size/2, centerY - size/2);
            gc.fillRect(centerX + size/3 - 2, centerY - size/4 - 5, 4, 10);
        }

        // Draw selection border if active
        if (active) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeOval(centerX - size/2, centerY - size/2, size, size);
        }

        // Draw name
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(name, centerX - name.length() * 3, centerY + size/2 + 15);

        // Draw type info
        gc.setFont(new Font("Arial", 10));
        gc.fillText(type, centerX - type.length() * 2.5, centerY + size/2 + 28);
    }

    protected Color getColorFromString(String colorName) {
        if (colorName == null) return Color.GRAY;

        try {
            switch (colorName.toUpperCase()) {
                case "RED": return Color.RED;
                case "BLUE": return Color.BLUE;
                case "GREEN": return Color.GREEN;
                case "BLACK": return Color.BLACK;
                case "WHITE": return Color.WHITE;
                case "BROWN": return Color.BROWN;
                case "ORANGE": return Color.ORANGE;
                case "PURPLE": return Color.PURPLE;
                case "PINK": return Color.PINK;
                case "YELLOW": return Color.YELLOW;
                case "GRAY": return Color.GRAY;
                case "SILVER": return Color.SILVER;
                case "GOLD": return Color.GOLD;
                default: return Color.valueOf(colorName);
            }
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    // Методи для управління совою
    public void move(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;

        // Update canvas position
        canvas.setLayoutX(x - (size + 20) / 2);
        canvas.setLayoutY(y - (size + 40) / 2);

        // Update bounding circle position
        boundingCircle.setCenterX(x);
        boundingCircle.setCenterY(y);

        draw();
    }

    public boolean contains(double mouseX, double mouseY) {
        double distance = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        return distance <= size / 2;
    }

    public void toggleActive() {
        setActive(!active);
    }

    public void setActive(boolean active) {
        this.active = active;
        draw();
    }

    public boolean isActive() {
        return active;
    }

    public void removeFromScene() {
        if (canvas != null) {
            Main.group.getChildren().remove(canvas);
        }
        if (boundingCircle != null) {
            Main.group.getChildren().remove(boundingCircle);
        }
        if (macroObject != null) {
            macroObject.removeOwl(this);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Owl cloned = new Owl(this.name + "_Copy", this.type, this.hasWeapon,
                this.weaponColor, this.color, this.x, this.y);
        cloned.size = this.size;
        return cloned;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        draw();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        draw();
    }

    public boolean hasWeapon() {
        return hasWeapon;
    }

    public void setHasWeapon(boolean hasWeapon) {
        this.hasWeapon = hasWeapon;
        draw();
    }

    public String getWeaponColor() {
        return weaponColor;
    }

    public void setWeaponColor(String weaponColor) {
        this.weaponColor = weaponColor;
        draw();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        draw();
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
        // Recreate canvas with new size
        removeFromScene();
        createCanvas();
        draw();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        canvas.setLayoutX(x - (size + 20) / 2);
        canvas.setLayoutY(y - (size + 40) / 2);
        boundingCircle.setCenterX(x);
        boundingCircle.setCenterY(y);
        draw();
    }

    public MacroObject getMacroObject() {
        return macroObject;
    }

    public void setMacroObject(MacroObject macroObject) {
        this.macroObject = macroObject;
    }

    @Override
    public String toString() {
        return String.format("Owl{name='%s', type='%s', hasWeapon=%s, weaponColor='%s', color='%s', x=%.1f, y=%.1f}",
                name, type, hasWeapon, weaponColor, color, x, y);
    }
}
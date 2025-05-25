package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Owl implements Cloneable {
    protected String name;
    protected String type;
    protected boolean hasFeather;
    protected String featherColor;
    protected String color;
    protected double size;
    protected double x, y;
    protected boolean active;
    protected MacroObject macroObject;
    protected Canvas canvas;
    protected Circle boundingCircle;

    public Owl(String name, String type, boolean hasFeather, String featherColor, String color, double x, double y) {
        this.name = name;
        this.type = type;
        this.hasFeather = hasFeather;
        this.featherColor = featherColor;
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

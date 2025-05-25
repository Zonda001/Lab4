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
            owls.ad
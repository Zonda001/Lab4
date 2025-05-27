package sekiro;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;

public class Castle {
    String name;
    String castleType;
    Canvas canvas;
    ArrayList<Owl> owls;
    double x, y;

    public Castle(String name, String castleType, double x, double y) {
        this.name = name;
        this.castleType = castleType;
        this.x = x;
        this.y = y;
        this.owls = new ArrayList<>();

        canvas = new Canvas(250, 200);
        drawCastle();

        Main.group.getChildren().add(canvas);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    private void drawCastle() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Визначаємо колір та стиль в залежності від типу замку
        Color castleColor = Color.GRAY;
        Color roofColor = Color.DARKRED;

        switch (castleType) {
            case "Замок Асіна":
                castleColor = Color.LIGHTGRAY;
                roofColor = Color.RED;
                break;
            case "Хіру-ден":
                castleColor = Color.SANDYBROWN;
                roofColor = Color.BROWN;
                break;
            case "Верхній Баштовий Додзьо":
                castleColor = Color.DARKGRAY;
                roofColor = Color.DARKBLUE;
                break;
        }

        // Малюємо основу замку
        gc.setFill(castleColor);
        gc.fillRect(50, 100, 150, 80);

        // Малюємо стіни замку
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(50, 100, 150, 80);

        // Малюємо башти
        gc.fillRect(30, 80, 40, 100);
        gc.fillRect(180, 80, 40, 100);
        gc.strokeRect(30, 80, 40, 100);
        gc.strokeRect(180, 80, 40, 100);

        // Малюємо дахи башт
        gc.setFill(roofColor);
        double[] xTower1 = {25, 50, 75};
        double[] yTower1 = {80, 50, 80};
        gc.fillPolygon(xTower1, yTower1, 3);

        double[] xTower2 = {175, 200, 225};
        double[] yTower2 = {80, 50, 80};
        gc.fillPolygon(xTower2, yTower2, 3);

        // Малюємо головний дах
        double[] xRoof = {40, 125, 210};
        double[] yRoof = {100, 70, 100};
        gc.fillPolygon(xRoof, yRoof, 3);

        // Малюємо ворота
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(110, 140, 30, 40);
        gc.strokeRect(110, 140, 30, 40);

        // Малюємо вікна
        gc.setFill(Color.YELLOW);
        gc.fillRect(70, 120, 15, 15);
        gc.fillRect(165, 120, 15, 15);
        gc.fillRect(40, 100, 15, 15);
        gc.fillRect(195, 100, 15, 15);

        // Текст з назвою замку
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 16));
        gc.fillText(name, 10, 20);

        // Показуємо кількість сов
        gc.setFill(Color.BLUE);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Сов: " + owls.size(), 10, 190);

        // Показуємо тип замку
        gc.setFill(Color.DARKGREEN);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(castleType, 150, 190);
    }

    public void addOwl(Owl owl) {
        if (!owls.contains(owl)) {
            owls.add(owl);
            owl.setBelongsToCastle(this);
            redraw();
        }
    }

    public void removeOwl(Owl owl) {
        if (owls.contains(owl)) {
            owls.remove(owl);
            owl.setBelongsToCastle(null);
            redraw();
        }
    }

    public void redraw() {
        drawCastle();
    }

    // Метод для оновлення відображення (синонім для redraw)
    public void updateDisplay() {
        drawCastle();
    }

    // Метод для отримання кількості сов
    public int getOwlCount() {
        return owls.size();
    }

    // Метод для отримання списку сов
    public ArrayList<Owl> getOwlsList() {
        return new ArrayList<>(owls);
    }

    public boolean containsPoint(double x, double y) {
        return canvas.getBoundsInParent().contains(x, y);
    }

    public ArrayList<Owl> getOwls() {
        return new ArrayList<>(owls);
    }

    public String getInfo() {
        return name + " (" + castleType + ") - Сов: " + owls.size();
    }
}
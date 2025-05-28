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

    // Виправлений метод для отримання списку назв сов
    public String getOwlsList() {
        if (owls.isEmpty()) {
            return "Немає сов";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < owls.size(); i++) {
            Owl owl = owls.get(i);
            sb.append(owl.name);
            if (owl.hasShinobiTechniques) {
                sb.append(" (Shinobi)");
            }
            sb.append(" [").append(owl.skillLevel).append("]");

            if (i < owls.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    // Метод для отримання детальної інформації про сов
    public String getDetailedOwlsList() {
        if (owls.isEmpty()) {
            return "У цьому замку немає сов";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Список сов у замку ").append(name).append(":\n\n");

        for (int i = 0; i < owls.size(); i++) {
            Owl owl = owls.get(i);
            sb.append((i + 1)).append(". ").append(owl.name);
            sb.append("\n   Тип: ").append(owl.owlType);
            sb.append("\n   Рівень: ").append(owl.skillLevel);
            sb.append("\n   Техніки Shinobi: ").append(owl.hasShinobiTechniques ? "Так" : "Ні");

            if (i < owls.size() - 1) {
                sb.append("\n\n");
            }
        }

        return sb.toString();
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

    // Метод для видалення сови за індексом (для меню)
    public boolean removeOwlByIndex(int index) {
        if (index >= 0 && index < owls.size()) {
            Owl owl = owls.get(index);
            removeOwl(owl);
            return true;
        }
        return false;
    }

    // Метод для отримання сови за індексом
    public Owl getOwlByIndex(int index) {
        if (index >= 0 && index < owls.size()) {
            return owls.get(index);
        }
        return null;
    }

    // Метод для отримання списку назв сов для вибору в діалозі
    public String[] getOwlNamesArray() {
        String[] names = new String[owls.size()];
        for (int i = 0; i < owls.size(); i++) {
            Owl owl = owls.get(i);
            names[i] = (i + 1) + ". " + owl.name + " [" + owl.skillLevel + "]" +
                    (owl.hasShinobiTechniques ? " (Shinobi)" : "");
        }
        return names;
    }
}
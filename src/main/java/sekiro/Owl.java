package sekiro;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class Owl implements Cloneable {
    String name;
    String type;
    boolean hasShinobiTechniques;
    String skillLevel;
    Canvas canvas;
    Rectangle rectActive;
    Rectangle shinobiRect;
    boolean active = false;
    Castle belongsToCastle;
    ArrayList<Technique> techniques; // Список технік сови

    public Owl(String name, String type, boolean hasShinobiTechniques, String skillLevel, double x, double y) {
        this.name = name;
        this.type = type;
        this.hasShinobiTechniques = hasShinobiTechniques;
        this.skillLevel = skillLevel;

        // Генеруємо випадкові техніки для сови
        this.techniques = Technique.generateRandomTechniques(hasShinobiTechniques, skillLevel, Main.rnd);

        canvas = new Canvas(80, 80);
        drawOwl();

        // Створюємо прямокутник для активності
        rectActive = new Rectangle(80, 80);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStroke(Color.RED);
        rectActive.setStrokeWidth(3);
        rectActive.setVisible(false);

        // Створюємо індикатор shinobi технік
        if (hasShinobiTechniques) {
            shinobiRect = new Rectangle(10, 10);
            shinobiRect.setFill(Color.PURPLE);
            shinobiRect.setStroke(Color.MAGENTA);
            shinobiRect.setStrokeWidth(1);
        }

        // Додаємо до сцени
        Main.group.getChildren().add(canvas);
        Main.group.getChildren().add(rectActive);
        if (hasShinobiTechniques) {
            Main.group.getChildren().add(shinobiRect);
        }

        // Встановлюємо позицію
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        updatePosition();
    }

    private void drawOwl() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Визначаємо колір в залежності від типу та рівня
        Color bodyColor = Color.BROWN;
        Color eyeColor = Color.YELLOW;

        switch (type) {
            case "Великий Сова":
                bodyColor = Color.DARKBLUE;
                eyeColor = Color.ORANGE;
                break;
            case "Нащадок Сови":
                bodyColor = Color.LIGHTCYAN;
                eyeColor = Color.LIGHTYELLOW;
                break;
            case "Сова":
                bodyColor = Color.BROWN;
                eyeColor = Color.YELLOW;
                break;
        }

        // Колір залежно від рівня майстерності
        switch (skillLevel) {
            case "Майстер":
                bodyColor = bodyColor.deriveColor(0, 1.2, 0.8, 1.0); // Темніше
                break;
            case "Експерт":
                bodyColor = bodyColor.deriveColor(0, 1.1, 0.9, 1.0);
                break;
        }

        // Тіло сови
        gc.setFill(bodyColor);
        gc.fillOval(20, 30, 40, 35);

        // Голова
        gc.fillOval(25, 15, 30, 30);

        // Очі
        gc.setFill(eyeColor);
        gc.fillOval(30, 22, 8, 8);
        gc.fillOval(42, 22, 8, 8);

        // Зіниці
        gc.setFill(Color.BLACK);
        gc.fillOval(32, 24, 4, 4);
        gc.fillOval(44, 24, 4, 4);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] xPoints = {38, 42, 40};
        double[] yPoints = {32, 32, 38};
        gc.fillPolygon(xPoints, yPoints, 3);

        // Крила
        gc.setFill(bodyColor.darker());
        gc.fillOval(10, 35, 20, 25);
        gc.fillOval(50, 35, 20, 25);

        // Лапи
        gc.setFill(Color.ORANGE);
        gc.fillRect(28, 60, 4, 15);
        gc.fillRect(48, 60, 4, 15);

        // Кігті
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(26, 75, 24, 78);
        gc.strokeLine(30, 75, 28, 78);
        gc.strokeLine(32, 75, 34, 78);
        gc.strokeLine(46, 75, 44, 78);
        gc.strokeLine(50, 75, 48, 78);
        gc.strokeLine(52, 75, 54, 78);

        // Індикатор кількості технік
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 10));
        gc.fillText("T:" + techniques.size(), 5, 12);

        // Показуємо найпотужнішу техніку
        if (!techniques.isEmpty()) {
            Technique strongest = getStrongestTechnique();
            gc.setFill(getElementColor(strongest.element));
            gc.fillOval(65, 5, 12, 12);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 8));
            gc.fillText(String.valueOf(strongest.power), 69, 13);
        }

        // Текст з іменем
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 10));
        String displayName = name.length() > 10 ? name.substring(0, 10) + "..." : name;
        gc.fillText(displayName, 5, canvas.getHeight() - 5);
    }

    private Color getElementColor(String element) {
        switch (element) {
            case "Вогонь": return Color.RED;
            case "Вода": return Color.BLUE;
            case "Земля": return Color.SADDLEBROWN;
            case "Повітря": return Color.LIGHTCYAN;
            case "Тінь": return Color.DARKGRAY;
            case "Світло": return Color.GOLD;
            default: return Color.GRAY;
        }
    }

    public Technique getStrongestTechnique() {
        if (techniques.isEmpty()) return null;

        Technique strongest = techniques.get(0);
        for (Technique technique : techniques) {
            if (technique.power > strongest.power) {
                strongest = technique;
            }
        }
        return strongest;
    }

    public int getTotalPower() {
        int total = 0;
        for (Technique technique : techniques) {
            total += technique.power;
        }
        return total;
    }

    public String getTechniquesInfo() {
        if (techniques.isEmpty()) {
            return "Немає технік";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Техніки сови ").append(name).append(":\n\n");

        for (int i = 0; i < techniques.size(); i++) {
            Technique technique = techniques.get(i);
            sb.append((i + 1)).append(". ").append(technique.toString()).append("\n");
        }

        sb.append("\nЗагальна сила: ").append(getTotalPower());
        return sb.toString();
    }

    public String getDetailedTechniquesInfo() {
        if (techniques.isEmpty()) {
            return "У цієї сови немає технік";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Детальна інформація про техніки сови ").append(name).append(":\n\n");

        for (int i = 0; i < techniques.size(); i++) {
            Technique technique = techniques.get(i);
            sb.append("=== Техніка ").append(i + 1).append(" ===\n");
            sb.append(technique.getDetailedInfo()).append("\n\n");
        }

        sb.append("Загальна сила всіх технік: ").append(getTotalPower());
        return sb.toString();
    }

    // Методи для додавання/видалення технік
    public void addTechnique(Technique technique) {
        if (!techniques.contains(technique)) {
            techniques.add(technique);
            drawOwl(); // Перемалювати для оновлення індикаторів
        }
    }

    public boolean removeTechnique(Technique technique) {
        if (techniques.remove(technique)) {
            drawOwl(); // Перемалювати для оновлення індикаторів
            return true;
        }
        return false;
    }

    public boolean removeTechniqueByIndex(int index) {
        if (index >= 0 && index < techniques.size()) {
            techniques.remove(index);
            drawOwl(); // Перемалювати для оновлення індикаторів
            return true;
        }
        return false;
    }

    public ArrayList<Technique> getTechniques() {
        return new ArrayList<>(techniques); // Повертаємо копію
    }

    public void setActive(boolean active) {
        this.active = active;
        rectActive.setVisible(active);
    }

    public boolean isActive() {
        return active;
    }

    public void setBelongsToCastle(Castle castle) {
        this.belongsToCastle = castle;
    }

    public Castle getBelongsToCastle() {
        return belongsToCastle;
    }

    public boolean contains(double x, double y) {
        return canvas.getBoundsInParent().contains(x, y);
    }

    public void move(double dx, double dy) {
        canvas.setLayoutX(canvas.getLayoutX() + dx);
        canvas.setLayoutY(canvas.getLayoutY() + dy);
        updatePosition();
    }

    public void updatePosition() {
        rectActive.setLayoutX(canvas.getLayoutX());
        rectActive.setLayoutY(canvas.getLayoutY());

        if (hasShinobiTechniques && shinobiRect != null) {
            shinobiRect.setLayoutX(canvas.getLayoutX() + 70);
            shinobiRect.setLayoutY(canvas.getLayoutY());
        }
    }

    public void removeFromScene() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);
        if (hasShinobiTechniques && shinobiRect != null) {
            Main.group.getChildren().remove(shinobiRect);
        }
    }

    // Метод клонування з копіюванням технік
    public Owl createCopy(String newName, double x, double y) {
        Owl copy = new Owl(newName, this.type, this.hasShinobiTechniques, this.skillLevel, x, y);

        // Копіюємо всі техніки
        copy.techniques.clear();
        for (Technique technique : this.techniques) {
            copy.techniques.add(new Technique(technique));
        }

        // Перемалювати для оновлення індикаторів
        copy.drawOwl();

        return copy;
    }

    @Override
    public String toString() {
        return name + " [" + type + ", " + skillLevel + ", Технік: " + techniques.size() +
                ", Сила: " + getTotalPower() + "]";
    }
}
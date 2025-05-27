package sekiro;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.InputStream;
import java.util.ArrayList;

public class Owl implements Cloneable {
    String name;
    String owlType;
    boolean hasShinobiTechniques;
    String skillLevel;
    Canvas canvas;
    boolean active;
    Rectangle rectActive;
    Rectangle shinobiRect; // Елемент посилального типу для демонстрації глибинного копіювання
    ImageView owlImage;
    ArrayList<String> techniques; // Елемент посилального типу

    // Посилання на макрооб'єкт
    Castle belongsToCastle = null;

    public Owl(String name, String owlType, boolean hasShinobiTechniques,
               String skillLevel, double x, double y) {
        this.name = name;
        this.owlType = owlType;
        this.hasShinobiTechniques = hasShinobiTechniques;
        this.skillLevel = skillLevel;
        this.techniques = new ArrayList<>();

        // Додаємо деякі техніки в залежності від типу
        if (hasShinobiTechniques) {
            techniques.add("Невидимість");
            techniques.add("Швидкий удар");
            if (owlType.equals("Великий Сова")) {
                techniques.add("Вогняна атака");
            }
        }

        canvas = new Canvas(200, 180);
        drawOwl();

        // Додаємо канвас до головної групи
        Main.group.getChildren().add(canvas);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);

        // Створюємо прямокутник для показу синобі технік
        shinobiRect = new Rectangle(30, 15);
        if (hasShinobiTechniques) {
            shinobiRect.setFill(Color.GOLD);
            Main.group.getChildren().add(shinobiRect);
        } else {
            shinobiRect.setFill(Color.GRAY);
        }
        shinobiRect.setX(x + 170);
        shinobiRect.setY(y + 10);

        active = false;
        rectActive = new Rectangle(204, 184);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStrokeWidth(3);
        rectActive.setStroke(Color.BLACK);
        Main.group.getChildren().add(rectActive);
        rectActive.setX(x - 2);
        rectActive.setY(y - 2);
    }

    private void drawOwl() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Визначаємо колір в залежності від типу сови
        Color owlColor = Color.BROWN;
        double size = 1.0;

        switch (owlType) {
            case "Великий Сова":
                owlColor = Color.DARKRED;
                size = 1.2;
                break;
            case "Нащадок Сови":
                owlColor = Color.PURPLE;
                size = 0.8;
                break;
        }

        // Малюємо тіло сови (еліпс)
        gc.setFill(owlColor);
        gc.fillOval(50 * size, 60 * size, 100 * size, 80 * size);

        // Малюємо голову
        gc.fillOval(70 * size, 20 * size, 60 * size, 60 * size);

        // Малюємо очі
        gc.setFill(Color.YELLOW);
        gc.fillOval(80 * size, 35 * size, 15 * size, 15 * size);
        gc.fillOval(105 * size, 35 * size, 15 * size, 15 * size);

        // Зіниці
        gc.setFill(Color.BLACK);
        gc.fillOval(85 * size, 40 * size, 5 * size, 5 * size);
        gc.fillOval(110 * size, 40 * size, 5 * size, 5 * size);

        // Дзьоб
        gc.fillOval(95 * size, 50 * size, 10 * size, 8 * size);

        // Крила
        gc.setFill(owlColor.darker());
        gc.fillOval(30 * size, 70 * size, 40 * size, 60 * size);
        gc.fillOval(130 * size, 70 * size, 40 * size, 60 * size);

        // Лапи
        gc.setFill(Color.ORANGE);
        gc.fillRect(80 * size, 130 * size, 8 * size, 20 * size);
        gc.fillRect(112 * size, 130 * size, 8 * size, 20 * size);

        // Текст з ім'ям
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 14));
        gc.fillText(name, 10, 165);

        // Показуємо рівень майстерності
        gc.setFill(Color.LIGHTBLUE);
        gc.fillText(skillLevel, 120, 165);
    }

    public void redraw() {
        drawOwl();
        // Оновлюємо колір прямокутника синобі технік
        if (hasShinobiTechniques) {
            shinobiRect.setFill(Color.GOLD);
            if (!Main.group.getChildren().contains(shinobiRect)) {
                Main.group.getChildren().add(shinobiRect);
            }
        } else {
            shinobiRect.setFill(Color.GRAY);
        }
    }

    public void removeFromScene() {
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);
        if (Main.group.getChildren().contains(shinobiRect)) {
            Main.group.getChildren().remove(shinobiRect);
        }
    }

    public boolean isActive() {
        return active;
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

    public void move(double dx, double dy) {
        canvas.setLayoutX(canvas.getLayoutX() + dx);
        canvas.setLayoutY(canvas.getLayoutY() + dy);
        rectActive.setX(rectActive.getX() + dx);
        rectActive.setY(rectActive.getY() + dy);
        shinobiRect.setX(shinobiRect.getX() + dx);
        shinobiRect.setY(shinobiRect.getY() + dy);
    }

    public boolean contains(double x, double y) {
        return canvas.getBoundsInParent().contains(x, y);
    }

    // Метод для оновлення позиції елементів (для клонованих об'єктів)
    public void updatePosition() {
        double x = canvas.getLayoutX();
        double y = canvas.getLayoutY();

        rectActive.setX(x - 2);
        rectActive.setY(y - 2);
        shinobiRect.setX(x + 170);
        shinobiRect.setY(y + 10);
    }

    // Глибинне копіювання
    @Override
    public Owl clone() {
        try {
            Owl cloned = (Owl) super.clone();

            // Глибинне копіювання посилальних типів
            cloned.techniques = new ArrayList<>(this.techniques);
            cloned.shinobiRect = new Rectangle(shinobiRect.getWidth(), shinobiRect.getHeight());
            cloned.shinobiRect.setFill(shinobiRect.getFill());

            // Створюємо новий канвас та прямокутники
            cloned.canvas = new Canvas(200, 180);
            cloned.rectActive = new Rectangle(204, 184);
            cloned.rectActive.setFill(Color.TRANSPARENT);
            cloned.rectActive.setStrokeWidth(3);
            cloned.rectActive.setStroke(Color.BLACK);

            cloned.active = false;
            cloned.belongsToCastle = null; // Клонована сова не належить замку

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBelongsToCastle(Castle castle) {
        this.belongsToCastle = castle;
    }

    public Castle getBelongsToCastle() {
        return belongsToCastle;
    }

    public String getStatusInfo() {
        String castleInfo = belongsToCastle != null ?
                " (належить: " + belongsToCastle.name + ")" : " (вільна)";
        return name + " (" + owlType + ")" + castleInfo;
    }
}
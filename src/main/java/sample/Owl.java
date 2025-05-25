package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

// Клас для елемента посилального типу (для глибинного копіювання)
class WeaponInfo implements Cloneable {
    private String weaponType;
    private int sharpness;

    public WeaponInfo(String weaponType, int sharpness) {
        this.weaponType = weaponType;
        this.sharpness = sharpness;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new WeaponInfo(this.weaponType, this.sharpness);
    }

    public String getWeaponType() { return weaponType; }
    public void setWeaponType(String weaponType) { this.weaponType = weaponType; }
    public int getSharpness() { return sharpness; }
    public void setSharpness(int sharpness) { this.sharpness = sharpness; }
}

public class Owl implements Cloneable {
    String name;
    String type; // "Сова", "Великий Сова", "Нащадок Сови"
    Canvas canvas;
    boolean hasWeapon;
    String weaponColor;
    String owlColor;
    Rectangle rectWeapon;
    Rectangle rectActive;
    boolean active;
    MacroObject belongsToMacro; // Належність до макрооб'єкта

    // Елемент посилального типу для демонстрації глибинного копіювання
    WeaponInfo weaponInfo;

    public Owl(String name, String type, boolean hasWeapon, String weaponColor,
               String owlColor, double x, double y) {
        this.name = name;
        this.type = type;
        this.hasWeapon = hasWeapon;
        this.weaponColor = weaponColor;
        this.owlColor = owlColor;
        this.belongsToMacro = null;

        // Ініціалізуємо елемент посилального типу
        if (hasWeapon) {
            weaponInfo = new WeaponInfo("Katana", 85);
        } else {
            weaponInfo = new WeaponInfo("None", 0);
        }

        // Розмір canvas залежить від типу сови
        int width = 180, height = 160;
        if (type.equals("Великий Сова")) {
            width = 220;
            height = 200;
        } else if (type.equals("Нащадок Сови")) {
            width = 160;
            height = 140;
        }

        canvas = new Canvas(width, height);
        drawOwl(canvas, name, type, hasWeapon, owlColor, weaponColor);
        Main.group.getChildren().add(canvas);

        // Створюємо прямокутник для відображення зброї
        if (hasWeapon) {
            rectWeapon = new Rectangle(25, 15);
            rectWeapon.setFill(Color.valueOf(weaponColor));
            Main.group.getChildren().add(rectWeapon);
        }

        // Створюємо прямокутник для відображення активності
        active = false;
        rectActive = new Rectangle(width + 4, height + 4);
        rectActive.setFill(Color.TRANSPARENT);
        rectActive.setStrokeWidth(3);
        rectActive.setStroke(Color.TRANSPARENT);
        Main.group.getChildren().add(rectActive);

        // Встановлюємо позицію
        move(x - canvas.getLayoutX(), y - canvas.getLayoutY());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Owl cloned = new Owl(this.name + "_Copy", this.type, this.hasWeapon,
                this.weaponColor, this.owlColor,
                canvas.getLayoutX(), canvas.getLayoutY());

        // Глибинне копіювання елемента посилального типу
        cloned.weaponInfo = (WeaponInfo) this.weaponInfo.clone();
        cloned.belongsToMacro = this.belongsToMacro;

        return cloned;
    }

    public void removeFromScene() {
        if (hasWeapon && rectWeapon != null) {
            Main.group.getChildren().remove(rectWeapon);
        }
        Main.group.getChildren().remove(canvas);
        Main.group.getChildren().remove(rectActive);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            rectActive.setStroke(Color.YELLOW);
        } else {
            rectActive.setStroke(Color.TRANSPARENT);
        }
    }

    public void toggleActive() {
        setActive(!active);
    }

    public void move(double dx, double dy) {
        if (hasWeapon && rectWeapon != null) {
            rectWeapon.setX(rectWeapon.getX() + dx);
            rectWeapon.setY(rectWeapon.getY() + dy);
        }
        canvas.setLayoutX(canvas.getLayoutX() + dx);
        canvas.setLayoutY(canvas.getLayoutY() + dy);
        rectActive.setX(rectActive.getX() + dx);
        rectActive.setY(rectActive.getY() + dy);
    }

    public boolean contains(double x, double y) {
        return x >= canvas.getLayoutX() && x <= canvas.getLayoutX() + canvas.getWidth() &&
                y >= canvas.getLayoutY() && y <= canvas.getLayoutY() + canvas.getHeight();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    // Оновлення параметрів сови (для діалогу редагування)
    public void updateOwl(String newName, String newType, boolean newHasWeapon,
                          String newWeaponColor, String newOwlColor) {
        this.name = newName;
        this.type = newType;
        this.hasWeapon = newHasWeapon;
        this.weaponColor = newWeaponColor;
        this.owlColor = newOwlColor;

        // Оновлюємо weaponInfo
        if (hasWeapon) {
            weaponInfo.setWeaponType("Katana");
            weaponInfo.setSharpness(85);
        } else {
            weaponInfo.setWeaponType("None");
            weaponInfo.setSharpness(0);
        }

        // Перемальовуємо сову
        drawOwl(canvas, name, type, hasWeapon, owlColor, weaponColor);

        // Оновлюємо прямокутник зброї
        if (hasWeapon) {
            if (rectWeapon == null) {
                rectWeapon = new Rectangle(25, 15);
                Main.group.getChildren().add(rectWeapon);
            }
            rectWeapon.setFill(Color.valueOf(weaponColor));
            rectWeapon.setX(canvas.getLayoutX() + canvas.getWidth() - 30);
            rectWeapon.setY(canvas.getLayoutY() + 20);
        } else {
            if (rectWeapon != null) {
                Main.group.getChildren().remove(rectWeapon);
                rectWeapon = null;
            }
        }
    }

    public static void drawOwl(Canvas canvas, String name, String type, boolean hasWeapon,
                               String owlColor, String weaponColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Очищуємо canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Розмір залежить від типу
        double scale = 1.0;
        if (type.equals("Великий Сова")) {
            scale = 1.3;
        } else if (type.equals("Нащадок Сови")) {
            scale = 0.8;
        }

        // Малюємо тіло сови
        gc.setFill(Color.valueOf(owlColor));
        gc.fillOval(centerX - 30 * scale, centerY - 20 * scale, 60 * scale, 50 * scale);

        // Малюємо голову
        gc.fillOval(centerX - 25 * scale, centerY - 45 * scale, 50 * scale, 40 * scale);

        // Малюємо очі
        gc.setFill(Color.YELLOW);
        gc.fillOval(centerX - 15 * scale, centerY - 35 * scale, 12 * scale, 12 * scale);
        gc.fillOval(centerX + 3 * scale, centerY - 35 * scale, 12 * scale, 12 * scale);

        // Зіниці
        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - 12 * scale, centerY - 32 * scale, 6 * scale, 6 * scale);
        gc.fillOval(centerX + 6 * scale, centerY - 32 * scale, 6 * scale, 6 * scale);

        // Дзьоб
        gc.setFill(Color.ORANGE);
        double[] beakX = {centerX - 3 * scale, centerX + 3 * scale, centerX};
        double[] beakY = {centerY - 25 * scale, centerY - 25 * scale, centerY - 15 * scale};
        gc.fillPolygon(beakX, beakY, 3);

        // Крила
        gc.setFill(Color.valueOf(owlColor).darker());
        gc.fillOval(centerX - 45 * scale, centerY - 10 * scale, 25 * scale, 35 * scale);
        gc.fillOval(centerX + 20 * scale, centerY - 10 * scale, 25 * scale, 35 * scale);

        // Лапи
        gc.setFill(Color.ORANGE);
        gc.fillRect(centerX - 10 * scale, centerY + 20 * scale, 8 * scale, 15 * scale);
        gc.fillRect(centerX + 2 * scale, centerY + 20 * scale, 8 * scale, 15 * scale);

        // Відображення стану weaponInfo (елемент посилального типу)
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 8));
        gc.fillText("Weapon: " + (hasWeapon ? "Yes" : "No"), 5, canvas.getHeight() - 25);

        // Відображення приналежності до макрооб'єкта
        gc.setFill(Color.DARKBLUE);
        gc.setFont(new Font("Arial", 8));
        String macroText = "Macro: None";
        // Це буде оновлено в Main після створення
        gc.fillText(macroText, 5, canvas.getHeight() - 10);

        // Малюємо зброю, якщо є
        if (hasWeapon) {
            gc.setFill(Color.valueOf(weaponColor));
            gc.fillRect(centerX + 25 * scale, centerY - 20 * scale, 4 * scale, 25 * scale);
            gc.fillRect(centerX + 20 * scale, centerY - 25 * scale, 14 * scale, 5 * scale);
        }

        // Назва сови
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(name, centerX - name.length() * 3, centerY + 50 * scale);

        // Тип сови
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(new Font("Arial", 10));
        gc.fillText(type, centerX - type.length() * 2.5, centerY + 65 * scale);
    }

    // Оновлення відображення приналежності до макрооб'єкта
    public void updateMacroDisplay() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Очищуємо область з текстом про макрооб'єкт
        gc.clearRect(0, canvas.getHeight() - 15, canvas.getWidth(), 15);

        // Перемальовуємо текст
        gc.setFill(Color.DARKBLUE);
        gc.setFont(new Font("Arial", 8));
        String macroText = belongsToMacro != null ? "Macro: " + belongsToMacro.name : "Macro: None";
        gc.fillText(macroText, 5, canvas.getHeight() - 5);
    }
}
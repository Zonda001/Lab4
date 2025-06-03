package sekiro;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    ArrayList<Technique> techniques;

    // Статичні поля для текстур
    private static Map<String, Image> owlTextures = new HashMap<>();
    private static boolean texturesLoaded = false;

    // Статичний блок для завантаження текстур
    static {
        loadTextures();
    }

    private static void loadTextures() {
        try {
            // Спробуємо завантажити текстури з ресурсів
            String[] textureFiles = {
                    "/textures/Idle.PNG"
            };

            for (String textureFile : textureFiles) {
                try {
                    InputStream stream = Owl.class.getResourceAsStream(textureFile);
                    if (stream != null) {
                        Image image = new Image(stream);
                        String key = textureFile.substring(textureFile.lastIndexOf("/") + 1, textureFile.lastIndexOf("."));
                        owlTextures.put(key, image);
                        System.out.println("Завантажено текстуру: " + key);
                    }
                } catch (Exception e) {
                    System.out.println("Не вдалося завантажити текстуру: " + textureFile);
                }
            }

            texturesLoaded = true;
            System.out.println("Завантажено текстур: " + owlTextures.size());

        } catch (Exception e) {
            System.out.println("Помилка завантаження текстур: " + e.getMessage());
            texturesLoaded = true; // Встановлюємо true щоб не блокувати роботу
        }
    }

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

    public void drawOwl() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Спочатку намагаємося використати текстуру
        if (texturesLoaded && tryDrawWithTexture(gc)) {
            // Якщо текстура успішно намальована, додаємо тільки індикатори
            drawIndicators(gc);
            return;
        }

        // Якщо текстури немає або не завантажилася, малюємо вручну
        drawOwlManually(gc);
        drawIndicators(gc);
    }

    private boolean tryDrawWithTexture(GraphicsContext gc) {
        try {
            // Визначаємо ключ текстури на основі типу та рівня
            String textureKey = getTextureKey();
            Image texture = owlTextures.get(textureKey);

            if (texture != null && !texture.isError()) {
                // Малюємо текстуру, масштабуючи її під розмір canvas
                gc.drawImage(texture, 0, 0, canvas.getWidth(), canvas.getHeight());
                return true;
            }

            // Якщо основна текстура не знайдена, спробуємо загальну
            texture = owlTextures.get("Idle");
            if (texture != null && !texture.isError()) {
                gc.drawImage(texture, 0, 0, canvas.getWidth(), canvas.getHeight());
                return true;
            }

        } catch (Exception e) {
            System.out.println("Помилка при малюванні текстури: " + e.getMessage());
        }

        return false;
    }

    private String getTextureKey() {
        // Спочатку перевіряємо тип сови
        switch (type) {
            case "Великий Сова":
                return "owl_great";
            case "Нащадок Сови":
                return "owl_descendant";
            case "Сова":
            default:
                // Якщо тип стандартний, використовуємо рівень
                switch (skillLevel) {
                    case "Майстер":
                        return "owl_master";
                    case "Експерт":
                        return "owl_expert";
                    case "Новачок":
                        return "owl_novice";
                    default:
                        return "owl_regular";
                }
        }
    }

    private void drawOwlManually(GraphicsContext gc) {
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
    }

    private void drawIndicators(GraphicsContext gc) {
        // Індикатор кількості технік
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 10));
        gc.fillText("T:" + techniques.size(), 5, 25); // Було 12, тепер 25

        // Показуємо найпотужнішу техніку
        if (!techniques.isEmpty()) {
            Technique strongest = getStrongestTechnique();
            gc.setFill(getElementColor(strongest.element));
            gc.fillOval(65, 15, 12, 12); // Було 5, тепер 15
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 8));
            gc.fillText(String.valueOf(strongest.power), 69, 23); // Було 13, тепер 23
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
        // Створюємо нову сову з тими ж базовими параметрами
        Owl copy = new Owl(newName, this.type, this.hasShinobiTechniques, this.skillLevel, x, y);

        // Очищаємо згенеровані техніки і копіюємо оригінальні
        copy.techniques.clear();
        for (Technique technique : this.techniques) {
            copy.techniques.add(new Technique(technique)); // Глибинне копіювання кожної техніки
        }

        // Перемалювати для оновлення індикаторів
        copy.drawOwl();

        return copy;
    }

    // Статичний метод для перевірки чи завантажені текстури
    public static boolean areTexturesLoaded() {
        return texturesLoaded;
    }

    // Статичний метод для отримання інформації про завантажені текстури
    public static String getTexturesInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Завантажено текстур: ").append(owlTextures.size()).append("\n");
        for (String key : owlTextures.keySet()) {
            sb.append("- ").append(key).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return name + " [" + type + ", " + skillLevel + ", Технік: " + techniques.size() +
                ", Сила: " + getTotalPower() + "]";
    }
}
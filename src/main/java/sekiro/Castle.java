package sekiro;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Castle {
    String name;
    String castleType;
    Canvas canvas;
    ArrayList<Owl> owls;
    double x, y;

    // Система текстур
    private static Map<String, Image> textures = new HashMap<>();
    private static boolean texturesLoaded = false;
    private static boolean texturesLoading = false;
    private boolean readyToDraw = false;

    public Castle(String name, String castleType, double x, double y) {
        this.name = name;
        this.castleType = castleType;
        this.x = x;
        this.y = y;
        this.owls = new ArrayList<>();

        canvas = new Canvas(250, 200);

        // Спочатку завантажуємо текстури, потім малюємо
        loadTexturesIfNeeded(() -> {
            this.readyToDraw = true;
            drawCastle();
        });

        Main.group.getChildren().add(canvas);
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    // Статичний метод для завантаження всіх необхідних текстур
    private static void loadTexturesIfNeeded(Runnable onComplete) {
        if (texturesLoaded) {
            onComplete.run();
            return;
        }

        if (texturesLoading) {
            // Якщо текстури вже завантажуються, чекаємо
            new Thread(() -> {
                while (texturesLoading) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                onComplete.run();
            }).start();
            return;
        }

        texturesLoading = true;

        new Thread(() -> {
            try {
                // Список готових текстур замків
                String[] castleTextures = {
                        "asina_castle.png",      // Повна текстура замку Асіна
                        "hirata_estate.png",     // Повна текстура Хіру-ден
                        "senpou_temple.png",     // Повна текстура Верхнього Баштового Додзьо
                        "default_castle.png"     // Резервна текстура
                };

                for (String textureName : castleTextures) {
                    loadTexture(textureName);
                }

                texturesLoaded = true;
                texturesLoading = false;

                // Викликаємо callback у головному потоці JavaFX
                javafx.application.Platform.runLater(onComplete);

            } catch (Exception e) {
                System.err.println("Помилка завантаження текстур: " + e.getMessage());
                texturesLoaded = false;
                texturesLoading = false;
                // Навіть якщо текстури не завантажились, дозволяємо малювання
                javafx.application.Platform.runLater(onComplete);
            }
        }).start();
    }

    private static void loadTexture(String fileName) {
        try {
            InputStream stream = Castle.class.getResourceAsStream("/textures/castles/" + fileName);
            if (stream != null) {
                Image image = new Image(stream);
                textures.put(fileName, image);
                stream.close();
                System.out.println("Завантажено текстуру: " + fileName);
            } else {
                System.out.println("Текстура не знайдена: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("Помилка завантаження текстури " + fileName + ": " + e.getMessage());
        }
    }

    private void drawCastle() {
        if (!readyToDraw) {
            return; // Просто не малюємо нічого, поки текстури не завантажені
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Визначаємо яку текстуру використовувати
        String textureFileName = getCastleTextureFileName();
        Image castleTexture = textures.get(textureFileName);

        if (castleTexture != null && !castleTexture.isError()) {
            // Малюємо готову текстуру замку на весь canvas
            gc.drawImage(castleTexture, 0, 30, 250, 140);
        } else {
            // Якщо текстура не завантажилась, малюємо простий замок
            drawFallbackCastle(gc);
        }

        // Додаємо текстову інформацію поверх текстури
        drawCastleInfo(gc);
    }



    private String getCastleTextureFileName() {
        switch (castleType) {
            case "Замок Асіна":
                return "asina_castle.png";
            case "Хіру-ден":
                return "hirata_estate.png";
            case "Верхній Баштовий Додзьо":
                return "senpou_temple.png";
            default:
                return "default_castle.png";
        }
    }

    private void drawFallbackCastle(GraphicsContext gc) {
        // Резервне малювання, якщо текстури немає
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

        // Малюємо простий замок
        gc.setFill(castleColor);
        gc.fillRect(50, 100, 150, 80);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(50, 100, 150, 80);

        // Башти
        gc.fillRect(30, 80, 40, 100);
        gc.fillRect(180, 80, 40, 100);
        gc.strokeRect(30, 80, 40, 100);
        gc.strokeRect(180, 80, 40, 100);

        // Дахи
        gc.setFill(roofColor);
        double[] xTower1 = {25, 50, 75};
        double[] yTower1 = {80, 50, 80};
        gc.fillPolygon(xTower1, yTower1, 3);

        double[] xTower2 = {175, 200, 225};
        double[] yTower2 = {80, 50, 80};
        gc.fillPolygon(xTower2, yTower2, 3);

        double[] xRoof = {40, 125, 210};
        double[] yRoof = {100, 70, 100};
        gc.fillPolygon(xRoof, yRoof, 3);

        // Ворота та вікна
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(110, 140, 30, 40);
        gc.setFill(Color.YELLOW);
        gc.fillRect(70, 120, 15, 15);
        gc.fillRect(165, 120, 15, 15);
    }

    private void drawCastleInfo(GraphicsContext gc) {
        // Напівпрозорий фон для тексту
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(5, 5, 240, 25);
        gc.fillRect(5, 170, 240, 25);

        // Назва замку
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial Bold", 16));
        gc.fillText(name, 10, 22);

        // Тип замку
        gc.setFill(Color.LIGHTBLUE);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(castleType, 10, 185);

        // Кількість сов
        gc.setFill(Color.GOLD);
        gc.setFont(new Font("Arial Bold", 14));
        gc.fillText("🦉 Сов: " + owls.size(), 150, 185);
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
        if (readyToDraw) {
            drawCastle();
        }
    }

    // Метод для оновлення відображення (синонім для redraw)
    public void updateDisplay() {
        redraw();
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
            sb.append("\n   Тип: ").append(owl.type);
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

    // Статичний метод для перевірки стану завантаження текстур
    public static boolean areTexturesLoaded() {
        return texturesLoaded;
    }

    // Статичний метод для форсованого перезавантаження текстур
    public static void reloadTextures() {
        texturesLoaded = false;
        texturesLoading = false;
        textures.clear();
    }

    // Метод для отримання інформації про завантаження текстур
    public static String getTextureLoadingStatus() {
        if (texturesLoaded) {
            return "Текстури завантажені (" + textures.size() + " файлів)";
        } else if (texturesLoading) {
            return "Завантаження текстур...";
        } else {
            return "Текстури не завантажені";
        }
    }
}
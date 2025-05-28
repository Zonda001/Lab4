package sekiro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Technique {
    public String name;
    public String type;        // "Основна", "Shinobi", "Спеціальна"
    public String description;
    public int power;         // Сила техніки 1-10
    public String element;    // "Вогонь", "Вода", "Земля", "Повітря", "Тінь", "Світло"

    public Technique(String name, String type, String description, int power, String element) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.power = Math.max(1, Math.min(10, power)); // Обмежуємо 1-10
        this.element = element;
    }

    // Копіювальний конструктор
    public Technique(Technique other) {
        this.name = other.name;
        this.type = other.type;
        this.description = other.description;
        this.power = other.power;
        this.element = other.element;
    }

    @Override
    public String toString() {
        return name + " [" + type + ", " + element + ", Сила: " + power + "]";
    }

    public String getDetailedInfo() {
        return name + "\n" +
                "Тип: " + type + "\n" +
                "Елемент: " + element + "\n" +
                "Сила: " + power + "/10\n" +
                "Опис: " + description;
    }

    // Статичні методи для створення випадкових технік
    private static final List<String> BASIC_TECHNIQUES = Arrays.asList(
            "Удар кігтем", "Швидкий політ", "Гострий дзьоб", "Крик сови", "Нічне бачення"
    );

    private static final List<String> SHINOBI_TECHNIQUES = Arrays.asList(
            "Тіньовий удар", "Невидимість", "Клонування", "Телепортація", "Паралізуючий погляд",
            "Вогняний вихор", "Крижаний дощ", "Земляний щит", "Повітряна хвиля", "Світлова спалах"
    );

    private static final List<String> SPECIAL_TECHNIQUES = Arrays.asList(
            "Дракон сови", "Фенікс відродження", "Тисяча пір'я", "Часовий розрив", "Астральна проекція"
    );

    private static final List<String> ELEMENTS = Arrays.asList(
            "Вогонь", "Вода", "Земля", "Повітря", "Тінь", "Світло"
    );

    private static final List<String> BASIC_DESCRIPTIONS = Arrays.asList(
            "Базова атака кігтями з великою точністю",
            "Швидкий рух, що дозволяє уникнути атак",
            "Потужний удар дзьобом по ворогу",
            "Оглушуючий крик, що лякає противників",
            "Покращене бачення в темряві"
    );

    private static final List<String> SHINOBI_DESCRIPTIONS = Arrays.asList(
            "Атака з тіні з подвійною силою",
            "Тимчасова невидимість для ворогів",
            "Створення кількох копій себе",
            "Миттєве переміщення на короткі відстані",
            "Погляд, що парализує ворога страхом",
            "Вихор полум'я навколо сови",
            "Крижаний дощ з неба",
            "Захисний щит з каменю",
            "Потужна хвиля повітря",
            "Сліпучий спалах світла"
    );

    private static final List<String> SPECIAL_DESCRIPTIONS = Arrays.asList(
            "Легендарна техніка у вигляді дракона",
            "Відродження після смертельної рани",
            "Атака тисячами магічних пір'я",
            "Розрив у часі для зміни подій",
            "Вихід духу з тіла для розвідки"
    );

    public static Technique generateRandomBasicTechnique(Random rnd) {
        String name = BASIC_TECHNIQUES.get(rnd.nextInt(BASIC_TECHNIQUES.size()));
        String element = ELEMENTS.get(rnd.nextInt(ELEMENTS.size()));
        String description = BASIC_DESCRIPTIONS.get(rnd.nextInt(BASIC_DESCRIPTIONS.size()));
        int power = rnd.nextInt(3) + 2; // 2-4 для базових

        return new Technique(name, "Основна", description, power, element);
    }

    public static Technique generateRandomShinobiTechnique(Random rnd) {
        String name = SHINOBI_TECHNIQUES.get(rnd.nextInt(SHINOBI_TECHNIQUES.size()));
        String element = ELEMENTS.get(rnd.nextInt(ELEMENTS.size()));
        String description = SHINOBI_DESCRIPTIONS.get(rnd.nextInt(SHINOBI_DESCRIPTIONS.size()));
        int power = rnd.nextInt(4) + 5; // 5-8 для shinobi

        return new Technique(name, "Shinobi", description, power, element);
    }

    public static Technique generateRandomSpecialTechnique(Random rnd) {
        String name = SPECIAL_TECHNIQUES.get(rnd.nextInt(SPECIAL_TECHNIQUES.size()));
        String element = ELEMENTS.get(rnd.nextInt(ELEMENTS.size()));
        String description = SPECIAL_DESCRIPTIONS.get(rnd.nextInt(SPECIAL_DESCRIPTIONS.size()));
        int power = rnd.nextInt(3) + 8; // 8-10 для спеціальних

        return new Technique(name, "Спеціальна", description, power, element);
    }

    public static ArrayList<Technique> generateRandomTechniques(boolean hasShinobiTechniques,
                                                                String skillLevel, Random rnd) {
        ArrayList<Technique> techniques = new ArrayList<>();

        // Базові техніки (завжди є)
        int basicCount = 1 + rnd.nextInt(2); // 1-2 базові
        for (int i = 0; i < basicCount; i++) {
            techniques.add(generateRandomBasicTechnique(rnd));
        }

        // Shinobi техніки (якщо є shinobi здібності)
        if (hasShinobiTechniques) {
            int shinobiCount = 1 + rnd.nextInt(3); // 1-3 shinobi
            for (int i = 0; i < shinobiCount; i++) {
                techniques.add(generateRandomShinobiTechnique(rnd));
            }
        }

        // Спеціальні техніки (залежать від рівня)
        switch (skillLevel) {
            case "Майстер":
                if (rnd.nextBoolean()) { // 50% шанс
                    techniques.add(generateRandomSpecialTechnique(rnd));
                }
                break;
            case "Експерт":
                if (rnd.nextInt(4) == 0) { // 25% шанс
                    techniques.add(generateRandomSpecialTechnique(rnd));
                }
                break;
            // Новачки та Учні не мають спеціальних технік
        }

        return techniques;
    }

    // Метод для копіювання списку технік
    public static ArrayList<Technique> copyTechniques(ArrayList<Technique> original) {
        ArrayList<Technique> copy = new ArrayList<>();
        for (Technique technique : original) {
            copy.add(new Technique(technique));
        }
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Technique technique = (Technique) obj;
        return power == technique.power &&
                name.equals(technique.name) &&
                type.equals(technique.type) &&
                element.equals(technique.element);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode() + element.hashCode() + power;
    }
}
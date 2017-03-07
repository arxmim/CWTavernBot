package org.nia.model.lists;

/**
 * @author Иван, 27.02.2017.
 */
public enum Resource {
    THREAD("Нитки", 2),
    BRANCH("Ветки", 2),
    SKIN("Шкура", 3),
    ANIMAL_SKIN("Шкура животного", 3),
    BONE("Кость", 3),
    ANIMAL_BONE("Кость животного", 3),
    COAL("Уголь", 3),
    CHARCOAL("Древесный уголь", 3),
    DUST("Порошок", 4),
    IRON_ORE("Железная руда", 4),
    DENSE_TEXTURE("Плотная ткань", 4),
    SILVER_ORE("Серебряная руда", 6),
    ALUM_ORE("Алюминиевая руда", 15),
    PHILOSOPHER_STONE("Философский камень", 15),
    MITHRIL_ORE("Мифриловая руда", 15),
    ADAMANTITE_ORE("Адамантитовая руда", 20),
    SAPPHIRE("Сапфир", 40),
    SOLVENT("Растворитель", 60),
    RUBY("Рубин", 80),
    THICKENER("Загуститель", 100),

    STEEL("Сталь", 25),
    BONE_ASH("Костяная пудра", 16),
    CLEANED_ASH("Очищенная пудра", 60),
    LEATHER("Кожа", 10),
    STRING("Шнурок", 35),
    ROPE("Веревка", 9),
    CHARK("Кокс", 13),
    SILVER_ALLOY("Серебряный сплав", 100),
    METAL_SHEET("Металлический лист", 60),
    METAL_FIBER("Металлическое волокно", 120),
    THREATED_LEATHER("Обработанная кожа", 80),
    STEEL_BILLET("Стальная заготовка", 55),
    STEEL_THREAD("Стальная нить", 12),
    CHEST("Сундучок", 1),

    HELP("Мат. помощь", -1);
    private String name;
    private int price;

    Resource(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public static Resource getByName(String name) {
        for (Resource ct : values()) {
            if (ct.getName().equals(name)) {
                return ct;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

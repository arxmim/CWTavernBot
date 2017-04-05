package org.nia.logic.lists.items;

/**
 * @author IANazarov
 */
public enum EQuestItem implements Item {
    GOAT_CHEESE("Козий сыр", "Козий сыр с местного рынка") {
        @Override
        public String getMeasure() {
            return "/ 4";
        }
    },
    STONE_CHEESE("Твердый сыр", "Твердый сыр из Литограда") {
        @Override
        public String getMeasure() {
            return "/ 4";
        }
    },
    BLUE_CHEESE("Голубой сыр", "Элитный вонючий сыр из дальних стран") {
        @Override
        public String getMeasure() {
            return "/ 4";
        }
    },
    POTATO("Картошка", "Обычная картошка"),
    TOMATO("Помидор", "Обычный помидор"),
    REDIS("Редис", "Обычный редис"),
    APPLE("Яблоко", "Спелое яблоко из садов Эдема"),
    PLUM("Слива", "Слива с ближайшей фермы"),
    PEAR("Груша", "Груша с предгорий гномьего царства"),
    BAY_LEAF("Лавровый лист", "Свежий лавровый лист"),
    OLIVE_SOUCE("Оливковый соус", "Фирменный оливковый соус"),
    BASIL_SOUCE("Соус из базилика", "Склянка с соусом из базилика"),;
    private String name;
    private String desc;

    EQuestItem(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FOOD;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getMeasure() {
        return "штуки";
    }
}

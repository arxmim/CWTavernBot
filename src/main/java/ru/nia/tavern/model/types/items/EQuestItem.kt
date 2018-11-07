package ru.nia.tavern.model.types.items

/**
 * @author IANazarov
 */
enum class EQuestItem private constructor(override val name: String, override val desc: String) : Item {
    GOAT_CHEESE("Козий сыр", "Козий сыр с местного рынка") {
        override val measure: String
            get() = "/ 4"
    },
    STONE_CHEESE("Твердый сыр", "Твердый сыр из Литограда") {
        override val measure: String
            get() = "/ 4"
    },
    BLUE_CHEESE("Голубой сыр", "Элитный вонючий сыр из дальних стран") {
        override val measure: String
            get() = "/ 4"
    },
    POTATO("Картошка", "Обычная картошка"),
    TOMATO("Помидор", "Обычный помидор"),
    REDIS("Редис", "Обычный редис"),
    APPLE("Яблоко", "Спелое яблоко из садов Эдема"),
    PLUM("Слива", "Слива с ближайшей фермы"),
    PEAR("Груша", "Груша с предгорий гномьего царства"),
    BAY_LEAF("Лавровый лист", "Свежий лавровый лист"),
    OLIVE_SOUCE("Оливковый соус", "Фирменный оливковый соус"),
    BASIL_SOUCE("Соус из базилика", "Склянка с соусом из базилика");

    override val itemType: Item.ItemType
        get() = Item.ItemType.FOOD

    open val measure: String
        get() = "штуки"
}

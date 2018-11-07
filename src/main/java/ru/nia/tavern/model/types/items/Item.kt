package ru.nia.tavern.model.types.items

/**
 * @author IANazarov
 */
interface Item {


    val itemType: ItemType
    val name: String
    val desc: String

    enum class ItemType {
        FOOD,
        EQUIPMENT,
        QUEST
    }
}

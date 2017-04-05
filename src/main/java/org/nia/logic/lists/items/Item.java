package org.nia.logic.lists.items;

/**
 * @author IANazarov
 */
public interface Item {


    ItemType getItemType();
    String getName();
    String getDesc();

    enum ItemType {
        FOOD,
        EQUIPMENT,
        QUEST
    }
}

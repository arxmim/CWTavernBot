package org.nia.logic.lists.items;

/**
 * @author IANazarov
 */
public enum QuestItem implements Item {
    GOAT_CHEESE("", "")
    ;
    private String name;
    private String desc;

    QuestItem(String name, String desc) {
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
}

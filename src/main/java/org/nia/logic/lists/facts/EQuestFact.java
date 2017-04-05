package org.nia.logic.lists.facts;

/**
 * @author Иван, 06.04.2017.
 */
public enum EQuestFact implements Fact {
    KITCHEN_ELVEN_SHAURMA("", "");
    private String name;
    private String desc;

    EQuestFact(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}

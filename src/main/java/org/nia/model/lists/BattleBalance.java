package org.nia.model.lists;

/**
 * @author IANazarov
 */
public enum BattleBalance {
    EASY_ATK("со значительным преимуществом"),
    ATK("успешно атаковали защитников"),
    ATK_FORT("В атаке принимали участие"),
    HARD_ATK("разыгралась настоящая бойня, но всё-таки силы атакующих были чуть сильнее"),
    HARD_DEF("силы были почти равны, но защитники героически отразили атаку"),
    DEF("успешно отбились от"),
    EASY_DEF("защитники легко отбились от жалкой горстки");
    private String mark;

    BattleBalance(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }


    public static BattleBalance byMark(String mark) {
        for (BattleBalance bb : values()) {
            if (bb.getMark().equals(mark)) {
                return bb;
            }
        }
        return null;
    }
}

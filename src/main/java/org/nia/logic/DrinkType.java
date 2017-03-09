package org.nia.logic;

/**
 * @author Иван, 08.03.2017.
 */
public enum DrinkType {
    BEER("/bunt", "кружка золотистого пива \"БУНТ\"", "кружки БУНТА", "кружку БУНТА"),
    RED_POWER("/redpower", "бокал коктейля \"Red Power\"", "бокала \"Red Power\"", "бокал \"Red Power\""),
    AVE_WHITE("/ave_white", "жбан ядреной нордической сивухи", "жбана сивухи", "жбан сивухи"),
    GHOST("/ghost", "стопка водки \"Призрак коммунизма\"", "стопки водки", "стопку водки"),
    MORDOR("/mordor", "бокал \"Магма Ородруина\"", "бокала \"Магмы Ородруина\"", "бокал \"Магмы Ородруина\""),
    CHLEN("/chlen", "бокал коктейля \"Блю Куросао\"", "бокала \"Блю Куросао\"", "бокал \"Блю Куросао\"");
    private final String command;
    private final String onGive;
    private final String onDrink;
    private final String onThrow;

    DrinkType(String command, String onGive, String onDrink, String onThrow) {
        this.command = command;
        this.onGive = onGive;
        this.onDrink = onDrink;
        this.onThrow = onThrow;
    }

    public String getCommand() {
        return command;
    }

    public String getOnGive() {
        return onGive;
    }

    public String getOnDrink() {
        return onDrink;
    }

    public String getOnThrow() {
        return onThrow;
    }
}

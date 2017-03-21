package org.nia.logic.quests;

import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public interface IQuestStep {


    public String getText();

    public abstract String getName();

    public abstract List<IQuestStep> getNext();
    public abstract String getCommand();
    public abstract String getGoodText();
    public abstract String getBadText();
    public default boolean isWin() {
        return new Random().nextInt(101) > 70;
    }
}

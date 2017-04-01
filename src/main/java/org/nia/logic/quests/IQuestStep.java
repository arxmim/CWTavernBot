package org.nia.logic.quests;

import org.nia.model.Quest;
import org.nia.model.QuestEvent;

import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public interface IQuestStep {

    IQuestEvent getIQuest();

    String getText();

    String getName();

    List<IQuestStep> getNext();

    String getCommand(String formatParam);

    String getGoodText();

    String getBadText();

    default void doWork(QuestEvent questEvent) {}

    default boolean isWin(QuestEvent questEvent) {
        return new Random().nextInt(101) < questEvent.getWinChance();
    }

    default String getInterceptText() {
        return "";
    }

}

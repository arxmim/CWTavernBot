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

    String getText(Quest quest);

    String getName();

    List<IQuestStep> getNext(Quest quest);

    String getCommand(String formatParam);

    String getGoodText(Quest quest);

    String getBadText(Quest quest);

    default void doWork(QuestEvent questEvent) {}
    default void doFinal(QuestEvent questEvent) {}

    default boolean isWin(QuestEvent questEvent) {
        return new Random().nextInt(100) < questEvent.getWinChance();
    }

    default String getInterceptText() {
        return "";
    }

}

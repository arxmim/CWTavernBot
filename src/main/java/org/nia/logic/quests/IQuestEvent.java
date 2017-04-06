package org.nia.logic.quests;

import org.nia.model.Quest;

/**
 * @author IANazarov
 */
public interface IQuestEvent {

    String getName();

    default void init(Quest quest) { }

    IQuestStep getInit();
    default boolean canBeRandomed() {
        return true;
    }

    IQuestStep getQuestStep(String questStep);

    default int getReward() {
        return 7;
    }
    QuestsEnum getQuestsEnum();
}

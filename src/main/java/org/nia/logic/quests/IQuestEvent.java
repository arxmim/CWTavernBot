package org.nia.logic.quests;

/**
 * @author IANazarov
 */
public interface IQuestEvent {

    public String getName();
    public IQuestStep getInit();

    IQuestStep getQuestStep(String questStep);

    public default int getReward() {
        return 10;
    }
}

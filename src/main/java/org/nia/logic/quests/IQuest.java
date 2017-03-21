package org.nia.logic.quests;


/**
 * @author IANazarov
 */
public interface IQuest {
    public String getStart();

    public IQuestEvent getRandomEvent();

    IQuestEvent getEvent(String event);
}

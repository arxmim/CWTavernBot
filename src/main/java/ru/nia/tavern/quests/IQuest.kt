package ru.nia.tavern.quests

/**
 * @author IANazarov
 */
interface IQuest {
    val start: String

    val randomEvent: IQuestEvent

    fun getEvent(event: String): IQuestEvent
}

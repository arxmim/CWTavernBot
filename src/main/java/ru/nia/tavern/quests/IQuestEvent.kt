package ru.nia.tavern.quests

import ru.nia.tavern.model.Quest


/**
 * @author IANazarov
 */
interface IQuestEvent {

    val name: String

    val init: IQuestStep

    val reward: Int
        get() = 6
    val questsEnum: QuestsEnum

    open fun init(quest: Quest) {}
    open fun canBeRandomed(): Boolean {
        return true
    }

    fun getQuestStep(questStep: String): IQuestStep
}

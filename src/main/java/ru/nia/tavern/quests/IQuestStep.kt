package ru.nia.tavern.quests

import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import java.util.*

/**
 * @author IANazarov
 */
interface IQuestStep {

    val iQuest: IQuestEvent

    val name: String

    val interceptText: String
        get() = ""

    fun getText(quest: Quest): String

    fun getNext(quest: Quest): List<IQuestStep>

    fun getCommand(formatParam: String): String

    fun getGoodText(quest: Quest): String

    fun getBadText(quest: Quest): String

    open fun doWork(questEvent: QuestEvent) {}
    open fun doFinal(questEvent: QuestEvent) {}

    fun isWin(questEvent: QuestEvent): Boolean {
        return Random().nextInt(100) < questEvent.winChance
    }

}

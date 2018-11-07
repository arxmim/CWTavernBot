package ru.nia.tavern.quests

import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import java.util.*
import java.util.stream.Collectors

/**
 * @author IANazarov
 */
interface ICrossQuestStep : IQuestStep {

    val goodInactiveText: String
        get() = "Твой коллега ничего не предпринял, так что ты выставил его крайним и смотался."

    val badInactiveText: String
        get() = "Ты ничего не предпринял и всё пошло наперекосяк."

    val isWaitUser: Boolean
        get() = false

    val isButtonWithUser: Boolean
        get() = false

    val actionList: List<IQuestAction>

    override fun getGoodText(quest: Quest): String {
        return goodInactiveText
    }

    override fun getBadText(quest: Quest): String {
        return badInactiveText
    }

    override fun getText(quest: Quest): String {
        return ""
    }

    fun getProducedAction(event: QuestEvent): IQuestAction? {
        val actionList = actionList
        if (actionList.isEmpty()) {
            return null
        } else {
            val isFinal = !isWaitUser && getNext(event.quest).isEmpty()
            if (!isFinal) {
                return actionList[Random().nextInt(actionList.size)]
            } else if (isWin(event)) {
                val winList = actionList.stream()
                        .filter(Predicate<IQuestAction> { it.isWin() })
                        .collect<List<IQuestAction>, Any>(Collectors.toList())
                return if (winList.isEmpty()) {
                    null
                } else {
                    winList[Random().nextInt(winList.size)]
                }
            } else {
                val loseList = actionList.stream()
                        .filter(Predicate<IQuestAction> { it.isLose() })
                        .collect<List<IQuestAction>, Any>(Collectors.toList())
                return if (loseList.isEmpty()) {
                    null
                } else {
                    loseList[Random().nextInt(loseList.size)]
                }
            }
        }
    }
}

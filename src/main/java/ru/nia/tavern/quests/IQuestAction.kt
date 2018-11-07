package ru.nia.tavern.quests

import org.apache.commons.lang3.time.DateUtils
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.service.CWTavernBot
import java.util.*

/**
 * @author IANazarov
 */
interface IQuestAction {
    val isWin: Boolean

    val isLose: Boolean

    val explainText: String

    val eventText: String

    val fromEndText: String

    val toEndText: String

    val moveToStep: IQuestStep?

    fun doWork(from: QuestEvent, to: QuestEvent)

    fun initAction(from: QuestEvent): Boolean {
        val BOT = CWTavernBot.INSTANCE
        val randomActive = Quest.getRandomActive(this.moveToStep!!.iQuest.questsEnum)
        if (randomActive != null) {
            val linkedEvent = QuestEvent()
            linkedEvent.eventTime = DateUtils.addMinutes(Date(), -1)
            linkedEvent.quest = randomActive
            linkedEvent.linkedQuestEvent = from
            linkedEvent.setStep(this.moveToStep)
            linkedEvent.setIQuestEvent(this.moveToStep!!.iQuest)
            linkedEvent.save()
            from.linkedQuestEvent = linkedEvent
            randomActive.eventTime = linkedEvent.eventTime
            randomActive.save()
            try {
                BOT.sendMessage(ServingMessage.getTimedMessage(from.quest.user, String.format(from.step.interceptText, randomActive.user)))
                applyAction(from, linkedEvent)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

            return true
        } else {
            return false
        }
    }

    fun applyAction(from: QuestEvent, to: QuestEvent) {
        doWork(from, to)
        from.step.doWork(from)
        to.step.doWork(to)
        if (this.moveToStep != null) {
            to.setStep(this.moveToStep)
            to.save()
        }
        var ending = ""
        if (this.isWin || this.isLose) {
            if (this.isWin) {
                from.win = true
                to.win = true
                ending = "\n\nУдачное решение! Твоя награда за задание будет увеличена."
            } else if (this.isLose) {
                from.win = false
                to.win = false
                ending = "\n\nОчень жаль! Твоя награда за задание будет уменьшена."
            }
            from.step.doFinal(from)
            to.step.doFinal(to)
            from.quest.eventTime = from.quest.questEnum.getNextEventTime(from.quest)
            to.quest.eventTime = to.quest.questEnum.getNextEventTime(to.quest)
            from.save()
            to.save()
            from.quest.save()
            to.quest.save()
        }
        val BOT = CWTavernBot.INSTANCE
        val fromUser = from.quest.user
        val toUser = to.quest.user
        try {
            BOT.sendMessage(ServingMessage.getTimedMessage(fromUser, this.eventText + "\n" + String.format(this.fromEndText, toUser) + ending))
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

        try {
            BOT.sendMessage(ServingMessage.getTimedMessage(toUser, String.format(this.explainText, fromUser) + "\n"
                    + this.eventText + "\n"
                    + String.format(this.toEndText, fromUser) + ending))
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

    }
}

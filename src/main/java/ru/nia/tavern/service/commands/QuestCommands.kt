package ru.nia.tavern.service.commands

import org.telegram.telegrambots.api.objects.Message
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.User
import ru.nia.tavern.quests.ICrossQuestStep
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
class QuestCommands(private val user: User, private val quest: Quest, private val event: QuestEvent?) : Commands {

    override fun apply(message: Message, from: User): String {
        var res = ""
        val first = getNextStep(message.text)
        if (first.isPresent) {
            val iQuestStep = first.get()
            event!!.step = iQuestStep
            event.eventTime = Date()
            event.save()
            var actionTriggered = false
            val linkedEvent = event.linkedQuestEvent
            if (iQuestStep is ICrossQuestStep) {
                val producedAction = iQuestStep.getProducedAction(event)
                if (linkedEvent != null) {
                    producedAction!!.applyAction(event, linkedEvent)
                    actionTriggered = true
                } else if (producedAction != null) {
                    actionTriggered = producedAction.initAction(event)
                }
            }
            if (!actionTriggered) {
                iQuestStep.doWork(event)
                if (iQuestStep.getNext(quest).isEmpty()) {
                    val win = iQuestStep.isWin(event)
                    event.win = win
                    iQuestStep.doFinal(event)
                    if (win) {
                        res = iQuestStep.getGoodText(quest) + "\n\nУдачное решение! Твоя награда за задание будет увеличена."
                    } else {
                        res = iQuestStep.getBadText(quest) + "\n\nОчень жаль! Твоя награда за задание будет уменьшена."
                    }
                    quest.eventTime = quest.questEnum.getNextEventTime(quest)
                    quest.save()
                } else {
                    res = iQuestStep.getText(quest)
                }
            }
            event.save()
        }
        return res
    }

    override fun isApplicable(message: Message, from: User): Boolean {
        return !(event == null || event.step.getNext(quest).isEmpty()) && getNextStep(message.text).isPresent
    }

    private fun getNextStep(text: String): Optional<IQuestStep> {
        var formatParam = ""
        if (event!!.linkedQuestEvent != null) {
            formatParam = event.linkedQuestEvent!!.quest.user.toString()
        }
        val formatParamFinal = formatParam
        return event.step.getNext(quest).stream().filter { qs ->
            if (qs is ICrossQuestStep && qs.isButtonWithUser) {
                return@event.getStep().getNext(quest).stream().filter qs . getCommand formatParamFinal == text
            } else {
                return@event.getStep().getNext(quest).stream().filter qs . getCommand "" == text
            }
        }.findFirst()

    }
}

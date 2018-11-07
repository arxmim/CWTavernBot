package ru.nia.tavern.model.types

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.CallbackQuery
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Dancing
import ru.nia.tavern.model.User
import ru.nia.tavern.service.CWTavernBot
import ru.nia.tavern.service.SendMessageService
import java.util.*

/**
 * @author IANazarov
 */
enum class DanceStep private constructor(val danceName: String) {
    FOO1("\"Название танца 1\"") {

        override val stepActions: List<DanceActionList>
            get() = Arrays.asList(DanceActionList.FOO1_1, DanceActionList.FOO1_2, DanceActionList.FOO1_3, DanceActionList.FOO1_4)

        override fun hasNextStep(dancing: Dancing): Boolean {
            return true
        }

        override fun nextStep(dancing: Dancing): DanceStep {
            return FOO2
        }

        override fun getStepText(dancing: Dancing): String {
            return dancing.firstDancer.toString() + " и " + dancing.secondDancer + " стоят друг перед другом, " +
                    "начинает играть легкая музыка. Сперва " + dancing.firstDancer + " должен " +
                    DanceActionList.FOO1_3 + ", ну а потом его партнер должен " + DanceActionList.FOO1_2
        }

        override fun getNextAfter(lastDanceAction: DanceAction?): DanceAction? {
            if (lastDanceAction == null) {
                return DanceAction(DanceActionList.FOO1_3, true)
            }
            return if (lastDanceAction == DanceAction(DanceActionList.FOO1_3, true)) {
                DanceAction(DanceActionList.FOO1_2, false)
            } else null
        }
    },
    FOO2("\"Название танца 1\"") {

        override val stepActions: List<DanceActionList>
            get() = Arrays.asList(DanceActionList.FOO2_1, DanceActionList.FOO2_2, DanceActionList.FOO2_3, DanceActionList.FOO2_4)

        override fun hasNextStep(dancing: Dancing): Boolean {
            return false
        }

        override fun nextStep(dancing: Dancing): DanceStep? {
            return null
        }

        override fun getStepText(dancing: Dancing): String {
            return dancing.firstDancer.toString() + " и " + dancing.secondDancer + " уже неплохо отжигают! Сейчас " +
                    dancing.firstDancer + " должен " + DanceActionList.FOO2_2 + " и " +
                    DanceActionList.FOO2_1 + ", ну а потом его партнер должен " + DanceActionList.FOO2_4
        }

        override fun getNextAfter(lastDanceAction: DanceAction?): DanceAction? {
            if (lastDanceAction == null) {
                return DanceAction(DanceActionList.FOO2_2, true)
            }
            if (lastDanceAction == DanceAction(DanceActionList.FOO2_2, true)) {
                return DanceAction(DanceActionList.FOO2_1, true)
            }
            return if (lastDanceAction == DanceAction(DanceActionList.FOO2_1, true)) {
                DanceAction(DanceActionList.FOO2_4, false)
            } else null
        }
    };

    val stepDuration: Int

    abstract val stepActions: List<DanceActionList>

    init {
        this.stepDuration = 15
    }

    fun getInitialSendMessage(dancing: Dancing): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(SendMessageService.tavernChatID)
        sendMessage.enableHtml(true)
        sendMessage.text = getStepText(dancing)
        val inlineKeyboardMarkup = getInlineKeyboardMarkup(dancing)
        sendMessage.replyMarkup = inlineKeyboardMarkup
        return sendMessage
    }

    private fun getInlineKeyboardMarkup(dancing: Dancing): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()
        for (action in stepActions) {
            val button = InlineKeyboardButton()
            button.text = action.actionName
            button.callbackData = "dance," + action.name + "," + dancing.publicID
            val row = ArrayList<InlineKeyboardButton>()
            row.add(button)
            keyboard.add(row)
        }
        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }


    fun getTimedFailMessage(dancing: Dancing): SendMessage {
        return ServingMessage.getTournamentMessage("Танцоры немного постояли на сцене, сделали пару неловких движений, " +
                "но ничего приличного так и не показали. Позор вам, " + dancing.firstDancer + ", " +
                dancing.secondDancer + "!")
    }

    fun getFailMessage(failer: User, second: User): SendMessage {
        return ServingMessage.getTournamentMessage("Вместо танца " + failer + " оттоптал ноги своему партнеру, и вся " +
                "магия танца была разрушена... " + second + " должен быть крайне разочарован этим растяпой!")
    }

    fun getSuccessMessage(dancing: Dancing): SendMessage {
        return ServingMessage.getTournamentMessage("Поразительно! Эти ребята отлично танцуют! Так держать, " +
                dancing.firstDancer + " и " + dancing.secondDancer + ", ваше выступление было потрясающим!")
    }

    abstract fun hasNextStep(dancing: Dancing): Boolean

    abstract fun nextStep(dancing: Dancing): DanceStep

    abstract fun getStepText(dancing: Dancing): String

    abstract fun getNextAfter(lastDanceAction: DanceAction): DanceAction

    class DanceAction(internal var dal: DanceActionList, internal var isFirst: Boolean) {

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o !is DanceAction) return false

            val that = o as DanceAction?

            return if (isFirst != that!!.isFirst) false else dal == that.dal

        }

        override fun hashCode(): Int {
            var result = dal.hashCode()
            result = 31 * result + if (isFirst) 1 else 0
            return result
        }
    }

    companion object {

        fun processCallbackQuery(callbackQuery: CallbackQuery) {
            val split = callbackQuery.data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dancing = Dancing.getByID(Dancing::class.java, Integer.valueOf(split[2]))
            val userID = callbackQuery.from.id
            val dancer: User
            val second: User
            val isFirst: Boolean
            if (dancing!!.completed != null) {
                val answerCallbackQuery = AnswerCallbackQuery()
                answerCallbackQuery.callbackQueryId = callbackQuery.id
                answerCallbackQuery.text = "Танец уже закончился!"
                val editMessageReplyMarkup = EditMessageReplyMarkup()
                editMessageReplyMarkup.messageId = callbackQuery.message.messageId
                editMessageReplyMarkup.setChatId(callbackQuery.message.chatId!!)
                try {
                    CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                try {
                    CWTavernBot.INSTANCE.editMessageReplyMarkup(editMessageReplyMarkup)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                return
            }
            if (dancing.firstDancer.userID != userID && dancing.secondDancer.userID != userID) {
                val answerCallbackQuery = AnswerCallbackQuery()
                answerCallbackQuery.callbackQueryId = callbackQuery.id
                answerCallbackQuery.text = "Не мешай людям танцевать!"
                try {
                    CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                return
            } else if (dancing.firstDancer.userID == userID) {
                dancer = dancing.firstDancer
                second = dancing.secondDancer
                isFirst = true
            } else {
                isFirst = false
                dancer = dancing.secondDancer
                second = dancing.firstDancer
            }
            val dal = DanceActionList.valueOf(split[1])
            val action = DanceAction(dal, isFirst)
            val nextAction = dancing.nextAction
            if (nextAction == null) {
                val answerCallbackQuery = AnswerCallbackQuery()
                answerCallbackQuery.callbackQueryId = callbackQuery.id
                answerCallbackQuery.text = "Эта часть танца уже закончилась!"

                val editMessageReplyMarkup = EditMessageReplyMarkup()
                editMessageReplyMarkup.messageId = callbackQuery.message.messageId
                editMessageReplyMarkup.setChatId(callbackQuery.message.chatId!!)
                try {
                    CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                try {
                    CWTavernBot.INSTANCE.editMessageReplyMarkup(editMessageReplyMarkup)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                return
            } else if (nextAction == action) {
                dancing.lastActionFromFirst = isFirst
                dancing.setLastDanceAction(dal)
                dancing.save()
                val hasNext = dancing.nextAction != null
                val editMessageText = EditMessageText()
                editMessageText.messageId = callbackQuery.message.messageId
                editMessageText.setChatId(callbackQuery.message.chatId!!)
                if (hasNext) {
                    val inlineKeyboardMarkup = dancing.currentStep.getInlineKeyboardMarkup(dancing)
                    editMessageText.replyMarkup = inlineKeyboardMarkup
                    val text = callbackQuery.message.text + "\n\n" + dancer + " " + dal.doName() + "..."
                    editMessageText.text = text
                } else {
                    val text = callbackQuery.message.text + "\n\n" + dancer + " " + dal.doName() + " и закончил эту часть танца!"
                    editMessageText.text = text
                }
                val answerCallbackQuery = AnswerCallbackQuery()
                answerCallbackQuery.callbackQueryId = callbackQuery.id
                answerCallbackQuery.text = "Правильно!"
                try {
                    CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                try {
                    CWTavernBot.INSTANCE.editMessageText(editMessageText)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                return
            } else {
                dancing.completed = false
                dancing.save()
                dancer.danceWithUserID = null
                second.danceWithUserID = null
                dancer.save()
                second.save()
                val editMessageText = EditMessageText()
                editMessageText.messageId = callbackQuery.message.messageId
                editMessageText.setChatId(callbackQuery.message.chatId!!)
                val text = callbackQuery.message.text + "\n\n" + dancer + " запорол танец!"
                editMessageText.text = text
                val answerCallbackQuery = AnswerCallbackQuery()
                answerCallbackQuery.callbackQueryId = callbackQuery.id
                answerCallbackQuery.text = "Ты запорол танец!"
                val failMessage = dancing.currentStep.getFailMessage(dancer, second)
                try {
                    CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                try {
                    CWTavernBot.INSTANCE.editMessageText(editMessageText)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                try {
                    CWTavernBot.INSTANCE.sendMessage(failMessage)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                return
            }

        }
    }
}

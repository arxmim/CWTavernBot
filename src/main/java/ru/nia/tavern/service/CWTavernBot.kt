package ru.nia.tavern.service

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.nia.PropertiesLoader
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.CallbackQuery
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.logging.BotLogger
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.Tournament
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.types.DanceStep
import ru.nia.tavern.model.types.DrinkType
import ru.nia.tavern.quests.ICrossQuestStep
import ru.nia.tavern.service.commands.Commands
import ru.nia.tavern.service.commands.PersonalCommands
import ru.nia.tavern.service.commands.QuestCommands
import ru.nia.tavern.service.commands.TavernCommands
import java.util.*

/**
 * @author IANazarov
 */
class CWTavernBot : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        try {
            if (update.hasMessage()) {
                val message = update.message
                if (Date(message.date as Long * 1000).before(DateUtils.addMinutes(Date(), -3))) {
                    return
                }
                if (message.hasText() || message.hasLocation()) {
                    handleIncomingMessage(message)
                } else {
                    val newChatMembers = message.newChatMembers
                    if (newChatMembers != null) {
                        for (newChatMember in newChatMembers) {
                            val user = ru.nia.tavern.model.User.getFromMessage(newChatMember)
                            if (user.lastDrinkTime != null && user.lastDrinkTime!!.after(DateUtils.addMinutes(Date(), -20))) {
                                sendMessage(TavernCommands.GIVE.getMessage(message, user + ", ты либо сидишь в таверне, либо уходишь, хватит бегать туда-сюда!"))
                            } else {
                                user.alkoCount = 2
                                val rand = Random().nextInt(DrinkType.values().size)
                                val drinkType = DrinkType.values()[rand]
                                user.setDrinkType(drinkType)
                                user.save()
                                val answer = String.format(drinkType.enterPhrase, user)
                                sendMessage(TavernCommands.GIVE.getMessage(message, answer))
                            }
                        }
                    }
                }
            } else if (update.hasCallbackQuery()) {
                handleIncomingCallback(update.callbackQuery)
            }
        } catch (e: Exception) {
            BotLogger.error(LOGTAG, e)
        }

    }

    private fun handleIncomingCallback(callbackQuery: CallbackQuery) {
        if (callbackQuery.message != null && callbackQuery.message.from.userName == CWTavernBot.BOT_NAME) {
            val user = ru.nia.tavern.model.User.getFromMessage(callbackQuery.from)
            val text = callbackQuery.message.text
            if (text.contains("Количество болельщиков: ")) {
                val tUser = TournamentUsers.getCurrentByUserID(Integer.valueOf(callbackQuery.data))
                if (tUser == null) {
                    val answerCallbackQuery = AnswerCallbackQuery()
                    answerCallbackQuery.callbackQueryId = callbackQuery.id
                    answerCallbackQuery.text = "Боец уже отвоевал!"
                    try {
                        answerCallbackQuery(answerCallbackQuery)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }

                    return
                }
                val voteFor = tUser.getUser()
                if (!tUser.isInFight()) {
                    val answerCallbackQuery = AnswerCallbackQuery()
                    answerCallbackQuery.callbackQueryId = callbackQuery.id
                    answerCallbackQuery.text = "Боец уже отвоевал!"
                    try {
                        answerCallbackQuery(answerCallbackQuery)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }

                    return
                }
                if (user != null && user!!.voteFor != null) {
                    val answerCallbackQuery = AnswerCallbackQuery()
                    answerCallbackQuery.callbackQueryId = callbackQuery.id
                    answerCallbackQuery.text = "Ты уже проголосовал!"
                    try {
                        answerCallbackQuery(answerCallbackQuery)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }

                    return
                }
                if (user != null && user!!.voteFor == null) {
                    user!!.voteFor = callbackQuery.data
                    user!!.save()
                    var count: Int? = Integer.valueOf(StringUtils.substringAfter(text, "Количество болельщиков: "))
                    count++
                    val editMessageText = EditMessageText()
                    editMessageText.messageId = callbackQuery.message.messageId
                    editMessageText.setChatId(callbackQuery.message.chatId!!)
                    val inlineKeyboardMarkup = InlineKeyboardMarkup()
                    val keyboard = ArrayList<List<InlineKeyboardButton>>()
                    val row = ArrayList<InlineKeyboardButton>()
                    val button = InlineKeyboardButton()
                    button.text = "AVE_" + voteFor.toString().replace("@", "").toUpperCase()
                    button.callbackData = voteFor.userID.toString()
                    row.add(button)
                    keyboard.add(row)
                    inlineKeyboardMarkup.keyboard = keyboard
                    editMessageText.replyMarkup = inlineKeyboardMarkup
                    var resText = StringUtils.substringBefore(text, "Количество болельщиков: ")
                    resText += "Количество болельщиков: " + count!!
                    editMessageText.text = resText
                    val answerCallbackQuery = AnswerCallbackQuery()
                    answerCallbackQuery.callbackQueryId = callbackQuery.id
                    answerCallbackQuery.text = "Голос принят!"
                    try {
                        answerCallbackQuery(answerCallbackQuery)
                        editMessageText(editMessageText)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }

                }
            } else if (callbackQuery.data.startsWith("dance")) {
                DanceStep.processCallbackQuery(callbackQuery)
            }
        }
    }

    override fun getBotUsername(): String {
        return BOT_NAME
    }

    override fun getBotToken(): String {
        return PropertiesLoader.INSTANCE.botToken
    }

    @Throws(TelegramApiException::class)
    private fun handleIncomingMessage(message: Message) {
        var sendMessageRequest: SendMessage? = null
        val user = ru.nia.tavern.model.User.getFromMessage(message)
        if (isCommand(message.text) || message.isUserMessage) {
            val commandsList = ArrayList<Commands>()
            if (message.isUserMessage) {
                commandsList.addAll(Arrays.asList(*PersonalCommands.values()))
                if (user.onQuest()) {
                    val quest = Quest.Companion.getCurrent(user)
                    val event = QuestEvent.Companion.getCurrent(quest)
                    commandsList.add(QuestCommands(user, quest, event))
                }
            }
            val current = Tournament.current
            if (current != null && current.isInProgress) {
                if (user.inTavern()) {
                    commandsList.addAll(current.getType().getCommands())
                    commandsList.add(TavernCommands.DRINK)
                }
            } else {
                if (user.inTavern()) {
                    commandsList.addAll(Arrays.asList(*TavernCommands.values()))
                }
            }
            for (command in commandsList) {
                if (command.isApplicable(message, user)) {
                    val answer = command.apply(message, user)
                    if (!StringUtils.isEmpty(answer)) {
                        sendMessageRequest = command.getMessage(message, answer)
                    }
                    break
                }
            }
            if (sendMessageRequest == null && message.isUserMessage && user.inTavern()) {
                val answer = PersonalCommands.MY_INFO.apply(message, user)
                sendMessageRequest = PersonalCommands.MY_INFO.getMessage(message, answer)
            }
        }
        if (sendMessageRequest != null) {
            sendMessage(sendMessageRequest)
        }
    }

    companion object {
        var INSTANCE = CWTavernBot()
        //    private static final String BOT_NAME = "Tavern_Test_Bot";
        private val BOT_NAME = "CWTavernBot"
        private val LOGTAG = "CWTavernBot"


        /**
         * Если чат с пользователем еще не стартовал, а текущая команда не является одной из базовых команд чата
         *
         * @param text текст команды пользователя
         * @return true если команда не базовая и не должна обрабатываться, false - иначе
         */
        private fun isCommand(text: String): Boolean {
            return text.contains("/")
        }

        fun getKeyboard(user: ru.nia.tavern.model.User?): List<KeyboardRow> {
            val keyboardRows = ArrayList<KeyboardRow>()
            if (user == null) {
                return keyboardRows
            } else if (user.onQuest()) {
                val quest = Quest.Companion.getCurrent(user)
                val event = QuestEvent.Companion.getCurrent(quest)
                if (event == null || event!!.step.getNext(quest).isEmpty()) {
                    val keyboardButtons = KeyboardRow()
                    keyboardButtons.add(PersonalCommands.MY_INFO.text)
                    keyboardButtons.add(PersonalCommands.QUEST_RETURN.text)
                    keyboardRows.add(keyboardButtons)
                } else {
                    event!!.step.getNext(quest).forEach { iQuestStep ->
                        val keyboardButtons = KeyboardRow()
                        if (iQuestStep is ICrossQuestStep && (iQuestStep as ICrossQuestStep).isButtonWithUser) {
                            keyboardButtons.add(iQuestStep.getCommand(event!!.linkedQuestEvent!!.quest.user!!.toString()))
                        } else {
                            keyboardButtons.add(iQuestStep.getCommand(""))
                        }
                        keyboardRows.add(keyboardButtons)
                    }
                }
            } else if (user.inTavern()) {
                val currentByUserID = TournamentUsers.getCurrentByUserID(user.userID)
                if (currentByUserID != null && currentByUserID.isInFight() && currentByUserID.getScore() === 0) {
                    val buttons = currentByUserID.getTournament().getType().getCommandButtons()
                    var keyboardButtons = KeyboardRow()
                    var i = 0
                    for (btn in buttons) {
                        i++
                        keyboardButtons.add(btn)
                        if (i % 2 == 0) {
                            i = 0
                            keyboardRows.add(keyboardButtons)
                            keyboardButtons = KeyboardRow()
                        }
                    }
                    if (i > 0) {
                        keyboardRows.add(keyboardButtons)
                    }
                } else {
                    val keyboardButtons = KeyboardRow()
                    keyboardButtons.add(PersonalCommands.MY_INFO.text)
                    keyboardButtons.add(PersonalCommands.QUEST.text)
                    keyboardRows.add(keyboardButtons)
                }
            }
            return keyboardRows
        }
    }
}

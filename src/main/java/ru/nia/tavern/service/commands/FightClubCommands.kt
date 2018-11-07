package ru.nia.tavern.service.commands

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.User
import ru.nia.tavern.service.CWTavernBot
import java.util.*

/**
 * @author Иван, 11.03.2017.
 */
enum class FightClubCommands private constructor(protected var text: String) : Commands {
    DRAKA("/DRAKA") {
        override fun isApplicable(message: Message, from: User): Boolean {
            if (!super.isApplicable(message, from)) {
                return false
            }
            val currentByUserID = TournamentUsers.getCurrentByUserID(message.from.id!!)
            return currentByUserID != null && currentByUserID.isInFight && currentByUserID.score == 0
        }

        override fun apply(message: Message, from: User): String {
            val currentByUserID = TournamentUsers.getCurrentByUserID(message.from.id!!)
            val user = currentByUserID!!.user
            currentByUserID.score = user.fightClubStatsSum
            currentByUserID.save()
            var res = String.format(currentByUserID.tournament.type.startPhrase, user)
            if (message.isUserMessage) {
                try {
                    val tournamentMessage = ServingMessage.getTournamentMessage("$res\nКоличество болельщиков: 0")
                    setKeyboard(user, tournamentMessage)
                    CWTavernBot.INSTANCE.sendMessage(tournamentMessage)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            } else {
                res += "\nКоличество болельщиков: 0"
            }

            return res
        }
    };

    override fun isApplicable(message: Message, from: User): Boolean {
        return message.text.contains(this.text)
    }

    override fun getMessage(message: Message, answer: String): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chat.id!!)
        sendMessage.enableHtml(true)
        sendMessage.text = answer
        if (!message.isUserMessage) {
            setKeyboard(User.getFromMessage(message.from), sendMessage)
        }

        return sendMessage
    }

    protected fun setKeyboard(user: User, sendMessage: SendMessage) {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()
        val button = InlineKeyboardButton()
        button.text = "AVE_" + user.toString().replace("@", "").toUpperCase()
        button.callbackData = user.userID.toString()
        row.add(button)
        keyboard.add(row)
        inlineKeyboardMarkup.keyboard = keyboard
        sendMessage.replyMarkup = inlineKeyboardMarkup
    }

    override fun apply(message: Message, from: User): String {
        return ""
    }

}

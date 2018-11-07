package ru.nia.tavern.service.commands

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove
import ru.nia.tavern.model.User
import ru.nia.tavern.service.CWTavernBot

/**
 * @author Иван, 12.03.2017.
 */
interface Commands {

    fun apply(message: Message, from: User): String

    fun isApplicable(message: Message, from: User): Boolean

    open fun getMessage(message: Message, answer: String): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chat.id!!)
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true)
        sendMessage.text = answer


        if (message.isUserMessage) {
            val replyKeyboardMarkup = ReplyKeyboardMarkup()
            replyKeyboardMarkup.selective = true
            replyKeyboardMarkup.resizeKeyboard = true
            replyKeyboardMarkup.oneTimeKeyboard = true
            val keyboard = CWTavernBot.getKeyboard(User.getFromMessage(message.from))
            if (keyboard != null && !keyboard.isEmpty()) {
                replyKeyboardMarkup.keyboard = keyboard
            }
            sendMessage.replyMarkup = replyKeyboardMarkup
        } else {
            val replyKeyboardHide = ReplyKeyboardRemove()
            replyKeyboardHide.selective = true
            sendMessage.replyMarkup = replyKeyboardHide

        }

        return sendMessage
    }

    fun getPersonalMessage(user: User, answer: String): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(user.userID.toLong())
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true)
        sendMessage.text = answer


        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = true
        val keyboard = CWTavernBot.getKeyboard(user)
        if (keyboard != null && !keyboard.isEmpty()) {
            replyKeyboardMarkup.keyboard = keyboard
        }
        sendMessage.replyMarkup = replyKeyboardMarkup

        return sendMessage
    }
}

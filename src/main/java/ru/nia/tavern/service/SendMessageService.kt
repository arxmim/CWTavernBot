package ru.nia.tavern.service

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import ru.nia.tavern.model.User

/**
 * @author Иван, 09.03.2017.
 */
object SendMessageService {
    //    private static long tavernChatID = -1001100805125L;//test
    val tavernChatID = -1001104513622L//prod

    fun getMessage(served: List<User>, servedFood: List<User>): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(tavernChatID)
        sendMessage.enableHtml(true)
        val sb = StringBuilder()
        sb.append("А вот и я! ")
        if (!served.isEmpty()) {
            sb.append("Принесла напитки для посетителей:\n")
        }
        served.forEach { usr -> sb.append(usr).append(" - ").append(usr.drinkType.getName()).append("\n") }
        if (!servedFood.isEmpty()) {
            if (!served.isEmpty()) {
                sb.append("\nА еще вот закуски:\n")
            } else {
                sb.append("Принесла закуски для посетителей:\n")
            }
        }
        servedFood.forEach { usr -> sb.append(usr).append(" - ").append(usr.food.getName()).append("\n") }
        sb.append("\nМожете приступать к ")
        if (!served.isEmpty()) {
            sb.append("/drink")
        }
        if (!servedFood.isEmpty()) {
            if (!served.isEmpty()) {
                sb.append(" и /eat")
            } else {
                sb.append("/eat")
            }
        }
        sb.append("!")
        sendMessage.text = sb.toString()

        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = true
        sendMessage.replyMarkup = replyKeyboardMarkup

        return sendMessage
    }

    fun getTournamentMessage(answer: String): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(tavernChatID)
        sendMessage.enableHtml(true)
        sendMessage.text = answer

        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = true
        sendMessage.replyMarkup = replyKeyboardMarkup

        return sendMessage
    }

    fun getTimedMessage(user: User, answer: String): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(java.lang.Long.valueOf(user.userID.toLong()))
        sendMessage.enableHtml(true)
        sendMessage.text = answer
        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        val keyboard = CWTavernBot.getKeyboard(user)
        if (keyboard != null && !keyboard.isEmpty()) {
            replyKeyboardMarkup.keyboard = keyboard
        }
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = true
        sendMessage.replyMarkup = replyKeyboardMarkup
        return sendMessage
    }
}

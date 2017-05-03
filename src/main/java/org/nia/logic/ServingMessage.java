package org.nia.logic;

import org.nia.bots.CWTavernBot;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * @author Иван, 09.03.2017.
 */
public class ServingMessage {
    private static long tavernChatID = -1001104513622L;

    public static SendMessage getMessage(List<User> served, List<User> servedFood) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tavernChatID);
        sendMessage.enableHtml(true);
        StringBuilder sb = new StringBuilder();
        sb.append("А вот и я! ");
        if (!served.isEmpty()) {
            sb.append("Принесла напитки для посетителей:\n");
        }
        served.forEach(usr -> sb.append(usr).append(" - ").append(usr.getDrinkType().getName()).append("\n"));
        if (!servedFood.isEmpty()) {
            if (!served.isEmpty()) {
                sb.append("\nА еще вот закуски:\n");
            } else {
                sb.append("Принесла закуски для посетителей:\n");
            }
        }
        servedFood.forEach(usr -> sb.append(usr).append(" - ").append(usr.getFood().getName()).append("\n"));
        sb.append("\nМожете приступать к ");
        if (!served.isEmpty()) {
            sb.append("/drink");
        }
        if (!servedFood.isEmpty()) {
            if (!served.isEmpty()) {
                sb.append(" и /eat");
            } else {
                sb.append("/eat");
            }
        }
        sb.append("!");
        sendMessage.setText(sb.toString());

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage getTournamentMessage(String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tavernChatID);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage getTimedMessage(User user, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(Long.valueOf(user.getUserID()));
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = CWTavernBot.getKeyboard(user);
        if (keyboard != null && !keyboard.isEmpty()) {
            replyKeyboardMarkup.setKeyboard(keyboard);
        }
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}

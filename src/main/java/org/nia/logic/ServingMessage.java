package org.nia.logic;

import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

/**
 * @author Иван, 09.03.2017.
 */
public class ServingMessage {
    private static long chatID = -1001104513622L;
    private static long chatID_test = -213390213;
    private static long chatID_test_with_alex = -1001113989941L;
    public static SendMessage getMessage(List<User> served) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableHtml(true);
        StringBuilder sb = new StringBuilder();
        sb.append("А вот и я! Принесла напитки для посетителей:\n");
        served.forEach(usr -> sb.append(usr).append(" - ").append(usr.getDrinkType().getName()).append("\n"));
        sb.append("Можете приступать к /drink! (и попробуйте мне только /throw сделать!)");
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
        sendMessage.setChatId(chatID);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage getTimedMessage(Integer userID, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userID.longValue());
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
//        replyKeyboardMarkup.setKeyboard(getKeyboard(message));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}

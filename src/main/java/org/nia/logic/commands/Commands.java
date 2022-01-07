package org.nia.logic.commands;

import org.nia.bots.CWTavernBot;
import org.nia.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * @author Иван, 12.03.2017.
 */
public interface Commands {

    public String apply(Message message, User from);

    public boolean isApplicable(Message message, User from);

    public default SendMessage getMessage(Message message, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChat().getId()));
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);


        if (message.isUserMessage()) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setKeyboard(CWTavernBot.getKeyboard(User.getFromMessage(message.getFrom())));
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        } else {
            ReplyKeyboardRemove replyKeyboardHide = new ReplyKeyboardRemove();
            replyKeyboardHide.setSelective(true);
            replyKeyboardHide.setRemoveKeyboard(false);
            sendMessage.setReplyMarkup(replyKeyboardHide);

        }

        return sendMessage;
    }

    public default SendMessage getPersonalMessage(User user, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getUserID()));
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = CWTavernBot.getKeyboard(user);
        if (keyboard != null && !keyboard.isEmpty()) {
            replyKeyboardMarkup.setKeyboard(keyboard);
        }
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}

package org.nia.logic.commands;

import org.nia.bots.CWTavernBot;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardHide;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * @author Иван, 12.03.2017.
 */
public interface Commands {

    public String apply(Message message);

    public boolean isApplicable(Message message);

    public default SendMessage getMessage(Message message, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);


        if (message.isUserMessage()) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboad(true);
            List<KeyboardRow> keyboard = CWTavernBot.getKeyboard(User.getFromMessage(message.getFrom()));
            if (keyboard != null && !keyboard.isEmpty()) {
                replyKeyboardMarkup.setKeyboard(keyboard);
            }
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        } else {
            ReplyKeyboardHide replyKeyboardHide = new ReplyKeyboardHide();
            replyKeyboardHide.setSelective(true);
            sendMessage.setReplyMarkup(replyKeyboardHide);

        }

        return sendMessage;
    }

    public default SendMessage getPersonalMessage(User user, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId((long) user.getUserID());
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        List<KeyboardRow> keyboard = CWTavernBot.getKeyboard(user);
        if (keyboard != null && !keyboard.isEmpty()) {
            replyKeyboardMarkup.setKeyboard(keyboard);
        }
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}

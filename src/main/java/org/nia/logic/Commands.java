package org.nia.logic;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

/**
 * @author Иван, 12.03.2017.
 */
public interface Commands {

    public String apply(Message message);
    public boolean isApplicable(Message message);

    public default SendMessage getMessage(Message message, String answer) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        List<KeyboardRow> keyboard = getKeyboard(message);
        if (keyboard != null) {
            replyKeyboardMarkup.setKeyboard(keyboard);
        }
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    List<KeyboardRow> getKeyboard(Message message);
}

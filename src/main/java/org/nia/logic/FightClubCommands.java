package org.nia.logic;

import org.nia.model.TournamentUsers;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;


/**
 * @author Иван, 11.03.2017.
 */
public enum FightClubCommands implements Commands {
    DRAKA("/DRAKA") {
        @Override
        public boolean isApplicable(Message message) {
            if (!super.isApplicable(message)) {
                return false;
            }
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            return currentByUserID != null && currentByUserID.InFight() && currentByUserID.getScore() == 0;
        }

        @Override
        public String apply(Message message) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            currentByUserID.setScore((currentByUserID.getUser().getDrinkedToday() / 10) + new Random().nextInt(70));
            currentByUserID.save();
            return String.format(currentByUserID.getTournament().getType().getStartPhrase(), currentByUserID.getUser());
        }
    };
    protected String text;

    FightClubCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message) {
        return message.getText().contains(this.text);
    }

    @Override
    public String apply(Message message) {
        return "";
    }

    public static SendMessage getMessage(Message message, String answer) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        //replyKeyboardMarkup.setKeyboard(getKeyboard());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}

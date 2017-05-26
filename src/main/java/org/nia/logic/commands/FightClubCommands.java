package org.nia.logic.commands;

import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Иван, 11.03.2017.
 */
public enum FightClubCommands implements Commands {
    DRAKA("/DRAKA") {
        @Override
        public boolean isApplicable(Message message, User from) {
            if (!super.isApplicable(message, from)) {
                return false;
            }
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            return currentByUserID != null && currentByUserID.isInFight() && currentByUserID.getScore() == 0;
        }

        @Override
        public String apply(Message message, User from) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            User user = currentByUserID.getUser();
            currentByUserID.setScore(user.getFightClubStatsSum());
            currentByUserID.save();
            String res = String.format(currentByUserID.getTournament().getType().getStartPhrase(), user);
            if (message.isUserMessage()) {
                try {
                    SendMessage tournamentMessage = ServingMessage.getTournamentMessage(res + "\nКоличество болельщиков: 0");
                    setKeyboard(user, tournamentMessage);
                    CWTavernBot.INSTANCE.sendMessage(tournamentMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                res += "\nКоличество болельщиков: 0";
            }

            return res;
        }
    };
    protected String text;

    FightClubCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message, User from) {
        return message.getText().contains(this.text);
    }

    @Override
    public SendMessage getMessage(Message message, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);
        if (!message.isUserMessage()) {
            setKeyboard(User.getFromMessage(message.getFrom()), sendMessage);
        }

        return sendMessage;
    }

    protected void setKeyboard(User user, SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("AVE_" + user.toString().replace("@", "").toUpperCase());
        button.setCallbackData(String.valueOf(user.getUserID()));
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    @Override
    public String apply(Message message, User from) {
        return "";
    }

}

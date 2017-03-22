package org.nia.logic.commands;

import org.nia.logic.commands.Commands;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;


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
            User user = currentByUserID.getUser();
            currentByUserID.setScore(user.getFightClubStatsSum());
            currentByUserID.save();

            return String.format(currentByUserID.getTournament().getType().getStartPhrase(), user) + "\n\nХочешь поддержать участника? Жми /AVE_" + user.toString().toUpperCase().replace("@", "");
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
}
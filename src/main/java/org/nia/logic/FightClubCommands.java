package org.nia.logic;

import org.nia.model.TournamentUsers;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Random;


/**
 * @author Иван, 11.03.2017.
 */
enum FightClubCommands implements Commands {
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
            currentByUserID.setScore((currentByUserID.getUser().getDrinkedTotal() / 10) + new Random().nextInt(70));
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

    @Override
    public List<KeyboardRow> getKeyboard(Message message) {
        return null;
    }
}

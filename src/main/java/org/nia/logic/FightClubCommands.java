package org.nia.logic;

import org.nia.model.DrinkPrefs;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
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
            User user = currentByUserID.getUser();
            int knowledge = user.getDrinkedTotal() / 10;
            DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
            int strength = drinkPrefs.getPrefMap().entrySet().stream()
                    .filter(e -> Arrays.asList(DrinkType.AVE_WHITE, DrinkType.BEER, DrinkType.GHOST)
                            .contains(e.getKey()))
                    .mapToInt(e -> e.getValue().getToDrink()).sum() /5 + 1;
            int charism = drinkPrefs.getPrefMap().entrySet().stream()
                    .filter(e -> Arrays.asList(DrinkType.CHLEN, DrinkType.RED_POWER, DrinkType.MORDOR)
                            .contains(e.getKey()))
                    .mapToInt(e -> e.getValue().getToDrink()).sum() /5 + 1;
            int agility = drinkPrefs.getPrefMap().entrySet().stream()
                    .mapToInt(e -> e.getValue().getToThrow()).sum() /5 + 1;
            int constitution = drinkPrefs.getPrefMap().entrySet().stream()
                    .mapToInt(e -> e.getValue().getToBeThrown()).sum() /5 + 1;
            String stats = "\nСила: " + strength +"\nЛовкость: " + agility +"\nОбаяние: " + charism +"\nСтойкость: " + constitution+ "\nЗнание таверны: " + knowledge;
            currentByUserID.setScore(strength + charism + agility + constitution + knowledge);
            currentByUserID.save();

            return String.format(currentByUserID.getTournament().getType().getStartPhrase() + "\nТвои характеристики: " + stats, user);
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

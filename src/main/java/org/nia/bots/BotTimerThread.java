package org.nia.bots;

import org.nia.logic.Location;
import org.nia.logic.ServingMessage;
import org.nia.model.User;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */
public class BotTimerThread extends Thread {
    //private static BotTimerThread INSTANCE = null;
    private CWTavernBot bot;

    public BotTimerThread(CWTavernBot bot) {
        this.bot = bot;
        setDaemon(true);
        //INSTANCE = this;
    }

    @Override
    public void run() {
        Date now;
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
                now = new Date();
                for (User usr : User.getAll()) {
                    if (usr.onQuest() && usr.getLocationReturnTime().before(now)) {
                        Location location = usr.getLocation();
                        usr.setLocation(Location.TAVERN);
                        usr.setLocationReturnTime(null);
                        int earn = 3 + new Random().nextInt(3);
                        usr.setGold(usr.getGold() + earn);
                        usr.save();
                        try {
                            bot.sendMessage(ServingMessage.getTimedMessage(usr.getUserID(), location.getResult() + "\n\nТы выполнил просьбу Михалыча и получил " + earn + Emoji.GOLD));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

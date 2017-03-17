package org.nia.bots;

import org.apache.commons.lang3.StringUtils;
import org.nia.logic.ServingMessage;
import org.nia.model.Tournament;
import org.nia.model.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Иван, 09.03.2017.
 */
public class OficiantThread extends Thread {
    public static OficiantThread INSTANCE = null;
    private CWTavernBot bot;

    public OficiantThread(CWTavernBot bot) {
        this.bot = bot;
        setDaemon(true);
        INSTANCE = this;
    }

    private volatile boolean barmenCommand;
    private volatile boolean tournamentPhase;

    @Override
    public void run() {
        Date now = new Date();
        while (true) {
            try {
                GregorianCalendar gcWas = new GregorianCalendar();
                gcWas.setTime(now);
                TimeUnit.SECONDS.sleep(15);
                now = new Date();
                Tournament current = Tournament.getCurrent();
                if ((current == null || !current.isInProgress()) && (timedStart(gcWas) || barmenCommand)) {
                    barmenCommand = false;
                    serve();
                } else if ((current != null && (tournamentInterval(gcWas, current) || tournamentPhase))) {
                    tournamentPhase = false;
                    String answer = current.work();
                    if (!StringUtils.isEmpty(answer)) {
                        bot.sendMessage(ServingMessage.getTournamentMessage(answer));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean tournamentInterval(GregorianCalendar gcWas, Tournament current) {
        GregorianCalendar gcNow = new GregorianCalendar();
        GregorianCalendar gcRegistration = new GregorianCalendar();
        gcRegistration.setTime(current.getRegistrationDateTime());
        gcNow.setTime(new Date());
        boolean tournamentPhase = true;
        if (current.isAnnounced()) {
            tournamentPhase = gcNow.getTime().after(gcRegistration.getTime());
        } else if (current.isRegistration()) {
            gcRegistration.add(Calendar.MINUTE, 5);
            tournamentPhase = gcNow.getTime().after(gcRegistration.getTime());
        }
        return tournamentPhase && gcWas.get(GregorianCalendar.MINUTE) < gcNow.get(GregorianCalendar.MINUTE);
    }

    private boolean timedStart(GregorianCalendar gcWas) {
        GregorianCalendar gcNow = new GregorianCalendar();
        gcNow.setTime(new Date());
        int INIT_MINUTE = 10;
        int INTERVAL = 20;
        return gcWas.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE - 1 && gcNow.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE;
    }

    private void serve() {
        List<User> users = User.getAll();
        List<User> servedDrink = new ArrayList<>();
        List<User> servedFood = new ArrayList<>();
        users.stream().filter(usr -> usr.getWanted() != null || usr.getWantedFood() != null).forEach(usr -> {
            if (usr.getWanted() != null) {
                usr.setDrinkType(usr.getWanted());
                usr.setAlkoCount(2);
                usr.setWanted(null);
                usr.save();
                servedDrink.add(usr);
            } else if (usr.getWantedFood() != null) {
                usr.setFood(usr.getWantedFood());
                usr.setFoodCount(1);
                usr.setWantedFood(null);
                usr.save();
                servedFood.add(usr);
            }
        });
        try {
            if (!servedDrink.isEmpty() || !servedFood.isEmpty()) {
                bot.sendMessage(ServingMessage.getMessage(servedDrink, servedFood));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setBarmenCommand(boolean barmenCommand) {
        this.barmenCommand = barmenCommand;
    }

    public void setTournamentPhase(boolean tournamentPhase) {
        this.tournamentPhase = tournamentPhase;
    }
}

package org.nia;

import org.nia.bots.BotTimerThread;
import org.nia.bots.CWTavernBot;
import org.nia.bots.OficiantThread;
import org.nia.db.HibernateConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author IANazarov
 */
public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {

        try {
            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
            try {
                // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
                telegramBotsApi.registerBot(CWTavernBot.INSTANCE);
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                executorService.submit(new OficiantThread(CWTavernBot.INSTANCE));
                executorService.submit(new BotTimerThread(CWTavernBot.INSTANCE));
                HibernateConfig.getSessionFactory();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        return createLongPollingTelegramBotsApi();
    }

    /**
     * @return TelegramBotsApi to register the bots.
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

}

package org.nia;

import org.nia.bots.BotTimerThread;
import org.nia.bots.CWTavernBot;
import org.nia.bots.OficiantThread;
import org.nia.db.DatabaseManager;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * @author IANazarov
 */
public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
            try {
                // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
                DatabaseManager.getInstance();
                telegramBotsApi.registerBot(CWTavernBot.INSTANCE);
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                executorService.submit(new OficiantThread(CWTavernBot.INSTANCE));
                executorService.submit(new BotTimerThread(CWTavernBot.INSTANCE));
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
        BotLogger.info(LOGTAG, "done");
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        return createLongPollingTelegramBotsApi();
    }

    /**
     * @return TelegramBotsApi to register the bots.
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }

}

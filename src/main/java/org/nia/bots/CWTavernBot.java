package org.nia.bots;

import org.apache.commons.lang.StringUtils;
import org.nia.PropertiesLoader;
import org.nia.logic.Commands;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author IANazarov
 */
public class CWTavernBot extends TelegramLongPollingBot {
    public static final String BOT_NAME = "CWTavernBot";
    private static final String LOGTAG = "CWTavernBot";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText() || message.hasLocation()) {
                    handleIncomingMessage(message);
                } else {
                    BotLogger.info(LOGTAG, "no text");
                }
            } else {
                BotLogger.info(LOGTAG, "no message");
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }

    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return PropertiesLoader.INSTANCE.getBotToken();
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        SendMessage sendMessageRequest = null;
        if (message.getChat() != null) {
            BotLogger.info(LOGTAG, message.getChat().getTitle());
        }
        if (message.getFrom() != null) {
            BotLogger.info(LOGTAG, message.getFrom().getUserName());
        }
//        if (!message.getChat().getTitle().equals("test_grp_cw_off")) {
//            return;
//        }
        if (isCommand(message.getText()) || message.isUserMessage()) {
            for (Commands command : Commands.values()) {
                if (command.isApplicable(message)) {
                    String answer = command.apply(message);
                    if (!StringUtils.isEmpty(answer)) {
                        sendMessageRequest = command.getMessage(message, answer);
                    }
                    break;
                }
            }
            if (sendMessageRequest == null && message.isUserMessage()) {
                String answer = Commands.HELP.apply(message);
                sendMessageRequest = Commands.HELP.getMessage(message, answer);
            }
        }
        if (sendMessageRequest != null) {
            sendMessage(sendMessageRequest);
        }
    }



    /**
     * Если чат с пользователем еще не стартовал, а текущая команда не является одной из базовых команд чата
     *
     * @param text текст команды пользователя
     * @return true если команда не базовая и не должна обрабатываться, false - иначе
     */
    private static boolean isCommand(String text) {
        return text.contains("/");
    }
}

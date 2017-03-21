package org.nia.bots;

import org.apache.commons.lang.StringUtils;
import org.nia.PropertiesLoader;
import org.nia.logic.*;
import org.nia.logic.commands.Commands;
import org.nia.logic.commands.PersonalCommands;
import org.nia.logic.commands.QuestCommands;
import org.nia.logic.commands.TavernCommands;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.Tournament;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public class CWTavernBot extends TelegramLongPollingBot {
    public static CWTavernBot INSTANCE = new CWTavernBot();
    private static final String BOT_NAME = "CWTavernBot";
    private static final String LOGTAG = "CWTavernBot";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText() || message.hasLocation()) {
                    handleIncomingMessage(message);
                } else {
                    User newChatMember = message.getNewChatMember();
                    if (newChatMember != null) {
                        org.nia.model.User user = org.nia.model.User.getFromMessage(newChatMember);
                        user.setAlkoCount(2);
                        int rand = new Random().nextInt(DrinkType.values().length);
                        DrinkType drinkType = DrinkType.values()[rand];
                        user.setDrinkType(drinkType);
                        user.save();
                        String answer = String.format(drinkType.getEnterPhrase(), user);
                        sendMessage(TavernCommands.GIVE.getMessage(message, answer));
                    }
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
//        if (message.getChat() != null) {
//            BotLogger.info(LOGTAG, message.getChat().getTitle());
//        }
//        if (message.getFrom() != null) {
//            BotLogger.info(LOGTAG, message.getFrom().getUserName());
//        }
//        if (!message.getChat().getTitle().equals("test_grp_cw_off")) {
//            return;
//        }
        if (isCommand(message.getText()) || message.isUserMessage()) {
            Tournament current = Tournament.getCurrent();
            if (current != null && current.isInProgress()) {
                org.nia.model.User user = org.nia.model.User.getFromMessage(message);
                List<Commands> commandsList = new ArrayList<>();
                if (user.inTavern()) {
                    commandsList.addAll(current.getType().getCommands());
                }
                if (message.isUserMessage()) {
                    commandsList.addAll(Arrays.asList(PersonalCommands.values()));
                }
                for (Commands command : commandsList) {
                    if (command.isApplicable(message)) {
                        String answer = command.apply(message);
                        if (!StringUtils.isEmpty(answer)) {
                            sendMessageRequest = command.getMessage(message, answer);
                        }
                        break;
                    }
                }
            } else {
                org.nia.model.User user = org.nia.model.User.getFromMessage(message);
                List<Commands> commandsList = new ArrayList<>();
                if (user.inTavern()) {
                    commandsList.addAll(Arrays.asList(TavernCommands.values()));
                }
                if (message.isUserMessage()) {
                    commandsList.addAll(Arrays.asList(PersonalCommands.values()));
                    if (user.onQuest()) {
                        Quest quest = Quest.getCurrent(user);
                        QuestEvent event = QuestEvent.getCurrent(quest);
                        commandsList.add(new QuestCommands(user, quest, event));
                    }
                }
                for (Commands command : commandsList) {
                    if (command.isApplicable(message)) {
                        String answer = command.apply(message);
                        if (!StringUtils.isEmpty(answer)) {
                            sendMessageRequest = command.getMessage(message, answer);
                        }
                        break;
                    }
                }
            }
            if (sendMessageRequest == null && message.isUserMessage()) {
                String answer = PersonalCommands.MY_INFO.apply(message);
                sendMessageRequest = PersonalCommands.MY_INFO.getMessage(message, answer);
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

    public static List<KeyboardRow> getKeyboard(org.nia.model.User user) {
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        if (user == null) {
            return keyboardRows;
        } else if (user.onQuest()) {
            Quest quest = Quest.getCurrent(user);
            QuestEvent event = QuestEvent.getCurrent(quest);
            if (event == null) {
                KeyboardRow keyboardButtons = new KeyboardRow();
                keyboardButtons.add(PersonalCommands.MY_INFO.getText());
                keyboardButtons.add(PersonalCommands.QUEST_RETURN.getText());
                keyboardRows.add(keyboardButtons);
            } else {
                event.getStep().getNext().forEach(iQuestStep -> {
                    KeyboardRow keyboardButtons = new KeyboardRow();
                    keyboardButtons.add(iQuestStep.getCommand());
                    keyboardRows.add(keyboardButtons);
                });
            }
        } else if (user.inTavern()) {
            KeyboardRow keyboardButtons = new KeyboardRow();
            keyboardButtons.add(PersonalCommands.MY_INFO.getText());
            if (user.isAdmin()) {
                keyboardButtons.add(PersonalCommands.QUEST.getText());
            }
            keyboardRows.add(keyboardButtons);
        }
        return keyboardRows;
    }
}

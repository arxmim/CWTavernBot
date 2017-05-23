package org.nia.bots;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nia.PropertiesLoader;
import org.nia.logic.commands.Commands;
import org.nia.logic.commands.PersonalCommands;
import org.nia.logic.commands.QuestCommands;
import org.nia.logic.commands.TavernCommands;
import org.nia.logic.lists.DrinkType;
import org.nia.logic.quests.ICrossQuestStep;
import org.nia.model.*;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.*;

/**
 * @author IANazarov
 */
public class CWTavernBot extends TelegramLongPollingBot {
    public static CWTavernBot INSTANCE = new CWTavernBot();
    private static final String BOT_NAME = "Tavern_Test_Bot";
    private static final String LOGTAG = "CWTavernBot";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (new Date((long) message.getDate() * 1000).before(DateUtils.addMinutes(new Date(), -3))) {
                    return;
                }
                if (message.hasText() || message.hasLocation()) {
                    handleIncomingMessage(message);
                } else {
                    User newChatMember = message.getNewChatMember();
                    if (newChatMember != null) {
                        org.nia.model.User user = org.nia.model.User.getFromMessage(newChatMember);
                        if (user.getLastDrinkTime() != null && user.getLastDrinkTime().after(DateUtils.addMinutes(new Date(), -20))) {
                            sendMessage(TavernCommands.GIVE.getMessage(message, user + ", ты либо сидишь в таверне, либо уходишь, хватит бегать туда-сюда!"));
                        } else {
                            user.setAlkoCount(2);
                            int rand = new Random().nextInt(DrinkType.values().length);
                            DrinkType drinkType = DrinkType.values()[rand];
                            user.setDrinkType(drinkType);
                            user.save();
                            String answer = String.format(drinkType.getEnterPhrase(), user);
                            sendMessage(TavernCommands.GIVE.getMessage(message, answer));
                        }
                    }
                    BotLogger.info(LOGTAG, "no text");
                }
            } else if (update.hasCallbackQuery()) {
                handleIncomingCallback(update.getCallbackQuery());
            } else {
                BotLogger.info(LOGTAG, "no message");
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }

    }

    private void handleIncomingCallback(CallbackQuery callbackQuery) {
        if (callbackQuery.getMessage() != null && Objects.equals(callbackQuery.getMessage().getFrom().getUserName(), CWTavernBot.BOT_NAME)) {
            org.nia.model.User user = org.nia.model.User.getFromMessage(callbackQuery.getFrom());
            String text = callbackQuery.getMessage().getText();
            if (text.contains("Количество болельщиков: ")) {
                TournamentUsers tUser = TournamentUsers.getCurrentByUserID(Integer.valueOf(callbackQuery.getData()));
                if (tUser == null) {
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    answerCallbackQuery.setText("Боец уже отвоевал!");
                    try {
                        answerCallbackQuery(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                org.nia.model.User voteFor = tUser.getUser();
                if (!tUser.isInFight()) {
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    answerCallbackQuery.setText("Боец уже отвоевал!");
                    try {
                        answerCallbackQuery(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (user != null && user.getVoteFor() != null) {
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    answerCallbackQuery.setText("Ты уже проголосовал!");
                    try {
                        answerCallbackQuery(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (user != null && user.getVoteFor() == null) {
                    user.setVoteFor(callbackQuery.getData());
                    user.save();
                    Integer count = Integer.valueOf(StringUtils.substringAfter(text, "Количество болельщиков: "));
                    count++;
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                    editMessageText.setChatId(callbackQuery.getMessage().getChatId());
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText("AVE_" + voteFor.toString().replace("@", "").toUpperCase());
                    button.setCallbackData(String.valueOf(voteFor.getUserID()));
                    row.add(button);
                    keyboard.add(row);
                    inlineKeyboardMarkup.setKeyboard(keyboard);
                    editMessageText.setReplyMarkup(inlineKeyboardMarkup);
                    String resText = StringUtils.substringBefore(text, "Количество болельщиков: ");
                    resText += "Количество болельщиков: " + count;
                    editMessageText.setText(resText);
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    answerCallbackQuery.setText("Голос принят!");
                    try {
                        answerCallbackQuery(answerCallbackQuery);
                        editMessageText(editMessageText);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (callbackQuery.getData().startsWith("@votingBot")) {
                Voting.processVote(callbackQuery, user);
            }
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
        org.nia.model.User user = org.nia.model.User.getFromMessage(message);
        if (isCommand(message.getText()) || message.isUserMessage()) {
            List<Commands> commandsList = new ArrayList<>();
            if (message.isUserMessage()) {
                commandsList.addAll(Arrays.asList(PersonalCommands.values()));
                if (user.onQuest()) {
                    Quest quest = Quest.getCurrent(user);
                    QuestEvent event = QuestEvent.getCurrent(quest);
                    commandsList.add(new QuestCommands(user, quest, event));
                }
            }
            Tournament current = Tournament.getCurrent();
            if (current != null && current.isInProgress()) {
                if (user.inTavern()) {
                    commandsList.addAll(current.getType().getCommands());
                    commandsList.add(TavernCommands.DRINK);
                }
            } else {
                if (user.inTavern()) {
                    commandsList.addAll(Arrays.asList(TavernCommands.values()));
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
            if (sendMessageRequest == null && message.isUserMessage() && user.inTavern()) {
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
            if (event == null || event.getStep().getNext(quest).isEmpty()) {
                KeyboardRow keyboardButtons = new KeyboardRow();
                keyboardButtons.add(PersonalCommands.MY_INFO.getText());
                keyboardButtons.add(PersonalCommands.QUEST_RETURN.getText());
                keyboardRows.add(keyboardButtons);
            } else {
                event.getStep().getNext(quest).forEach(iQuestStep -> {
                    KeyboardRow keyboardButtons = new KeyboardRow();
                    if (iQuestStep instanceof ICrossQuestStep && ((ICrossQuestStep) iQuestStep).isButtonWithUser()) {
                        keyboardButtons.add(iQuestStep.getCommand(event.getLinkedQuestEvent().getQuest().getUser().toString()));
                    } else {
                        keyboardButtons.add(iQuestStep.getCommand(""));
                    }
                    keyboardRows.add(keyboardButtons);
                });
            }
        } else if (user.inTavern()) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(user.getUserID());
            if (currentByUserID != null && currentByUserID.isInFight() && currentByUserID.getScore() == 0) {
                List<String> buttons = currentByUserID.getTournament().getType().getCommandButtons();
                KeyboardRow keyboardButtons = new KeyboardRow();
                int i = 0;
                for (String btn : buttons) {
                    i++;
                    keyboardButtons.add(btn);
                    if (i % 2 == 0) {
                        i = 0;
                        keyboardRows.add(keyboardButtons);
                        keyboardButtons = new KeyboardRow();
                    }
                }
                if (i > 0) {
                    keyboardRows.add(keyboardButtons);
                }
            } else {
                KeyboardRow keyboardButtons = new KeyboardRow();
                keyboardButtons.add(PersonalCommands.MY_INFO.getText());
                keyboardButtons.add(PersonalCommands.QUEST.getText());
                keyboardRows.add(keyboardButtons);
            }
        }
        return keyboardRows;
    }
}

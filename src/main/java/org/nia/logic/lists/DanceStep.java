package org.nia.logic.lists;

import lombok.Getter;
import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.model.Dancing;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
@Getter
public enum DanceStep {
    VALS1("\"Вальс новичков\"") {
        @Override
        public boolean hasNextStep(Dancing dancing) {
            return true;
        }

        @Override
        public DanceStep nextStep(Dancing dancing) {
            return VALS2;
        }

        @Override
        public List<DanceActionList> getStepActions() {
            return Arrays.asList(DanceActionList.VALS1, DanceActionList.VALS2, DanceActionList.VALS3, DanceActionList.VALS4);
        }

        @Override
        public String getStepText(Dancing dancing) {
            return dancing.getFirstDancer() + " и " + dancing.getSecondDancer() + " стоят друг перед другом, " +
                    "начинает играть легкая музыка. Сперва " + dancing.getFirstDancer() + " должен " +
                    DanceActionList.VALS3.willDoName() + ", ну а потом его партнер должен " + DanceActionList.VALS2.willDoName();
        }

        @Override
        public DanceAction getNextAfter(DanceAction lastDanceAction) {
            if (lastDanceAction == null) {
                return new DanceAction(DanceActionList.VALS3, true);
            }
            if (lastDanceAction.equals(new DanceAction(DanceActionList.VALS3, true))) {
                return new DanceAction(DanceActionList.VALS2, false);
            }
            return null;
        }
    },
    VALS2("\"Вальс новичков\"") {
        @Override
        public boolean hasNextStep(Dancing dancing) {
            return false;
        }

        @Override
        public DanceStep nextStep(Dancing dancing) {
            return null;
        }

        @Override
        public List<DanceActionList> getStepActions() {
            return Arrays.asList(DanceActionList.VALS5, DanceActionList.VALS4, DanceActionList.VALS6, DanceActionList.VALS7);
        }

        @Override
        public String getStepText(Dancing dancing) {
            return dancing.getFirstDancer() + " и " + dancing.getSecondDancer() + " уже неплохо отжигают! Сейчас " +
                    dancing.getFirstDancer() + " должен " + DanceActionList.VALS4.willDoName() + " и " +
                    DanceActionList.VALS6.willDoName() + ", ну а потом его партнер должен " + DanceActionList.VALS5.willDoName();
        }

        @Override
        public DanceAction getNextAfter(DanceAction lastDanceAction) {
            if (lastDanceAction == null) {
                return new DanceAction(DanceActionList.VALS4, true);
            }
            if (lastDanceAction.equals(new DanceAction(DanceActionList.VALS4, true))) {
                return new DanceAction(DanceActionList.VALS6, true);
            }
            if (lastDanceAction.equals(new DanceAction(DanceActionList.VALS6, true))) {
                return new DanceAction(DanceActionList.VALS5, false);
            }
            return null;
        }
    };

    private String danceName;
    private int stepDuration;

    DanceStep(String danceName) {
        this.danceName = danceName;
        this.stepDuration = 15;
    }

    public SendMessage getInitialSendMessage(Dancing dancing) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ServingMessage.getTavernChatID());
        sendMessage.enableHtml(true);
        sendMessage.setText(getStepText(dancing));
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(dancing);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(Dancing dancing) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (DanceActionList action : getStepActions()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(action.getActionName());
            button.setCallbackData("dance," + action.name() + "," + dancing.getPublicID());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }


    public SendMessage getTimedFailMessage(Dancing dancing) {
        return ServingMessage.getTournamentMessage("Танцоры немного постояли на сцене, сделали пару неловких движений, " +
                "но ничего приличного так и не показали. Позор вам, " + dancing.getFirstDancer() + ", " +
                dancing.getSecondDancer() + "!");
    }

    public SendMessage getFailMessage(User failer, User second) {
        return ServingMessage.getTournamentMessage("Вместо танца " + failer + " оттоптал ноги своему партнеру, и вся " +
                "магия танца была разрушена... " + second + " должен быть крайне разочарован этим растяпой!");
    }

    public SendMessage getSuccessMessage(Dancing dancing) {
        return ServingMessage.getTournamentMessage("Поразительно! Эти ребята отлично танцуют! Так держать, " +
                dancing.getFirstDancer() + " и " + dancing.getSecondDancer() + ", ваше выступление было потрясающим!");
    }

    public int getStepDuration() {
        return stepDuration;
    }

    public abstract boolean hasNextStep(Dancing dancing);

    public abstract DanceStep nextStep(Dancing dancing);

    public abstract List<DanceActionList> getStepActions();

    public abstract String getStepText(Dancing dancing);

    public static void processCallbackQuery(CallbackQuery callbackQuery) {
        String[] split = callbackQuery.getData().split(",");
        Dancing dancing = Dancing.getByID(Dancing.class, Integer.valueOf(split[2]));
        Integer userID = callbackQuery.getFrom().getId();
        User dancer;
        User second;
        boolean isFirst;
        if (dancing.getCompleted() != null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Танец уже закончился!");
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                CWTavernBot.INSTANCE.editMessageReplyMarkup(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        if (dancing.getFirstDancer().getUserID() != userID && dancing.getSecondDancer().getUserID() != userID) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Не мешай людям танцевать!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        } else if (dancing.getFirstDancer().getUserID() == userID) {
            dancer = dancing.getFirstDancer();
            second = dancing.getSecondDancer();
            isFirst = true;
        } else {
            isFirst = false;
            dancer = dancing.getSecondDancer();
            second = dancing.getFirstDancer();
        }
        DanceActionList dal = DanceActionList.valueOf(split[1]);
        DanceAction action = new DanceAction(dal, isFirst);
        DanceAction nextAction = dancing.getNextAction();
        if (nextAction == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Эта часть танца уже закончилась!");

            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                CWTavernBot.INSTANCE.editMessageReplyMarkup(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        } else if (nextAction.equals(action)) {
            dancing.setLastActionFromFirst(isFirst);
            dancing.setLastDanceAction(dal);
            dancing.save();
            boolean hasNext = dancing.getNextAction() != null;
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageText.setChatId(callbackQuery.getMessage().getChatId());
            if (hasNext) {
                InlineKeyboardMarkup inlineKeyboardMarkup = dancing.getCurrentStep().getInlineKeyboardMarkup(dancing);
                editMessageText.setReplyMarkup(inlineKeyboardMarkup);
                String text = callbackQuery.getMessage().getText() + "\n\n" + dancer + " " + dal.doName() + "...";
                editMessageText.setText(text);
            } else {
                String text = callbackQuery.getMessage().getText() + "\n\n" + dancer + " " + dal.doName() + " и закончил эту часть танца!";
                editMessageText.setText(text);
            }
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Правильно!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                CWTavernBot.INSTANCE.editMessageText(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        } else {
            dancing.setCompleted(false);
            dancing.save();
            dancer.setDanceWithUserID(null);
            second.setDanceWithUserID(null);
            dancer.save();
            second.save();
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageText.setChatId(callbackQuery.getMessage().getChatId());
            String text = callbackQuery.getMessage().getText() + "\n\n" + dancer + " запорол танец!";
            editMessageText.setText(text);
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Ты запорол танец!");
            SendMessage failMessage = dancing.getCurrentStep().getFailMessage(dancer, second);
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                CWTavernBot.INSTANCE.editMessageText(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                CWTavernBot.INSTANCE.sendMessage(failMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

    }

    public abstract DanceAction getNextAfter(DanceAction lastDanceAction);

    public static class DanceAction {
        DanceActionList dal;
        boolean isFirst;

        public DanceAction(DanceActionList dal, boolean isFirst) {
            this.dal = dal;
            this.isFirst = isFirst;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DanceAction)) return false;

            DanceAction that = (DanceAction) o;

            if (isFirst != that.isFirst) return false;
            return dal == that.dal;

        }

        @Override
        public int hashCode() {
            int result = dal.hashCode();
            result = 31 * result + (isFirst ? 1 : 0);
            return result;
        }
    }
}

package org.nia.logic;

import org.apache.commons.lang3.StringUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.commands.CommandProcessor;
import org.nia.logic.commands.EmptyProcessor;
import org.nia.model.User;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */

public enum Commands {
    START("start") {
        @Override
        public String apply(Message message) {
            return "Добро пожаловать в нашу таверну! Я буду помогать бармену разливать напитки.";
        }
    },
    HELP("help") {
        @Override
        public String apply(Message message) {
            return "/help - справка\n";
        }
    },
    SET_ADMIN("set_admin") {
        @Override
        public boolean isApplicable(Message message) {
            boolean setAdminMessage = message.isUserMessage() && message.getText().startsWith("/set_admin ");
            return setAdminMessage && User.getFromMessage(message).IsAdmin();
        }

        @Override
        public String apply(Message message) {
            String nick = StringUtils.substringAfter(message.getText(), "/set_admin ");
            User user = User.getByNick(nick);
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику";
            } else {
                user.setIsAdmin(!user.IsAdmin());
                if (user.IsAdmin()) {
                    return "Пользователю " + nick + " даны админские права";
                } else {
                    return "Пользователь " + nick + " лишен админских прав";
                }
            }
        }
    },
    REPORT("") {
        @Override
        public boolean isApplicable(Message message) {
            Pattern compile = Pattern.compile("Твои результаты в бою:");
            return compile.matcher(message.getText()).find();
        }


        @Override
        public List<KeyboardRow> getKeyboard() {
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow buttonRow = new KeyboardRow();
            buttonRow.add(Emoji.RED_FLAG.toString());
            buttonRow.add(Emoji.FOREST.toString());
            buttonRow.add(Emoji.MOUNTAIN.toString());
            keyboard.add(buttonRow);
            buttonRow = new KeyboardRow();
            buttonRow.add(Emoji.BLACK_FLAG.toString());
            buttonRow.add(Emoji.WHITE_FLAG.toString());
            keyboard.add(buttonRow);
            buttonRow = new KeyboardRow();
            buttonRow.add(Emoji.BLUE_FLAG.toString());
            buttonRow.add(Emoji.YELLOW_FLAG.toString());
            keyboard.add(buttonRow);
            return keyboard;
        }
    };
    protected String text;

    Commands(String text) {
        this.text = text;
    }

    public boolean isApplicable(Message message) {
        return this.text.equals(message.getText().replace("@" + CWTavernBot.BOT_NAME, "").replace("/", ""));
    }

    public CommandProcessor getProcessor() {
        return new EmptyProcessor();
    }

    public String apply(Message message) {
        return getProcessor().apply(message);
    }

    public SendMessage getMessage(Message message, String answer) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        //sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        sendMessage.setText(answer);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);
        replyKeyboardMarkup.setKeyboard(getKeyboard());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public List<KeyboardRow> getKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow buttonRow = new KeyboardRow();
        buttonRow.add("Мой профиль");
        keyboard.add(buttonRow);
        return keyboard;
    }
}

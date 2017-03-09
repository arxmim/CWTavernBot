package org.nia.logic;

import org.apache.commons.lang3.StringUtils;
import org.nia.bots.CWTavernBot;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */

public enum Commands {
    START("/start") {
        @Override
        public String apply(Message message) {
            User.getFromMessage(message);
            return "Добро пожаловать в нашу таверну! Я буду помогать бармену разливать напитки.";
        }
    },
    HELP("/help") {
        @Override
        public String apply(Message message) {
            User.getFromMessage(message);
            return "/help - справка\n" +
                    "/set_barmen [ник в телеге] - сделать человека барменом (для этого надо самому быть барменом)\n" +
                    "/menu - список напитков. Напитки выдает только бармен\n" +
                    "/throw - бросить стакан в человека из reply-сообщения. Для этого должен быть стакан!\n" +
                    "/drink - выпить свой напиток";
        }
    },
    SET_BARMEN("/set_barmen ") {
        @Override
        public boolean isApplicable(Message message) {
            boolean setAdminMessage = message.isUserMessage() && message.getText().startsWith(text);
            return setAdminMessage && User.getFromMessage(message).IsAdmin();
        }

        @Override
        public String apply(Message message) {
            String nick = StringUtils.substringAfter(message.getText(), text);
            User user = User.getByNick(nick);
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику";
            } else {
                user.setIsAdmin(!user.IsAdmin());
                user.save();
                if (user.IsAdmin()) {
                    return "Пользователю " + nick + " даны админские права";
                } else {
                    return "Пользователь " + nick + " лишен админских прав";
                }
            }
        }
    },
    TOP("/top") {
        @Override
        public boolean isApplicable(Message message) {
            boolean topMessage = message.getText().startsWith(text);
            return topMessage && User.getFromMessage(message).IsAdmin();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за всё время:\n");
            User.getTop().forEach(dt->sb.append(dt.getNick()).append(" - ").append(dt.getDrinkedToday()).append("\n"));
            return sb.toString();
        }
    },
    MENU("/menu") {
        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(DrinkType.values()).forEach(dt->sb.append(dt.getCommand()).append(" - ").append(dt.getOnGive()).append("\n"));
            return sb.toString();
        }
    },
    THROW("/throw") {
        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (drinker.getDrinkType() == null) {
                return "";
            } else {
                String res = "";
                if (message.isReply()) {
                    User victim = User.getFromMessage(message.getReplyToMessage());
                    res = drinker + " швырнул " + drinker.getDrinkType().getOnThrow() + " в " + victim + ". Парни, давайте только без драки!";
                } else {
                    res = drinker + " швырнул " + drinker.getDrinkType().getOnThrow() + " об пол! Дебошир!";
                }
                drinker.setDrinkType(null);
                drinker.setAlkoCount(0);
                drinker.save();
                return res;
            }
        }
    },
    GIVE("") {
        @Override
        public boolean isApplicable(Message message) {
            return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent();
        }

        @Override
        public String apply(Message message) {
            User barmen = User.getFromMessage(message);
            if (!barmen.IsAdmin()) {
                return "Дружище " + barmen + ", ты не бармен, я не буду наливать напитки по твоему запросу!";
            }
            if (!message.isReply()) {
                return barmen + ", ты бы выбрал, кому наливать!";
            }
            User fromMessage = User.getFromMessage(message.getReplyToMessage());
            fromMessage.setAlkoCount(2);
            DrinkType drinkType = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().get();
            fromMessage.setDrinkType(drinkType);
            fromMessage.save();
            return fromMessage + ", вот тебе " + drinkType.getOnGive() + ", можешь смело её /drink! ну или /throw, но мы же тут разумные люди, да?";
        }
    },
    DRINK("/drink") {
        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (drinker.getAlkoCount() <= 0) {
                return "";
//                return "Эй, " + drinker + ", да ты видимо пьян раз не видишь что твой стакан пуст!";
            }
            if (drinker.getLastDrinkTime() != null) {
                long since = TimeUnit.MINUTES.convert(new Date().getTime() - drinker.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
                long wait = 5 - since;
                if (wait > 0) {
//                    return "";
                    return drinker + " ты недавно уже пил. Подожди еще " + wait + " минут";
                }
            }
            int drinked = new Random().nextInt(drinker.getAlkoCount()) + 1;
            drinker.setDrinkedToday(drinker.getDrinkedToday() + drinked);
            drinker.setLastDrinkTime(new Date());
            drinker.setAlkoCount(drinker.getAlkoCount() - drinked);
            String res = "";
            if (drinker.getAlkoCount() == 0) {
                res = drinker + " залпом выпил " + drinker.getDrinkType().getOnThrow() + ". Как насчет добавки?";
            } else {
                res = drinker + " выпил лишь половину " + drinker.getDrinkType().getOnDrink() + ", но сразу допить не смог. Слабак что ли?";
            }
            drinker.save();
            return res;
        }
    };
    protected String text;

    Commands(String text) {
        this.text = text;
    }

    public boolean isApplicable(Message message) {
        return message.getText().contains(this.text);
    }

    public String apply(Message message) {
        return "";
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
        //replyKeyboardMarkup.setKeyboard(getKeyboard());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}

package org.nia.logic;

import org.apache.commons.lang3.StringUtils;
import org.nia.bots.CWTavernBot;
import org.nia.model.DrinkPrefs;
import org.nia.model.Tournament;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */

public enum CommonCommands implements Commands {
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
                    return "Пользователю " + nick + " дан барменский фартук";
                } else {
                    return "Пользователь " + nick + " лишен барменского фартука";
                }
            }
        }
    },
    REGISTER("/register") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && Tournament.getCurrent().isRegistration();
        }

        @Override
        public String apply(Message message) {
            Tournament tournament = Tournament.getCurrent();
            String result = TournamentUsers.register(tournament, User.getFromMessage(message.getFrom()));
            return result;
        }
    },
    MY_INFO("/my_info") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && message.isUserMessage();
        }

        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            DrinkPrefs prefs = DrinkPrefs.getByUser(user);
            StringBuilder sb = new StringBuilder();
            sb.append("А ты успел засветиться в нашей таверне!\nВот твоя статистика в формате Напиток-Выпито-Брошено-В тебя бросили:\n\n");
            prefs.getPrefMap().entrySet().forEach(e -> sb.append(e.getKey().getCommand())
                    .append(": ").append(e.getValue().getToDrink())
                    .append(", ").append(e.getValue().getToThrow())
                    .append(", ").append(e.getValue().getToBeThrown()).append("\n\n"));
            return sb.toString();
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
            User.getTop().forEach(dt -> sb.append(dt.getNick()).append(" - ").append(dt.getDrinkedToday()).append("\n"));
            return sb.toString();
        }
    },
    MENU("/menu") {
        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(DrinkType.values()).forEach(dt -> sb.append(dt.getCommand()).append(" - ").append(dt.getOnGive()).append("\n"));
            return sb.toString();
        }
    },
    CREATE_TOURNAMENT("/create_tournament") {
        @Override
        public String apply(Message message) {
            Matcher matcher = Pattern.compile("/create_tournament ([\\w]+) ([0-9]{2}):([0-9]{2}) (4|8|16|32)").matcher(message.getText());
            if (matcher.find()) {
                Tournament tournament = new Tournament();
                try {
                    String group = matcher.group(1);
                    TournamentType tournamentType = TournamentType.valueOf(group);
                    tournament.setType(tournamentType);
                } catch (Exception ex) {
                    return "Неверно указан тип турнира";
                }
                tournament.setState(TournamentState.ANOUNCE);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(new Date());
                gc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(matcher.group(2)));
                gc.set(Calendar.MINUTE, Integer.valueOf(matcher.group(3)));
                gc.set(Calendar.SECOND, 0);
                gc.set(Calendar.MILLISECOND, 0);
                tournament.setRegistrationDateTime(gc.getTime());
                tournament.setMaxUsers(Integer.valueOf(matcher.group(4)));
                tournament.save();
                return "Турнир создан.\n" + tournament;
            } else {
                return "Что-то не заполнено. Турнир не создан";
            }
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
                    if (message.getReplyToMessage().getFrom().getUserName().equals("CWTavernBot")) {
                        User bot = User.getByNick("CWTavernBot");
                        try {
                            DrinkPrefs.incThrow(bot, DrinkType.AVE_WHITE);
                            DrinkPrefs.incToBeThrown(drinker, DrinkType.AVE_WHITE);
                            SendMessage msg1 = CommonCommands.getMessage(message, "Ха, еще один дурак нашелся! У меня черный пояс по киданию жбанов! /throw");
                            CWTavernBot.INSTANCE.sendMessage(msg1);
                            res = bot + " швырнул " + DrinkType.AVE_WHITE.getOnThrow() + " в " + drinker + ". И стакан я у тебя отберу!";
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (message.getReplyToMessage().getFrom().getId().equals(message.getFrom().getId())) {
                        res = drinker + " поставил " + drinker.getDrinkType().getOnThrow() + " себе на голову и полез танцевать на стуле. Уважаемый, тебе показать где выход?";
                    } else {
                        User victim = User.getFromMessage(message.getReplyToMessage());
                        DrinkPrefs.incThrow(drinker, drinker.getDrinkType());
                        DrinkPrefs.incToBeThrown(victim, drinker.getDrinkType());
                        res = drinker + " швырнул " + drinker.getDrinkType().getOnThrow() + " в " + victim + ". Парни, давайте только без драки!";
                    }
                } else {
                    DrinkPrefs.incThrow(drinker, drinker.getDrinkType());
                    res = drinker + " швырнул " + drinker.getDrinkType().getOnThrow() + " об пол! Дебошир!";
                }
                drinker.setDrinkType(null);
                drinker.setAlkoCount(0);
                drinker.save();
                return res;
            }
        }
    },
//    TEST("/test123") {
//        @Override
//        public String apply(Message message) {
//            return Emoji.DRINKS + "Турнир " + TournamentType.FIGHT_CLUB + " начинается!\nГлавный приз - право называть себя барменом!"+ Emoji.DRINK+"\n\nПолный список участников:\n" + "1 - вася" + "\n Первое состязание состоится через 1 минуту, всем занять свои места, МЫ НАЧИНАЕМ!";
//        }
//    },
//    GO("/gogogo") {
//        @Override
//        public String apply(Message message) {
//            OficiantThread.INSTANCE.setBarmenCommand(true);
//            return "";
//        }
//    },
    GIVE("") {
        @Override
        public boolean isApplicable(Message message) {
            return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent();
        }

        @Override
        public String apply(Message message) {
            User asker = User.getFromMessage(message);
            if (message.isReply() && asker.IsAdmin()) {
                if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                    return "Сам у себя заказываешь выпивку? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                }
                User fromMessage = User.getFromMessage(message.getReplyToMessage());
                fromMessage.setAlkoCount(2);
                DrinkType drinkType = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().get();
                fromMessage.setDrinkType(drinkType);
                fromMessage.setWanted(null);
                fromMessage.save();
                return fromMessage + ", вот тебе " + drinkType.getOnGive() + ", можешь смело /drink! ну или /throw, но мы же тут разумные люди, да?";
            }
            DrinkType drinkType = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().get();
            asker.setWanted(drinkType);
            asker.save();
            return "";
        }
    },
    DRINK("/drink") {
        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (drinker.getAlkoCount() <= 0) {
                return "";
            }
            if (drinker.getLastDrinkTime() != null) {
                long since = TimeUnit.MINUTES.convert(new Date().getTime() - drinker.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
                long wait = 5 - since;
                if (wait > 0) {
                    return drinker + " ты недавно уже пил. Подожди еще " + wait + " минут";
                }
            }
            int drinked = new Random().nextInt(drinker.getAlkoCount()) + 1;
            DrinkPrefs.incDrink(drinker, drinker.getDrinkType(), drinked);
            drinker.setDrinkedToday(drinker.getDrinkedToday() + drinked);
            drinker.setLastDrinkTime(new Date());
            drinker.setAlkoCount(drinker.getAlkoCount() - drinked);
            String res;
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

    CommonCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message) {
        return message.getText().contains(this.text);
    }

    @Override
    public String apply(Message message) {
        return "";
    }

    public static SendMessage getMessage(Message message, String answer) throws TelegramApiException {
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

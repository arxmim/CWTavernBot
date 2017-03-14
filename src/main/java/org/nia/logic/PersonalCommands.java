package org.nia.logic;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nia.model.DrinkPrefs;
import org.nia.model.Tournament;
import org.nia.model.User;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */
public enum PersonalCommands implements Commands {
    START("/start") {
        @Override
        public String apply(Message message) {
            User.getFromMessage(message);
            return "Добро пожаловать в нашу таверну! Я буду помогать бармену разливать напитки.";
        }
    },
    HELP("/help") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && message.isUserMessage();
        }

        @Override
        public String apply(Message message) {
            User.getFromMessage(message);
            return "/help - справка\n" +
                    "/menu - список напитков\n" +
                    "/throw - бросить стакан в человека из reply-сообщения. Для этого должен быть стакан!\n" +
                    "/drink - выпить свой напиток\n\n" +
                    "Чтобы получить напиток, надо попросить его у бармена в чате таверны, либо ввести команду " +
                    "напитка и тогда официантка выдаст его когда будет делать обход посетителей (обход происходит несколько раз в час)";
        }
    },
    CREATE_TOURNAMENT("/create_tournament") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message.getFrom()).isAdmin();
        }

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
    SET_BARMEN("/set_barmen ") {
        @Override
        public boolean isApplicable(Message message) {
            boolean setAdminMessage = message.getText().startsWith(text);
            return setAdminMessage && User.getFromMessage(message).isAdmin();
        }

        @Override
        public String apply(Message message) {
            String nick = StringUtils.substringAfter(message.getText(), text);
            User user = User.getByNick(nick);
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику";
            } else {
                user.setIsBarmen(!user.isBarmen());
                user.save();
                if (user.isBarmen()) {
                    return "Пользователю " + nick + " дан барменский фартук";
                } else {
                    return "Пользователь " + nick + " лишен барменского фартука";
                }
            }
        }
    },
    QUEST("Взять задание у Михалыча") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            if (user.inTavern()) {
                Location randomQuest = Location.getRandomQuest();
                user.setLocation(randomQuest);
                user.setLocationReturnTime(DateUtils.addMinutes(new Date(), 30));
                user.save();
                return randomQuest.getText() + "\n\nВернешься через полчаса";
            } else if (user.onQuest()) {
                return "Ты уже выполняешь поручение Михалыча.";
            } else {
                return "";
            }
        }
    },
    MY_INFO("Информация о тебе") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            if (user.inTavern()) {
                String drink = "нет напитка";
                if (user.getDrinkType() != null) {
                    drink = user.getDrinkType().getName();
                    if (user.getAlkoCount() == 0) {
                        drink += " (внутри пусто)";
                    } else if (user.getAlkoCount() == 1) {
                        drink += " (примерно половина)";
                    } else {
                        drink += " (полный)";
                    }
                }
                return "Ты находишься в таверне. У тебя в руках " + drink + ", а в кармане " + user.getGold() + Emoji.GOLD;
            } else if (user.onQuest()) {
                return "Ты выполняешь поручение Михалыча. В кармане у тебя " + user.getGold() + Emoji.GOLD;
            } else {
                return "";
            }
        }
    },
    SECRET_MY_INFO("/secret_my_info") {
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
    //    GO("/gogogo") {
//        @Override
//        public String apply(Message message) {
//            OficiantThread.INSTANCE.setBarmenCommand(true);
//            return "";
//        }
//    }
    ;
    protected String text;

    PersonalCommands(String text) {
        this.text = text;
    }


    @Override
    public String apply(Message message) {
        return "";
    }

    @Override
    public boolean isApplicable(Message message) {
        return message.getText().contains(this.text);
    }
    @Override
    public List<KeyboardRow> getKeyboard(Message message) {
        User user = User.getFromMessage(message.getFrom());
        if (user.isAdmin()) {
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
            KeyboardRow keyboardButtons = new KeyboardRow();
            keyboardButtons.add(MY_INFO.text);
            keyboardButtons.add(QUEST.text);
            keyboardRows.add(keyboardButtons);
            return keyboardRows;
        } else {
            return null;
        }
    }
}

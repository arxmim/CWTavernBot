package org.nia.logic.commands;

import org.apache.commons.lang3.StringUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.logic.lists.Location;
import org.nia.logic.lists.TournamentState;
import org.nia.logic.lists.TournamentType;
import org.nia.logic.quests.QuestsEnum;
import org.nia.model.*;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    CREATE_VOTING("/create_voting ") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message.getFrom()).isAdmin();
        }

        @Override
        public String apply(Message message) {
            String text = StringUtils.substringAfter(message.getText(), this.getText());
            Voting voting = new Voting();
            voting.setText(text);
            voting.save();
            return "Голосование создано.\nДля добавления вариантов введите /add_vote_option [id голосования] [вариант]\n" + voting;
        }
    },
    ADD_VOTE_OPTION("/add_vote_option ") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message.getFrom()).isAdmin();
        }

        @Override
        public String apply(Message message) {
            Pattern pattern = Pattern.compile("/add_vote_option (\\d+) (.*)");
            Matcher matcher = pattern.matcher(message.getText());
            if (matcher.find()) {
                String votingID = matcher.group(1);
                String voteOption = matcher.group(2);
                try {
                    Voting voting = Voting.getByID(Voting.class, Integer.valueOf(votingID));
                    if (voting == null) {
                        return "Неверно указан ID голосования";
                    } else {
                        VoteOption option = new VoteOption();
                        option.setText(voteOption);
                        option.setVoting(voting);
                        option.save();
                        return "Вариант \"" + voteOption + "\" добавлен в голосование " + votingID;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "Что-то пошло не так";
                }
            } else {
                return "Неверно введена команда. Правильный формат /add_vote_option [id голосования] [вариант]";
            }
        }
    },
    RUN_VOTING("/run_voting ") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message.getFrom()).isAdmin();
        }

        @Override
        public String apply(Message message) {
            String votingID = StringUtils.substringAfter(message.getText(), this.getText());
            try {
                Voting voting = Voting.getByID(Voting.class, Integer.valueOf(votingID));
                if (voting == null) {
                    return "Неверно указан ID голосования";
                }
                voting.start(User.getFromMessage(message.getFrom()));
                return "";
            } catch (Exception ex) {
                ex.printStackTrace();
                return "Что-то пошло не так";
            }
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
                gc.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                tournament.setRegistrationDateTime(gc.getTime());
                tournament.setMaxUsers(Integer.valueOf(matcher.group(4)));
                tournament.save();
                return "Турнир создан.\n" + tournament;
            } else {
                return "Что-то не заполнено. Турнир не создан";
            }
        }
    },
    SET_ADMIN("/secret_set_admin ") {

        @Override
        public String apply(Message message) {
            String nick = StringUtils.substringAfter(message.getText(), text);
            User user = User.getByNick(nick);
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику";
            } else {
                user.setAdmin(!user.isAdmin());
                user.save();
                if (user.isAdmin()) {
                    return "Пользователь " + nick + " теперь админ";
                } else {
                    return "Пользователь " + nick + " больше не админ";
                }
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
                user.setBarmen(!user.isBarmen());
                user.save();
                if (user.isBarmen()) {
                    return "Пользователю " + nick + " дан барменский фартук";
                } else {
                    return "Пользователь " + nick + " лишен барменского фартука";
                }
            }
        }
    },
    QUEST("Взять задание у Остапа") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            if (user.inTavern()) {
                QuestsEnum randomQuest = Location.getRandomQuest();
                user.setLocation(Location.QUEST);
                Quest quest = new Quest();
                quest.setStartTime(new Date());
                quest.setUser(user);
                quest.setEventTime(randomQuest.getFirstEventTime());
                quest.setQuestEnum(randomQuest);
                quest.setGoldEarned(0);
                quest.setReturnTime(null);
                quest.save();
                user.save();
                return randomQuest.getIQuest().getStart() + "\n\nТы можешь вернуться в любой момент, но чем дольше ты проведешь на задании, тем больше получишь в награду.";
            } else {
                return "";
            }
        }
    },
    QUEST_RETURN("Вернуться с задания") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            if (user.onQuest()) {
                Quest quest = Quest.getCurrent(user);
                QuestEvent event = QuestEvent.getCurrent(quest);
                if (event != null) {
                    event.setWin(false);
                    event.getStep().doFinal(event);
                    event.save();
                    QuestEvent linkedQuestEvent = event.getLinkedQuestEvent();
                    if (linkedQuestEvent != null) {
                        linkedQuestEvent.setWin(true);
                        linkedQuestEvent.getStep().doFinal(linkedQuestEvent);
                        Quest linkedQuest = linkedQuestEvent.getQuest();
                        linkedQuest.setEventTime(linkedQuest.getQuestEnum().getNextEventTime(linkedQuest));
                        linkedQuest.save();
                        linkedQuestEvent.save();
                        try {
                            CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTimedMessage(linkedQuest.getUser(), user + " сбежал. Ты свалил на него все проблемы и смог немного подзаработать."));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                quest.setReturnTime(new Date());
                int reward = quest.getReward();
                quest.setGoldEarned(reward);
                quest.save();
                user.setLocation(Location.TAVERN);
                user.setGold(user.getGold() + reward);
                user.save();
                return "Ты вернулся с задания, заработав " + reward + Emoji.GOLD;
            } else if (user.inTavern()) {
                return "Ты уже вернулся с задания.";
            } else {
                return "";
            }
        }
    },
    MY_INFO("Информация о тебе") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            String res = "";
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
                String eat = "нет еды";
                if (user.getFood() != null) {
                    eat = user.getFood().getName();
                }
                res = "Ты находишься в таверне. У тебя в руках " + drink + " и перед тобой на столе " + eat + ".\nВ кармане " + user.getGold() + Emoji.GOLD;
            } else if (user.onQuest()) {
                res = "Ты выполняешь поручение Остапа.\nВ кармане у тебя " + user.getGold() + Emoji.GOLD;
            }
            if (user.getCurseTime() != null && user.getCurseTime().after(new Date())) {
                long duration = TimeUnit.MINUTES.convert(user.getCurseTime().getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                res += "\nТы закодован еще на " + duration + " минут.";
            }
            res += "\n\n" + user.getFightClubStats()
                    + "\n\n " + Emoji.DRINK + "Выпито напитков в таверне за эту неделю/всего: " + user.getDrinkedWeekNormalized() + "/" + user.getDrinkedTotalNormalized()
                    + "\n" + Emoji.MEDAL + "Побед в боях бойцовского клуба: " + user.getFightClubWins();
            return res;
        }
    },
    SECRET_MY_INFO("/my_info") {
        @Override
        public String apply(Message message) {
            String name = StringUtils.substringAfter(message.getText(), text).trim();
            User user;
            if (name.isEmpty()) {
                user = User.getFromMessage(message.getFrom());
            } else {
                user = User.getByNick(name);
            }
            List<DrinkPref> prefs = DrinkPref.getByUser(user);
            StringBuilder sb = new StringBuilder();
            sb.append("А ты успел засветиться в нашей таверне!\nВот твоя статистика в формате Напиток-Выпито-Брошено-В тебя бросили:\n\n");
            prefs.forEach(e -> sb.append(e.getDrinkType().getCommand())
                    .append(": ").append(e.getToDrinkNormalized())
                    .append(", ").append(e.getToThrow())
                    .append(", ").append(e.getToBeThrown()).append("\n\n"));
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

    public String getText() {
        return text;
    }
}

package org.nia.logic.commands;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.lists.DrinkType;
import org.nia.logic.lists.Food;
import org.nia.logic.lists.TournamentType;
import org.nia.logic.quests.kitchen.KitchenQuest;
import org.nia.logic.quests.kitchen.RoofStairs;
import org.nia.model.*;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */

public enum TavernCommands implements Commands {
    BET("/bet ") {
        @Override
        public boolean isApplicable(Message message) {
            User user = User.getFromMessage(message);
            String matchText = text;
            if (user.getCurseTime() != null && user.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            if (Pattern.compile(matchText + "(\\d+)").matcher(message.getText()).find()) {
                Tournament current = Tournament.getCurrent();
                return current != null && current.isRegistration();
            }
            return false;
        }

        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message);
            String matchText = text;
            if (user.getCurseTime() != null && user.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            Matcher matcher = Pattern.compile(matchText + "(\\d+)").matcher(message.getText());
            if (matcher.find()) {
                int betCount;
                try {
                    betCount = Integer.valueOf(matcher.group(1));
                } catch (Exception ex) {
                    return "";
                }
//                if (betCount > 30) {
//                    return user + ", мы тут не магнаты, такие большие ставки не принимаем. Попробуй поставить меньше 30 " + Emoji.GOLD;
//                }
                if (betCount > user.getGold()) {
                    return user + ", у тебя нет столько золота, чтобы делать такие ставки";
                }
                if (message.getReplyToMessage() == null) {
                    return "";
                }
                Integer toBetUserID = message.getReplyToMessage().getFrom().getId();
                List<TournamentBet> betsByUserID = TournamentBet.getCurrentBetsByUserID(user);
                Optional<TournamentBet> betOptional = betsByUserID.stream().filter(bet -> bet.getTo().getUser().getUserID() == toBetUserID).findFirst();
                if (betOptional.isPresent()) {
                    TournamentBet tournamentBet = betOptional.get();
                    int sum = tournamentBet.getSum();
//                    if (sum + betCount > 30) {
//                        return user + ", мы тут не магнаты, такие большие ставки не принимаем. Попробуй поставить меньше 30 " + Emoji.GOLD;
//                    }
                    tournamentBet.setSum(sum + betCount);
                    tournamentBet.save();
                    user.setGold(user.getGold() - betCount);
                    user.save();
                    return user + ", твоя ставка увеличена!";
                } else {
                    TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(toBetUserID);
                    if (currentByUserID == null) {
                        return user + ", извини, но твой друг еще не зарегистрировался на турнир!";
                    } else {
                        TournamentBet tb = new TournamentBet();
                        tb.setFrom(user);
                        tb.setSum(betCount);
                        tb.setTo(currentByUserID);
                        tb.setTournament(currentByUserID.getTournament());
                        tb.save();
                        user.setGold(user.getGold() - betCount);
                        user.save();
                        return user + ", твоя ставка принята!";
                    }
                }
            }
            return "";
        }
    },
    GIVE_MONEY("/give ") {
        @Override
        public boolean isApplicable(Message message) {
            User user = User.getFromMessage(message);
            String matchText = text;
            if (user.getCurseTime() != null && user.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            return Pattern.compile(matchText + "(\\d+)").matcher(message.getText()).find() && message.isReply();
        }

        @Override
        public String apply(Message message) {
            User from = User.getFromMessage(message);
            String matchText = text;
            if (from.getCurseTime() != null && from.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            Matcher matcher = Pattern.compile(matchText + "(\\d+)").matcher(message.getText());
            if (matcher.find()) {
                User to = User.getFromMessage(message.getReplyToMessage().getFrom());
                if (from.getUserID() == to.getUserID()) {
                    return "";
                }
                int giveCount;
                try {
                    giveCount = Integer.valueOf(matcher.group(1));
                } catch (Exception ex) {
                    return "";
                }
                if (from.getGold() < giveCount) {
                    return from + ", у тебя нет такой суммы.";
                }
                from.setGold(from.getGold() - giveCount);
                to.setGold(to.getGold() + giveCount);
                from.save();
                to.save();
                return from + " любезно поделился " + giveCount + Emoji.GOLD + " с " + to + ". Какой щедрый человек!";
            }
            return "";
        }
    },
    ASSIST("/assist") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && message.isReply();
        }

        @Override
        public String apply(Message message) {
            User helper = User.getFromMessage(message);
            User helpTo = User.getFromMessage(message.getReplyToMessage());
            if (helpTo.onQuest()) {
                Quest currentQuest = Quest.getCurrent(helpTo);
                QuestEvent questEvent = QuestEvent.getCurrent(currentQuest);
                if (questEvent != null && questEvent.getIQuestEvent() == KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                    return RoofStairs.INIT.solve(helper, questEvent, true);
                }
            }
            return "";
        }
    },
    REGISTER("/register") {
        @Override
        public boolean isApplicable(Message message) {
            if (super.isApplicable(message)) {
                Tournament current = Tournament.getCurrent();
                User user = User.getFromMessage(message);
                return (current != null && current.isRegistration()) || (current != null && current.isAnnounced() && user.isBarmen() && message.isUserMessage());
            }
            return false;
        }

        @Override
        public String apply(Message message) {
            Tournament tournament = Tournament.getCurrent();
            User user = User.getFromMessage(message.getFrom());
            if (user.isBarmen()) {
                String text = message.getText();
                String nick = StringUtils.substringAfter(text, this.text + " ").trim();
                if (!nick.isEmpty()) {
                    User byNick = User.getByNick(nick);
                    if (byNick != null) {
                        user = byNick;
                    } else {
                        return "";
                    }
                }
            }
            return TournamentUsers.register(tournament, user);
        }
    },
    TOP("/top") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за всё время:\n");
            User.getTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getDrinkedTotal()).append("\n"));
            return sb.toString();
        }
    },
    WEEK_TOP("/week_top") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за эту неделю:\n");
            User.getWeekTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getDrinkedWeek()).append("\n"));
            return sb.toString();
        }
    },
    BK_TOP("/bk_top") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Количество побед в бойцовском клубе за всё время:\n");
            User.getBkTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getFightClubWins()).append("\n"));
            return sb.toString();
        }
    },
    BARMEN_TOP("/barmen_top") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Количество налитых напитков и розданных закусок за всё время:\n");
            User.getBarmenTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getBrewCount()).append("\n"));
            return sb.toString();
        }
    },
    DRAKA("/DRAKA") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && message.isReply();
        }

        @Override
        public String apply(Message message) {
            User current = User.getFromMessage(message.getFrom());
            if (current.getFightTime() != null) {
                long duration = TimeUnit.MINUTES.convert(DateUtils.addMinutes(current.getFightTime(), 10).getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                if (duration > 0) {
                    return current + ", ты еще не отдышался после прошлой драки, подожди ещё " + duration + " минут";
                }
            }
            User to = User.getFromMessage(message.getReplyToMessage());
            if (to.getFightWithUser() != null && to.getFightWithUser().getUserID() == current.getUserID()) {
                Random random = new Random();
                to.setFightTime(new Date());
                to.setFightWithUser(null);
                to.save();
                current.setFightTime(new Date());
                current.save();
                int curStat = current.getFightClubStatsSum() + random.nextInt(81);
                int toStat = to.getFightClubStatsSum() + random.nextInt(81);
                if (curStat >= toStat) {
                    return TournamentType.FIGHT_CLUB.getWinPhrase(current, to);
                } else {
                    return TournamentType.FIGHT_CLUB.getWinPhrase(to, current);
                }
            } else {
                current.setFightWithUser(to);
                current.save();
                return "Кажется, " + current + " хочет надрать задницу " + to + "! Посмотрим, ответит ли " + to + " на вызов.";
            }
        }
    },
    MENU("/menu") {
        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Закуски:\n");
            Arrays.stream(Food.values()).forEach(dt -> sb.append(dt.getCommand()).append(" - ").append(dt.getName()).append("\n"));
            sb.append("\nВыпивка:\n");
            Arrays.stream(DrinkType.values()).forEach(dt -> sb.append(dt.getCommand()).append(" - ").append(dt.getName()).append("\n"));
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
                    if ("CWTavernBot".equals(message.getReplyToMessage().getFrom().getUserName())) {
                        User bot = User.getByNick("CWTavernBot");
                        try {
                            DrinkPrefs.incThrow(bot, DrinkType.AVE_WHITE);
                            DrinkPrefs.incToBeThrown(drinker, DrinkType.AVE_WHITE);
                            SendMessage msg1 = getMessage(message, "Ха, еще один дурак нашелся! У меня черный пояс по метанию жбанов! /throw");
                            CWTavernBot.INSTANCE.sendMessage(msg1);
                            res = "Вот тебе жбаном по лицу, гадкий " + drinker + ". И стакан я у тебя отберу!";
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (message.getReplyToMessage().getFrom().getId().equals(message.getFrom().getId())) {
                        res = String.format(drinker.getDrinkType().getSelfThrowPhrase(), drinker);
                    } else {

                        User victim = User.getFromMessage(message.getReplyToMessage());
                        if (victim.onQuest()) {
                            Quest currentQuest = Quest.getCurrent(victim);
                            QuestEvent questEvent = QuestEvent.getCurrent(currentQuest);
                            if (questEvent != null && questEvent.getIQuestEvent() == KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                                DrinkPrefs.incThrow(drinker, drinker.getDrinkType());
                                DrinkPrefs.incToBeThrown(victim, drinker.getDrinkType());
                                drinker.setDrinkType(null);
                                drinker.setAlkoCount(0);
                                drinker.save();
                                return RoofStairs.INIT.solve(drinker, questEvent, false);
                            }
                        }
                        DrinkPrefs.incThrow(drinker, drinker.getDrinkType());
                        DrinkPrefs.incToBeThrown(victim, drinker.getDrinkType());
                        if (drinker.getAlkoCount() > 0) {
                            res = String.format(drinker.getDrinkType().getThrowTargetFullPhrase(), drinker, victim);
                        } else {
                            res = String.format(drinker.getDrinkType().getThrowTargetEmptyPhrase(), drinker, victim);
                        }
                    }
                } else {
                    DrinkPrefs.incThrow(drinker, drinker.getDrinkType());
                    res = String.format(drinker.getDrinkType().getThrowNonePhrase(), drinker);
                }
                drinker.setDrinkType(null);
                drinker.setAlkoCount(0);
                drinker.save();
                return res;
            }
        }
    },
    SHOW_STATS("/show_stats") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isAdmin();
        }

        @Override
        public String apply(Message message) {
            String name = StringUtils.substringAfter(message.getText(), text).trim();
            if (name.isEmpty()) {
                User user = User.getFromMessage(message);
                return user.getFightClubStats() + "\n" + user.getPublicFightClubStats();
            } else {
                User user = User.getByNick(name);
                return user.getFightClubStats() + "\n" + user.getPublicFightClubStats();
            }
        }
    },
    GIVE("") {
        @Override
        public boolean isApplicable(Message message) {
            User user = User.getFromMessage(message);
            if (user.getCurseTime() == null || user.getCurseTime().before(new Date())) {
                return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent() ||
                        Arrays.stream(Food.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent();
            } else {
                return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst().isPresent() ||
                        Arrays.stream(Food.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst().isPresent();
            }
        }

        @Override
        public String apply(Message message) {
            User asker = User.getFromMessage(message);
            DrinkType drinkType = null;
            Food food = null;
            if (asker.getCurseTime() == null || asker.getCurseTime().before(new Date())) {
                Optional<DrinkType> drinkTypeOptional = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst();
                Optional<Food> foodOptional = Arrays.stream(Food.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst();
                if (drinkTypeOptional.isPresent()) {
                    drinkType = drinkTypeOptional.get();
                } else if (foodOptional.isPresent()) {
                    food = foodOptional.get();
                }
            } else {
                Optional<DrinkType> drinkTypeOptional = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst();
                Optional<Food> foodOptional = Arrays.stream(Food.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst();
                if (drinkTypeOptional.isPresent()) {
                    drinkType = drinkTypeOptional.get();
                } else if (foodOptional.isPresent()) {
                    food = foodOptional.get();
                }
            }
//            Optional<DrinkType> first = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand() + "_all")).findFirst();
//            if (asker.isAdmin() && first.isPresent()) {
//                DrinkType drinkType = first.get();
//                User.getAll().forEach(user -> {
//                    if (user.getLastDrinkTime() != null) {
//                        long since = TimeUnit.MINUTES.convert(new Date().getTime() - user.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
//                        if (since < 60) {
//                            user.setDrinkType(drinkType);
//                            user.setLastDrinkTime(null);
//                            user.setAlkoCount(2);
//                            user.setWanted(null);
//                            user.save();
//                        }
//                    }
//                });
//                return "Всем кто недавно пил обновили напитки, " + drinkType.getName() + " для всех и каждому! Пейте, гости дорогие!";
//            }
            if (drinkType != null) {
                if (message.isReply() && asker.isBarmen()) {
                    if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                        return "Сам у себя заказываешь выпивку? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                    }
                    User fromMessage = User.getFromMessage(message.getReplyToMessage());
                    if (fromMessage.getAlkoCount() == 2) {
                        return "У гостя и так налито, зачем ему еще наливать?";
                    }
//                    if (fromMessage.getWanted() == drinkType) {
                    asker.incBrewCount();
                    asker.incGold();
                    asker.save();
//                    }
                    fromMessage.setAlkoCount(2);
                    fromMessage.setDrinkType(drinkType);
                    fromMessage.setWanted(null);
                    fromMessage.save();
//                if (!fromMessage.IsVisitTavernToday()) {
//                    fromMessage.setGold(fromMessage.getGold()-30);
//                }
                    return String.format(drinkType.getGivePhrase(), fromMessage);
                }
//            if (!asker.IsVisitTavernToday()) {
//                asker.setGold(asker.getGold()-30);
//            }
                asker.setWanted(drinkType);
                asker.save();
                return "";
            } else {
                if (food != null) {
                    if (message.isReply() && asker.isBarmen()) {
                        if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                            return "Сам у себя заказываешь поесть? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                        }
                        User fromMessage = User.getFromMessage(message.getReplyToMessage());
                        if (fromMessage.getFoodCount() == 1) {
                            return "У гостя и так есть закуска, зачем ему еще?";
                        }
//                        if (fromMessage.getWantedFood() == food) {
                        asker.incBrewCount();
                        asker.incGold();
                        asker.save();
//                        }
                        fromMessage.setFoodCount(1);
                        fromMessage.setFood(food);
                        fromMessage.setWantedFood(null);
                        fromMessage.save();
//                if (!fromMessage.IsVisitTavernToday()) {
//                    fromMessage.setGold(fromMessage.getGold()-30);
//                }
                        return String.format(food.getGivePhrase(), fromMessage);
                    }
//            if (!asker.IsVisitTavernToday()) {
//                asker.setGold(asker.getGold()-30);
//            }
                    asker.setWantedFood(food);
                    asker.save();
                }
            }
            return "";
        }
    },
    CURSE("/curse") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (message.isReply()) {
                drinker = User.getFromMessage(message.getReplyToMessage());
            }
            if (drinker.getCurseTime() != null && drinker.getCurseTime().after(new Date())) {
                if (drinker.getUserID() == message.getFrom().getId()) {
                    return drinker + ", вжух... не сработало!\n Похоже, ты не можешь расколдовать сам себя!";
                }
                drinker.setCurseTime(new Date());
                drinker.save();
                return drinker + ", вжух, и тебя расколдовали!";
            } else {
                drinker.setCurseTime(DateUtils.addMinutes(new Date(), 10));
                drinker.save();
                return drinker + ", вжух, и на ближайшие 10 минут тебя заколдовал бармен, придется использовать команды " +
                        "задом наперед, чтобы все получилось.";
            }
        }
    },
    EAT("/eat") {
        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (drinker.getFoodCount() <= 0) {
                return "";
            }
            if (drinker.getLastEatTime() != null) {
                long since = TimeUnit.MINUTES.convert(new Date().getTime() - drinker.getLastEatTime().getTime(), TimeUnit.MILLISECONDS);
                long wait = 30 - since;
                if (wait > 0) {
                    return drinker + " ты недавно уже поел, нельзя много кушать, лопнешь. Подожди еще " + wait + " минут";
                }
            }
            drinker.setFoodCount(0);
            drinker.setLastEatTime(new Date());
            drinker.setEatTotal(drinker.getEatTotal() + 1);
            String res = String.format(drinker.getFood().getEatPhrase(), drinker);
            drinker.setFood(null);
            drinker.save();
            return res;
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
            drinker.setDrinkedTotal(drinker.getDrinkedTotal() + drinked);
            drinker.setDrinkedWeek(drinker.getDrinkedWeek() + drinked);
            drinker.setLastDrinkTime(new Date());
            drinker.setAlkoCount(drinker.getAlkoCount() - drinked);
            String res;
            if (drinked == 2) {
                res = String.format(drinker.getDrinkType().getDrinkAllPhrase(), drinker);
            } else if (drinker.getAlkoCount() == 0) {
                res = String.format(drinker.getDrinkType().getDrinkRemainPhrase(), drinker);
            } else {
                res = String.format(drinker.getDrinkType().getDrinkPartPhrase(), drinker);
//                if (90 > new Random().nextInt(101)) {
//                    drinker.setCurseTime(DateUtils.addMinutes(new Date(), 3));
//                    res+="\n\nКажется, ты прикусил язык. Ближайшее время тебе надо писать все команды задом наперед, чтобы всё получилось!";
//                }
            }
            drinker.save();
            return res;
        }
    };
    protected String text;

    TavernCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message) {
        User user = User.getFromMessage(message);
        if (user.getCurseTime() == null || user.getCurseTime().before(new Date())) {
            return message.getText().contains(this.text);
        } else {
            return message.getText().contains("/" + StringUtils.reverse(this.text.substring(1)));
        }
    }

    @Override
    public String apply(Message message) {
        return "";
    }

}

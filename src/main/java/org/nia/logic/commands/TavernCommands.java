package org.nia.logic.commands;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.lists.DanceStep;
import org.nia.logic.lists.DrinkType;
import org.nia.logic.lists.Food;
import org.nia.logic.lists.TournamentType;
import org.nia.logic.quests.kitchen.KitchenQuest;
import org.nia.logic.quests.kitchen.RoofStairs;
import org.nia.model.*;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        public boolean isApplicable(Message message, User from) {
            String matchText = text;
            if (from.getCurseTime() != null && from.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            if (Pattern.compile(matchText + "(\\d+)").matcher(message.getText()).find()) {
                Tournament current = Tournament.getCurrent();
                return current != null && current.isRegistration();
            }
            return false;
        }

        @Override
        public String apply(Message message, User from) {
            String matchText = text;
            if (from.getCurseTime() != null && from.getCurseTime().after(new Date())) {
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
                if (betCount < 10) {
                    return from + ", у нас тут серьезное мероприятие, гроши не собираем! Хочешь поставить, ставь хотя бы десятку " + Emoji.GOLD + "!";
                }
                if (betCount > from.getGold()) {
                    return from + ", у тебя нет столько золота, чтобы делать такие ставки";
                }
                if (message.getReplyToMessage() == null) {
                    return "";
                }
                Long toBetUserID = message.getReplyToMessage().getFrom().getId();
                Tournament current = Tournament.getCurrent();
                List<TournamentBet> betsByUserID = TournamentBet.getCurrentBetsByUserID(current.getPublicID(), from);
                Optional<TournamentBet> betOptional = betsByUserID.stream().filter(bet -> bet.getTo().getUser().getUserID() == toBetUserID).findFirst();
                if (betOptional.isPresent()) {
                    TournamentBet tournamentBet = betOptional.get();
                    int sum = tournamentBet.getSum();
//                    if (sum + betCount > 30) {
//                        return user + ", мы тут не магнаты, такие большие ставки не принимаем. Попробуй поставить меньше 30 " + Emoji.GOLD;
//                    }
                    tournamentBet.setSum(sum + betCount);
                    tournamentBet.save();
                    from.setGold(from.getGold() - betCount);
                    from.save();
                    return from + ", твоя ставка увеличена!";
                } else {
                    TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(toBetUserID);
                    if (currentByUserID == null) {
                        return from + ", извини, но твой друг еще не зарегистрировался на турнир!";
                    } else {
                        TournamentBet tb = new TournamentBet();
                        tb.setFrom(from);
                        tb.setSum(betCount);
                        tb.setTo(currentByUserID);
                        tb.setTournament(currentByUserID.getTournament());
                        tb.save();
                        from.setGold(from.getGold() - betCount);
                        from.save();
                        return from + ", твоя ставка принята!";
                    }
                }
            }
            return "";
        }
    },
    GIVE_MONEY("/give ") {
        @Override
        public boolean isApplicable(Message message, User from) {
            String matchText = text;
            if (from.getCurseTime() != null && from.getCurseTime().after(new Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim().substring(1)) + " ";
            }
            return Pattern.compile(matchText + "(\\d+)").matcher(message.getText()).find() && message.isReply();
        }

        @Override
        public String apply(Message message, User from) {
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
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && message.isReply();
        }

        @Override
        public String apply(Message message, User from) {
            User helpTo = User.getFromMessage(message.getReplyToMessage());
            if (helpTo.onQuest()) {
                Quest currentQuest = Quest.getCurrent(helpTo);
                QuestEvent questEvent = QuestEvent.getCurrent(currentQuest);
                if (questEvent != null && questEvent.getIQuestEvent() == KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                    return RoofStairs.INIT.solve(from, questEvent, true);
                }
            }
            return "";
        }
    },
    REGISTER("/register") {
        @Override
        public boolean isApplicable(Message message, User from) {
            if (super.isApplicable(message, from)) {
                Tournament current = Tournament.getCurrent();
                return (current != null && current.isRegistration()) || (current != null && current.isAnnounced()
                        && from.isBarmenOrAdmin() && message.isUserMessage());
            }
            return false;
        }

        @Override
        public String apply(Message message, User from) {
            Tournament tournament = Tournament.getCurrent();
            User user = from;
            if (user.isBarmenOrAdmin()) {
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
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за всё время:\n");
            User.getTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getDrinkedTotalNormalized()).append("\n"));
            return sb.toString();
        }
    },
    WEEK_TOP("/week_top") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за эту неделю:\n");
            User.getWeekTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getDrinkedWeekNormalized()).append("\n"));
            return sb.toString();
        }
    },
    BK_TOP("/bk_top") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            StringBuilder sb = new StringBuilder();
            sb.append("Количество побед в бойцовском клубе за всё время:\n");
            User.getBkTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getFightClubWins()).append("\n"));
            return sb.toString();
        }
    },
    BARMEN_TOP("/barmen_top") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            StringBuilder sb = new StringBuilder();
            sb.append("Количество налитых напитков и розданных закусок за всё время:\n");
            User.getBarmenTop().forEach(dt -> sb.append(dt.getNick() != null ? dt.getNick() : dt.getName()).append(" - ").append(dt.getBrewCount()).append("\n"));
            return sb.toString();
        }
    },
    DRAKA("/DRAKA") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && message.isReply();
        }

        @Override
        public String apply(Message message, User from) {
            if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                from.setFightTime(new Date());
                from.save();
                return from + " начал биться головой об стену. К моменту когда его остановили, физиономия опухла так, " +
                        "что теперь его и родная мать не узнает. Похоже кому-то нельзя пить так много " + DrinkType.getRandom().getCommand() + "!";
            }
            if (from.getFightTime() != null) {
                long duration = TimeUnit.MINUTES.convert(DateUtils.addMinutes(from.getFightTime(), 10).getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                if (duration > 0) {
                    return from + ", ты еще не отдышался после прошлой драки, подожди ещё " + duration + " минут.";
                }
            }
            User to = User.getFromMessage(message.getReplyToMessage());
            if (to.getFightWithUser() != null && to.getFightWithUser().getUserID() == from.getUserID()) {
                Random random = new Random();
                to.setFightTime(new Date());
                to.setFightWithUser(null);
                to.save();
                from.setFightTime(new Date());
                from.save();
                int curStat = from.getFightClubStatsSum() + random.nextInt(81);
                int toStat = to.getFightClubStatsSum() + random.nextInt(81);
                if (curStat >= toStat) {
                    return TournamentType.FIGHT_CLUB.getWinPhrase(from, to);
                } else {
                    return TournamentType.FIGHT_CLUB.getWinPhrase(to, from);
                }
            } else {
                from.setFightWithUser(to);
                from.save();
                return "Кажется, " + from + " хочет надрать задницу " + to + "! Посмотрим, ответит ли " + to + " на вызов.";
            }
        }
    },
    DANCE("/secret_dance") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && message.isReply();
        }

        @Override
        public String apply(Message message, User from) {
            if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                return "Форэверэлоны танцуют в другом месте!";
            }
            if (Dancing.getCurrent(from) != null) {
                return from + ", ты уже танцуешь!";
            }
            if (from.getDanceTime() != null) {
                long duration = TimeUnit.MINUTES.convert(DateUtils.addMinutes(from.getDanceTime(), 2).getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                if (duration > 0) {
                    return from + ", ты совсем недавно отжигал своим танцем, не занимай место, дай другим " +
                            "потанцевать! Подожди ещё " + duration + " минут.";
                }
            }
            User to = User.getFromMessage(message.getReplyToMessage());
            if (to.getDanceWithUserID() != null && to.getDanceWithUser().getUserID() == from.getUserID()) {
                Dancing dancing = new Dancing();
                dancing.setFirstDancer(to);
                dancing.setSecondDancer(from);
                dancing.setChatID(message.getChatId());
                dancing.setCurrentStep(DanceStep.VALS1);
                dancing.save();
                to.setDanceTime(new Date());
                to.setDanceWithUser(from);
                to.save();
                from.setDanceTime(new Date());
                from.save();
                return from + " принял приглашение на танец от " + to + " и сейчас они будут зажигать! Танцоры - " +
                        "готовьтесь, зрители - поддержите смельчаков аплодисментами! Сегодня они покажут нам " + dancing.getCurrentStep().getDanceName();
            } else {
                from.setDanceWithUser(to);
                from.save();
                return from + " приглашает потанцевать " + to + "! Что же это, расцветает новая любовь вместе с " +
                        "романтическим танцем, или просто два мастера хотят зажечь публику? " + to + ", принимай приглашение!";
            }
        }
    },
    MENU("/menu") {
        @Override
        public String apply(Message message, User from) {
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
        public String apply(Message message, User from) {
            if (from.getDrinkType() == null) {
                return "";
            } else {
                String res = "";
                if (message.isReply()) {
                    if ("CWTavernBot".equals(message.getReplyToMessage().getFrom().getUserName())) {
                        User bot = User.getByNick("CWTavernBot");
                        try {
                            bot.incThrow(DrinkType.AVE_WHITE);
                            from.incToBeThrown(DrinkType.AVE_WHITE);
                            SendMessage msg1 = getMessage(message, "Ха, еще один дурак нашелся! У меня черный пояс по метанию жбанов! /throw");
                            CWTavernBot.INSTANCE.execute(msg1);
                            res = "Вот тебе жбаном по лицу, гадкий " + from + ". И стакан я у тебя отберу!";
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (message.getReplyToMessage().getFrom().getId().equals(message.getFrom().getId())) {
                        res = String.format(from.getDrinkType().getSelfThrowPhrase(), from);
                    } else {

                        User victim = User.getFromMessage(message.getReplyToMessage());
                        if (victim.onQuest()) {
                            Quest currentQuest = Quest.getCurrent(victim);
                            QuestEvent questEvent = QuestEvent.getCurrent(currentQuest);
                            if (questEvent != null && questEvent.getIQuestEvent() == KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                                from.incThrow(from.getDrinkType());
                                victim.incToBeThrown(from.getDrinkType());
                                from.setDrinkType(null);
                                from.setAlkoCount(0);
                                from.save();
                                return RoofStairs.INIT.solve(from, questEvent, false);
                            }
                        }
                        from.incThrow(from.getDrinkType());
                        victim.incToBeThrown(from.getDrinkType());
                        if (from.getAlkoCount() > 0) {
                            res = String.format(from.getDrinkType().getThrowTargetFullPhrase(), from, victim);
                        } else {
                            res = String.format(from.getDrinkType().getThrowTargetEmptyPhrase(), from, victim);
                        }
                    }
                } else {
                    from.incThrow(from.getDrinkType());
                    res = String.format(from.getDrinkType().getThrowNonePhrase(), from);
                }
                from.setDrinkType(null);
                from.setAlkoCount(0);
                from.save();
                return res;
            }
        }
    },
    SHOW_STATS("/show_stats") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            String name = StringUtils.substringAfter(message.getText(), text).trim();
            if (name.isEmpty()) {
                return from.getFightClubStats() + "\n" + from.getPublicFightClubStats();
            } else {
                User user = User.getByNick(name);
                return user.getFightClubStats() + "\n" + user.getPublicFightClubStats();
            }
        }
    },
    GIVE("") {
        @Override
        public boolean isApplicable(Message message, User from) {
            if (from.getCurseTime() == null || from.getCurseTime().before(new Date())) {
                return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent() ||
                        Arrays.stream(Food.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent();
            } else {
                return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst().isPresent() ||
                        Arrays.stream(Food.values()).filter(dt -> message.getText().contains("/" + StringUtils.reverse(dt.getCommand().substring(1)))).findFirst().isPresent();
            }
        }

        @Override
        public String apply(Message message, User from) {
            DrinkType drinkType = null;
            Food food = null;
            if (from.getCurseTime() == null || from.getCurseTime().before(new Date())) {
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
                if (message.isReply() && from.isBarmenOrAdmin()) {
                    if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                        return "Сам у себя заказываешь выпивку? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                    }
                    User fromMessage = User.getFromMessage(message.getReplyToMessage());
                    if (fromMessage.getAlkoCount() == 2) {
                        return "У гостя и так налито, зачем ему еще наливать?";
                    }
//                    if (fromMessage.getWanted() == drinkType) {
                    from.incBrewCount();
                    from.incGold();
                    from.save();
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
                from.setWanted(drinkType);
                from.save();
                return "";
            } else {
                if (food != null) {
                    if (message.isReply() && from.isBarmenOrAdmin()) {
                        if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                            return "Сам у себя заказываешь поесть? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                        }
                        User fromMessage = User.getFromMessage(message.getReplyToMessage());
                        if (fromMessage.getFoodCount() == 1) {
                            return "У гостя и так есть закуска, зачем ему еще?";
                        }
//                        if (fromMessage.getWantedFood() == food) {
                        from.incBrewCount();
                        from.incGold();
                        from.save();
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
                    from.setWantedFood(food);
                    from.save();
                }
            }
            return "";
        }
    },
    CURSE("/curse") {
        @Override
        public boolean isApplicable(Message message, User from) {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin();
        }

        @Override
        public String apply(Message message, User from) {
            if (message.isReply()) {
                from = User.getFromMessage(message.getReplyToMessage());
            }
            if (from.getCurseTime() != null && from.getCurseTime().after(new Date())) {
                if (from.getUserID() == message.getFrom().getId()) {
                    return from + ", вжух... не сработало!\n Похоже, ты не можешь расколдовать сам себя!";
                }
                from.setCurseTime(new Date());
                from.save();
                return from + ", вжух, и тебя расколдовали!";
            } else {
                from.setCurseTime(DateUtils.addMinutes(new Date(), 10));
                from.save();
                return from + ", вжух, и на ближайшие 10 минут тебя заколдовал бармен, придется использовать команды " +
                        "задом наперед, чтобы все получилось.";
            }
        }
    },
    EAT("/eat") {
        @Override
        public String apply(Message message, User from) {
            if (from.getFoodCount() <= 0) {
                return "";
            }
            if (from.getLastEatTime() != null) {
                long since = TimeUnit.MINUTES.convert(new Date().getTime() - from.getLastEatTime().getTime(), TimeUnit.MILLISECONDS);
                long wait = 30 - since;
                if (wait > 0) {
                    return from + " ты недавно уже поел, нельзя много кушать, лопнешь. Подожди еще " + wait + " минут";
                }
            }
            from.setFoodCount(0);
            from.setLastEatTime(new Date());
            from.setEatTotal(from.getEatTotal() + 1);
            String res = String.format(from.getFood().getEatPhrase(), from);
            from.setFood(null);
            from.save();
            return res;
        }
    },
    DRINK("/drink") {
        @Override
        public String apply(Message message, User from) {
            if (from.getAlkoCount() <= 0) {
                return "";
            }
            if (from.getLastDrinkTime() != null) {
                long since = TimeUnit.MINUTES.convert(new Date().getTime() - from.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
                long wait = 5 - since;
                if (wait > 0) {
                    return from + " ты недавно уже пил. Подожди еще " + wait + " минут";
                }
            }
            int drinked = new Random().nextInt(from.getAlkoCount()) + 1;
            from.incDrink(from.getDrinkType(), drinked);
            from.setDrinkedTotal(from.getDrinkedTotal() + drinked);
            from.setDrinkedWeek(from.getDrinkedWeek() + drinked);
            from.setLastDrinkTime(new Date());
            from.setAlkoCount(from.getAlkoCount() - drinked);
            String res;
            if (drinked == 2) {
                res = String.format(from.getDrinkType().getDrinkAllPhrase(), from);
            } else if (from.getAlkoCount() == 0) {
                res = String.format(from.getDrinkType().getDrinkRemainPhrase(), from);
            } else {
                res = String.format(from.getDrinkType().getDrinkPartPhrase(), from);
//                if (90 > new Random().nextInt(101)) {
//                    drinker.setCurseTime(DateUtils.addMinutes(new Date(), 3));
//                    res+="\n\nКажется, ты прикусил язык. Ближайшее время тебе надо писать все команды задом наперед, чтобы всё получилось!";
//                }
            }
            from.save();
            return res;
        }
    };
    protected String text;

    TavernCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message, User from) {
        if (from.getCurseTime() == null || from.getCurseTime().before(new Date())) {
            return message.getText().contains(this.text);
        } else {
            return message.getText().contains("/" + StringUtils.reverse(this.text.substring(1)));
        }
    }

    @Override
    public String apply(Message message, User from) {
        return "";
    }

}

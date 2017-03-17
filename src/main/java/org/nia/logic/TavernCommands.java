package org.nia.logic;

import org.apache.commons.lang3.StringUtils;
import org.nia.bots.CWTavernBot;
import org.nia.model.DrinkPrefs;
import org.nia.model.Tournament;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */

public enum TavernCommands implements Commands {
    REGISTER("/register") {
        @Override
        public boolean isApplicable(Message message) {
            return super.isApplicable(message) && Tournament.getCurrent().isRegistration();
        }

        @Override
        public String apply(Message message) {
            Tournament tournament = Tournament.getCurrent();
            return TournamentUsers.register(tournament, User.getFromMessage(message.getFrom()));
        }
    },
    TOP("/top") {
        @Override
        public boolean isApplicable(Message message) {
            boolean topMessage = message.getText().startsWith(text);
            return topMessage && User.getFromMessage(message).isBarmen();
        }

        @Override
        public String apply(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("Главные выпивохи таверны за всё время:\n");
            User.getTop().forEach(dt -> sb.append(dt.getNick()).append(" - ").append(dt.getDrinkedTotal()).append("\n"));
            return sb.toString();
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
    TEST("/test123") {
        @Override
        public String apply(Message message) {
            User user = User.getFromMessage(message.getFrom());
            if (message.getText().startsWith("/test123 ")) {
                user = User.getByNick(StringUtils.substringAfter(message.getText(), "/test123 "));
            }
            int knowledge = user.getDrinkedTotal() / 10;
            DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
            int strength = drinkPrefs.getPrefMap().entrySet().stream()
                    .filter(e -> Arrays.asList(DrinkType.AVE_WHITE, DrinkType.BEER, DrinkType.GHOST)
                            .contains(e.getKey()))
                    .mapToInt(e -> e.getValue().getToDrink()).sum() / 5 + 1;
            int charism = drinkPrefs.getPrefMap().entrySet().stream()
                    .filter(e -> Arrays.asList(DrinkType.CHLEN, DrinkType.RED_POWER, DrinkType.MORDOR)
                            .contains(e.getKey()))
                    .mapToInt(e -> e.getValue().getToDrink()).sum() / 5 + 1;
            int agility = drinkPrefs.getPrefMap().entrySet().stream()
                    .mapToInt(e -> e.getValue().getToThrow()).sum() / 5 + 1;
            int constitution = drinkPrefs.getPrefMap().entrySet().stream()
                    .mapToInt(e -> e.getValue().getToBeThrown()).sum() / 5 + 1;
            String stats = "\nСила: " + strength + "\nЛовкость: " + agility + "\nОбаяние: " + charism + "\nСтойкость: " + constitution + "\nЗнание таверны: " + knowledge;
            int score = strength + charism + agility + constitution + knowledge;

            return String.format(TournamentType.FIGHT_CLUB.getStartPhrase() + "\nТвои характеристики: " + stats + "\n\nОТЛАДКА:" + score, user);
        }
    },
    GIVE("") {
        @Override
        public boolean isApplicable(Message message) {
            return Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent() ||
                    Arrays.stream(Food.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst().isPresent();
        }

        @Override
        public String apply(Message message) {
            User asker = User.getFromMessage(message);
            Optional<DrinkType> first = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand() + "_all")).findFirst();
            if (asker.isAdmin() && first.isPresent()) {
                DrinkType drinkType = first.get();
                User.getAll().forEach(user -> {
                    if (user.getLastDrinkTime() != null) {
                        long since = TimeUnit.MINUTES.convert(new Date().getTime() - user.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
                        if (since < 60) {
                            user.setDrinkType(drinkType);
                            user.setLastDrinkTime(null);
                            user.setAlkoCount(2);
                            user.setWanted(null);
                            user.save();
                        }
                    }
                });
                return "Всем кто недавно пил обновили напитки, " + drinkType.getName() + " для всех и каждому! Пейте, гости дорогие!";
            }
            Optional<DrinkType> drink = Arrays.stream(DrinkType.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst();
            if (drink.isPresent()) {
                if (message.isReply() && asker.isBarmen()) {
                    if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                        return "Сам у себя заказываешь выпивку? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                    }
                    User fromMessage = User.getFromMessage(message.getReplyToMessage());
                    fromMessage.setAlkoCount(2);
                    DrinkType drinkType = drink.get();
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
                DrinkType drinkType = drink.get();
                asker.setWanted(drinkType);
                asker.save();
                return "";
            } else {
                Optional<Food> eat = Arrays.stream(Food.values()).filter(dt -> message.getText().contains(dt.getCommand())).findFirst();
                if (eat.isPresent()) {
                    Food food = eat.get();
                    if (message.isReply() && asker.isBarmen()) {
                        if (message.getFrom().getId().equals(message.getReplyToMessage().getFrom().getId())) {
                            return "Сам у себя заказываешь поесть? Ну нет, так дело не пойдет, кто тебя потом домой понесет?";
                        }
                        User fromMessage = User.getFromMessage(message.getReplyToMessage());
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
    EAT("/eat") {
        @Override
        public String apply(Message message) {
            User drinker = User.getFromMessage(message);
            if (drinker.getFoodCount() <= 0) {
                return "";
            }
//            if (drinker.getLastDrinkTime() != null) {
//                long since = TimeUnit.MINUTES.convert(new Date().getTime() - drinker.getLastDrinkTime().getTime(), TimeUnit.MILLISECONDS);
//                long wait = 5 - since;
//                if (wait > 0) {
//                    return drinker + " ты недавно уже пил. Подожди еще " + wait + " минут";
//                }
//            }
            drinker.setFoodCount(0);
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
            drinker.setLastDrinkTime(new Date());
            drinker.setAlkoCount(drinker.getAlkoCount() - drinked);
            String res;
            if (drinked == 2) {
                res = String.format(drinker.getDrinkType().getDrinkAllPhrase(), drinker);
            } else if (drinker.getAlkoCount() == 0) {
                res = String.format(drinker.getDrinkType().getDrinkRemainPhrase(), drinker);
            } else {
                res = String.format(drinker.getDrinkType().getDrinkPartPhrase(), drinker);
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
        return message.getText().contains(this.text);
    }

    @Override
    public List<KeyboardRow> getKeyboard(Message message) {
        return null;
    }

    @Override
    public String apply(Message message) {
        return "";
    }

}

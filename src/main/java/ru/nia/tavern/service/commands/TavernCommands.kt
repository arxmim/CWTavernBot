package ru.nia.tavern.service.commands

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.nia.strings.Emoji
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Dancing
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.Tournament
import ru.nia.tavern.model.TournamentBet
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.User
import ru.nia.tavern.model.types.DanceStep
import ru.nia.tavern.model.types.DrinkType
import ru.nia.tavern.model.types.Food
import ru.nia.tavern.model.types.TournamentType
import ru.nia.tavern.quests.kitchen.KitchenQuest
import ru.nia.tavern.quests.kitchen.RoofStairs
import ru.nia.tavern.service.CWTavernBot
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author IANazarov
 */

enum class TavernCommands private constructor(protected var text: String) : Commands {
    BET("/bet ") {
        override fun isApplicable(message: Message, from: User): Boolean {
            var matchText = text
            if (from.curseTime != null && from.curseTime.after(Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim { it <= ' ' }.substring(1)) + " "
            }
            if (Pattern.compile("$matchText(\\d+)").matcher(message.text).find()) {
                val current = Tournament.getCurrent()
                return current != null && current.isRegistration
            }
            return false
        }

        override fun apply(message: Message, from: User): String {
            var matchText = text
            if (from.curseTime != null && from.curseTime.after(Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim { it <= ' ' }.substring(1)) + " "
            }
            val matcher = Pattern.compile("$matchText(\\d+)").matcher(message.text)
            if (matcher.find()) {
                val betCount: Int
                try {
                    betCount = Integer.valueOf(matcher.group(1))
                } catch (ex: Exception) {
                    return ""
                }

                if (betCount < 10) {
                    return from.toString() + ", у нас тут серьезное мероприятие, гроши не собираем! Хочешь поставить, ставь хотя бы десятку " + Emoji.GOLD + "!"
                }
                if (betCount > from.gold) {
                    return from.toString() + ", у тебя нет столько золота, чтобы делать такие ставки"
                }
                if (message.replyToMessage == null) {
                    return ""
                }
                val toBetUserID = message.replyToMessage.from.id
                val current = Tournament.getCurrent()
                val betsByUserID = TournamentBet.getCurrentBetsByUserID(current!!.publicID, from)
                val betOptional = betsByUserID.stream().filter { bet -> bet.to.user.userID == toBetUserID }.findFirst()
                if (betOptional.isPresent) {
                    val tournamentBet = betOptional.get()
                    val sum = tournamentBet.sum
                    //                    if (sum + betCount > 30) {
                    //                        return user + ", мы тут не магнаты, такие большие ставки не принимаем. Попробуй поставить меньше 30 " + Emoji.GOLD;
                    //                    }
                    tournamentBet.sum = sum + betCount
                    tournamentBet.save()
                    from.gold = from.gold - betCount
                    from.save()
                    return from.toString() + ", твоя ставка увеличена!"
                } else {
                    val currentByUserID = TournamentUsers.getCurrentByUserID(toBetUserID!!)
                    if (currentByUserID == null) {
                        return from.toString() + ", извини, но твой друг еще не зарегистрировался на турнир!"
                    } else {
                        val tb = TournamentBet()
                        tb.from = from
                        tb.sum = betCount
                        tb.to = currentByUserID
                        tb.tournament = currentByUserID.tournament
                        tb.save()
                        from.gold = from.gold - betCount
                        from.save()
                        return from.toString() + ", твоя ставка принята!"
                    }
                }
            }
            return ""
        }
    },
    GIVE_MONEY("/give ") {
        override fun isApplicable(message: Message, from: User): Boolean {
            var matchText = text
            if (from.curseTime != null && from.curseTime.after(Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim { it <= ' ' }.substring(1)) + " "
            }
            return Pattern.compile("$matchText(\\d+)").matcher(message.text).find() && message.isReply
        }

        override fun apply(message: Message, from: User): String {
            var matchText = text
            if (from.curseTime != null && from.curseTime.after(Date())) {
                matchText = "/" + StringUtils.reverse(matchText.trim { it <= ' ' }.substring(1)) + " "
            }
            val matcher = Pattern.compile("$matchText(\\d+)").matcher(message.text)
            if (matcher.find()) {
                val to = User.getFromMessage(message.replyToMessage.from)
                if (from.userID == to.userID) {
                    return ""
                }
                val giveCount: Int
                try {
                    giveCount = Integer.valueOf(matcher.group(1))
                } catch (ex: Exception) {
                    return ""
                }

                if (from.gold < giveCount) {
                    return from.toString() + ", у тебя нет такой суммы."
                }
                from.gold = from.gold - giveCount
                to.gold = to.gold + giveCount
                from.save()
                to.save()
                return from.toString() + " любезно поделился " + giveCount + Emoji.GOLD + " с " + to + ". Какой щедрый человек!"
            }
            return ""
        }
    },
    ASSIST("/assist") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && message.isReply
        }

        override fun apply(message: Message, from: User): String {
            val helpTo = User.getFromMessage(message.replyToMessage)
            if (helpTo.onQuest()) {
                val currentQuest = Quest.getCurrent(helpTo)
                val questEvent = QuestEvent.getCurrent(currentQuest)
                if (questEvent != null && questEvent.iQuestEvent === KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                    return RoofStairs.INIT.solve(from, questEvent, true)
                }
            }
            return ""
        }
    },
    REGISTER("/register") {
        override fun isApplicable(message: Message, from: User): Boolean {
            if (super.isApplicable(message, from)) {
                val current = Tournament.getCurrent()
                return current != null && current.isRegistration || (current != null && current.isAnnounced
                        && from.isBarmenOrAdmin && message.isUserMessage)
            }
            return false
        }

        override fun apply(message: Message, from: User): String {
            val tournament = Tournament.getCurrent()
            var user: User? = from
            if (user!!.isBarmenOrAdmin) {
                val text = message.text
                val nick = StringUtils.substringAfter(text, this.text + " ").trim { it <= ' ' }
                if (!nick.isEmpty()) {
                    val byNick = User.getByNick(nick)
                    if (byNick != null) {
                        user = byNick
                    } else {
                        return ""
                    }
                }
            }
            return TournamentUsers.register(tournament, user)
        }
    },
    TOP("/top") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin
        }

        override fun apply(message: Message, from: User): String {
            val sb = StringBuilder()
            sb.append("Главные выпивохи таверны за всё время:\n")
            User.getTop().forEach { dt -> sb.append(if (dt.nick != null) dt.nick else dt.name).append(" - ").append(dt.drinkedTotalNormalized).append("\n") }
            return sb.toString()
        }
    },
    WEEK_TOP("/week_top") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin
        }

        override fun apply(message: Message, from: User): String {
            val sb = StringBuilder()
            sb.append("Главные выпивохи таверны за эту неделю:\n")
            User.getWeekTop().forEach { dt -> sb.append(if (dt.nick != null) dt.nick else dt.name).append(" - ").append(dt.drinkedWeekNormalized).append("\n") }
            return sb.toString()
        }
    },
    BK_TOP("/bk_top") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin
        }

        override fun apply(message: Message, from: User): String {
            val sb = StringBuilder()
            sb.append("Количество побед в бойцовском клубе за всё время:\n")
            User.getBkTop().forEach { dt -> sb.append(if (dt.nick != null) dt.nick else dt.name).append(" - ").append(dt.fightClubWins).append("\n") }
            return sb.toString()
        }
    },
    BARMEN_TOP("/barmen_top") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin
        }

        override fun apply(message: Message, from: User): String {
            val sb = StringBuilder()
            sb.append("Количество налитых напитков и розданных закусок за всё время:\n")
            User.getBarmenTop().forEach { dt -> sb.append(if (dt.nick != null) dt.nick else dt.name).append(" - ").append(dt.brewCount).append("\n") }
            return sb.toString()
        }
    },
    DRAKA("/DRAKA") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && message.isReply
        }

        override fun apply(message: Message, from: User): String {
            if (message.from.id == message.replyToMessage.from.id) {
                from.fightTime = Date()
                from.save()
                return from.toString() + " начал биться головой об стену. К моменту когда его остановили, физиономия опухла так, " +
                        "что теперь его и родная мать не узнает. Похоже кому-то нельзя пить так много " + DrinkType.getRandom().command + "!"
            }
            if (from.fightTime != null) {
                val duration = TimeUnit.MINUTES.convert(DateUtils.addMinutes(from.fightTime, 10).time - Date().time, TimeUnit.MILLISECONDS)
                if (duration > 0) {
                    return from.toString() + ", ты еще не отдышался после прошлой драки, подожди ещё " + duration + " минут."
                }
            }
            val to = User.getFromMessage(message.replyToMessage)
            if (to.fightWithUser != null && to.fightWithUser!!.userID == from.userID) {
                val random = Random()
                to.fightTime = Date()
                to.fightWithUser = null
                to.save()
                from.fightTime = Date()
                from.save()
                val curStat = from.fightClubStatsSum + random.nextInt(81)
                val toStat = to.fightClubStatsSum + random.nextInt(81)
                return if (curStat >= toStat) {
                    TournamentType.FIGHT_CLUB.getWinPhrase(from, to)
                } else {
                    TournamentType.FIGHT_CLUB.getWinPhrase(to, from)
                }
            } else {
                from.fightWithUser = to
                from.save()
                return "Кажется, $from хочет надрать задницу $to! Посмотрим, ответит ли $to на вызов."
            }
        }
    },
    DANCE("/secret_dance") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && message.isReply
        }

        override fun apply(message: Message, from: User): String {
            if (message.from.id == message.replyToMessage.from.id) {
                return "Форэверэлоны танцуют в другом месте!"
            }
            if (Dancing.getCurrent(from) != null) {
                return from.toString() + ", ты уже танцуешь!"
            }
            if (from.danceTime != null) {
                val duration = TimeUnit.MINUTES.convert(DateUtils.addMinutes(from.danceTime, 5).time - Date().time, TimeUnit.MILLISECONDS)
                if (duration > 0) {
                    return from.toString() + ", ты совсем недавно отжигал своим танцем, не занимай место, дай другим " +
                            "потанцевать! Подожди ещё " + duration + " минут."
                }
            }
            val to = User.getFromMessage(message.replyToMessage)
            if (to.danceWithUserID != null && to.danceWithUser!!.userID == from.userID) {
                val dancing = Dancing()
                dancing.firstDancer = to
                dancing.secondDancer = from
                dancing.currentStep = DanceStep.FOO1
                dancing.save()
                to.danceTime = Date()
                to.danceWithUser = from
                to.save()
                from.danceTime = Date()
                from.save()
                return from.toString() + " принял приглашение на танец от " + to + " и сейчас они будут зажигать! Танцоры - " +
                        "готовьтесь, зрители - поддержите смельчаков аплодисментами! Сегодня они покажут нам " + dancing.currentStep.danceName
            } else {
                from.danceWithUser = to
                from.save()
                return from.toString() + " приглашает потанцевать " + to + "! Что же это, расцветает новая любовь вместе с " +
                        "романтическим танцем, или просто два мастера хотят зажечь публику? " + to + ", принимай приглашение!"
            }
        }
    },
    MENU("/menu") {
        override fun apply(message: Message, from: User): String {
            val sb = StringBuilder()
            sb.append("Закуски:\n")
            Arrays.stream(Food.values()).forEach { dt -> sb.append(dt.command).append(" - ").append(dt.getName()).append("\n") }
            sb.append("\nВыпивка:\n")
            Arrays.stream(DrinkType.values()).forEach { dt -> sb.append(dt.command).append(" - ").append(dt.getName()).append("\n") }
            return sb.toString()
        }
    },
    THROW("/throw") {
        override fun apply(message: Message, from: User): String {
            if (from.drinkType == null) {
                return ""
            } else {
                var res = ""
                if (message.isReply) {
                    if ("CWTavernBot" == message.replyToMessage.from.userName) {
                        val bot = User.getByNick("CWTavernBot")
                        try {
                            bot!!.incThrow(DrinkType.AVE_WHITE)
                            from.incToBeThrown(DrinkType.AVE_WHITE)
                            val msg1 = getMessage(message, "Ха, еще один дурак нашелся! У меня черный пояс по метанию жбанов! /throw")
                            CWTavernBot.INSTANCE.sendMessage(msg1)
                            res = "Вот тебе жбаном по лицу, гадкий $from. И стакан я у тебя отберу!"
                        } catch (e: TelegramApiException) {
                            e.printStackTrace()
                        }

                    } else if (message.replyToMessage.from.id == message.from.id) {
                        res = String.format(from.drinkType.selfThrowPhrase, from)
                    } else {

                        val victim = User.getFromMessage(message.replyToMessage)
                        if (victim.onQuest()) {
                            val currentQuest = Quest.getCurrent(victim)
                            val questEvent = QuestEvent.getCurrent(currentQuest)
                            if (questEvent != null && questEvent.iQuestEvent === KitchenQuest.KitchenEvent.ROOF_STAIRS) {
                                from.incThrow(from.drinkType)
                                victim.incToBeThrown(from.drinkType)
                                from.drinkType = null
                                from.alkoCount = 0
                                from.save()
                                return RoofStairs.INIT.solve(from, questEvent, false)
                            }
                        }
                        from.incThrow(from.drinkType)
                        victim.incToBeThrown(from.drinkType)
                        if (from.alkoCount > 0) {
                            res = String.format(from.drinkType.throwTargetFullPhrase, from, victim)
                        } else {
                            res = String.format(from.drinkType.throwTargetEmptyPhrase, from, victim)
                        }
                    }
                } else {
                    from.incThrow(from.drinkType)
                    res = String.format(from.drinkType.throwNonePhrase, from)
                }
                from.drinkType = null
                from.alkoCount = 0
                from.save()
                return res
            }
        }
    },
    SHOW_STATS("/show_stats") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isAdmin
        }

        override fun apply(message: Message, from: User): String {
            val name = StringUtils.substringAfter(message.text, text).trim { it <= ' ' }
            if (name.isEmpty()) {
                return from.fightClubStats + "\n" + from.publicFightClubStats
            } else {
                val user = User.getByNick(name)
                return user!!.fightClubStats + "\n" + user.publicFightClubStats
            }
        }
    },
    GIVE("") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return if (from.curseTime == null || from.curseTime.before(Date())) {
                Arrays.stream(DrinkType.values()).filter { dt -> message.text.contains(dt.command) }.findFirst().isPresent || Arrays.stream(Food.values()).filter { dt -> message.text.contains(dt.command) }.findFirst().isPresent
            } else {
                Arrays.stream(DrinkType.values()).filter { dt -> message.text.contains("/" + StringUtils.reverse(dt.command.substring(1))) }.findFirst().isPresent || Arrays.stream(Food.values()).filter { dt -> message.text.contains("/" + StringUtils.reverse(dt.command.substring(1))) }.findFirst().isPresent
            }
        }

        override fun apply(message: Message, from: User): String {
            var drinkType: DrinkType? = null
            var food: Food? = null
            if (from.curseTime == null || from.curseTime.before(Date())) {
                val drinkTypeOptional = Arrays.stream(DrinkType.values()).filter { dt -> message.text.contains(dt.command) }.findFirst()
                val foodOptional = Arrays.stream(Food.values()).filter { dt -> message.text.contains(dt.command) }.findFirst()
                if (drinkTypeOptional.isPresent) {
                    drinkType = drinkTypeOptional.get()
                } else if (foodOptional.isPresent) {
                    food = foodOptional.get()
                }
            } else {
                val drinkTypeOptional = Arrays.stream(DrinkType.values()).filter { dt -> message.text.contains("/" + StringUtils.reverse(dt.command.substring(1))) }.findFirst()
                val foodOptional = Arrays.stream(Food.values()).filter { dt -> message.text.contains("/" + StringUtils.reverse(dt.command.substring(1))) }.findFirst()
                if (drinkTypeOptional.isPresent) {
                    drinkType = drinkTypeOptional.get()
                } else if (foodOptional.isPresent) {
                    food = foodOptional.get()
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
                if (message.isReply && from.isBarmenOrAdmin) {
                    if (message.from.id == message.replyToMessage.from.id) {
                        return "Сам у себя заказываешь выпивку? Ну нет, так дело не пойдет, кто тебя потом домой понесет?"
                    }
                    val fromMessage = User.getFromMessage(message.replyToMessage)
                    if (fromMessage.alkoCount == 2) {
                        return "У гостя и так налито, зачем ему еще наливать?"
                    }
                    //                    if (fromMessage.getWanted() == drinkType) {
                    from.incBrewCount()
                    from.incGold()
                    from.save()
                    //                    }
                    fromMessage.alkoCount = 2
                    fromMessage.drinkType = drinkType
                    fromMessage.wanted = null
                    fromMessage.save()
                    //                if (!fromMessage.IsVisitTavernToday()) {
                    //                    fromMessage.setGold(fromMessage.getGold()-30);
                    //                }
                    return String.format(drinkType.givePhrase, fromMessage)
                }
                //            if (!asker.IsVisitTavernToday()) {
                //                asker.setGold(asker.getGold()-30);
                //            }
                from.wanted = drinkType
                from.save()
                return ""
            } else {
                if (food != null) {
                    if (message.isReply && from.isBarmenOrAdmin) {
                        if (message.from.id == message.replyToMessage.from.id) {
                            return "Сам у себя заказываешь поесть? Ну нет, так дело не пойдет, кто тебя потом домой понесет?"
                        }
                        val fromMessage = User.getFromMessage(message.replyToMessage)
                        if (fromMessage.foodCount == 1) {
                            return "У гостя и так есть закуска, зачем ему еще?"
                        }
                        //                        if (fromMessage.getWantedFood() == food) {
                        from.incBrewCount()
                        from.incGold()
                        from.save()
                        //                        }
                        fromMessage.foodCount = 1
                        fromMessage.food = food
                        fromMessage.wantedFood = null
                        fromMessage.save()
                        //                if (!fromMessage.IsVisitTavernToday()) {
                        //                    fromMessage.setGold(fromMessage.getGold()-30);
                        //                }
                        return String.format(food.givePhrase, fromMessage)
                    }
                    //            if (!asker.IsVisitTavernToday()) {
                    //                asker.setGold(asker.getGold()-30);
                    //            }
                    from.wantedFood = food
                    from.save()
                }
            }
            return ""
        }
    },
    CURSE("/curse") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isBarmenOrAdmin
        }

        override fun apply(message: Message, from: User): String {
            var from = from
            if (message.isReply) {
                from = User.getFromMessage(message.replyToMessage)
            }
            if (from.curseTime != null && from.curseTime.after(Date())) {
                if (from.userID == message.from.id) {
                    return from.toString() + ", вжух... не сработало!\n Похоже, ты не можешь расколдовать сам себя!"
                }
                from.curseTime = Date()
                from.save()
                return from.toString() + ", вжух, и тебя расколдовали!"
            } else {
                from.curseTime = DateUtils.addMinutes(Date(), 10)
                from.save()
                return from.toString() + ", вжух, и на ближайшие 10 минут тебя заколдовал бармен, придется использовать команды " +
                        "задом наперед, чтобы все получилось."
            }
        }
    },
    EAT("/eat") {
        override fun apply(message: Message, from: User): String {
            if (from.foodCount <= 0) {
                return ""
            }
            if (from.lastEatTime != null) {
                val since = TimeUnit.MINUTES.convert(Date().time - from.lastEatTime.time, TimeUnit.MILLISECONDS)
                val wait = 30 - since
                if (wait > 0) {
                    return from.toString() + " ты недавно уже поел, нельзя много кушать, лопнешь. Подожди еще " + wait + " минут"
                }
            }
            from.foodCount = 0
            from.lastEatTime = Date()
            from.eatTotal = from.eatTotal + 1
            val res = String.format(from.food.eatPhrase, from)
            from.food = null
            from.save()
            return res
        }
    },
    DRINK("/drink") {
        override fun apply(message: Message, from: User): String {
            if (from.alkoCount <= 0) {
                return ""
            }
            if (from.lastDrinkTime != null) {
                val since = TimeUnit.MINUTES.convert(Date().time - from.lastDrinkTime.time, TimeUnit.MILLISECONDS)
                val wait = 5 - since
                if (wait > 0) {
                    return from.toString() + " ты недавно уже пил. Подожди еще " + wait + " минут"
                }
            }
            val drinked = Random().nextInt(from.alkoCount) + 1
            from.incDrink(from.drinkType, drinked)
            from.drinkedTotal = from.drinkedTotal + drinked
            from.drinkedWeek = from.drinkedWeek + drinked
            from.lastDrinkTime = Date()
            from.alkoCount = from.alkoCount - drinked
            val res: String
            if (drinked == 2) {
                res = String.format(from.drinkType.drinkAllPhrase, from)
            } else if (from.alkoCount == 0) {
                res = String.format(from.drinkType.drinkRemainPhrase, from)
            } else {
                res = String.format(from.drinkType.drinkPartPhrase, from)
                //                if (90 > new Random().nextInt(101)) {
                //                    drinker.setCurseTime(DateUtils.addMinutes(new Date(), 3));
                //                    res+="\n\nКажется, ты прикусил язык. Ближайшее время тебе надо писать все команды задом наперед, чтобы всё получилось!";
                //                }
            }
            from.save()
            return res
        }
    };

    override fun isApplicable(message: Message, from: User): Boolean {
        return if (from.curseTime == null || from.curseTime.before(Date())) {
            message.text.contains(this.text)
        } else {
            message.text.contains("/" + StringUtils.reverse(this.text.substring(1)))
        }
    }

    override fun apply(message: Message, from: User): String {
        return ""
    }

}

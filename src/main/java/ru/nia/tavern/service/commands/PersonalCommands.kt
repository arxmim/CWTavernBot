package ru.nia.tavern.service.commands

import org.apache.commons.lang3.StringUtils
import org.nia.strings.Emoji
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.DrinkPref
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.Tournament
import ru.nia.tavern.model.User
import ru.nia.tavern.model.types.Location
import ru.nia.tavern.model.types.TournamentState
import ru.nia.tavern.model.types.TournamentType
import ru.nia.tavern.service.CWTavernBot
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author IANazarov
 */
enum class PersonalCommands private constructor(text: String) : Commands {
    START("/start") {
        override fun apply(message: Message, from: User): String {
            return "Добро пожаловать в нашу таверну! Я буду помогать бармену разливать напитки."
        }
    },
    HELP("/help") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && message.isUserMessage
        }

        override fun apply(message: Message, from: User): String {
            return "/help - справка\n" +
                    "/menu - список напитков\n" +
                    "/throw - бросить стакан в человека из reply-сообщения. Для этого должен быть стакан!\n" +
                    "/drink - выпить свой напиток\n\n" +
                    "Чтобы получить напиток, надо попросить его у бармена в чате таверны, либо ввести команду " +
                    "напитка и тогда официантка выдаст его когда будет делать обход посетителей (обход происходит несколько раз в час)"
        }
    },
    CREATE_TOURNAMENT("/create_tournament") {
        override fun isApplicable(message: Message, from: User): Boolean {
            return super.isApplicable(message, from) && from.isAdmin
        }

        override fun apply(message: Message, from: User): String {
            val matcher = Pattern.compile("/create_tournament ([\\w]+) ([0-9]{2}):([0-9]{2}) (4|8|16|32)").matcher(message.text)
            if (matcher.find()) {
                val tournament = Tournament()
                try {
                    val group = matcher.group(1)
                    val tournamentType = TournamentType.valueOf(group)
                    tournament.type = tournamentType
                } catch (ex: Exception) {
                    return "Неверно указан тип турнира"
                }

                tournament.state = TournamentState.ANOUNCE

                val tz = TimeZone.getTimeZone("Europe/Moscow")
                val gc = Calendar.getInstance(tz)
                gc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(matcher.group(2)))
                gc.set(Calendar.MINUTE, Integer.valueOf(matcher.group(3)))
                gc.set(Calendar.SECOND, 0)
                gc.set(Calendar.MILLISECOND, 0)

                tournament.registrationDateTime = gc.time
                tournament.maxUsers = Integer.valueOf(matcher.group(4))
                tournament.save()
                return "Турнир создан.\n$tournament"
            } else {
                return "Что-то не заполнено. Турнир не создан"
            }
        }
    },
    SET_ADMIN("/secret_set_admin ") {

        override fun apply(message: Message, from: User): String {
            val nick = StringUtils.substringAfter(message.text, text)
            val user = User.getByNick(nick)
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику"
            } else {
                user.isAdmin = !user.isAdmin
                user.save()
                return if (user.isAdmin) {
                    "Пользователь $nick теперь админ"
                } else {
                    "Пользователь $nick больше не админ"
                }
            }
        }
    },
    SET_BARMEN("/set_barmen ") {
        override fun isApplicable(message: Message, from: User): Boolean {
            val setAdminMessage = message.text.startsWith(text)
            return setAdminMessage && from.isAdmin
        }

        override fun apply(message: Message, from: User): String {
            val nick = StringUtils.substringAfter(message.text, text)
            val user = User.getByNick(nick)
            if (user == null) {
                return "Этот посетитель еще не обращался к тавернщику"
            } else {
                user.isBarmen = !user.isBarmen
                user.save()
                return if (user.isBarmen) {
                    "Пользователю $nick дан барменский фартук"
                } else {
                    "Пользователь $nick лишен барменского фартука"
                }
            }
        }
    },
    QUEST("Взять задание у Остапа") {
        override fun apply(message: Message, from: User): String {
            if (from.inTavern()) {
                val randomQuest = Location.getRandomQuest()
                from.location = Location.QUEST
                val quest = Quest()
                quest.startTime = Date()
                quest.user = from
                quest.eventTime = randomQuest.firstEventTime
                quest.questEnum = randomQuest
                quest.goldEarned = 0
                quest.returnTime = null
                quest.save()
                from.save()
                return randomQuest.iQuest.start + "\n\nТы можешь вернуться в любой момент, но чем дольше ты проведешь на задании, тем больше получишь в награду."
            } else {
                return ""
            }
        }
    },
    QUEST_RETURN("Вернуться с задания") {
        override fun apply(message: Message, from: User): String {
            if (from.onQuest()) {
                val quest = Quest.getCurrent(from)
                val event = QuestEvent.getCurrent(quest)
                if (event != null) {
                    event.win = false
                    event.step.doFinal(event)
                    event.save()
                    val linkedQuestEvent = event.linkedQuestEvent
                    if (linkedQuestEvent != null) {
                        linkedQuestEvent.win = true
                        linkedQuestEvent.step.doFinal(linkedQuestEvent)
                        val linkedQuest = linkedQuestEvent.quest
                        linkedQuest.eventTime = linkedQuest.questEnum.getNextEventTime(linkedQuest)
                        linkedQuest.save()
                        linkedQuestEvent.save()
                        try {
                            CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTimedMessage(linkedQuest.user, from.toString() + " сбежал. Ты свалил на него все проблемы и смог немного подзаработать."))
                        } catch (e: TelegramApiException) {
                            e.printStackTrace()
                        }

                    }
                }
                quest!!.returnTime = Date()
                val reward = quest.reward
                quest.goldEarned = reward
                quest.save()
                from.location = Location.TAVERN
                from.gold = from.gold + reward
                from.save()
                return "Ты вернулся с задания, заработав " + reward + Emoji.GOLD
            } else return if (from.inTavern()) {
                "Ты уже вернулся с задания."
            } else {
                ""
            }
        }
    },
    MY_INFO("Информация о тебе") {
        override fun apply(message: Message, from: User): String {
            var res = ""
            if (from.inTavern()) {
                var drink = "нет напитка"
                if (from.drinkType != null) {
                    drink = from.drinkType.getName()
                    if (from.alkoCount == 0) {
                        drink += " (внутри пусто)"
                    } else if (from.alkoCount == 1) {
                        drink += " (примерно половина)"
                    } else {
                        drink += " (полный)"
                    }
                }
                var eat = "нет еды"
                if (from.food != null) {
                    eat = from.food.getName()
                }
                res = "Ты находишься в таверне. У тебя в руках " + drink + " и перед тобой на столе " + eat + ".\nВ кармане " + from.gold + Emoji.GOLD
            } else if (from.onQuest()) {
                res = "Ты выполняешь поручение Остапа.\nВ кармане у тебя " + from.gold + Emoji.GOLD
            }
            if (from.curseTime != null && from.curseTime.after(Date())) {
                val duration = TimeUnit.MINUTES.convert(from.curseTime.time - Date().time, TimeUnit.MILLISECONDS)
                res += "\nТы закодован еще на $duration минут."
            }
            res += ("\n\n" + from.fightClubStats
                    + "\n\n " + Emoji.DRINK + "Выпито напитков в таверне за эту неделю/всего: " + from.drinkedWeekNormalized + "/" + from.drinkedTotalNormalized
                    + "\n" + Emoji.MEDAL + "Побед в боях бойцовского клуба: " + from.fightClubWins)
            return res
        }
    },
    SECRET_MY_INFO("/my_info") {
        override fun apply(message: Message, from: User): String {
            val name = StringUtils.substringAfter(message.text, text).trim { it <= ' ' }
            val user: User?
            if (name.isEmpty()) {
                user = from
            } else {
                user = User.getByNick(name)
            }
            val prefs = DrinkPref.getByUser(user)
            val sb = StringBuilder()
            sb.append("А ты успел засветиться в нашей таверне!\nВот твоя статистика в формате Напиток-Выпито-Брошено-В тебя бросили:\n\n")
            prefs.forEach { e ->
                sb.append(e.drinkType.command)
                        .append(": ").append(e.toDrinkNormalized)
                        .append(", ").append(e.toThrow)
                        .append(", ").append(e.toBeThrown).append("\n\n")
            }
            return sb.toString()
        }
    };

    var text: String
        protected set

    init {
        this.text = text
    }


    override fun apply(message: Message, from: User): String {
        return ""
    }

    override fun isApplicable(message: Message, from: User): Boolean {
        return message.text.contains(this.text)
    }
}

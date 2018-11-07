package ru.nia.tavern.quests.kitchen

import org.nia.strings.Emoji
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.User
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.service.CWTavernBot
import java.util.*

/**
 * @author IANazarov
 */
enum class RoofStairs : IQuestStep {

    JUMP("Прыгнуть", "Ты успешно спрыгнул со своего насеста.\nБармен на всякий случай дал тебе денег на поход к лекарю, " + "и хотя ты совсем не пострадал, от денег ты не стал отказываться.", "Ты спрыгнул ровно на столик %s. Надо было смотреть куда прыгаешь! \nТы не только испортил людям " + "отдых, но еще и больно ударился, вдобавок тебе пришлось возмещать пострадавшему весь нанесенный ущерб.") {
        override fun doWork(questEvent: QuestEvent) {
            questEvent.winChance = 20
        }

        override fun getGoodText(quest: Quest): String {
            try {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(quest.user.toString() + " спрыгнул сам!"))
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

            return super.getGoodText(quest)
        }

        override fun getBadText(quest: Quest): String {
            val max = User.getAll().stream().max { c1, c2 ->
                if (c1.userID == quest.user.userID) return@User.getAll().stream().max - 1
                if (c2.userID == quest.user.userID) return@User.getAll().stream().max 1
                if (c1.lastDrinkTime != null && c2.lastDrinkTime == null) return@User.getAll().stream().max 1
                if (c1.lastDrinkTime == null && c2.lastDrinkTime != null) return@User.getAll().stream().max - 1
                if (c1.lastDrinkTime != null && c2.lastDrinkTime != null) {
                    return@User.getAll().stream().max java . lang . Long . compare c1.lastDrinkTime.time, c2.getLastDrinkTime().getTime())
                }
                0
            }
            var res = "посетителя"
            if (max.isPresent) {
                res = max.get().toString()
                try {
                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(quest.user.toString()
                            + " неудачно спрыгнул ровно на " + res + ", сшиб того со стула и разлил напитки. Недотепа!"))
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            }
            return String.format(badText, res)
        }
    },
    INIT("", "Тебя отправили менять свечи в лампадах, расположенных под самым потолком общего зала. Ты залез на " +
            "балку под самой крышей, заменил свечи, и тут твоя стремянка начала заваливаться!\nТы можешь попробовать " +
            "спрыгнуть, но это точно ничем хорошим не кончится!\nЛучше попроси кого-нибудь в таверне поставить " +
            "лестницу обратно, для этого напиши в чат таверны и попроси кого-нибудь ответить на твое сообщение " +
            "командой /assist. Но берегись /throw!", listOf<IQuestStep>(JUMP)) {
        override fun doWork(questEvent: QuestEvent) {
            try {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(questEvent.quest.user.toString()
                        + " менял свечи под потолком таверны, и лестница под ним опрокинулась! Бедолага сидит на " +
                        "балке под крышей таверны и ему нужна помощь! Ответьте на его сообщение командой " +
                        "/assist, чтобы подать ему лестницу!"))
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        }
    };

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    protected var badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = KitchenQuest.KitchenEvent.ROOF_STAIRS

    private constructor(command: String, goodText: String, badText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
    }

    private constructor(command: String, text: String, next: List<IQuestStep>) {
        this.command = command
        this.text = text
        this.next.addAll(next)
    }

    fun solve(helper: User, questEvent: QuestEvent, isWin: Boolean): String {
        val currentQuest = questEvent.quest
        val helpTo = currentQuest.user
        if (isWin) {
            questEvent.win = true
            questEvent.save()
            currentQuest.eventTime = currentQuest.questEnum.getNextEventTime(currentQuest)
            currentQuest.save()
            val sendMessage = ServingMessage.getTimedMessage(helpTo, "Тебе помог " + helper + ", вернув " +
                    "лестницу на место. Ты успешно спустился, а бармен выдал тебе и твоему спасителю небольшую премию.")
            helper.gold = helper.gold + 3
            helper.save()
            try {
                CWTavernBot.INSTANCE.sendMessage(sendMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

            return helper.toString() + " поставил лестницу на место и помог " + helpTo + " спуститься. В благодарность " +
                    "за спасение сотрудника, он получает 3" + Emoji.GOLD + " от таверны!"
        } else {
            questEvent.win = true
            questEvent.save()
            currentQuest.eventTime = currentQuest.questEnum.getNextEventTime(currentQuest)
            currentQuest.save()
            val sendMessage = ServingMessage.getTimedMessage(helpTo, helper.toString() + " кинул в тебя стакан, и ты " +
                    "упал вниз. Хулигана оштрафовали, а ты получил небольшую премию от бармена для компенсации " +
                    "морального вреда.")
            var gold = helper.gold
            if (gold < 10) {
                gold = 0
            } else {
                gold = gold - 10
            }
            helper.gold = gold
            helper.save()
            try {
                CWTavernBot.INSTANCE.sendMessage(sendMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

            return helper.toString() + " кинул свой напиток в " + helpTo + " и тот сорвался вниз. За нападение на сотрудника " +
                    "таверны он оштрафован на 10" + Emoji.GOLD + "!"//" и ближайший час его не будут обслуживать!";
        }

    }

    override fun getText(quest: Quest): String {
        return text
    }

    override fun getNext(quest: Quest): List<IQuestStep> {
        return next
    }

    override fun getCommand(formatParam: String): String {
        return command
    }

    override fun getGoodText(quest: Quest): String {
        return goodText
    }

    override fun getBadText(quest: Quest): String {
        return badText
    }
}

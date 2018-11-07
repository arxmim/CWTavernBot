package ru.nia.tavern.quests.buyfish

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author Иван, 26.03.2017.
 */
enum class NoisyMan : IQuestStep {

    KICK("Выгнать мужика", "Ты без труда притянул мужика за воротник и пообещал устроить ему веселую жизнь, если он " +
            "сунется к тебе еще раз. Кажется, зануда испачкал штаны! Впрочем, ему хватило ума дать тебе немного " +
            "\"отступных\", чтобы не эскалировать конфликт.", "Ты попытался выгнать мужика, но тот был явно сильнее " +
            "тебя. Хорошо, что твоя голова оказалась крепче соседского прилавка, об который он тебя бил." +
            "Тебе пришлось заплатить администрации рынка за разбитый прилавок."),
    ASK("\"Ты рыбу будешь покупать?\"", "- Да! - ответил мужик.\nТы продал ему воблу по цене королевского осьминога.", "- Нет!  - сказал мужик и свалил.\nТы впустую потратил время."),
    RUDE("\"А вам какая разница?\"", "- А вы со всеми такой грубый?", Arrays.asList<IQuestStep>(ASK, KICK)),
    COST("\"С побережья!\"", "- С речного?", Arrays.asList<IQuestStep>(RUDE, KICK)),
    NO_DEGUSTATE("\"Нет, не дам\"", "- А откуда рыбу возите?", Arrays.asList<IQuestStep>(COST, KICK)),
    FRESH("\"Да, свежая\"", "- А попробовать дадите?", Arrays.asList<IQuestStep>(NO_DEGUSTATE, KICK)),
    SELL("\"Да, рыбу продаю\"", "- А рыба свежая?", Arrays.asList<IQuestStep>(FRESH, KICK)),
    INIT("", "К тебе подошел мужик в заношенной куртке.\n\n- Вы тут рыбу продаете? А рыба свежая? А попробовать дадите? " + "- начал он засыпать тебя вопросами.", Arrays.asList<IQuestStep>(SELL, KICK));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = SellFishQuest.SellFishEvent.SELL_FISH_NOISY_MAN

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

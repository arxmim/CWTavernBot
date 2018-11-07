package ru.nia.tavern.quests.buyfish

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class DragonMeat : IQuestStep {

    IGNORE("Отказаться от драконьего мяса", "Карлик отошел от твоего прилавка, и продолжил торговать своим сомнительным товаром в другой части рынка. " + "Через некоторое время к тебе пришла стража, вылавливающая карлика-жулика, и за помощь следствию ты получил небольшую награду.", "Карлик отошел от твоего прилавка, а через некоторое время ты заметил, что с твоего прилавка пропала " + "часть товара. Похоже, этот жулик обворовал тебя!"),
    BUY_DRAGON_MEAT("Купить драконье мясо", "Ты купил один ломтик драконьего мяса за часть собранной выручки. Видимо ты был вдребезги пьян, так как сразу же решил попробовать его на вкус...\n\n" +
            "Похоже, карлик не врал, и мясо действительно драконье! У тебя прибавилось сил, обаяния и ума, доспех засиял, " +
            "и даже твоё копьё как будто стало длиннее. Окрыленный новыми возможностями, ты без труда продал часть " +
            "своего товара за двойную цену. Жаль, что чудодейственный эффект мяса быстро выветрился, до того как ты " +
            "испробовал своё удлинненное копье в бою.", "Ты купил один ломтик драконьего мяса за часть собранной выручки. Чуть позже по запаху ты понял, что " + "это совсем не мясо, но карлика и след простыл."),
    INIT("", "К твоему прилавку с рыбой подошел карлик с мешком.\n" +
            "- Драконье мясо, недорого! Эй, купи драконьего мяса! Ты торгуешь рыбой? Хм, у дракона тоже есть чешуя, " +
            "так что считай что мы с тобой оба торгуем рыбой! - с этими словами путник раскрыл перед тобой мешок. \n" +
            "Он оказался забит чем-то, похожим на засохшее дерьмо.", Arrays.asList<IQuestStep>(BUY_DRAGON_MEAT, IGNORE));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = SellFishQuest.SellFishEvent.SELL_FISH_HOBBIT

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
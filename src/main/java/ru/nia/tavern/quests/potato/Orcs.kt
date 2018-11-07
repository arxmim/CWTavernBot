package ru.nia.tavern.quests.potato

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class Orcs : IQuestStep {
    ATACK("Уничтожить грибы", "У тебя был с собой огнемет, так что ты смог быстро сжечь грибы и вернуться к работе.", "Ты долго бился на кулаках с грибами. Когда последний гриб был побежден, уже стемнело. " + "Похоже, ты не успеешь накопать достаточно картошки в срок."),
    IGNORE("Проигнорировать грибы", "Ты решил не трогать эти странные грибы. Пока ты копал картошку, грибы выкопались и достали " +
            "откуда-то острые предметы, похожие на смесь бензопилы и топора. Тебе повезло, на тебе была фиолетовая " +
            "спецовка и грибы, не заметив тебя, умчались с грозными воплями на соседний огород.", "Ты решил не трогать эти странные грибы. Пока ты копал картошку, грибы выкопались и достали " +
            "откуда-то острые предметы, похожие на смесь бензопилы и топора, и решили тебя ограбить. " +
            "Увы, у тебя ничего с собой не было, так что они просто выбили тебе зуб и залутали часть картошки."),
    INIT("Неподалеку от грядки ты увидел странные гигантские грибы, у каждого есть пара рук и голова.", Arrays.asList<IQuestStep>(ATACK, IGNORE));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private val command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = PotatoQuest.PotatoEvent.POTATO_ORCS

    private constructor(command: String, goodText: String, badText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
    }

    private constructor(text: String, next: List<IQuestStep>) {
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

package ru.nia.tavern.quests.judge

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class AIJudging : IQuestStep {
    END1("", "", ""),
    END2("", "", ""),
    SGT_FIRST("Выслушать сержанта", "", Arrays.asList<IQuestStep>(END1, END2)),
    GHOST_FIRST("Выслушать призрака", "", Arrays.asList<IQuestStep>(END1, END2)),
    INIT("", "Следующее дело оказалось таким необычным, что местный судья решил передать свои полномочия тебе.\nМожешь " +
            "гордиться временным повышением! Суть дела такова - сержант из городской охраны обратился к помощи " +
            "нелегальных волшебников и вселил дух своего деда в стальную оболочку доспехов. Теперь дух вполне " +
            "самостоятелен и хочет жить своей жизнью, ну а сержант считает его своей собственностью. Дело осложняется " +
            "еще тем, что призрак в доспехах успел разбить пару лиц, и теперь от пострадавших поступают иски. Только " +
            "неясно, кого считать виноватым - призрака или сержанта.\nСперва тебе нужно выслушать аргументы сторон " +
            "относительно самостоятельности призрака.", Arrays.asList<IQuestStep>(GHOST_FIRST, SGT_FIRST));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override//        return PotatoQuest.PotatoEvent.POTATO_GIANT_JUK;
    val iQuest: IQuestEvent?
        get() = null

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

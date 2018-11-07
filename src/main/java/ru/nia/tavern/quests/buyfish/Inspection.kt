package ru.nia.tavern.quests.buyfish

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author Иван, 26.03.2017.
 */
enum class Inspection : IQuestStep {
    GIVE_DOCS("Поискать документы", "Ты не нашел документы, но пока инспектор зевал, смог стащить документы у соседа. " +
            "По ним выходило, что ты продаешь детские игрушки и письменные принадлежности, но твой ораторский талант " +
            "выручил тебя. Ничего не заподозривший инспектор направился к соседу, которому ты вернул \"неожиданно " +
            "нашедшиеся\" документы за небольшую награду.", "Ты не нашел документы, но пока инспектор зевал, смог стащить документы у соседа. По ним выходило, что " +
            "ты продаешь детские игрушки и письменные принадлежности, чего не мог не заметить инспектор. Ты был " +
            "оштрафован!"),
    GIVE_MONEY("Дать взятку", "Ты был очень обаятелен с инспектором:\n" +
            "- Лучше поделиться частью выручки с вами, инспектор, чем с этими гадами из налоговой!\nИнспектор ушел к " +
            "соседу, кляня коррупционеров из налоговой, которые не дают спокойно спать честным людям. Сосед, тоже " +
            "торгующий рыбой, не смог договориться с инспектором, у тебя стало на одного конкурента меньше, и ты " +
            "неплохо заработал.", "Твои медвежьи манеры не помогли договориться с инспектором. Тебя оштрафовали!"),
    INIT("", "К тебе подошел инспектор из РыбПотребНадзора.\n-Ваши документы на товар, пожалуйста!", Arrays.asList<IQuestStep>(GIVE_DOCS, GIVE_MONEY));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = SellFishQuest.SellFishEvent.SELL_FISH_INSPECTION

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

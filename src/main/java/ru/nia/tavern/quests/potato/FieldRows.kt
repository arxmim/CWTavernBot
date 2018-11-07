package ru.nia.tavern.quests.potato

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.ICrossQuestStep
import ru.nia.tavern.quests.IQuestAction
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.crossevents.FieldRowsAction
import java.util.*

/**
 * @author IANazarov
 */
enum class FieldRows : ICrossQuestStep {
    ANSWER_HOW_BAD("Ну, я старался...", listOf<IQuestAction>(FieldRowsAction.ANSWER_HOW_BAD), true),
    ANSWER_HOW_GOOD("Отлично вскопал!", listOf<IQuestAction>(FieldRowsAction.ANSWER_HOW_GOOD), true),
    QUESTION_HOW(Arrays.asList<IQuestStep>(ANSWER_HOW_GOOD, ANSWER_HOW_BAD)),
    ANSWER_WHAT_GRASS("Росла какая-то трава", listOf<IQuestAction>(FieldRowsAction.ANSWER_WHAT_GRASS), true),
    ANSWER_WHAT_NOTHING("Ничего не росло", listOf<IQuestAction>(FieldRowsAction.ANSWER_WHAT_NOTHING), true),
    QUESTION_WHAT(Arrays.asList<IQuestStep>(ANSWER_WHAT_NOTHING, ANSWER_WHAT_GRASS)),
    AGREE("Договориться", "Твоё обояние не подвело тебя - сосед выплатил тебе деньги за работу, причем даже больше чем обещал Остап." + " Может, стоит работать на соседа?", "Твоя пьяная рожа и заплетающийся язык не убедили соседа. Он заставил тебя выплатить небольшую сумму за " + "нанесенный его рассаде ущерб.", FieldRowsAction.INIT,
            "Тебе не удалось договориться с соседом мирным путем, и вы пошли в местную администрацию, разбираться в " +
                    "досудебном порядке. Там вы встретили %s, который согласился рассмотреть ваш спор и постарается " +
                    "тебе помочь."),
    THREAT("Угрожать", "Твоя пьяная рожа и дико выпученные глаза убедили соседа что с тобой лучше не связываться - он заплатил " + "тебе за работу на его огороде.", "Ты набычился, закатал рукава, взял лопату в руки и начал двигаться на соседа.\n\nВнезапно заиграл " +
            "бессмертный хит Эннио Мориконе, а сосед неторопливым движением достал из кобуры на поясе армейский кольт.\n\n" +
            "Через мнгновение ты уже доставал кошелек и отсчитывал возмещение за нанесенный соседу ущерб."),
    NEW_ROW("Начать копать новую грядку", "Пока ты вскапывал новую грядку, тебе попался какой-то предмет.\n\nОго, похоже это мешочек с золотом!", "Вскапывание новой грядки шло гораздо медленнее. Ты потерял кучу времени, выкорчевывая валуны и пропалывая сорняки."),
    CONTINUE("Продолжить копать грядку", "Через некоторое время пришел какой-то мужик, и сказал что ты уже давно вышел за пределы огорода Остапа " +
            "и теперь копаешь на его земле. Мда, ты опять попал в неудобное положение. Неплохо бы получить с этого мужика" +
            " деньги за твою работу - попробуй договориться с ним, по-хорошему или по-плохому.", Arrays.asList<IQuestStep>(AGREE, THREAT)),
    INIT("", "Тебе удалось найти хорошую рыхлую почву, на которой копать картошку проще. Ты уже вскопал достаточно длинную грядку.", Arrays.asList<IQuestStep>(CONTINUE, NEW_ROW));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private var goodText = ""
    private var badText = "Ты ничего не предпринял и всё пошло наперекосяк."
    override var isWaitUser = false
        private set(value: Boolean) {
            super.isWaitUser = value
        }
    override val interceptText = ""
    private val actionList = ArrayList<IQuestAction>()

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = PotatoQuest.PotatoEvent.POTATO_FIELD_ROWS


    private constructor(next: List<IQuestStep>) {
        this.next.addAll(next)
    }

    private constructor(command: String, actionList: MutableList<IQuestAction>, isWaitUser: Boolean) {
        this.command = command
        this.actionList = actionList
        this.isWaitUser = isWaitUser
    }

    private constructor(command: String, goodText: String, badText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
    }

    private constructor(command: String, goodText: String, badText: String, interceptionAction: IQuestAction, interceptText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
        this.actionList.add(interceptionAction)
        this.interceptText = interceptText
        this.isWaitUser = true
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

    override fun getActionList(): List<IQuestAction> {
        return actionList
    }
}

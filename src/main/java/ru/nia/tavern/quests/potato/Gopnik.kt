package ru.nia.tavern.quests.potato

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class Gopnik : IQuestStep {
    PAPIROSA("- Папироску будете?", "Хорошо что у тебя осталась пара папирос после общения с той черепахой и чертовски хорошо что парням " +
            "они пришлись по вкусу. Ты уговорил их сходить на соседский огород. Спустя некоторое время парни " +
            "вернулись и отсыпали тебе часть добытой ветоши, которую можно продать барахольщикам.", "Ты внезапно вспомнил, что последнюю папиросу ты выкурил еще на прошлой неделе. Парням такой развод " + "не понравился и ты получил по шее.\nОчнувшись, ты увидел что с тебя стащили сапоги, придется покупать новые.\n"),
    POTATO("- Нуууу, есть саженцы картошки...", "- Ну и дурак, кому нужна твоя картошка! - рассмеялись молодые люди.\n" +
            "- Держи, тут пара монет, выпей чего-нибудь в таверне, может поумнеешь! - с этими словами один из парней " +
            "выдал тебе мешочек с золотом.", "Кажется, ты оскорбил парней, и тебя избили. Уходя, они растоптали часть твоих грядок."),
    NO_DUST("- Нет у меня порошка! Я картошку копаю!", "- А что есть?", Arrays.asList<IQuestStep>(PAPIROSA, POTATO)),
    DRAKA("Ввязаться в драку", "Ты не зря столько времени проводишь на свежем воздухе, твоей силе позавидуют многие. Тебе даже драться " + "не пришлось, парни сами разбежались, увидев какой ты агрессивный.", "Такому слабаку как ты не стоило бы искать драки. Тебя избили.\n" + "Поскольку никакого порошка у тебя не оказалось, парни просто растоптали часть твоих грядок.\n"),
    INIT("", "К тебе подошли гоповатого вида парни:\n" + "- Есть чё? Мы видели как ты на рынке порошком по 15 торговал. Гони товар!", Arrays.asList<IQuestStep>(NO_DUST, DRAKA));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = PotatoQuest.PotatoEvent.POTATO_GOPNIK

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

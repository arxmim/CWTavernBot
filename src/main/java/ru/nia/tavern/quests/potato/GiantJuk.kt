package ru.nia.tavern.quests.potato

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class GiantJuk : IQuestStep {
    ATACK("Атаковать жука", "Ты храбро атаковал насекомое и жук бежал от твоей праведной ярости.", "Когда жук начал приближаться, ты внезапно вспомнил, что забыл меч в таверне. " + "Ты сбегал в таверну за мечом, но когда вернулся, жук уже уполз в закат, сожрав часть картошки."),
    PLAY("Подразнить жука", "Тебе удалось привлечь внимание жука лязганьем своих доспехов. Жук начал неспеша " + "ползти в твою сторону и в итоге тебе удалось увести его в соседний огород. Теперь это не твоя поблема.", "Ты кричал жуку разные неприличные слова, но он так и не отреагировал. Жаль, на тебе нет железных " + "доспехов, говорят, такие жуки реагируют только на грохот металла. Наевшись картошки, жук уполз в закат."),
    INIT("На огороде ты увидел колорадского жука размером с лошадь.", Arrays.asList<IQuestStep>(ATACK, PLAY));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private val command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = PotatoQuest.PotatoEvent.POTATO_GIANT_JUK

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

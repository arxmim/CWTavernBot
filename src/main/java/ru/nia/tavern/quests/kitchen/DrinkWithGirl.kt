package ru.nia.tavern.quests.kitchen

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class DrinkWithGirl : IQuestStep {

    DRINK_FINAL("Накатить еще", "С каждой следующей рюмкой ты блистал остроумием всё больше и больше, но в какой-то момент выпитое взяло " +
            "свое и ты просто вырубился. Девушка позаботилась о тебе - с помощью Лизы она отнесла тебя, пьяненького, в " +
            "подсобку. Когда ты немного пришел в себя, тебя повели к самому Хозяину Таверны. Ты ждал, что сейчас " +
            "будет показательная порка, но...\nХозяин пожал тебе руку, поблагодарил за то, что ты прекрасно развлек " +
            "дочку мэра, благодаря чему у таверны теперь меньше проблем с налоговой, и выписал тебе премию.\n", "После следующей пары стаканов тебя едва не стошнило прямо на твою спутницу, и тебе пришлось срочно " +
            "ретироваться в сторону уборной. Ну и позор!\nКогда ты вернулся, тебя встретила не та очаровательная " +
            "девушка, а самый злючий из всех барменов. Он напомнил тебе, что пить на работе запрещено и оштрафовал тебя.\n"),
    DENY("Отказаться", "Ты отказался пить с девушкой - наверное, оно и к лучшему. Сегодня очень много посетителей и ты получил " + "много чаевых.", "Ты отказался пить с девушкой. Увы, она оказалось дочкой мэра и подобный отказ сочла " +
            "за оскобление. Когда ты в очередной раз выносил ведро с помоями на заднем дворе таверны, к тебе подошли " +
            "двое неизвестных, скрутили и вылили твоё ведро тебе же на голову. Увы, пока ты отмывался, твоё " +
            "отсутствие заметили и по возвращении выписали тебе штраф."),
    DRINK_MORE("Выпить еще", "Вы выпили еще по рюмашке. \nКажется, твой взгляд начинает затуманиваться.\nА девушка смотрит на тебя " + "таким взглядом...\n", Arrays.asList<IQuestStep>(DRINK_FINAL, DENY)),
    DRINK("Выпить", "Вы накатили по рюмочке.\nНе останавливаться же на одной рюмке, можно накатить еще!\n", Arrays.asList<IQuestStep>(DRINK_MORE, DENY)),
    INIT("", "Ты как обычно протирал столики, когда к тебе подошла симпатичная девушка и сказала что хочет угостить " +
            "тебя выпивкой.\nС одной стороны, ты не пил уже целую вечность (на самом деле нет), " +
            "а с другой стороны, лучше не пить пока работаешь.", Arrays.asList<IQuestStep>(DRINK, DENY));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = KitchenQuest.KitchenEvent.DRINK_WITH_GIRL

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

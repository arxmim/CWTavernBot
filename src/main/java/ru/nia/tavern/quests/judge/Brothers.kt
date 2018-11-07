package ru.nia.tavern.quests.judge

import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class Brothers : IQuestStep {

    ASK_BARMEN("Вызвать дежурившего в тот день бармена", "В тот день дежурила прекрасная девушка, которая вспомнила парней (наверное потому что они красавцы) и " + "дала показания в их защиту. Ты выиграл дело!", "В тот день дежурил какой-то неразговорчивый мужик, который много наливает и мало разговаривает. " + "Обвиняемых он не узнал, так что тебе не удалось убедить судью в невиновности братьев. Ты проиграл дело!") {
        override fun doWork(questEvent: QuestEvent) {
            questEvent.winChance = 70
        }
    },
    ASK_IVAN("Вызвать дровосека Ивана", "Пришедший дровосек вспомнил твоих подопечных, но долго не мог сообразить, где и когда он их видел.\n" +
            "Твоё знание таверны помогло тебе подобрать наводящие вопросы, и в конце концов пьяненький дровосек " +
            "поручился за братьев. Ты выиграл дело!", "Пришедший дровосек вспомнил твоих подопечных, но долго не мог сообразить, где и когда он их видел. " +
            "Увидев его, братья заволновались и попытались на него напасть. Это ни к чему не привело, но судья не " +
            "стал дальше тебя слушать и принял решение о виновности братьев. Ты проиграл дело!") {
        override fun doWork(questEvent: QuestEvent) {
            questEvent.winChance = 80
        }
    },
    ASK_KING("Попробовать призвать короля ада", "Ты попросил братьев призвать короля ада. Они произнесли какое-то имя, которое ты не расслышал, и в тот " +
            "же момент в зал вошел плюгавый лысеющий мужичок. Он последовательно вытащил из рукава два десятка расписок от " +
            "разных людей, в которых утверждалось, что братья были в таверне всё время. Под конец он даже вытащил " +
            "картину, на которой были запечатлены братья, он сам, кто-то из барменов и часы, показывающие время " +
            "преступления. С таким алиби у судьи не осталось вопросов, и обвиняемые были оправданы.", "Ты торжественно произнес \"Крекс-фекс-пекс\", но ничего не произошло. Тебя подняли на смех, и ты проиграл дело.") {
        override fun doWork(questEvent: QuestEvent) {
            questEvent.winChance = 90
        }
    },
    READY("Сообщить судье о готовности", "Судебный процесс начался.\n\nТы как мог убеждал судью в невиновности обвиняемых, но этого оказалось " +
            "недостаточно. Твой единственный шанс - вызвать какого-нибудь свидетеля, который поручится, что видел " +
            "братьев в другом месте.", Arrays.asList<IQuestStep>(ASK_KING, ASK_IVAN, ASK_BARMEN)),
    READY_BARMEN_IVAN(READY.command, READY.text, Arrays.asList<IQuestStep>(ASK_IVAN, ASK_BARMEN)),
    READY_BARMEN(READY.command, READY.text, listOf<IQuestStep>(ASK_BARMEN)),
    ASK_SUMMON_KING("- Как вызвать короля ада?", "- Нужно просто произнести его имя. Есть еще вариант произнести \"Крекс-фекс-пекс\", но мы не уверены, " + "что он работает.", listOf<IQuestStep>(READY)),
    ASK_WHO_SUPPORT("- Кто может подтвердить ваши слова?", "- Конечно! Король ада мог бы за нас поручиться! А еще в таверне был странный тип с рыжими волосами, " +
            "он явно следил за нами, и похоже он демон. Если его еще не пришили другие охотники на монстров, он " +
            "наверняка нас помнит.\n\nЕдинственный человек, подходящий под описание братьев - это дровосек Иван. " +
            "Единственная его супер-сила, о которой тебе известно - умение пить без просыху, но возможно он и правда помнит этих ребят.", Arrays.asList<IQuestStep>(ASK_SUMMON_KING, READY_BARMEN_IVAN)),
    ASK_WHERE("- Где вы были в момент совершения преступления?", "- Мы сидели в своей комнате в таверне и размышляли с королем ада о том как вернуть упавших с небес " + "ангелов назад.\n\nТы грустно вздохнул про себя:\n- Идиоты....", Arrays.asList<IQuestStep>(ASK_WHO_SUPPORT, READY_BARMEN)),
    READY_WITHOUT_INFO("Сообщить судье о готовности", "Судебный процесс начался.\n\nНесмотря на то, что даже ты не знаешь, что за типов ты защищаешь, тебе удалось " + "убедить судью, что они не совершали преступлений, в которых их обвиняют.", "Судебный процесс начался.\n\nТы даже не знал что сказать в защиту этих ребят, ведь ты даже не попытался " + "с ними поговорить. Твои попытки убедить судью в их невиновности не увенчались успехом.") {
        override fun doWork(questEvent: QuestEvent) {
            questEvent.winChance = 30
        }
    },
    INIT("", "Тебе досталось дело о двух братьях, которых обвиняют в подделке инквизиторских документов, вскрытии " +
            "могил и сжигании покойников. У ребят нет денег на адвоката, так что им достался штатный городской адвокат " +
            "- ты.\nБратья рассказали, что они - борцы со злом, на их счету множество вампиров, обортней и других " +
            "адских тварей.\nВпрочем, одеты они в какое-то тряпье, да и подходящего для таких подвигов оружия при них " +
            "найдено не было. Похоже, они просто хвастуны.\n\nТебе надо опросить обвиняемых, чтобы выстроить линию защиты.", Arrays.asList<IQuestStep>(ASK_WHERE, READY_WITHOUT_INFO));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."


    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = JudgeQuest.JudgeEvent.JUDGE_BROTHERS

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

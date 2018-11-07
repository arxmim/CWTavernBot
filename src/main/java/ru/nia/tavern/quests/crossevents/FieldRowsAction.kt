package ru.nia.tavern.quests.crossevents

import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.quests.IQuestAction
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.judge.FieldRowsJudgement
import ru.nia.tavern.quests.potato.FieldRows

/**
 * @author IANazarov
 */
enum class FieldRowsAction : IQuestAction {
    INIT("Тебе досталось дело о парне, который перепутал участки и копал картошку на чужом огороде. Парня зовут " + "%s. Ты и сам не раз бывал в подобной ситуации, поэтому не раздумывая решил ему помочь.", "Доказать невиновность в суде будет нереально, поэтому единственный способ не потерять деньги - " + "попробовать урегулировать дело до суда.", "%s задаст серию вопросов, которые должны помочь соседу понять, что он только выиграл от твоей работы " + "на его огороде.", "Ты должен задать серию вопросов, которые помогут соседу понять, что он только выиграл от работы %s на " + "его огороде. Первое, что ты должен узнать - что росло на соседском огороде. Выбери, у кого ты будешь спрашивать:") {
        override val moveToStep: IQuestStep?
            get() = FieldRowsJudgement.INIT

        override fun doWork(from: QuestEvent, to: QuestEvent) {
            setWinChance(from, to, 70)
        }
    },
    ASK_USER_WHAT("%s спрашивает у тебя, что росло на огороде.", "", "Ждешь реакции %s...", "") {
        override val moveToStep: IQuestStep?
            get() = FieldRows.QUESTION_WHAT
    },
    ASK_NPC_WHAT("%s спросил у соседа, что росло на огороде.", "- Да у меня там была ценная рассада! Я выращивал эксклюзивный вид редиса!\n\nКажется, он начинает злиться.", "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:", "Ждешь реакции %s...") {
        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, -10)
        }
    },
    ANSWER_WHAT_NOTHING("%s ответил:\n- Ничего не росло.", "Услышав такой ответ, сосед начал ругаться:\n- Парень, да ты совсем слепой! Там была моя ценнейшая " + "рассада эксклюзивного вида редиса!", "Ждешь реакции %s...", "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:") {
        override val moveToStep: IQuestStep?
            get() = FieldRowsJudgement.USER_WHAT_ANSWER_NOTHING

        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, -10)
        }
    },
    ANSWER_WHAT_GRASS("%s ответил:\n- Росла какая-то трава.", "Услышав такой ответ, сосед что-то проворчал про себя. Видимо, он согласен, но пока не хочет " + "признавать, что он только в плюсе от проделанной работы.", "Ждешь реакции %s...", "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:") {
        override val moveToStep: IQuestStep?
            get() = FieldRowsJudgement.USER_WHAT_ANSWER_GRASS

        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, 10)
        }
    },
    ASK_USER_HOW("%s спрашивает у тебя, насколько хорошо и качественно ты работал.", "", "Ждешь реакции %s...", "") {
        override val moveToStep: IQuestStep?
            get() = FieldRows.QUESTION_HOW
    },
    ASK_NPC_HOW("%s спросил у соседа, насколько хорошо и качественно ты работал.", "- Ну, парень явно старался, ничего не могу сказать. Картошка вскопана что надо!", "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " + "за работу.", "Ждешь реакции %s...") {
        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, 10)
        }
    },
    ANSWER_HOW_GOOD("%s ответил:\n- Отлично вскопал!", "Услышав эти слова, сосед немного успокоился и кивком согласился с ответом.", "Ждешь реакции %s...", "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " + "за работу.") {
        override val moveToStep: IQuestStep?
            get() = FieldRowsJudgement.USER_HOW_ANSWER_GOOD

        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, 10)
        }
    },
    ANSWER_HOW_BAD("%s ответил:\n- Ну, я старался...", "Услышав такой ответ, сосед начал орать:\n- Да что этот сопляк понимает в картошке!", "Ждешь реакции %s...", "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " + "за работу.") {
        override val moveToStep: IQuestStep?
            get() = FieldRowsJudgement.USER_HOW_ANSWER_BAD

        override fun doWork(from: QuestEvent, to: QuestEvent) {
            incWinChance(from, to, -10)
        }
    },
    ASK_NPC_FINAL_WIN(true, "%s спросил, готов ли сосед заплатить за проделанную работу.", "Сосед поворчал, но согласился, что вскопанная картошка ему пригодится, и что была проделана " + "отличная работа.", "Он заплатил небольшую сумму %s, частью которой тот поделился с тобой, за оказанную помощь.", "Он заплатил тебе небольшую сумму, частью которой ты поделился с %s, за оказанную помощь."),
    ASK_NPC_FINAL_LOSE(false, "%s спросил, готов ли сосед заплатить за проделанную работу.", "Сосед отказался платить парню за работу и вы пошли в суд.", "В суде %s пришлось заплатить штраф за нанесенный ущерб. Ты по доброте душевной заплатил за него половину суммы.", "В суде у тебя не было никаких шансов - и тебе пришлось заплатить штраф соседу. Радует хотя бы то, " + "что %s заплатил за тебя половину штрафа, спасибо ему за это.");

    override var explainText: String? = null
        private set
    override var eventText: String? = null
        private set
    override var fromEndText: String? = null
        private set
    override var toEndText: String? = null
        private set
    private val win: Boolean? = null

    override val moveToStep: IQuestStep?
        get() = null

    override val isWin: Boolean
        get() = win != null && win

    override val isLose: Boolean
        get() = win != null && !win

    private constructor(explainText: String, eventText: String, fromEndText: String, toEndText: String) {
        this.explainText = explainText
        this.eventText = eventText
        this.fromEndText = fromEndText
        this.toEndText = toEndText
    }

    private constructor(win: Boolean, explainText: String, eventText: String, fromEndText: String, toEndText: String) {
        this.explainText = explainText
        this.eventText = eventText
        this.fromEndText = fromEndText
        this.toEndText = toEndText
        this.win = win
    }

    override fun doWork(from: QuestEvent, to: QuestEvent) {}

    protected fun incWinChance(from: QuestEvent, to: QuestEvent, delta: Int) {
        from.winChance = from.winChance + delta
        to.winChance = to.winChance + delta
        from.save()
        to.save()
    }

    protected fun setWinChance(from: QuestEvent, to: QuestEvent, delta: Int) {
        from.winChance = delta
        to.winChance = delta
        from.save()
        to.save()
    }

}

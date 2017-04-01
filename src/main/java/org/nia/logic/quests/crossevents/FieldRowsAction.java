package org.nia.logic.quests.crossevents;

import org.nia.logic.quests.IQuestAction;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.judge.FieldRowsJudgement;
import org.nia.logic.quests.potato.FieldRows;
import org.nia.model.QuestEvent;

/**
 * @author IANazarov
 */
public enum FieldRowsAction implements IQuestAction {
    INIT(FieldRowsJudgement.INIT
            , "Тебе досталось дело о парне, который перепутал участки и копал картошку на чужом огороде. Парня зовут " +
            "%s. Ты и сам не раз бывал в подобной ситуации, поэтому не раздумывая решил ему помочь."
            , "Доказать невиновность в суде будет нереально, поэтому единственный способ не потерять деньги - " +
            "попробовать урегулировать дело до суда."
            , "%s задаст серию вопросов, которые должны помочь соседу понять, что он только выиграл от твоей работы " +
            "на его огороде."
            , "Ты должен задать серию вопросов, которые помогут соседу понять, что он только выиграл от работы %s на " +
            "его огороде. Первое, что ты должен узнать - что росло на соседском огороде. Выбери, у кого ты будешь спрашивать:") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            setWinChance(from, to, 70);
        }
    },
    ASK_USER_WHAT(FieldRows.QUESTION_WHAT
            , "%s спрашивает у тебя, что росло на огороде."
            , ""
            , "Ожидание ответа %s"
            , ""),
    ASK_NPC_WHAT(null
            , "%s спросил у соседа, что росло на огороде."
            , "- Да у меня там была ценная рассада! Я выращивал эксклюзивный вид редиса!\n\nКажется, он начинает злиться."
            , "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:"
            , "Ждешь реакции %s...") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, -10);
        }
    },
    ANSWER_WHAT_NOTHING(FieldRowsJudgement.USER_WHAT_ANSWER_NOTHING
            , "%s ответил:\n- Ничего не росло."
            , "Услышав такой ответ, сосед начал ругаться:\n- Парень, да ты совсем слепой! Там была моя ценнейшая " +
            "рассада эксклюзивного вида редиса!"
            , "Ждешь реакции %s"
            , "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, -10);
        }
    },
    ANSWER_WHAT_GRASS(FieldRowsJudgement.USER_WHAT_ANSWER_GRASS
            , "%s ответил:\n- Росла какая-то трава."
            , "Услышав такой ответ, сосед что-то проворчал про себя. Видимо, он согласен, но пока не хочет " +
            "признавать, что он только в плюсе от проделанной работы."
            , "Ждешь реакции %s"
            , "Твой следующий вопрос касается качества работы %s. Выбери, у кого ты будешь спрашивать:") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, 10);
        }
    },
    ASK_USER_HOW(FieldRows.QUESTION_HOW
            , "%s спрашивает у тебя, насколько хорошо и качественно ты работал."
            , ""
            , "Ожидание ответа %s"
            , ""),
    ASK_NPC_HOW(null
            , "%s спросил у соседа, насколько хорошо и качественно ты работал."
            , "- Ну, парень явно старался, ничего не могу сказать. Картошка вскопана что надо!"
            , "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " +
            "за работу."
            , "Ждешь реакции %s...") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, 10);
        }
    },
    ANSWER_HOW_GOOD(FieldRowsJudgement.USER_HOW_ANSWER_GOOD
            , "%s ответил:\n- Отлично вскопал!"
            , "Услышав эти слова, сосед немного успокоился и кивком согласился с ответом."
            , "Ждешь реакции %s"
            , "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " +
            "за работу.") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, 10);
        }
    },
    ANSWER_HOW_BAD(FieldRowsJudgement.USER_HOW_ANSWER_BAD
            , "%s ответил:\n- Ну, я старался..."
            , "Услышав такой ответ, сосед начал орать:\n- Да что этот сопляк понимает в картошке!"
            , "Ждешь реакции %s"
            , "У тебя больше не осталось вопросов по делу, и теперь надо лишь узнать, готов ли сосед заплатить %s " +
            "за работу.") {
        @Override
        public void doWork(QuestEvent from, QuestEvent to) {
            incWinChance(from, to, -10);
        }
    },
    ASK_NPC_FINAL_WIN(true
            , "%s спросил, готов ли сосед заплатить за проделанную работу."
            , "Сосед поворчал, но согласился, что вскопанная картошка ему пригодится, и что была проделана " +
            "отличная работа."
            , "Он заплатил небольшую сумму %s, частью которой тот поделился с тобой, за оказанную помощь."
            , "Он заплатил тебе небольшую сумму, частью которой ты поделился с %s, за оказанную помощь."),
    ASK_NPC_FINAL_LOSE(false
            , "%s спросил, готов ли сосед заплатить за проделанную работу."
            , "Сосед отказался платить парню за работу и вы пошли в суд."
            , "В суде %s пришлось заплатить штраф за нанесенный ущерб. Ты по доброте душевной заплатил за него половину суммы."
            , "В суде у тебя не было никаких шансов - и тебе пришлось заплатить штраф соседу. Радует хотя бы то, " +
            "что %s заплатил за тебя половину штрафа, спасибо ему за это.");
    private String explainText;
    private String eventText;
    private String fromEndText;
    private String toEndText;
    private IQuestStep moveToStep;
    private Boolean win = null;

    FieldRowsAction(IQuestStep moveToStep, String explainText, String eventText, String fromEndText, String toEndText) {
        this.moveToStep = moveToStep;
        this.explainText = explainText;
        this.eventText = eventText;
        this.fromEndText = fromEndText;
        this.toEndText = toEndText;
    }

    FieldRowsAction(boolean win, String explainText, String eventText, String fromEndText, String toEndText) {
        this.moveToStep = null;
        this.explainText = explainText;
        this.eventText = eventText;
        this.fromEndText = fromEndText;
        this.toEndText = toEndText;
        this.win = win;
    }

    public String getExplainText() {
        return explainText;
    }

    public String getEventText() {
        return eventText;
    }

    public String getFromEndText() {
        return fromEndText;
    }

    public String getToEndText() {
        return toEndText;
    }

    public IQuestStep getMoveToStep() {
        return moveToStep;
    }

    @Override
    public void doWork(QuestEvent from, QuestEvent to) {
    }

    @Override
    public boolean isWin() {
        return win != null && win;
    }

    @Override
    public boolean isLose() {
        return win != null && !win;
    }

    protected void incWinChance(QuestEvent from, QuestEvent to, int delta) {
        from.setWinChance(from.getWinChance() + delta);
        to.setWinChance(to.getWinChance() + delta);
        from.save();
        to.save();
    }

    protected void setWinChance(QuestEvent from, QuestEvent to, int delta) {
        from.setWinChance(delta);
        to.setWinChance(delta);
        from.save();
        to.save();
    }

}

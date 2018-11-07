package ru.nia.tavern.quests.judge

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
enum class FieldRowsJudgement : ICrossQuestStep {

    ASK_NPC_FINAL("Спросить, готов ли сосед заплатить за работу %s", false) {
        override val actionList: List<IQuestAction>
            get() = Arrays.asList<IQuestAction>(FieldRowsAction.ASK_NPC_FINAL_WIN, FieldRowsAction.ASK_NPC_FINAL_LOSE)

    },
    USER_HOW_ANSWER_BAD(listOf<IQuestStep>(ASK_NPC_FINAL)),
    USER_HOW_ANSWER_GOOD(listOf<IQuestStep>(ASK_NPC_FINAL)),
    ASK_USER_HOW("Спросить у %s", true) {
        override val actionList: List<IQuestAction>
            get() = ArrayList<IQuestAction>(listOf(FieldRowsAction.ASK_USER_HOW))

    },
    ASK_NPC_HOW("Спросить у соседа", listOf<IQuestStep>(ASK_NPC_FINAL)) {
        override val actionList: List<IQuestAction>
            get() = ArrayList<IQuestAction>(listOf(FieldRowsAction.ASK_NPC_HOW))

    },
    USER_WHAT_ANSWER_GRASS(Arrays.asList<IQuestStep>(ASK_USER_HOW, ASK_NPC_HOW)),
    USER_WHAT_ANSWER_NOTHING(Arrays.asList<IQuestStep>(ASK_USER_HOW, ASK_NPC_HOW)),
    ASK_USER_WHAT("Спросить у %s", true) {
        override val actionList: List<IQuestAction>
            get() = ArrayList<IQuestAction>(listOf(FieldRowsAction.ASK_USER_WHAT))

    },
    ASK_NPC_WHAT("Спросить у соседа", Arrays.asList<IQuestStep>(ASK_USER_HOW, ASK_NPC_HOW)) {
        override val actionList: List<IQuestAction>
            get() = ArrayList<IQuestAction>(listOf(FieldRowsAction.ASK_NPC_WHAT))

    },
    INIT(Arrays.asList<IQuestStep>(ASK_USER_WHAT, ASK_NPC_WHAT));

    override val isWaitUser = false
    private val next = ArrayList<IQuestStep>()
    private var command = ""
    override val isButtonWithUser = false

    override val name: String
        get() = name

    override val actionList: List<IQuestAction>
        get() = emptyList()

    override val iQuest: IQuestEvent
        get() = JudgeQuest.JudgeEvent.JUDGE_FIELD_ROWS_JUDGEMENT

    private constructor(next: List<IQuestStep>) {
        this.next.addAll(next)
    }

    private constructor(command: String, next: List<IQuestStep>) {
        this.command = command
        this.next.addAll(next)
    }

    private constructor(command: String, isWaitUser: Boolean) {
        this.command = command
        this.isWaitUser = isWaitUser
        this.isButtonWithUser = true
    }


    override fun getNext(quest: Quest): List<IQuestStep> {
        return next
    }

    override fun getCommand(formatParam: String): String {
        return if (!formatParam.isEmpty()) {
            String.format(command, formatParam)
        } else {
            command
        }
    }
}

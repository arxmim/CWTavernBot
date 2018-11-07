package ru.nia.tavern.quests.judge

import ru.nia.tavern.quests.IQuest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.QuestsEnum
import java.util.*
import java.util.stream.Collectors

/**
 * @author Иван, 28.03.2017.
 */
class JudgeQuest : IQuest {

    override val start: String
        get() = "Остап попросил тебя заменить заболевшего городского юриста на сегодняшних заседаниях суда. " +
                "Ты берешь подобающий по случаю парик и идешь в здание суда. В зависимости от желаний судьи и " +
                "потерпевшего, тебе придется исполнять роль прокурора или адвоката. Награда за " +
                "задание будет увеличиваться или уменьшаться в зависимости от того, насколько успешно ты будешь вести дела."

    override val randomEvent: IQuestEvent
        get() {
            val events = Arrays.stream(JudgeEvent.values())
                    .filter(Predicate<JudgeEvent> { it.canBeRandomed() })
                    .collect<List<JudgeEvent>, Any>(Collectors.toList())
            return events[Random().nextInt(events.size)]
        }

    enum class JudgeEvent private constructor(override val init: IQuestStep) : IQuestEvent {
        JUDGE_FIELD_ROWS_JUDGEMENT(FieldRowsJudgement.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return FieldRowsJudgement.valueOf(questStep)
            }

            override fun canBeRandomed(): Boolean {
                return false
            }
        },
        JUDGE_STOLEN_HORSE(StolenHorse.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return StolenHorse.valueOf(questStep)
            }
        },
        JUDGE_BROTHERS(Brothers.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return Brothers.valueOf(questStep)
            }
        };

        override val name: String
            get() = name

        override val questsEnum: QuestsEnum
            get() = QuestsEnum.JUDGE
    }

    override fun getEvent(event: String): IQuestEvent {
        return JudgeEvent.valueOf(event)
    }

    companion object {
        var INSTANCE = JudgeQuest()
    }
}

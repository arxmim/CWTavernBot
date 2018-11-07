package ru.nia.tavern.quests.potato

import ru.nia.tavern.quests.IQuest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.QuestsEnum
import java.util.*

/**
 * @author IANazarov
 */
class PotatoQuest : IQuest {

    override val start: String
        get() = "Остап попросил тебя вскопать картошку. Делать нечего, ты берешь лопату и идешь совершать трудовой подвиг."

    override val randomEvent: IQuestEvent
        get() {
            val events = Arrays.asList(*PotatoEvent.values())
            return events[Random().nextInt(events.size)]
        }

    enum class PotatoEvent private constructor(override val init: IQuestStep) : IQuestEvent {
        POTATO_FIELD_ROWS(FieldRows.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return FieldRows.valueOf(questStep)
            }
        },
        POTATO_GIANT_JUK(GiantJuk.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return GiantJuk.valueOf(questStep)
            }
        },
        POTATO_ORCS(Orcs.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return Orcs.valueOf(questStep)
            }
        },
        POTATO_GOPNIK(Gopnik.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return Gopnik.valueOf(questStep)
            }
        };

        override val name: String
            get() = name

        override val questsEnum: QuestsEnum
            get() = QuestsEnum.POTATO
    }

    override fun getEvent(event: String): IQuestEvent {
        return PotatoEvent.valueOf(event)
    }

    companion object {
        var INSTANCE = PotatoQuest()
    }

}

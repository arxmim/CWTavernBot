package ru.nia.tavern.quests.buyfish

import ru.nia.tavern.quests.IQuest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.QuestsEnum
import java.util.*

/**
 * @author IANazarov
 */
class SellFishQuest : IQuest {

    override val start: String
        get() = "Остап попросил тебя поторговать рыбой на рынке. Ему недавно привезли несколько повозок с рыбой, и " + "теперь её надо продать, пока не испортилась."

    override val randomEvent: IQuestEvent
        get() {
            val events = Arrays.asList(*SellFishEvent.values())
            return events[Random().nextInt(events.size)]
        }

    enum class SellFishEvent private constructor(override val init: IQuestStep) : IQuestEvent {
        SELL_FISH_NOISY_MAN(NoisyMan.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return NoisyMan.valueOf(questStep)
            }
        },
        SELL_FISH_INSPECTION(Inspection.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return Inspection.valueOf(questStep)
            }
        },
        SELL_FISH_TURTLE_PANDA(TurtlePanda.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return TurtlePanda.valueOf(questStep)
            }
        },
        SELL_FISH_MAGIC_FISH(MagicFish.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return MagicFish.valueOf(questStep)
            }
        },
        SELL_FISH_HOBBIT(DragonMeat.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return DragonMeat.valueOf(questStep)
            }
        };

        override val name: String
            get() = name

        override val questsEnum: QuestsEnum
            get() = QuestsEnum.SELL_FISH
    }

    override fun getEvent(event: String): IQuestEvent {
        return SellFishEvent.valueOf(event)
    }

    companion object {
        var INSTANCE = SellFishQuest()
    }
}

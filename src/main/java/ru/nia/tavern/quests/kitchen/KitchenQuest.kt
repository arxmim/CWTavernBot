package ru.nia.tavern.quests.kitchen

import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestFact
import ru.nia.tavern.model.types.facts.EQuestFact
import ru.nia.tavern.quests.IQuest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import ru.nia.tavern.quests.QuestsEnum
import java.util.*

/**
 * @author Иван, 06.04.2017.
 */
class KitchenQuest : IQuest {

    override val start: String
        get() = "Помощник Лизы ушел в отпуск, и нужна разнообразная помощь по таверне - готовка, уборка, ремонт, всего " + "понемногу.\nТы надел фартук помощника и пошел отдавать долг таверне."

    override val randomEvent: IQuestEvent
        get() {
            val events = Arrays.asList(*KitchenEvent.values())
            return events[Random().nextInt(events.size)]
        }

    enum class KitchenEvent private constructor(override val init: IQuestStep) : IQuestEvent {
        KITCHEN_COOKING(Cooking.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return Cooking.valueOf(questStep)
            }
        },
        DRINK_WITH_GIRL(DrinkWithGirl.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return DrinkWithGirl.valueOf(questStep)
            }
        },
        ROOF_STAIRS(RoofStairs.INIT) {
            override fun getQuestStep(questStep: String): IQuestStep {
                return RoofStairs.valueOf(questStep)
            }
        };

        override val name: String
            get() = name

        override val questsEnum: QuestsEnum
            get() = QuestsEnum.KITCHEN

        override fun init(quest: Quest) {
            val eQuestFacts = Arrays.asList(EQuestFact.KITCHEN_ELVEN_SHAURMA)
            val eQuestFact = eQuestFacts[Random().nextInt(eQuestFacts.size)]
            val questFact = QuestFact()
            questFact.quest = quest
            questFact.questFact = eQuestFact
            questFact.save()
        }
    }//        POTATO_GIANT_JUK(GiantJuk.INIT) {
    //            @Override
    //            public IQuestStep getQuestStep(String questStep) {
    //                return GiantJuk.valueOf(questStep);
    //            }
    //        },
    //        POTATO_ORCS(Orcs.INIT) {
    //            @Override
    //            public IQuestStep getQuestStep(String questStep) {
    //                return Orcs.valueOf(questStep);
    //            }
    //        },
    //        POTATO_GOPNIK(Gopnik.INIT) {
    //            @Override
    //            public IQuestStep getQuestStep(String questStep) {
    //                return Gopnik.valueOf(questStep);
    //            }
    //        }

    override fun getEvent(event: String): IQuestEvent {
        return KitchenEvent.valueOf(event)
    }

    companion object {
        var INSTANCE = KitchenQuest()
    }
}

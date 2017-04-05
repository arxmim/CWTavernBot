package org.nia.logic.quests.kitchen;

import org.nia.logic.lists.facts.EQuestFact;
import org.nia.logic.quests.IQuest;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.QuestsEnum;
import org.nia.model.Quest;
import org.nia.model.QuestFact;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Иван, 06.04.2017.
 */
public class KitchenQuest implements IQuest {
    public static KitchenQuest INSTANCE = new KitchenQuest();

    @Override
    public String getStart() {
        return "Помощник Лизы ушел в отпуск, и нужна разнообразная помощь по таверне - готовка, уборка, ремонт, всего " +
                "понемногу.\nТы одел фартук помощника и пошел отдавать долг таверне.";
    }

    public enum KitchenEvent implements IQuestEvent {
        KITCHEN_COOKING(Cooking.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return Cooking.valueOf(questStep);
            }
        },
//        POTATO_GIANT_JUK(GiantJuk.INIT) {
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
        ;
        private IQuestStep init;

        KitchenEvent(IQuestStep init) {
            this.init = init;
        }

        @Override
        public void init(Quest quest) {
            List<EQuestFact> eQuestFacts = Arrays.asList(EQuestFact.KITCHEN_ELVEN_SHAURMA);
            EQuestFact eQuestFact = eQuestFacts.get(new Random().nextInt(eQuestFacts.size()));
            QuestFact questFact = new QuestFact();
            questFact.setQuest(quest);
            questFact.setQuestFact(eQuestFact);
            questFact.save();
        }

        @Override
        public IQuestStep getInit() {
            return init;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public QuestsEnum getQuestsEnum() {
            return QuestsEnum.KITCHEN;
        }
    }

    @Override
    public IQuestEvent getRandomEvent() {
        List<KitchenEvent> events = Arrays.asList(KitchenEvent.values());
        return events.get(new Random().nextInt(events.size()));
    }

    @Override
    public IQuestEvent getEvent(String event) {
        return org.nia.logic.quests.potato.PotatoQuest.PotatoEvent.valueOf(event);
    }
}

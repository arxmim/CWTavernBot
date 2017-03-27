package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuest;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public class SellFishQuest implements IQuest {
    public static SellFishQuest INSTANCE = new SellFishQuest();

    @Override
    public String getStart() {
        return "Остап попросил тебя поторговать рыбой на рынке. Ему недавно привезли несколько повозок с рыбой, и " +
                "теперь её надо продать, пока не испортилась.";
    }

    private enum SellFishEvent implements IQuestEvent {
        SELL_FISH_NOISY_MAN(NoisyMan.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return NoisyMan.valueOf(questStep);
            }
        },
        SELL_FISH_INSPECTION(Inspection.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return Inspection.valueOf(questStep);
            }
        },
        SELL_FISH_TURTLE_PANDA(TurtlePanda.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return TurtlePanda.valueOf(questStep);
            }
        },
        SELL_FISH_MAGIC_FISH(MagicFish.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return MagicFish.valueOf(questStep);
            }
        },
        SELL_FISH_HOBBIT(DragonMeat.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return DragonMeat.valueOf(questStep);
            }
        };
        private IQuestStep init;

        SellFishEvent(IQuestStep init) {
            this.init = init;
        }

        @Override
        public IQuestStep getInit() {
            return init;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    @Override
    public IQuestEvent getRandomEvent() {
        List<SellFishEvent> events = Arrays.asList(SellFishEvent.values());
        return events.get(new Random().nextInt(events.size()));
    }

    @Override
    public IQuestEvent getEvent(String event) {
        return SellFishEvent.valueOf(event);
    }
}

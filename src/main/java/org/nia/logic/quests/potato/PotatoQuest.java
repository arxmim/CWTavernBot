package org.nia.logic.quests.potato;

import org.nia.logic.quests.IQuest;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.QuestsEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public class PotatoQuest implements IQuest {
    public static PotatoQuest INSTANCE = new PotatoQuest();

    @Override
    public String getStart() {
        return "Остап попросил тебя вскопать картошку. Делать нечего, ты берешь лопату и идешь совершать трудовой подвиг.";
    }

    public enum PotatoEvent implements IQuestEvent {
        POTATO_FIELD_ROWS(FieldRows.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return FieldRows.valueOf(questStep);
            }
        },
        POTATO_GIANT_JUK(GiantJuk.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return GiantJuk.valueOf(questStep);
            }
        },
        POTATO_ORCS(Orcs.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return Orcs.valueOf(questStep);
            }
        },
        POTATO_GOPNIK(Gopnik.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return Gopnik.valueOf(questStep);
            }
        }
        ;
        private IQuestStep init;

        PotatoEvent(IQuestStep init) {
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

        @Override
        public QuestsEnum getQuestsEnum() {
            return QuestsEnum.POTATO;
        }
    }

    @Override
    public IQuestEvent getRandomEvent() {
        List<PotatoEvent> events = Arrays.asList(PotatoEvent.values());
        return events.get(new Random().nextInt(events.size()));
    }

    @Override
    public IQuestEvent getEvent(String event) {
        return PotatoEvent.valueOf(event);
    }

}

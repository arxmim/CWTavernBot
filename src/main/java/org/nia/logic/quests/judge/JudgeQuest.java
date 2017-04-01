package org.nia.logic.quests.judge;

import org.nia.logic.quests.IQuest;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.QuestsEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Иван, 28.03.2017.
 */
public class JudgeQuest implements IQuest {
    public static JudgeQuest INSTANCE = new JudgeQuest();

    @Override
    public String getStart() {
        return "Остап попросил тебя заменить заболевшего городского юриста на сегодняшних заседаниях суда. " +
                "Ты берешь подобающий по случаю парик и идешь в здание суда. В зависимости от желаний судьи и " +
                "потерпевшего, тебе придется исполнять роль прокурора или адвоката. Награда за " +
                "задание будет увеличиваться или уменьшаться в зависимости от того, насколько успешно ты будешь вести дела.";
    }

    public enum JudgeEvent implements IQuestEvent {
        JUDGE_FIELD_ROWS_JUDGEMENT(FieldRowsJudgement.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return FieldRowsJudgement.valueOf(questStep);
            }

            @Override
            public boolean canBeRandomed() {
                return false;
            }
        },
        JUDGE_STOLEN_HORSE(StolenHorse.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return StolenHorse.valueOf(questStep);
            }
        },
        JUDGE_BROTHERS(Brothers.INIT) {
            @Override
            public IQuestStep getQuestStep(String questStep) {
                return Brothers.valueOf(questStep);
            }
        }
        ;
        private IQuestStep init;

        JudgeEvent(IQuestStep init) {
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
            return QuestsEnum.JUDGE;
        }
    }

    @Override
    public IQuestEvent getRandomEvent() {
        List<JudgeEvent> events = Arrays.stream(JudgeEvent.values())
                .filter(IQuestEvent::canBeRandomed)
                .collect(Collectors.toList());
        return events.get(new Random().nextInt(events.size()));
    }

    @Override
    public IQuestEvent getEvent(String event) {
        return JudgeEvent.valueOf(event);
    }
}

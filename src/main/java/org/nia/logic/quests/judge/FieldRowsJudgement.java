package org.nia.logic.quests.judge;

import org.nia.logic.quests.ICrossQuestStep;
import org.nia.logic.quests.IQuestAction;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.crossevents.FieldRowsAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author IANazarov
 */
public enum FieldRowsJudgement implements ICrossQuestStep {

    ASK_NPC_FINAL("Спросить, готов ли сосед заплатить за работу %s", false) {
        @Override
        public List<IQuestAction> getActionList() {
            return Arrays.asList(FieldRowsAction.ASK_NPC_FINAL_WIN, FieldRowsAction.ASK_NPC_FINAL_LOSE);
        }

    },
    USER_HOW_ANSWER_BAD(Collections.singletonList(ASK_NPC_FINAL)),
    USER_HOW_ANSWER_GOOD(Collections.singletonList(ASK_NPC_FINAL)),
    ASK_USER_HOW("Спросить у %s", true) {
        @Override
        public List<IQuestAction> getActionList() {
            return new ArrayList<>(Collections.singletonList(FieldRowsAction.ASK_USER_HOW));
        }

    },
    ASK_NPC_HOW("Спросить у соседа", Collections.singletonList(ASK_NPC_FINAL)) {
        @Override
        public List<IQuestAction> getActionList() {
            return new ArrayList<>(Collections.singletonList(FieldRowsAction.ASK_NPC_HOW));
        }

    },
    USER_WHAT_ANSWER_GRASS(Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)),
    USER_WHAT_ANSWER_NOTHING(Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)),
    ASK_USER_WHAT("Спросить у %s", true) {
        @Override
        public List<IQuestAction> getActionList() {
            return new ArrayList<>(Collections.singletonList(FieldRowsAction.ASK_USER_WHAT));
        }

    },
    ASK_NPC_WHAT("Спросить у соседа", Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)) {
        @Override
        public List<IQuestAction> getActionList() {
            return new ArrayList<>(Collections.singletonList(FieldRowsAction.ASK_NPC_WHAT));
        }

    },
    INIT(Arrays.asList(ASK_USER_WHAT, ASK_NPC_WHAT));
    private boolean isWaitUser = false;
    private List<IQuestStep> next = new ArrayList<>();
    private String command = "";
    private boolean isButtonWithUser = false;

    FieldRowsJudgement(List<IQuestStep> next) {
        this.next.addAll(next);
    }

    FieldRowsJudgement(String command, List<IQuestStep> next) {
        this.command = command;
        this.next.addAll(next);
    }

    FieldRowsJudgement(String command, boolean isWaitUser) {
        this.command = command;
        this.isWaitUser = isWaitUser;
        this.isButtonWithUser = true;
    }


    @Override
    public List<IQuestStep> getNext() {
        return next;
    }

    @Override
    public String getCommand(String formatParam) {
        if (!formatParam.isEmpty()) {
            return String.format(command, formatParam);
        } else {
            return command;
        }
    }

    @Override
    public boolean isWaitUser() {
        return isWaitUser;
    }

    @Override
    public boolean isButtonWithUser() {
        return isButtonWithUser;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public List<IQuestAction> getActionList() {
        return Collections.emptyList();
    }

    @Override
    public IQuestEvent getIQuest() {
        return JudgeQuest.JudgeEvent.JUDGE_FIELD_ROWS_JUDGEMENT;
    }
}

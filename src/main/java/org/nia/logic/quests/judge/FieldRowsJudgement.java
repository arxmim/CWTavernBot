package org.nia.logic.quests.judge;

import org.nia.logic.quests.ICrossQuestStep;
import org.nia.logic.quests.IQuestAction;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.crossevents.FieldRowsAction;

import java.util.*;

/**
 * @author IANazarov
 */
public enum FieldRowsJudgement implements ICrossQuestStep {

    ASK_NPC_FINAL("Спросить, готов ли сосед заплатить за работу %s", Arrays.asList(FieldRowsAction.ASK_NPC_FINAL_WIN, FieldRowsAction.ASK_NPC_FINAL_LOSE), false),
    USER_HOW_ANSWER_BAD(Collections.singletonList(ASK_NPC_FINAL)),
    USER_HOW_ANSWER_GOOD(Collections.singletonList(ASK_NPC_FINAL)),
    ASK_USER_HOW("Спросить у %s", Collections.singletonList(FieldRowsAction.ASK_USER_HOW), true),
    ASK_NPC_HOW("Спросить у соседа", Collections.singletonList(FieldRowsAction.ASK_NPC_HOW), Collections.singletonList(ASK_NPC_FINAL)),
    USER_WHAT_ANSWER_GRASS(Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)),
    USER_WHAT_ANSWER_NOTHING(Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)),
    ASK_USER_WHAT("Спросить у %s", Collections.singletonList(FieldRowsAction.ASK_USER_WHAT), true),
    ASK_NPC_WHAT("Спросить у соседа", Collections.singletonList(FieldRowsAction.ASK_NPC_WHAT), Arrays.asList(ASK_USER_HOW, ASK_NPC_HOW)),
    INIT(Arrays.asList(ASK_USER_WHAT, ASK_NPC_WHAT));
    private boolean isWaitUser = false;
    private List<IQuestStep> next = new ArrayList<>();
    private String command = "";
    private List<IQuestAction> actionList = new ArrayList<>();
    private boolean isButtonWithUser = false;

    FieldRowsJudgement(List<IQuestStep> next) {
        this.next.addAll(next);
    }

    FieldRowsJudgement(String command, List<IQuestAction> actionList, List<IQuestStep> next) {
        this.command = command;
        this.next.addAll(next);
        this.actionList.addAll(actionList);
    }

    FieldRowsJudgement(String command, List<IQuestAction> actionList, boolean isWaitUser) {
        this.command = command;
        this.actionList.addAll(actionList);
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
        return actionList;
    }
    @Override
    public IQuestEvent getIQuest() {
        return JudgeQuest.JudgeEvent.JUDGE_FIELD_ROWS_JUDGEMENT;
    }
}

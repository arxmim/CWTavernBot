package org.nia.logic.quests;

import org.nia.model.QuestEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author IANazarov
 */
public interface ICrossQuestStep extends IQuestStep {

    @Override
    default String getGoodText() {
        return "Твой коллега ничего не предпринял, так что ты выставил его крайним и смотался.";
    }

    @Override
    default String getBadText() {
        return "Ты ничего не предпринял и всё пошло наперекосяк.";
    }

    @Override
    default String getText() {
        return "";
    }

    default boolean isWaitUser() {
        return false;
    }

    default boolean isButtonWithUser() {
        return false;
    }

    List<IQuestAction> getActionList();

    default IQuestAction getProducedAction(QuestEvent event) {
        List<IQuestAction> actionList = getActionList();
        if (actionList.isEmpty()) {
            return null;
        } else {
            boolean isFinal = !isWaitUser() && getNext().isEmpty();
            if (!isFinal) {
                return actionList.get(new Random().nextInt(actionList.size()));
            } else if (isWin(event)) {
                List<IQuestAction> winList = actionList.stream()
                        .filter(IQuestAction::isWin)
                        .collect(Collectors.toList());
                if (winList.isEmpty()) {
                    return null;
                } else {
                    return winList.get(new Random().nextInt(winList.size()));
                }
            } else {
                List<IQuestAction> loseList = actionList.stream()
                        .filter(IQuestAction::isLose)
                        .collect(Collectors.toList());
                if (loseList.isEmpty()) {
                    return null;
                } else {
                    return loseList.get(new Random().nextInt(loseList.size()));
                }
            }
        }
    }
}

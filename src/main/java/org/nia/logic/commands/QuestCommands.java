package org.nia.logic.commands;

import org.nia.logic.quests.ICrossQuestStep;
import org.nia.logic.quests.IQuestAction;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Date;
import java.util.Optional;

/**
 * @author IANazarov
 */
public class QuestCommands implements Commands {
    private User user;
    private Quest quest;
    private QuestEvent event;

    public QuestCommands(User user, Quest quest, QuestEvent event) {
        this.user = user;
        this.quest = quest;
        this.event = event;
    }

    @Override
    public String apply(Message message) {
        String res = "";
        Optional<IQuestStep> first = getNextStep(message.getText());
        if (first.isPresent()) {
            IQuestStep iQuestStep = first.get();
            event.setStep(iQuestStep);
            event.setEventTime(new Date());
            boolean actionTriggered = false;
            QuestEvent linkedEvent = event.getLinkedQuestEvent();
            if (iQuestStep instanceof ICrossQuestStep) {
                ICrossQuestStep qs = (ICrossQuestStep) iQuestStep;
                IQuestAction producedAction = qs.getProducedAction(event);
                if (linkedEvent != null) {
                    producedAction.applyAction(event, linkedEvent);
                    actionTriggered = true;
                } else if (producedAction != null) {
                    actionTriggered = producedAction.initAction(event);
                }
            }
            if (!actionTriggered) {
                if (iQuestStep.getNext().isEmpty()) {
                    iQuestStep.doWork(event);
                    boolean win = iQuestStep.isWin(event);
                    event.setWin(win);
                    if (win) {
                        res = iQuestStep.getGoodText() + "\n\nУдачное решение! Твоя награда за задание будет увеличена.";
                    } else {
                        res = iQuestStep.getBadText() + "\n\nОчень жаль! Твоя награда за задание будет уменьшена.";
                    }
                    quest.setEventTime(quest.getQuestEnum().getNextEventTime(quest));
                    quest.save();
                } else {
                    res = iQuestStep.getText();
                }
            }
            event.save();
        }
        return res;
    }

    @Override
    public boolean isApplicable(Message message) {
        return !(event == null || event.getStep().getNext().isEmpty()) && getNextStep(message.getText()).isPresent();
    }

    private Optional<IQuestStep> getNextStep(String text) {
        String formatParam = "";
        if (event.getLinkedQuestEvent() != null) {
            formatParam = event.getLinkedQuestEvent().getQuest().getUser().toString();
        }
        final String formatParamFinal = formatParam;
        return event.getStep().getNext().stream().filter(qs -> {
            if (qs instanceof ICrossQuestStep && ((ICrossQuestStep) qs).isWaitUser()) {
                return qs.getCommand(formatParamFinal).equals(text);
            } else {
                return qs.getCommand("").equals(text);
            }
        }).findFirst();

    }
}

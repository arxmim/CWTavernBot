package org.nia.logic.commands;

import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
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
        Optional<IQuestStep> first = event.getStep().getNext().stream().filter(qs -> qs.getCommand().equals(message.getText())).findFirst();
        if (first.isPresent()) {
            IQuestStep iQuestStep = first.get();
            event.setStep(iQuestStep);
            if (iQuestStep.getNext().isEmpty()) {
                boolean win = iQuestStep.isWin();
                event.setWin(win);
                if (win) {
                    res = iQuestStep.getGoodText() + "\n\nУдачное решение! Твоя награда за задание будет увеличена.";
                } else {
                    res = iQuestStep.getBadText() + "\n\nОчень жаль! Твоя награда за задание будет уменьшена.";
                }
            } else {
                res = iQuestStep.getText();
            }
            event.save();
            quest.setEventTime(quest.getQuestEnum().getNextEventTime());
            quest.save();
        }
        return res;
    }

    @Override
    public boolean isApplicable(Message message) {
        return event != null && event.getStep().getNext().stream().filter(qs -> qs.getCommand().equals(message.getText())).findFirst().isPresent();
    }
}

package org.nia.logic.quests;

import org.apache.commons.lang3.time.DateUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Date;

/**
 * @author IANazarov
 */
public interface IQuestAction {
    boolean isWin();

    boolean isLose();

    String getExplainText();

    String getEventText();

    String getFromEndText();

    String getToEndText();

    IQuestStep getMoveToStep();

    void doWork(QuestEvent from, QuestEvent to);

    default boolean initAction(QuestEvent from) {
        CWTavernBot BOT = CWTavernBot.INSTANCE;
        Quest randomActive = Quest.getRandomActive(this.getMoveToStep().getIQuest().getQuestsEnum());
        if (randomActive != null) {
            QuestEvent linkedEvent = new QuestEvent();
            linkedEvent.setEventTime(DateUtils.addMinutes(new Date(), -1));
            linkedEvent.setQuest(randomActive);
            linkedEvent.setLinkedQuestEvent(from);
            from.setLinkedQuestEvent(linkedEvent);
            linkedEvent.setStep(this.getMoveToStep());
            linkedEvent.setIQuestEvent(this.getMoveToStep().getIQuest());
            linkedEvent.save();
            randomActive.setEventTime(linkedEvent.getEventTime());
            randomActive.save();
            try {
                BOT.sendMessage(ServingMessage.getTimedMessage(from.getQuest().getUser()
                        , String.format(from.getStep().getInterceptText(), randomActive.getUser())));
                applyAction(from, linkedEvent);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    default void applyAction(QuestEvent from, QuestEvent to) {
        doWork(from, to);
        if (this.getMoveToStep() != null) {
            to.setStep(this.getMoveToStep());
            to.save();
        }
        if (this.isWin() || this.isLose()) {
            if (this.isWin()) {
                from.setWin(true);
                to.setWin(true);
            } else if (this.isLose()) {
                from.setWin(false);
                to.setWin(false);
            }
            from.getQuest().setEventTime(from.getQuest().getQuestEnum().getNextEventTime(from.getQuest()));
            to.getQuest().setEventTime(to.getQuest().getQuestEnum().getNextEventTime(to.getQuest()));
            from.save();
            to.save();
            from.getQuest().save();
            to.getQuest().save();
        }
        CWTavernBot BOT = CWTavernBot.INSTANCE;
        try {
            User fromUser = from.getQuest().getUser();
            User toUser = to.getQuest().getUser();
            BOT.sendMessage(ServingMessage.getTimedMessage(fromUser
                    , this.getEventText() + String.format(this.getFromEndText(), toUser)));
            BOT.sendMessage(ServingMessage.getTimedMessage(toUser
                    , String.format(this.getExplainText(), fromUser)
                            + this.getEventText()
                            + String.format(this.getToEndText(), fromUser)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}

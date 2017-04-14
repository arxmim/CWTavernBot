package org.nia.bots;

import org.apache.commons.lang.time.DateUtils;
import org.nia.logic.ServingMessage;
import org.nia.logic.lists.Location;
import org.nia.logic.quests.ICrossQuestStep;
import org.nia.logic.quests.IQuestEvent;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.User;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */
public class BotTimerThread extends Thread {
    //private static BotTimerThread INSTANCE = null;
    private CWTavernBot bot;

    public BotTimerThread(CWTavernBot bot) {
        this.bot = bot;
        setDaemon(true);
        //INSTANCE = this;
    }

    @Override
    public void run() {
        Date now;
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
                now = new Date();
                for (User usr : User.getAll()) {
                    if (usr.onQuest()) {
                        Quest quest = Quest.getCurrent(usr);
                        QuestEvent event = QuestEvent.getCurrent(quest);
                        if (event != null && event.getEventTime().before(DateUtils.addMinutes(now, -30))) {
                            QuestEvent linkedEvent = event.getLinkedQuestEvent();
                            String badText = event.getStep().getBadText(quest);
                            if (linkedEvent != null) {
                                badText = ((ICrossQuestStep) event.getStep()).getBadInactiveText();
                                Quest linkedQuest = linkedEvent.getQuest();
                                String goodText = ((ICrossQuestStep) linkedEvent.getStep()).getGoodInactiveText() + "\n\nУдачное решение! Твоя награда за задание будет увеличена.";
                                linkedEvent.setWin(true);
                                linkedEvent.getStep().doFinal(linkedEvent);
                                linkedEvent.save();
                                linkedQuest.setEventTime(linkedQuest.getQuestEnum().getNextEventTime(linkedQuest));
                                linkedQuest.save();
                                try {
                                    bot.sendMessage(ServingMessage.getTimedMessage(linkedQuest.getUser(), goodText));
                                } catch (TelegramApiRequestException e) {
                                    if (!e.getErrorCode().equals(403)) {
                                        e.printStackTrace();
                                    }
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            badText += "\n\nОчень жаль! Твоя награда за задание будет уменьшена.";
                            event.setWin(false);
                            event.getStep().doFinal(event);
                            event.save();
                            quest.setEventTime(quest.getQuestEnum().getNextEventTime(quest));
                            quest.save();
                            try {
                                bot.sendMessage(ServingMessage.getTimedMessage(usr, badText));
                            } catch (TelegramApiRequestException e) {
                                if (!e.getErrorCode().equals(403)) {
                                    e.printStackTrace();
                                }
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        } else if (event == null && quest.getEventTime().before(new Date())) {
                            if (quest.getStartTime().before(org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -2))) {
                                quest.setReturnTime(new Date());
                                int reward = quest.getReward();
                                quest.setGoldEarned(reward);
                                quest.save();
                                usr.setLocation(Location.TAVERN);
                                usr.setGold(usr.getGold() + reward);
                                usr.save();
                                try {
                                    bot.sendMessage(ServingMessage.getTimedMessage(usr, "Ты пробыл на задании довольно долго, пора возвращаться. Ты заработав " + reward + Emoji.GOLD));
                                } catch (TelegramApiRequestException e) {
                                    if (!e.getErrorCode().equals(403)) {
                                        e.printStackTrace();
                                    }
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                IQuestEvent iQuestEvent = quest.getQuestEnum().getIQuest().getRandomEvent();
                                event = new QuestEvent();
                                event.setEventTime(quest.getEventTime());
                                event.setQuest(quest);
                                event.setStep(iQuestEvent.getInit());
                                iQuestEvent.init(quest);
                                event.setIQuestEvent(iQuestEvent);
                                event.save();
                                iQuestEvent.getInit().doWork(event);
                                try {
                                    bot.sendMessage(ServingMessage.getTimedMessage(usr, iQuestEvent.getInit().getText(quest)));
                                } catch (TelegramApiRequestException e) {
                                    if (!e.getErrorCode().equals(403)) {
                                        e.printStackTrace();
                                    }
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

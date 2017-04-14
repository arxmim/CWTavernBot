package org.nia.logic.quests.kitchen;

import org.apache.commons.lang3.time.DateUtils;
import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.User;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;

/**
 * @author IANazarov
 */
public enum RoofStairs implements IQuestStep {

    JUMP("Прыгнуть"
            , "Ты успешно спрыгнул со своего насеста.\nБармен на всякий случай дал тебе денег на поход к лекарю, " +
            "и хотя ты совсем не пострадал, от денег ты не стал отказываться."
            , "Ты спрыгнул ровно на столик %s. Надо было смотреть куда прыгаешь! \nТы не только испортил людям " +
            "отдых, но еще и больно ударился, вдобавок тебе пришлось возмещать пострадавшему весь нанесенный ущерб.") {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.setWinChance(20);
        }

        @Override
        public String getGoodText(Quest quest) {
            try {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(quest.getUser() + " спрыгнул сам!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return super.getGoodText(quest);
        }

        @Override
        public String getBadText(Quest quest) {
            Optional<User> max = User.getAll().stream().max((c1, c2) -> {
                if (c1.getUserID() == quest.getUser().getUserID()) return -1;
                if (c2.getUserID() == quest.getUser().getUserID()) return 1;
                if (c1.getLastDrinkTime() != null && c2.getLastDrinkTime() == null) return 1;
                if (c1.getLastDrinkTime() == null && c2.getLastDrinkTime() != null) return -1;
                if (c1.getLastDrinkTime() != null && c2.getLastDrinkTime() != null) {
                    return Long.compare(c1.getLastDrinkTime().getTime(), c2.getLastDrinkTime().getTime());
                };
                return 0;
            });
            String res = "посетителя";
            if (max.isPresent()) {
                res = max.get().toString();
                try {
                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(quest.getUser()
                            + " неудачно спрыгнул ровно на " + res + ", сшиб того со стула и разлил напитки. Недотепа!"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            return String.format(badText, res);
        }
    },
    INIT("", "Тебя отправили менять свечи в лампадах, расположенных под самым потолком общего зала. Ты залез на " +
            "балку под самой крышей, заменил свечи, и тут твоя стремянка начала заваливаться!\nТы можешь попробовать " +
            "спрыгнуть, но это точно ничем хорошим не кончится!\nЛучше попроси кого-нибудь в таверне поставить " +
            "лестницу обратно, для этого напиши в чат таверны и попроси кого-нибудь ответить на твое сообщение " +
            "командой /assist. Но берегись /throw!"
            , Collections.singletonList(JUMP)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            try {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(questEvent.getQuest().getUser()
                        + " менял свечи под потолком таверны, и лестница под ним опрокинулась! Бедолага сидит на " +
                        "балке под крышей таверны и ему нужна помощь! Ответьте на его сообщение командой " +
                        "/assist, чтобы подать ему лестницу!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    };
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    protected String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    RoofStairs(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    RoofStairs(String command, String text, List<IQuestStep> next) {
        this.command = command;
        this.text = text;
        this.next.addAll(next);
    }

    public String solve(User helper, QuestEvent questEvent, boolean isWin) {
        Quest currentQuest = questEvent.getQuest();
        User helpTo = currentQuest.getUser();
        if (isWin) {
            questEvent.setWin(true);
            questEvent.save();
            currentQuest.setEventTime(currentQuest.getQuestEnum().getNextEventTime(currentQuest));
            currentQuest.save();
            SendMessage sendMessage = ServingMessage.getTimedMessage(helpTo, "Тебе помог " + helper + ", вернув " +
                    "лестницу на место. Ты успешно спустился, а бармен выдал тебе и твоему спасителю небольшую премию.");
            helper.setGold(helper.getGold() + 3);
            helper.save();
            try {
                CWTavernBot.INSTANCE.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return helper + " поставил лестницу на место и помог " + helpTo + " спуститься. В благодарность " +
                    "за спасение сотрудника, он получает 3" + Emoji.GOLD + " от таверны!";
        } else {
            questEvent.setWin(true);
            questEvent.save();
            currentQuest.setEventTime(currentQuest.getQuestEnum().getNextEventTime(currentQuest));
            currentQuest.save();
            SendMessage sendMessage = ServingMessage.getTimedMessage(helpTo, helper + " кинул в тебя стакан, и ты " +
                    "упал вниз. Хулигана оштрафовали, а ты получил небольшую премию от бармена для компенсации " +
                    "морального вреда.");
            int gold = helper.getGold();
            if (gold < 10) {
                gold = 0;
            } else {
                gold = gold - 10;
            }
            helper.setGold(gold);
            helper.save();
            try {
                CWTavernBot.INSTANCE.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return helper + " кинул свой напиток в " + helpTo + " и тот сорвался вниз. За нападение на сотрудника " +
                    "таверны он оштрафован на 10" + Emoji.GOLD + "!";//" и ближайший час его не будут обслуживать!";
        }

    }

    @Override
    public String getText(Quest quest) {
        return text;
    }

    @Override
    public List<IQuestStep> getNext(Quest quest) {
        return next;
    }

    @Override
    public String getCommand(String formatParam) {
        return command;
    }

    @Override
    public String getGoodText(Quest quest) {
        return goodText;
    }

    @Override
    public String getBadText(Quest quest) {
        return badText;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public IQuestEvent getIQuest() {
        return KitchenQuest.KitchenEvent.ROOF_STAIRS;
    }
}

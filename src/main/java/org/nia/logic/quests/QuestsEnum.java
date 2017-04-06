package org.nia.logic.quests;

import org.apache.commons.lang3.time.DateUtils;
import org.nia.logic.quests.buyfish.SellFishQuest;
import org.nia.logic.quests.judge.JudgeQuest;
import org.nia.logic.quests.kitchen.KitchenQuest;
import org.nia.logic.quests.potato.PotatoQuest;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;

import java.util.Date;
import java.util.Random;

/**
 * @author IANazarov
 */
public enum QuestsEnum {

//    GRANDMA_ROAD("Остап сказал что видел за городом старушку, которая никак не может перейти дорогу, и попросил тебя помочь ей."
//            , "Дойдя до обозначенного Остапом места, ты и правда увидел бабулю с клюкой."
//            , "Дойдя до обозначенного Остапом места, ты увидел указатель с надписью \"Бабуля - 10км на юг\". Пройдя в указанном " +
//            "направлении, ты увидел еще один указатель \"Бабуля - 3км на запад вдоль реки\".\nПройдя 3км, ты увидел женщину-огра, " +
//            "которая попыталась тебя ограбить. Тебе удалось спастись бегством."
//            , "На дороге бабули не оказалось, зато оказался дедуля. Ты перевел его через дорогу."
//            , "Бабуля отказалась от твоей помощи. И не таких обламывали! - подумал ты и, связав бабулю, перетащил её через дорогу."
//            , "Пока ты переводил бабулю, ты обратил внимание на её странную внешность.\n- Бабуля, почему у тебя такие большие уши?" +
//            ", - спросил ты.\nБабуля сняла капюшон и ты понял что перед тобой стоит эльфийская Видящая. Она предупредила тебя напоследок:\n" +
//            "- Не пей мордор, орком станешь!")

    JUDGE(JudgeQuest.INSTANCE),
    KITCHEN(KitchenQuest.INSTANCE),
    POTATO(PotatoQuest.INSTANCE) {

//        public Date getFirstEventTime() {
//        return DateUtils.addMinutes(new Date(), 1);
//        }
//
//        public Date getNextEventTime(Quest quest) { return DateUtils.addMinutes(new Date(), 1);}
    },
    SELL_FISH(SellFishQuest.INSTANCE)
    ;

    private IQuest quest;


    QuestsEnum(IQuest quest) {
        this.quest = quest;
    }

    public IQuest getIQuest() {
        return quest;
    }

    public Date getFirstEventTime() {

        return DateUtils.addMinutes(new Date(), 30 + new Random().nextInt(31));
//        return DateUtils.addMinutes(new Date(),0);
    }

    public Date getNextEventTime(Quest quest) {
        final int EVENT_INTERVAL = 2;
        int count = QuestEvent.getCount(quest);
        Date startTime = quest.getStartTime();
        startTime = DateUtils.addHours(startTime, EVENT_INTERVAL * count);
        return DateUtils.addMinutes(startTime, 10 + new Random().nextInt(51));
//        return DateUtils.addMinutes(new Date(), 1);
    }
}

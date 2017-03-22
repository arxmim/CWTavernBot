package org.nia.logic.quests;

import org.apache.commons.lang3.time.DateUtils;
import org.nia.logic.quests.buyfish.SellFishQuest;
import org.nia.logic.quests.potato.PotatoQuest;
import org.nia.model.Quest;

import java.util.Date;

/**
 * @author IANazarov
 */
public enum QuestsEnum {

//    BUY_FISH("Остап попросил тебя купить рыбу на рынке."
//            , "На рынке тебе удалось спереть рыбину с прилавка. На выданные Остапом деньги ты покатался на карусели."
//            , "На рынке ты купил черепаху. Она угрожала что тебя найдет какой-то черно-белый медведь и отомстит за нее."
//            , "Ты решил наловить рыбы сам, а деньги Остапа забрать себе. На рыбалке тебе разморило и ты заснул, а " +
//            "когда очнулся обнаружил что к твоим пожиткам приделали ноги. Ты потерял удочку, деньги, сапоги и всю наловленную рыбу... " +
//            "зато в кармане ты обнаружил лягушку. Напоив Остапа, тебе удалось убедить его что лягушка - тоже рыба."),
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

    POTATO(PotatoQuest.INSTANCE),
    SELL_FISH(SellFishQuest.INSTANCE);

    private IQuest quest;


    QuestsEnum(IQuest quest) {
        this.quest = quest;
    }

    public IQuest getIQuest() {
        return quest;
    }

    public Date getFirstEventTime() {

//        return DateUtils.addMinutes(new Date(), 30 + new Random().nextInt(191));
        return DateUtils.addMinutes(new Date(), 2);
    }

    public Date getNextEventTime(Quest quest) {
//        final int EVENT_INTERVAL = 4;
//        int count = QuestEvent.getCount(quest);
//        Date startTime = quest.getStartTime();
//        startTime = DateUtils.addHours(startTime, EVENT_INTERVAL * count);
//        return DateUtils.addMinutes(startTime, 20 + new Random().nextInt(201));
        return DateUtils.addMinutes(new Date(), 1);
    }
}

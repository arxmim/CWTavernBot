package ru.nia.tavern.quests

import org.apache.commons.lang3.time.DateUtils
import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.buyfish.SellFishQuest
import ru.nia.tavern.quests.judge.JudgeQuest
import ru.nia.tavern.quests.kitchen.KitchenQuest
import ru.nia.tavern.quests.potato.PotatoQuest
import java.util.*

/**
 * @author IANazarov
 */
enum class QuestsEnum private constructor(val iQuest: IQuest, val isRunnable: Boolean) {

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

    JUDGE(JudgeQuest.INSTANCE, true),
    KITCHEN(KitchenQuest.INSTANCE, true) {

        //        public Date getFirstEventTime() {
        //        return DateUtils.addMinutes(new Date(), 0);
        //        }
        //
        //        public Date getNextEventTime(Quest quest) { return DateUtils.addMinutes(new Date(), 1);}
    },
    POTATO(PotatoQuest.INSTANCE, true),
    SELL_FISH(SellFishQuest.INSTANCE, true);

    //        return DateUtils.addMinutes(new Date(),0);
    val firstEventTime: Date
        get() = DateUtils.addMinutes(Date(), 30 + Random().nextInt(31))

    fun getNextEventTime(quest: Quest): Date {
        //        final int EVENT_INTERVAL = 1;
        //        int count = QuestEvent.getCount(quest);
        //        Date startTime = quest.getStartTime();
        //        startTime = DateUtils.addHours(startTime, EVENT_INTERVAL * count);
        //        return DateUtils.addMinutes(startTime, 10 + new Random().nextInt(51));
        return DateUtils.addMinutes(Date(), 20 + Random().nextInt(41))
    }
}

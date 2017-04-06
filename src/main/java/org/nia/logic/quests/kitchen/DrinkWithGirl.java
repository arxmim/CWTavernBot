package org.nia.logic.quests.kitchen;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum DrinkWithGirl implements IQuestStep {

    DRINK_FINAL("Накатить еще"
            , "С каждой следующей рюмкой ты блистал остроумием всё больше и больше, но в какой-то момент выпитое взяло " +
            "свое и ты просто вырубился. Девушка позаботилась о тебе - с помощью Лизы она отнесла тебя, пьяненького, в " +
            "подсобку. Когда ты немного пришел в себя, тебя повели к самому Хозяину Таверны. Ты ждал, что сейчас " +
            "будет показательная порка, но...\nХозяин пожал тебе руку, поблагодарил за то, что ты прекрасно развлек " +
            "дочку мэра, благодаря чему у таверны теперь меньше проблем с налоговой, и выписал тебе премию.\n"
            , "После следующей пары стаканов тебя едва не стошнило прямо на твою спутницу, и тебе пришлось срочно " +
            "ретироваться в сторону уборной. Ну и позор!\nКогда ты вернулся, тебя встретила не та очаровательная " +
            "девушка, а самый злючий из всех барменов. Он напомнил тебе, что пить на работе запрещено и оштрафовал тебя.\n"),
    DENY("Отказаться"
            , "Ты отказался пить с девушкой - наверное, оно и к лучшему. Сегодня очень много посетителей и ты получил " +
            "много чаевых."
            , "Ты отказался пить с девушкой. Увы, она оказалось дочкой мэра и подобный отказ сочла " +
            "за оскобление. Когда ты в очередной раз выносил ведро с помоями на заднем дворе таверны, к тебе подошли " +
            "двое неизвестных, скрутили и вылили твоё ведро тебе же на голову. Увы, пока ты отмывался, твоё " +
            "отсутствие заметили и по возвращении выписали тебе штраф."),
    DRINK_MORE("Выпить еще"
            , "Вы выпили еще по рюмашке. \nКажется, твой взгляд начинает затуманиваться.\nА девушка смотрит на тебя " +
            "таким взглядом...\n"
            , Arrays.asList(DRINK_FINAL, DENY)),
    DRINK("Выпить"
            , "Вы накатили по рюмочке.\nНе останавливаться же на одной рюмке, можно накатить еще!\n"
            , Arrays.asList(DRINK_MORE, DENY)),
    INIT("", "Ты как обычно протирал столики, когда к тебе подошла симпатичная девушка и сказала что хочет угостить " +
            "тебя выпивкой.\nС одной стороны, ты не пил уже целую вечность (на самом деле нет), " +
            "а с другой стороны, лучше не пить пока работаешь."
            , Arrays.asList(DRINK, DENY));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    DrinkWithGirl(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    DrinkWithGirl(String command, String text, List<IQuestStep> next) {
        this.command = command;
        this.text = text;
        this.next.addAll(next);
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
        return KitchenQuest.KitchenEvent.DRINK_WITH_GIRL;
    }
}

package org.nia.logic.quests.judge;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum AIJudging implements IQuestStep {
    END1(""
            , ""
            , ""),
    END2(""
            , ""
            , ""),
    SGT_FIRST("Выслушать сержанта"
            , ""
            , Arrays.asList(END1, END2)),
    GHOST_FIRST("Выслушать призрака"
            , ""
            , Arrays.asList(END1, END2)),
    INIT("", "Следующее дело оказалось таким необычным, что местный судья решил передать свои полномочия тебе.\nМожешь " +
            "гордиться временным повышением! Суть дела такова - сержант из городской охраны обратился к помощи " +
            "нелегальных волшебников и вселил дух своего деда в стальную оболочку доспехов. Теперь дух вполне " +
            "самостоятелен и хочет жить своей жизнью, ну а сержант считает его своей собственностью. Дело осложняется " +
            "еще тем, что призрак в доспехах успел разбить пару лиц, и теперь от пострадавших поступают иски. Только " +
            "неясно, кого считать виноватым - призрака или сержанта.\nСперва тебе нужно выслушать аргументы сторон " +
            "относительно самостоятельности призрака."
            , Arrays.asList(GHOST_FIRST, SGT_FIRST));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    AIJudging(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    AIJudging(String command, String text, List<IQuestStep> next) {
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
        return null;
//        return PotatoQuest.PotatoEvent.POTATO_GIANT_JUK;
    }
}

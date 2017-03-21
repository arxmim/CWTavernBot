package org.nia.logic.quests.potato;

import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum Orcs implements IQuestStep {
    ATACK("Уничтожить грибы"
            , "У тебя был с собой огнемет, так что ты смог быстро сжечь грибы и вернуться к работе."
            , "Ты долго бился на кулаках с грибами. Когда последний гриб был побежден, уже стемнело. " +
            "Похоже, ты не успеешь накопать достаточно картошки в срок."),
    IGNORE("Проигнорировать грибы"
            , "Ты решил не трогать эти странные грибы. Пока ты копал картошку, грибы выкопались и достали " +
            "откуда-то острые предметы, похожие на смесь бензопилы и топора. Тебе повезло, на тебе была фиолетовая " +
            "спецовка и грибы, не заметив тебя, умчались с грозными воплями на соседний огород."
            , "Ты решил не трогать эти странные грибы. Пока ты копал картошку, грибы выкопались и достали " +
            "откуда-то острые предметы, похожие на смесь бензопилы и топора, и решили тебя ограбить. " +
            "Увы, у тебя ничего с собой не было, так что они просто выбили тебе зуб и залутали часть картошки."),
    INIT("Неподалеку от грядки ты увидел странные гигантские грибы, у каждого есть пара рук и голова."
            , Arrays.asList(ATACK, IGNORE));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    Orcs(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    Orcs(String text, List<IQuestStep> next) {
        this.text = text;
        this.next.addAll(next);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<IQuestStep> getNext() {
        return next;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getGoodText() {
        return goodText;
    }

    @Override
    public String getBadText() {
        return badText;
    }

    @Override
    public String getName() {
        return name();
    }
}

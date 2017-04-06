package org.nia.logic.quests.potato;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum Gopnik implements IQuestStep {
    PAPIROSA("- Папироску будете?"
            , "Хорошо что у тебя осталась пара папирос после общения с той черепахой и чертовски хорошо что парням " +
            "они пришлись по вкусу. Ты уговорил их сходить на соседский огород. Спустя некоторое время парни " +
            "вернулись и отсыпали тебе часть добытой ветоши, которую можно продать барахольщикам."
            , "Ты внезапно вспомнил, что последнюю папиросу ты выкурил еще на прошлой неделе. Парням такой развод " +
            "не понравился и ты получил по шее.\nОчнувшись, ты увидел что с тебя стащили сапоги, придется покупать новые.\n"),
    POTATO("- Нуууу, есть саженцы картошки..."
            , "- Ну и дурак, кому нужна твоя картошка! - рассмеялись молодые люди.\n" +
            "- Держи, тут пара монет, выпей чего-нибудь в таверне, может поумнеешь! - с этими словами один из парней " +
            "выдал тебе мешочек с золотом."
            , "Кажется, ты оскорбил парней, и тебя избили. Уходя, они растоптали часть твоих грядок."),
    NO_DUST("- Нет у меня порошка! Я картошку копаю!"
            , "- А что есть?"
            , Arrays.asList(PAPIROSA, POTATO)),
    DRAKA("Ввязаться в драку"
            , "Ты не зря столько времени проводишь на свежем воздухе, твоей силе позавидуют многие. Тебе даже драться " +
            "не пришлось, парни сами разбежались, увидев какой ты агрессивный."
            , "Такому слабаку как ты не стоило бы искать драки. Тебя избили.\n" +
            "Поскольку никакого порошка у тебя не оказалось, парни просто растоптали часть твоих грядок.\n"),
    INIT("", "К тебе подошли гоповатого вида парни:\n" +
            "- Есть чё? Мы видели как ты на рынке порошком по 15 торговал. Гони товар!"
            , Arrays.asList(NO_DUST, DRAKA));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    Gopnik(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    Gopnik(String command, String text, List<IQuestStep> next) {
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
        return PotatoQuest.PotatoEvent.POTATO_GOPNIK;
    }
}

package org.nia.logic.quests.potato;

import org.nia.logic.quests.ICrossQuestStep;
import org.nia.logic.quests.IQuestAction;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.logic.quests.crossevents.FieldRowsAction;
import org.nia.model.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author IANazarov
 */
public enum FieldRows implements ICrossQuestStep {
    ANSWER_HOW_BAD("Ну, я старался...", Collections.singletonList(FieldRowsAction.ANSWER_HOW_BAD), true),
    ANSWER_HOW_GOOD("Отлично вскопал!", Collections.singletonList(FieldRowsAction.ANSWER_HOW_GOOD), true),
    QUESTION_HOW(Arrays.asList(ANSWER_HOW_GOOD, ANSWER_HOW_BAD)),
    ANSWER_WHAT_GRASS("Росла какая-то трава", Collections.singletonList(FieldRowsAction.ANSWER_WHAT_GRASS), true),
    ANSWER_WHAT_NOTHING("Ничего не росло", Collections.singletonList(FieldRowsAction.ANSWER_WHAT_NOTHING), true),
    QUESTION_WHAT(Arrays.asList(ANSWER_WHAT_NOTHING, ANSWER_WHAT_GRASS)),
    AGREE("Договориться"
            , "Твоё обояние не подвело тебя - сосед выплатил тебе деньги за работу, причем даже больше чем обещал Остап." +
            " Может, стоит работать на соседа?"
            , "Твоя пьяная рожа и заплетающийся язык не убедили соседа. Он заставил тебя выплатить небольшую сумму за " +
            "нанесенный его рассаде ущерб."
            , FieldRowsAction.INIT,
            "Тебе не удалось договориться с соседом мирным путем, и вы пошли в местную администрацию, разбираться в " +
                    "досудебном порядке. Там вы встретили %s, который согласился рассмотреть ваш спор и постарается " +
                    "тебе помочь."),
    THREAT("Угрожать"
            , "Твоя пьяная рожа и дико выпученные глаза убедили соседа что с тобой лучше не связываться - он заплатил " +
            "тебе за работу на его огороде."
            , "Ты набычился, закатал рукава, взял лопату в руки и начал двигаться на соседа.\n\nВнезапно заиграл " +
            "бессмертный хит Эннио Мориконе, а сосед неторопливым движением достал из кобуры на поясе армейский кольт.\n\n" +
            "Через мнгновение ты уже доставал кошелек и отсчитывал возмещение за нанесенный соседу ущерб."),
    NEW_ROW("Начать копать новую грядку"
            , "Пока ты вскапывал новую грядку, тебе попался какой-то предмет.\n\nОго, похоже это мешочек с золотом!"
            , "Вскапывание новой грядки шло гораздо медленнее. Ты потерял кучу времени, выкорчевывая валуны и пропалывая сорняки."),
    CONTINUE("Продолжить копать грядку"
            , "Через некоторое время пришел какой-то мужик, и сказал что ты уже давно вышел за пределы огорода Остапа " +
            "и теперь копаешь на его земле. Мда, ты опять попал в неудобное положение. Неплохо бы получить с этого мужика" +
            " деньги за твою работу - попробуй договориться с ним, по-хорошему или по-плохому."
            , Arrays.asList(AGREE, THREAT)),
    INIT("", "Тебе удалось найти хорошую рыхлую почву, на которой копать картошку проще. Ты уже вскопал достаточно длинную грядку."
            , Arrays.asList(CONTINUE, NEW_ROW));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";
    private boolean waitUser = false;
    private String interceptText = "";
    private List<IQuestAction> actionList = new ArrayList<>();


    FieldRows(List<IQuestStep> next) {
        this.next.addAll(next);
    }

    FieldRows(String command, List<IQuestAction> actionList, boolean isWaitUser) {
        this.command = command;
        this.actionList = actionList;
        this.waitUser = isWaitUser;
    }

    FieldRows(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    FieldRows(String command, String goodText, String badText, IQuestAction interceptionAction, String interceptText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
        this.actionList.add(interceptionAction);
        this.interceptText = interceptText;
        this.waitUser = true;
    }

    FieldRows(String command, String text, List<IQuestStep> next) {
        this.command = command;
        this.text = text;
        this.next.addAll(next);
    }

    @Override
    public boolean isWaitUser() {
        return waitUser;
    }

    public String getInterceptText() {
        return interceptText;
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
    public List<IQuestAction> getActionList() {
        return actionList;
    }

    @Override
    public IQuestEvent getIQuest() {
        return PotatoQuest.PotatoEvent.POTATO_FIELD_ROWS;
    }
}

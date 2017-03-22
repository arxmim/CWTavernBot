package org.nia.logic.quests.potato;

import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum FieldRows implements IQuestStep {
    AGREE("Договориться"
            , "Твоё обояние не подвело тебя - сосед выплатил тебе деньги за работу, причем даже больше чем обещал Остап." +
            " Может, стоит работать на соседа?"
            , "Твоя пьяная рожа и заплетающийся язык не убедили соседа. Он заставил тебя выплатить небольшую сумму за " +
            "нанесенный его рассаде ущерб."),
    THREAT("Угрожать"
            , "Твоя пьяная рожа и дико выпученные глаза убедили соседа что с тобой лучше не связываться - он заплатил " +
            "тебе за работу на его огороде."
            , "Ты набычился, закатал рукава, взял лопату в руки и начал двигаться на соседа.\n\nВнезапно заиграл " +
            "бессмертный хит Эннио Мориконе, а сосед неторопливым движением достал из кобуры на поясе армейский кольт.\n\n" +
            "Через мнгновение ты уже доставал кошелек и отсчитывал возмещение за причененный соседу ущерб."),
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

    FieldRows(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    FieldRows(String command, String text, List<IQuestStep> next) {
        this.command = command;
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

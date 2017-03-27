package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Иван, 26.03.2017.
 */
public enum Inspection implements IQuestStep {
    GIVE_DOCS("Поискать документы", "Ты не нашел документы, но пока инспектор зевал, смог стащить документы у соседа. " +
            "По ним выходило, что ты продаешь детские игрушки и письменные принадлежности, но твой ораторский талант " +
            "выручил тебя. Ничего не заподозривший инспектор направился к соседу, которому ты вернул \"неожиданно " +
            "нашедшиеся\" документы за небольшую награду."
            , "Ты не нашел документы, но пока инспектор зевал, смог стащить документы у соседа. По ним выходило, что " +
            "ты продаешь детские игрушки и письменные принадлежности, чего не мог не заметить инспектор. Ты был " +
            "оштрафован!"),
    GIVE_MONEY("Дать взятку"
                 , "Ты был очень обаятелен с инспектором:\n" +
            "- Лучше поделиться частью выручки с вами, инспектор, чем с этими гадами из налоговой!\nИнспектор ушел к " +
            "соседу, кляня коррупционеров из налоговой, которые не дают спокойно спать честным людям. Сосед, тоже " +
            "торгующий рыбой, не смог договориться с инспектором, у тебя стало на одного конкурента меньше, и ты " +
            "неплохо заработал."
                 , "Твои медвежьи манеры не помогли договориться с инспектором. Тебя оштрафовали!"),
    INIT("", "К тебе подошел инспектор из РыбПотребНадзора.\n-Ваши документы на товар, пожалуйста!"
                 , Arrays.asList(GIVE_DOCS, GIVE_MONEY));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    Inspection(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    Inspection(String command, String text, List<IQuestStep> next) {
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

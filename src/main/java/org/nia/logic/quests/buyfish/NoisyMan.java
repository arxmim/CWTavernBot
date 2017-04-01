package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Иван, 26.03.2017.
 */
public enum NoisyMan implements IQuestStep {

    KICK("Выгнать мужика", "Ты без труда притянул мужика за воротник и пообещал устроить ему веселую жизнь, если он " +
            "сунется к тебе еще раз. Кажется, зануда испачкал штаны! Впрочем, ему хватило ума дать тебе немного " +
            "\"отступных\", чтобы не эскалировать конфликт.", "Ты попытался выгнать мужика, но тот был явно сильнее " +
            "тебя. Хорошо, что твоя голова оказалась крепче соседского прилавка, об который он тебя бил." +
            "Тебе пришлось заплатить администрации рынка за разбитый прилавок."),
    ASK("\"Ты рыбу будешь покупать?\"", "- Да! - ответил мужик.\nТы продал ему воблу по цене королевского осьминога."
            , "- Нет!  - сказал мужик и свалил.\nТы впустую потратил время."),
    RUDE("\"А вам какая разница?\""
            , "- А вы со всеми такой грубый?"
            , Arrays.asList(ASK, KICK)),
    COST("\"С побережья!\""
            , "- С речного?"
            , Arrays.asList(RUDE, KICK)),
    NO_DEGUSTATE("\"Нет, не дам\""
            , "- А откуда рыбу возите?"
            , Arrays.asList(COST, KICK)),
    FRESH("\"Да, свежая\""
            , "- А попробовать дадите?"
            , Arrays.asList(NO_DEGUSTATE, KICK)),
    SELL("\"Да, рыбу продаю\""
            , "- А рыба свежая?"
            , Arrays.asList(FRESH, KICK)),
    INIT("", "К тебе подошел мужик в заношенной куртке.\n\n- Вы тут рыбу продаете? А рыба свежая? А попробовать дадите? " +
            "- начал он засыпать тебя вопросами."
            , Arrays.asList(SELL, KICK));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    NoisyMan(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    NoisyMan(String command, String text, List<IQuestStep> next) {
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
    public String getCommand(String formatParam) {
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

    @Override
    public IQuestEvent getIQuest() {
        return SellFishQuest.SellFishEvent.SELL_FISH_NOISY_MAN;
    }
}

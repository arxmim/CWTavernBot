package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum DragonMeat implements IQuestStep {

    IGNORE("Отказаться от драконьего мяса"
            , "Карлик отошел от твоего прилавка, и продолжил торговать своим сомнительным товаром в другой части рынка. " +
            "Через некоторое время к тебе пришла стража, вылавливающая карлика-жулика, и за помощь следствию ты получил небольшую награду."
            , "Карлик отошел от твоего прилавка, а через некоторое время ты заметил, что с твоего прилавка пропала " +
            "часть товара. Похоже, этот жулик обворовал тебя!"),
    BUY_DRAGON_MEAT("Купить драконье мясо"
            , "Ты купил один ломтик драконьего мяса за часть собранной выручки. Видимо ты был вдребезги пьян, так как сразу же решил попробовать его на вкус...\n\n" +
            "Похоже, карлик не врал, и мясо действительно драконье! У тебя прибавилось сил, обаяния и ума, доспех засиял, " +
            "и даже твоё копьё как будто стало длиннее. Окрыленный новыми возможностями, ты без труда продал часть " +
            "своего товара за двойную цену. Жаль, что чудодейственный эффект мяса быстро выветрился, до того как ты " +
            "испробовал своё удлинненное копье в бою."
            , "Ты купил один ломтик драконьего мяса за часть собранной выручки. Чуть позже по запаху ты понял, что " +
            "это совсем не мясо, но карлика и след простыл."),
    INIT("", "К твоему прилавку с рыбой подошел карлик с мешком.\n" +
            "- Драконье мясо, недорого! Эй, купи драконьего мяса! Ты торгуешь рыбой? Хм, у дракона тоже есть чешуя, " +
            "так что считай что мы с тобой оба торгуем рыбой! - с этими словами путник раскрыл перед тобой мешок. \n" +
            "Он оказался забит чем-то, похожим на засохшее дерьмо."
            , Arrays.asList(BUY_DRAGON_MEAT, IGNORE));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    DragonMeat(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    DragonMeat(String command, String text, List<IQuestStep> next) {
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
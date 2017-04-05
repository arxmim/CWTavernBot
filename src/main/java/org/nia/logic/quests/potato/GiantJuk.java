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
public enum GiantJuk implements IQuestStep {
    ATACK("Атаковать жука"
            , "Ты храбро атаковал насекомое и жук бежал от твоей праведной ярости."
            , "Когда жук начал приближаться, ты внезапно вспомнил, что забыл меч в таверне. " +
            "Ты сбегал в таверну за мечом, но когда вернулся, жук уже уполз в закат, сожрав часть картошки."),
    PLAY("Подразнить жука"
            , "Тебе удалось привлечь внимание жука лязганьем своих доспехов. Жук начал неспеша " +
            "ползти в твою сторону и в итоге тебе удалось увести его в соседний огород. Теперь это не твоя поблема."
            , "Ты кричал жуку разные неприличные слова, но он так и не отреагировал. Жаль, на тебе нет железных " +
            "доспехов, говорят, такие жуки реагируют только на грохот металла. Наевшись картошки, жук уполз в закат."),
    INIT("На огороде ты увидел колорадского жука размером с лошадь."
            , Arrays.asList(ATACK, PLAY));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    GiantJuk(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    GiantJuk(String text, List<IQuestStep> next) {
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
        return PotatoQuest.PotatoEvent.POTATO_GIANT_JUK;
    }
}

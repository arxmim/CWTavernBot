package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author IANazarov
 */
public enum MagicFish implements IQuestStep {

    THREAT("Спросить о награде", "Хитрая рыбина захотела уморить тебя своими ужасными анекдотами, но ты вовремя " +
            "догадался оглушить её ударом об колено. Тебе пришлось повторить \"воспитание\" несколько раз, пока наконец" +
            "щука не поняла, что с тобой опасно иметь дело. Ты заставил щуку выплатить награду вперед - в пруду внезапно " +
            "поднялась волна и принесла к берегу небольшой сундучок с золотом - после чего отпустил говорящую рыбу."
            , "Хитрая рыбина продолжила рассказывать тебе ужасные анекдоты, и ради спасения " +
            "своих ушей ты был вынужден бросить её в пруд. Никакой награды ты не дождался, только время потерял."),
    THROW("Бросить щуку в пруд", "Ты бросил щуку в пруд, и тут же в пруду поднялась волна и принесла к берегу небольшой " +
            "сундучок с золотом.", "Ты бросил щуку в пруд, и больше не видел ни рыбу, ни награду. Только время потерял"),
    SELL("Продать говорящую щуку"
            , "Ты продал щуку за приличную сумму какому-то идиоту."
            , "Ты вырубил рыбину и продал её какому-то худому старику с сундуком в руках. Сразу после этого тебя " +
            "обворовали, часть товара внезапно протухла, а еще шпана закидала тебя камнями. Странно, наверное просто " +
            "совпадение. Сегодня торговля явно не задалась."),
    GO("Пойти к пруду"
            , "Ты оставил свой прилавок и пошел вместе с щукой к пруду. По пути она травила бородатые анекдоты и шутки " +
            "из кривого зеркала, которые были не смешными еще когда твой дед воевал за Красный Замок. Почуяв поблизости " +
            "воду, щука разволновалась и стала еще более несносной.", Arrays.asList(THROW, THREAT)),
    INIT("", "Одна из щук, которые ты продавал, внезапно заговорила с тобой:\n- Не продавай меня, добрый молодец! " +
            "Выпусти меня на волю, я тебя отблагодарю! Сходи до ближайшего пруда, и выпусти меня в воду, а я тебе дам " +
            "мешок с драгоценными камнями!"
            , Arrays.asList(GO, SELL));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    MagicFish(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    MagicFish(String command, String text, List<IQuestStep> next) {
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

package org.nia.logic.quests.buyfish;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Иван, 25.03.2017.
 */
public enum TurtlePanda implements IQuestStep {

    SELL("Вырубить и продать черепаху", "Ты вырубил черепаху. Её никто не хотел покупать и ты уже отчаялся избавиться " +
            "от неё, как вдруг подошел толстяк-громила и восторженно уставился на черепаху. Тебе хватило смекалки " +
            "впарить ему черепаху, умело обходя неудобные вопросы."
            , "Ты вырубил черепаху. Её никто не хотел покупать и ты уже отчаялся избавиться " +
            "от неё, как вдруг подошел толстяк-громила и восторженно уставился на черепаху. На вопрос, почему она без " +
            "сознания ты не смог убедительно соврать. Толстяк тебя побил, забрал черепаху и на всякий случай ограбил."),
    DISAGREE("Отказаться", "Ты отказался учиться у черепахи.\n- Я вижу, ты добрый и миролюбивый человек - сказала " +
            "черепаха - возьми этот цветок персикового дерева в память об этом дне.\nДля тебя осталось загадкой, где " +
            "черепаха хранила этот цветок, но термоядерная смесь запаха рыбы и персиков точно не дадут тебе забыть " +
            "этот случай. Тебе удалось его продать какому-то мужику, искавшему \"аленький\" цветочек для своей дочери."
            , "Ты отказался учиться у черепахи.\n- Ты оскорбил меня своим отказом, грязная обезьяны! - заявила " +
            "черепаха и, прыгнула на тебя, спрятавшись в панцирь. Ты не смог увернуться и потерял сознание от удара. " +
            "Очнувшись, ты увидел что черепаха пропала вместе с частью твоей выручки."),
    AGREE("Согласиться стать воином дракона", "Ты прошел экспресс-курс восточных единоборств. Твоя ловкость впечатлила " +
            "черепаху, и напоследок ты получил свиток в футляре. Непонятно, где черепаха его хранила, но не отказываться " +
            "же от халявы? Впрочем, свиток оказался пустым, так что ты пустил его на папиросы, а вот футляр удалось " +
            "толкнуть на базаре за хорошую цену."
            , "Ты прошел экспресс-курс восточных единоборств. Честно говоря, ты был больше похож на танцующую корову, " +
            "чем на воина дракона. Черепаха была явно разочарована, и отказалась продолжать обучение. Ты впустую потерял время."),
    IGNORE("Проигнорировать черепаху", "Ты не стал следить за черепахой.\nСпустя некоторое время черепаха вернулась, " +
            "и со словами \"Спасибо что отпустил меня, ты такой очаровашка!\" дала тебе денег. Больше ты её не видел."
            , "Ты не стал следить за черепахой.\nСпустя некоторое время черепаха вернулась с каким-то толстым громилой, " +
            "одетым в шкуры черно-белого медведя, они тебя избили и забрали часть товара.\n- Не надо было меня кидать" +
            ", козел! - заявила черепаха напоследок."),
    FOLLOW("Проследить за черепахой"
            , "Черепаха вышла из города, и буквально за первым кустом начала копаться в земле.\n\nДождавшись её ухода, " +
            "ты разрыл её тайник и достал оттуда свиток в дорогом футляре. Свиток оказался пуст, а вот футляр тебе " +
            "удалось толкнуть на базаре за хорошую цену."
            , "Твои неловкие движения привлекли внимание черепахи, и она, ускорив шаг, " +
            "смогла от тебя скрыться. Похоже, ты был слишком пьян, раз не смог догнать черепаху!\nТы впустую потерял время."),
    THROW("Выкинуть черепаху"
            , "Ты выкинул черепаху из ящика с рыбой. Стоило ей оказаться на земле, как она встала на задние лапы и " +
            "деловым шагом двинулась прочь с рынка.", Arrays.asList(FOLLOW, IGNORE)),
    INSPECT("Осмотреть черепаху"
            , "Как только ты приблизился к черепахе, она заговорила с тобой:\n" +
            "- Я не простая черепаха, мне известны тайные боевые искусства. Я могу сделать тебя " +
            "воином дракона!"
            , Arrays.asList(AGREE, DISAGREE, SELL)),
    INIT("", "Под верхним слоем рыбы, которую ты продаешь, ты неожиданно заметил живую огромную черепаху.\n" +
            "Она посмотрела на тебя умными глазами, и как будто сгруппировалась."
            , Arrays.asList(INSPECT, THROW));
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    TurtlePanda(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    TurtlePanda(String command, String text, List<IQuestStep> next) {
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
        return SellFishQuest.SellFishEvent.SELL_FISH_TURTLE_PANDA;
    }

}

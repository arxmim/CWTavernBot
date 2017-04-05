package org.nia.logic.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Иван, 08.03.2017.
 */
public enum DrinkType {
    BEER("/bunt", "кружка золотистого пива \"БУНТ\"", "кружки БУНТА", "кружку БУНТА", "кружкой БУНТА", "пустую"),
    RED_POWER("/redpower", "бокал коктейля \"Red Power\"", "бокала \"Red Power\"", "бокал \"Red Power\"", "бокалом \"Red Power\"", "пустой"),
    AVE_WHITE("/ave_white", "жбан ядреной нордической сивухи", "жбана сивухи", "жбан сивухи", "жбаном сивухи", "пустой"),
    GHOST("/ghost", "стопка водки \"Призрак коммунизма\"", "стопки водки", "стопку водки", "стопкой водки", "пустую"),
    MORDOR("/mordor", "бокал \"Магма Ородруина\"", "бокала \"Магмы Ородруина\"", "бокал \"Магмы Ородруина\"", "бокалом \"Магмы Ородруина\"", "пустой"),
    MANDARINE("/mandarine", "бокал коктейля \"Мандариновка\"", "бокала \"Мандариновки\"", "бокал \"Мандариновки\"", "бокалом \"Мандариновки\"", "пустой"),
    CHLEN("/chlen", "бокал коктейля \"Блю Куросао\"", "бокала \"Блю Куросао\"", "бокал \"Блю Куросао\"", "бокалом \"Блю Куросао\"", "пустой");
    private final String command;
    private final String name;
    private final String onThrow;
    private final List<String> givePhrases;
    private final List<String> drinkPartPhrases;
    private final List<String> drinkRemainPhrases;
    private final List<String> drinkAllPhrases;
    private final List<String> throwNonePhrases;
    private final List<String> throwTargetFullPhrases;
    private final List<String> throwTargetEmptyPhrases;
    private final List<String> selfThrowPhrases;
    private final List<String> enterPhrases;

    DrinkType(String command, String onGive, String onDrink, String onThrow, String whom, String empty) {
        this.command = command;
        this.name = onGive;
        this.onThrow = onThrow;
        this.givePhrases = new ArrayList<>();
        this.drinkPartPhrases = new ArrayList<>();
        this.drinkRemainPhrases = new ArrayList<>();
        this.drinkAllPhrases = new ArrayList<>();
        this.throwNonePhrases = new ArrayList<>();
        this.throwTargetFullPhrases = new ArrayList<>();
        this.throwTargetEmptyPhrases = new ArrayList<>();
        this.selfThrowPhrases = new ArrayList<>();
        this.enterPhrases = new ArrayList<>();
        givePhrases.add("%s, вот тебе " + onGive + ", можешь смело /drink! ну или /throw, но мы же тут разумные люди, да?");
        givePhrases.add("%s, специально для тебя " + onGive + " - лучше во всех 5 замках не найти! /drink - ну ты знаешь что делать.");
        givePhrases.add("%s, сегодня нет отбоя от желающих выпить " + onThrow + ", но для тебя у меня всегда найдется в запасе.");
        givePhrases.add("%s, ты вроде уже пил сегодня " + onThrow + ", но раз ты настаиваешь, держи еще один.");
        givePhrases.add("%s, держи " + onThrow + ", приготовлено по рецепту моей бабушки.");
        drinkPartPhrases.add("%s выпил лишь половину " + onDrink + ", сразу допить не смог. Слабак что ли?");
        drinkPartPhrases.add("%s отпил немного из " + onDrink + ", и как будто немного позеленел. Возможно ему стоит заказывать не такие крепкие напитки?");
        drinkPartPhrases.add("%s отхлебнул из " + onDrink + ", и откинулся на стуле. Эй, ты сюда пить пришел или по сторонам смотреть?");
        drinkRemainPhrases.add("%s допил оставшуюся половину " + onDrink + ", ну наконец-то.");
        drinkRemainPhrases.add("%s зажмурился и допил " + onThrow + ". Видимо, у человека сегодня тяжелый день.");
        drinkAllPhrases.add("%s залпом выпил " + onThrow + ". Как насчет добавки?");
        drinkAllPhrases.add("%s сделал глубокий вдох и осушил целиком " + onThrow + ". Вот это мастерство!");
        drinkAllPhrases.add("Едва увидев " + onThrow + ", %s схватил свой напиток и под одобряющие возгласы посетителей осушил его.");
        drinkAllPhrases.add("%s сделал пару богатырских глотков и отставил в сторону " + empty + " " + onThrow + ". Да ты похоже соскучился по выпивке!");
        throwNonePhrases.add("%s швырнул " + onThrow + " об пол! Дебошир!");
        throwNonePhrases.add("Кажется, %s хотел швырнуть " + onThrow + " в кого-то из посетителей, но попал в стену.");
        throwNonePhrases.add("Метким броском %s отправил " + onThrow + " в открытое окно. Спасибо что не в барную стойку!");
        throwNonePhrases.add("%s швырнул " + onThrow + " в барную стойку, но особая барменская магия отклонила напиток в сторону. Барная стойка под надежной защитой!");
        throwTargetFullPhrases.add("%s подошел к %s и вылил свой напиток тому за шиворот. Эх, испортил такую рубаху!");
        throwTargetFullPhrases.add("Не допивая, %s кинул " + onThrow + " в %s. Весь пол и половина посетителей теперь в бухле. Впрочем, адресату досталось больше всех!");
        throwTargetEmptyPhrases.add("%s кинул " + empty + " " + onThrow + " в %s. Эй, давайте только без драки!");
        throwTargetEmptyPhrases.add("Без содержимого кидать " + onThrow + " проще! - заявил %s и метким броском подбил глаз ничего не подозревающему %s. Видимо, и правда проще.");
        selfThrowPhrases.add("%s поставил " + onThrow + " себе на голову и полез танцевать на стуле. Уважаемый, тебе показать где выход?");
        selfThrowPhrases.add("%s начал жонглировать " + whom + ", но что-то пошло не так и он уронил " + onThrow + " на себя.");
        enterPhrases.add("%s добро пожаловать в нашу таверну! Держи для рывка " + onThrow + ", можешь смело /drink! ну или /throw, но мы же тут разумные люди, да?");
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getGivePhrase() {
        return givePhrases.get(new Random().nextInt(givePhrases.size()));
    }

    public String getDrinkPartPhrase() {
        return drinkPartPhrases.get(new Random().nextInt(drinkPartPhrases.size()));
    }

    public String getDrinkRemainPhrase() {
        return drinkRemainPhrases.get(new Random().nextInt(drinkRemainPhrases.size()));
    }

    public String getDrinkAllPhrase() {
        return drinkAllPhrases.get(new Random().nextInt(drinkAllPhrases.size()));
    }

    public String getThrowNonePhrase() {
        return throwNonePhrases.get(new Random().nextInt(throwNonePhrases.size()));
    }

    public String getThrowTargetFullPhrase() {
        return throwTargetFullPhrases.get(new Random().nextInt(throwTargetFullPhrases.size()));
    }

    public String getThrowTargetEmptyPhrase() {
        return throwTargetEmptyPhrases.get(new Random().nextInt(throwTargetEmptyPhrases.size()));
    }

    public String getSelfThrowPhrase() {
        return selfThrowPhrases.get(new Random().nextInt(selfThrowPhrases.size()));
    }

    public String getEnterPhrase() {
        return enterPhrases.get(new Random().nextInt(enterPhrases.size()));
    }

    public static DrinkType getRandom() {
        return values()[new Random().nextInt(values().length)];
    }

    public String getOnThrow() {
        return onThrow;
    }
}

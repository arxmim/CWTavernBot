package ru.nia.tavern.model.types

import java.util.*

/**
 * @author Иван, 08.03.2017.
 */
enum class DrinkType private constructor(val command: String, val drinkName: String, onDrink: String, val onThrow: String, whom: String, empty: String) {
    BEER("/bunt", "кружка золотистого пива \"БУНТ\"", "кружки БУНТА", "кружку БУНТА", "кружкой БУНТА", "пустую"),
    RED_POWER("/redpower", "бокал коктейля \"Red Power\"", "бокала \"Red Power\"", "бокал \"Red Power\"", "бокалом \"Red Power\"", "пустой"),
    AVE_WHITE("/ave_white", "жбан ядреной нордической сивухи", "жбана сивухи", "жбан сивухи", "жбаном сивухи", "пустой"),
    GHOST("/ghost", "стопка водки \"Призрак коммунизма\"", "стопки водки", "стопку водки", "стопкой водки", "пустую"),
    MORDOR("/mordor", "бокал \"Магма Ородруина\"", "бокала \"Магмы Ородруина\"", "бокал \"Магмы Ородруина\"", "бокалом \"Магмы Ородруина\"", "пустой"),
    MANDARINE("/mandarine", "бокал коктейля \"Мандариновка\"", "бокала \"Мандариновки\"", "бокал \"Мандариновки\"", "бокалом \"Мандариновки\"", "пустой"),
    CHLEN("/chlen", "бокал коктейля \"Блю Куросао\"", "бокала \"Блю Куросао\"", "бокал \"Блю Куросао\"", "бокалом \"Блю Куросао\"", "пустой");

    private val givePhrases: MutableList<String>
    private val drinkPartPhrases: MutableList<String>
    private val drinkRemainPhrases: MutableList<String>
    private val drinkAllPhrases: MutableList<String>
    private val throwNonePhrases: MutableList<String>
    private val throwTargetFullPhrases: MutableList<String>
    private val throwTargetEmptyPhrases: MutableList<String>
    private val selfThrowPhrases: MutableList<String>
    private val enterPhrases: MutableList<String>

    val givePhrase: String
        get() = givePhrases[Random().nextInt(givePhrases.size)]

    val drinkPartPhrase: String
        get() = drinkPartPhrases[Random().nextInt(drinkPartPhrases.size)]

    val drinkRemainPhrase: String
        get() = drinkRemainPhrases[Random().nextInt(drinkRemainPhrases.size)]

    val drinkAllPhrase: String
        get() = drinkAllPhrases[Random().nextInt(drinkAllPhrases.size)]

    val throwNonePhrase: String
        get() = throwNonePhrases[Random().nextInt(throwNonePhrases.size)]

    val throwTargetFullPhrase: String
        get() = throwTargetFullPhrases[Random().nextInt(throwTargetFullPhrases.size)]

    val throwTargetEmptyPhrase: String
        get() = throwTargetEmptyPhrases[Random().nextInt(throwTargetEmptyPhrases.size)]

    val selfThrowPhrase: String
        get() = selfThrowPhrases[Random().nextInt(selfThrowPhrases.size)]

    val enterPhrase: String
        get() = enterPhrases[Random().nextInt(enterPhrases.size)]

    init {
        this.givePhrases = ArrayList()
        this.drinkPartPhrases = ArrayList()
        this.drinkRemainPhrases = ArrayList()
        this.drinkAllPhrases = ArrayList()
        this.throwNonePhrases = ArrayList()
        this.throwTargetFullPhrases = ArrayList()
        this.throwTargetEmptyPhrases = ArrayList()
        this.selfThrowPhrases = ArrayList()
        this.enterPhrases = ArrayList()
        givePhrases.add("%s, вот тебе $drinkName, можешь смело /drink! ну или /throw, но мы же тут разумные люди, да?")
        givePhrases.add("%s, специально для тебя $drinkName - лучше во всех 7 замках не найти! /drink - ну ты знаешь что делать.")
        givePhrases.add("%s, сегодня нет отбоя от желающих выпить $onThrow, но для тебя у меня всегда найдется в запасе.")
        givePhrases.add("%s, ты вроде уже пил сегодня $onThrow, но раз ты настаиваешь, держи еще.")
        givePhrases.add("%s, держи $onThrow, приготовлено по рецепту моей бабушки.")
        drinkPartPhrases.add("%s выпил лишь половину $onDrink, сразу допить не смог. Слабак что ли?")
        drinkPartPhrases.add("%s отпил немного из $onDrink, и как будто немного позеленел. Возможно ему стоит заказывать не такие крепкие напитки?")
        drinkPartPhrases.add("%s отхлебнул из $onDrink, и откинулся на стуле. Эй, ты сюда пить пришел или по сторонам смотреть?")
        drinkRemainPhrases.add("%s допил оставшуюся половину $onDrink, ну наконец-то.")
        drinkRemainPhrases.add("%s зажмурился и допил $onThrow. Видимо, у человека сегодня тяжелый день.")
        drinkAllPhrases.add("%s залпом выпил $onThrow. Как насчет добавки?")
        drinkAllPhrases.add("%s сделал глубокий вдох и осушил целиком $onThrow. Вот это мастерство!")
        drinkAllPhrases.add("Едва увидев $onThrow, %s схватил свой напиток и под одобряющие возгласы посетителей осушил его.")
        drinkAllPhrases.add("%s сделал пару богатырских глотков и отставил в сторону $empty $onThrow. Да ты похоже соскучился по выпивке!")
        throwNonePhrases.add("%s швырнул $onThrow об пол! Дебошир!")
        throwNonePhrases.add("Кажется, %s хотел швырнуть $onThrow в кого-то из посетителей, но попал в стену.")
        throwNonePhrases.add("Метким броском %s отправил $onThrow в открытое окно. Спасибо что не в барную стойку!")
        throwNonePhrases.add("%s швырнул $onThrow в барную стойку, но особая барменская магия отклонила напиток в сторону. Барная стойка под надежной защитой!")
        throwTargetFullPhrases.add("%s подошел к %s и вылил свой напиток тому за шиворот. Эх, испортил такую рубаху!")
        throwTargetFullPhrases.add("Не допивая, %s кинул $onThrow в %s. Весь пол и половина посетителей теперь в бухле. Впрочем, адресату досталось больше всех!")
        throwTargetEmptyPhrases.add("%s кинул $empty $onThrow в %s. Эй, давайте только без драки!")
        throwTargetEmptyPhrases.add("Без содержимого кидать $onThrow проще! - заявил %s и метким броском подбил глаз ничего не подозревающему %s. Видимо, и правда проще.")
        selfThrowPhrases.add("%s поставил $onThrow себе на голову и полез танцевать на стуле. Уважаемый, тебе показать где выход?")
        selfThrowPhrases.add("%s начал жонглировать $whom, но что-то пошло не так и он уронил $onThrow на себя.")
        enterPhrases.add("%s добро пожаловать в нашу таверну! Держи для рывка $onThrow, можешь смело /drink! ну или /throw, но мы же тут разумные люди, да?")
    }

    companion object {

        val random: DrinkType
            get() = values()[Random().nextInt(values().size)]
    }
}

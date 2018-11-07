package ru.nia.tavern.model.types

import java.util.*

/**
 * @author IANazarov
 */
enum class Food private constructor(val command: String, val name: String, onEat: String, thisPrefix: String) {
    GUS("/gus", "подушенный и зажаренный до хрустящей корочки гусь", "зажаренного гуся", "этого "),
    CHEESE("/cheese", "тарелка сыра", "тарелку сыра", "эту "),
    FRI("/fri", "картошка фри", "картошку фри", "эту "),
    SHAURMA("/shaurma", "сочная шаурма из поросенка", "шаурму", "эту ");

    private val givePhrases: MutableList<String>
    private val eatPhrases: MutableList<String>

    val givePhrase: String
        get() = givePhrases[Random().nextInt(givePhrases.size)]

    val eatPhrase: String
        get() = eatPhrases[Random().nextInt(eatPhrases.size)]

    init {
        this.givePhrases = ArrayList()
        this.eatPhrases = ArrayList()
        givePhrases.add("%s, для тебя $name, приступай к /eat!")
        givePhrases.add("%s, было сложно, но мы достали для тебя $onEat! Советую тебе быстрее /eat, смотри как соседи жадно на тебя смотрят!")
        givePhrases.add("Эй, %s, когда ты съешь $thisPrefix$onEat, то станешь похож на толстую панду. Обожаю панд!")
        givePhrases.add("%s, у нас сегодня был завоз, так что ты не найдешь $onEat свежее чем у нас!")
        eatPhrases.add("%s съел $onEat. Рррр, вот это аппетит!")
        eatPhrases.add("Хм, %s, пока я отвернулась ты съел целиком $onEat. Бедолага, совсем тебя дома не кормят.")
        eatPhrases.add("%s уже не раз демонстрировал свою любовь к нашим блюдам. Вот и сейчас, он мгновенно съел $onEat.")
    }

    companion object {

        val random: Food
            get() = values()[Random().nextInt(values().size)]
    }
}

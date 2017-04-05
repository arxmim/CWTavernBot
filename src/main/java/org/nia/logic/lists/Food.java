package org.nia.logic.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public enum Food {
    GUS("/gus", "подушенный и зажаренный до хрустящей корочки гусь", "зажаренного гуся", "этого "),
    CHEESE("/cheese", "тарелка сыра", "тарелку сыра", "эту "),
    FRI("/fri", "картошка фри", "картошку фри", "эту "),
    SHAURMA("/shaurma", "сочная шаурма из поросенка", "шаурму", "эту ");
    private final String command;
    private final String name;
    private final List<String> givePhrases;
    private final List<String> eatPhrases;

    Food(String command, String onGive, String onEat, String thisPrefix) {
        this.command = command;
        this.name = onGive;
        this.givePhrases = new ArrayList<>();
        this.eatPhrases = new ArrayList<>();
        givePhrases.add("%s, для тебя " + onGive + ", приступай к /eat!");
        givePhrases.add("%s, было сложно, но мы достали для тебя  " + onEat + "! Советую тебе быстрее /eat, смотри как соседи жадно на тебя смотрят!");
        givePhrases.add("Эй, %s, когда ты съешь " + thisPrefix + onEat + ", то станешь похож на толстую панду. Обожаю панд!");
        givePhrases.add("%s, у нас сегодня был завоз, так что ты не найдешь " + onEat + " свежее чем у нас!");
        eatPhrases.add("%s съел " + onEat + ". Рррр, вот это аппетит!");
        eatPhrases.add("Хм, %s, пока я отвернулась ты съел целиком " + onEat + ". Бедолага, совсем тебя дома не кормят.");
        eatPhrases.add("%s уже не раз демонстрировал свою любовь к нашим блюдам. Вот и сейчас, он мгновенно съел " + onEat + ".");
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

    public String getEatPhrase() {
        return eatPhrases.get(new Random().nextInt(eatPhrases.size()));
    }

    public static Food getRandom() {
        return values()[new Random().nextInt(values().length)];
    }
}

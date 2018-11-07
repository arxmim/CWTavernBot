package ru.nia.tavern.quests.buyfish

import ru.nia.tavern.model.Quest
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*

/**
 * @author IANazarov
 */
enum class MagicFish : IQuestStep {

    THREAT("Спросить о награде", "Хитрая рыбина захотела уморить тебя своими ужасными анекдотами, но ты вовремя " +
            "догадался оглушить её ударом об колено. Тебе пришлось повторить \"воспитание\" несколько раз, пока наконец " +
            "щука не поняла, что с тобой опасно иметь дело. Ты заставил щуку выплатить награду вперед - в пруду внезапно " +
            "поднялась волна и принесла к берегу небольшой сундучок с золотом - после чего отпустил говорящую рыбу.", "Хитрая рыбина продолжила рассказывать тебе ужасные анекдоты, и ради спасения " + "своих ушей ты был вынужден бросить её в пруд. Никакой награды ты не дождался, только время потерял."),
    THROW("Бросить щуку в пруд", "Ты бросил щуку в пруд, и тут же в пруду поднялась волна и принесла к берегу небольшой " + "сундучок с золотом.", "Ты бросил щуку в пруд, и больше не видел ни рыбу, ни награду. Только время потерял."),
    SELL("Продать говорящую щуку", "Ты продал щуку за приличную сумму какому-то идиоту.", "Ты вырубил рыбину и продал её какому-то худому старику с сундуком в руках. Сразу после этого тебя " +
            "обворовали, часть товара внезапно протухла, а еще шпана закидала тебя камнями. Странно, наверное просто " +
            "совпадение. Сегодня торговля явно не задалась."),
    GO("Пойти к пруду", "Ты оставил свой прилавок и пошел вместе с щукой к пруду. По пути она травила бородатые анекдоты и шутки " +
            "из кривого зеркала, которые были не смешными еще когда твой дед воевал за Красный Замок. Почуяв поблизости " +
            "воду, щука разволновалась и стала еще более несносной.", Arrays.asList<IQuestStep>(THROW, THREAT)),
    INIT("", "Одна из щук, которые ты продавал, внезапно заговорила с тобой:\n- Не продавай меня, добрый молодец! " +
            "Выпусти меня на волю, я тебя отблагодарю! Сходи до ближайшего пруда, и выпусти меня в воду, а я тебе дам " +
            "мешок с драгоценными камнями!", Arrays.asList<IQuestStep>(GO, SELL));

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = SellFishQuest.SellFishEvent.SELL_FISH_MAGIC_FISH

    private constructor(command: String, goodText: String, badText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
    }

    private constructor(command: String, text: String, next: List<IQuestStep>) {
        this.command = command
        this.text = text
        this.next.addAll(next)
    }

    override fun getText(quest: Quest): String {
        return text
    }

    override fun getNext(quest: Quest): List<IQuestStep> {
        return next
    }

    override fun getCommand(formatParam: String): String {
        return command
    }

    override fun getGoodText(quest: Quest): String {
        return goodText
    }

    override fun getBadText(quest: Quest): String {
        return badText
    }

}

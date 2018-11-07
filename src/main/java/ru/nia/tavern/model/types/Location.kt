package ru.nia.tavern.model.types

import ru.nia.tavern.quests.QuestsEnum
import java.util.*
import java.util.stream.Collectors

/**
 * @author IANazarov
 */
enum class Location {
    TAVERN,
    QUEST;


    companion object {


        val randomQuest: QuestsEnum
            get() {
                val list = Arrays.stream(QuestsEnum.values()).filter(Predicate<QuestsEnum> { it.isRunnable() }).collect<List<QuestsEnum>, Any>(Collectors.toList())
                return list[Random().nextInt(list.size)]
            }
    }
}

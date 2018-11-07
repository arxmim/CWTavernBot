package ru.nia.tavern.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Repository
interface QuestEventRepository : JpaRepository<QuestEvent, Int> {
    // ROM QuestEvent WHERE win is null and quest.publicID
    fun findTop1ByQuestAndWinIsNull(quest: Quest): QuestEvent?

    fun findTop1ByQuest(quest: Quest): List<QuestEvent>

}
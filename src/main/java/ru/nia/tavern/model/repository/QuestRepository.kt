package ru.nia.tavern.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.User
import ru.nia.tavern.quests.QuestsEnum
import java.util.*

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Repository
interface QuestRepository : JpaRepository<Quest, Int> {
    fun findTop1ByUserAndReturnTimeIsNull(user: User): Quest?

    //FROM Quest WHERE returnTime is NULL and eventTime > current_date and questName = :questName
    fun findActiveRandomTask(questsEnum: QuestsEnum): Quest? {
        val list = findByEventTimeAfterAndQuestEnumAndReturnTimeIsNull(Date(), questsEnum)
        if (list.isEmpty()) {
            return null
        }
        return list.random()
    }

    fun findByEventTimeAfterAndQuestEnumAndReturnTimeIsNull(eventTime: Date, questsEnum: QuestsEnum): List<Quest>

}
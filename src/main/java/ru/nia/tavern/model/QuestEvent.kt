package ru.nia.tavern.model

import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author IANazarov
 */
@Entity
@Table(name = "cwt_QuestEvent")
class QuestEvent {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "questID", nullable = false)
    lateinit var quest: Quest
    @Column
    var eventTime: Date? = null
    @Column(columnDefinition = "INT DEFAULT 80")
    var winChance = 80
    @Column
    var win: Boolean? = null
    @Column(name = "linkedQuestEventID")
    @ManyToOne
    var linkedQuestEvent: QuestEvent? = null
    @Column
    var eventName: String? = null
    @Column
    var eventStep: String? = null

    var iQuestEvent: IQuestEvent
        get() = quest.questEnum.iQuest.getEvent(eventName)
        set(event) {
            this.eventName = event.name
        }

    var step: IQuestStep
        get() = iQuestEvent.getQuestStep(eventStep)
        set(step) {
            this.eventStep = step.name
        }

    fun incWinChance(delta: Int) {
        this.winChance += delta
    }

//    companion object {
//
//        fun getCurrent(quest: Quest): QuestEvent? {
//            var res: QuestEvent? = null
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("FROM QuestEvent WHERE win is null and quest.publicID = " + quest.publicID!!, QuestEvent::class.java)
//                    val list = query.list()
//                    if (!list.isEmpty()) {
//                        res = list[0]
//                    }
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return res
//        }
//
//        fun getAll(quest: Quest): List<QuestEvent> {
//            var res: List<QuestEvent> = ArrayList()
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("FROM QuestEvent WHERE quest.publicID = " + quest.publicID!!, QuestEvent::class.java)
//                    res = query.list()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return res
//        }
//    }
}

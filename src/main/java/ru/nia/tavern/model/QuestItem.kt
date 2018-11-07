package ru.nia.tavern.model

import ru.nia.tavern.model.types.items.EQuestItem
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author Иван, 06.04.2017.
 */
@Entity
@Table(name = "cwt_QuestItem")
class QuestItem {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "questID", nullable = false)
    lateinit var quest: Quest
    @Enumerated(EnumType.STRING)
    var questItem: EQuestItem? = null
    @Column
    var itemCount: Int = 0

//    fun delete(): Boolean {
//        var res = false
//        val factory = HibernateConfig.getSessionFactory()
//        try {
//            factory.openSession().use { session ->
//                val tx = session.beginTransaction()
//                session.delete(this)
//                tx.commit()
//                res = true
//            }
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//
//        return res
//    }
//
//    companion object {
//
//        fun getAll(quest: Quest): List<QuestItem> {
//            var res: List<QuestItem> = ArrayList()
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("FROM QuestItem WHERE quest.publicID = " + quest.publicID!!, QuestItem::class.java)
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

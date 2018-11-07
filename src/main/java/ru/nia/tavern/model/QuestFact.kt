package ru.nia.tavern.model

import ru.nia.tavern.model.types.facts.EQuestFact
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
@Table(name = "cwt_QuestFact")
class QuestFact {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "questID", nullable = false)
    lateinit var quest: Quest
    @Enumerated(EnumType.STRING)
    var questFact: EQuestFact? = null

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
//        fun getAll(quest: Quest): List<QuestFact> {
//            var res: List<QuestFact> = ArrayList()
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("FROM QuestFact WHERE quest.publicID = " + quest.publicID!!, QuestFact::class.java)
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

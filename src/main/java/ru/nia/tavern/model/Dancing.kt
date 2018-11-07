package ru.nia.tavern.model

import ru.nia.tavern.model.types.DanceActionList
import ru.nia.tavern.model.types.DanceStep
import java.util.*
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
 * @author IANazarov
 */
@Entity
@Table(name = "cwt_dancing")
class Dancing {
    @Column
    @Id
    @GeneratedValue
    var publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "firstDancerID", nullable = false)
    lateinit var firstDancer: User
    @ManyToOne
    @JoinColumn(name = "secondDancerID", nullable = false)
    lateinit var secondDancer: User
    @Enumerated(EnumType.STRING)
    lateinit var currentStep: DanceStep
    @Enumerated(EnumType.STRING)
    var lastDanceAction: DanceActionList? = null
    @Column
    var lastActionFromFirst: Boolean? = null
    @Column
    var nextStepTime: Date? = null
    @Column
    var completed: Boolean? = null

    val nextAction: DanceStep.DanceAction?
        get() {
            val danceAction: DanceStep.DanceAction?
            if (lastDanceAction == null || lastActionFromFirst == null) {
                danceAction = null
            } else {
                danceAction = DanceStep.DanceAction(lastDanceAction, lastActionFromFirst!!)
            }
            return currentStep.getNextAfter(danceAction)
        }

    fun process() {
        // DancingService.doDance
    }

    companion object {

//        fun getCurrent(user: User): Dancing? {
//            val sessionFactory = HibernateConfig.getSessionFactory()
//            try {
//                sessionFactory.openSession().use { session ->
//                    val query = session.createQuery("FROM Dancing where (firstDancer.userID = :uID " + "OR secondDancer.userID = :uID) and completed is NULL", Dancing::class.java)
//                    query.setParameter("uID", user.userID)
//                    return query.uniqueResult()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return null
//        }
//
//        val allCurrent: List<Dancing>
//            get() {
//                var res = emptyList<Dancing>()
//                val sessionFactory = HibernateConfig.getSessionFactory()
//                try {
//                    sessionFactory.openSession().use { session ->
//                        val query = session.createQuery("FROM Dancing where completed is NULL", Dancing::class.java)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
    }


}

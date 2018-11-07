package ru.nia.tavern.model

import ru.nia.tavern.model.types.DrinkType
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
 * @author Иван, 11.03.2017.
 */
@Entity
@Table(name = "cwt_DrinkPrefs")
class DrinkPref {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int = 0
    @ManyToOne
    @JoinColumn(name = "userID")
    val user: User
    @Enumerated(EnumType.STRING)
    val drinkType: DrinkType
    @Column
    var toDrink: Int = 0
    @Column
    var toThrow: Int = 0
    @Column
    var toBeThrown: Int = 0

    val toDrinkNormalized: Int
        get() = toDrink / 2

    constructor(user: User, drinkType: DrinkType) {
        this.user = user
        this.drinkType = drinkType
    }

//    fun save(): Boolean {
//        var res = false
//        val factory = HibernateConfig.getSessionFactory()
//        try {
//            factory.openSession().use { session ->
//                val tx = session.beginTransaction()
//                session.saveOrUpdate(this)
//                tx.commit()
//                session.refresh(this)
//                res = true
//            }
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//
//        return res
//    }

    internal fun incToDrink(plus: Int) {
        this.toDrink += plus
    }

    internal fun incToThrow() {
        this.toThrow++
    }

    internal fun incToBeThrown() {
        this.toBeThrown++
    }

    companion object {

//        fun getByUser(usr: User): List<DrinkPref> {
//            var res = emptyList<DrinkPref>()
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//
//                val openSession = factory.openSession()
//                openSession.use { session ->
//                    val query = session.createQuery("FROM DrinkPref WHERE user.userID = " + usr.userID, DrinkPref::class.java)
//                    res = query.list()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return res
//        }
//
//        internal fun getByUserAndDrinkType(usr: User, drinkType: DrinkType): DrinkPref? {
//            var res: DrinkPref? = null
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("FROM DrinkPref WHERE user.userID = " + usr.userID + " and drinkType=:drinkType", DrinkPref::class.java)
//                    query.setParameter("drinkType", drinkType)
//                    val list = query.list()
//                    if (!list.isEmpty()) {
//                        res = list.get(0)
//                    } else {
//                        res = DrinkPref(usr, drinkType, 0, 0, 0)
//                    }
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return res
//        }
    }
}

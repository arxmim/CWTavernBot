package ru.nia.tavern.model

import org.apache.commons.lang3.StringUtils
import ru.nia.tavern.model.types.DrinkType
import ru.nia.tavern.model.types.Food
import ru.nia.tavern.model.types.Location
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author IANazarov
 */
@Entity
@Table(name = "cwt_User")
class User {
    @Id
    @Column
    var userID: Int = 0
    @Column
    var nick: String? = null
    @Column(nullable = false)
    var name: String? = null
    @Column
    val lastDrinkTime: Date? = null
    @Column
    val lastEatTime: Date? = null
    @Enumerated(EnumType.STRING)
    val drinkType: DrinkType? = null
    @Enumerated(EnumType.STRING)
    val wanted: DrinkType? = null
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isBarmen: Boolean = false
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isAdmin: Boolean = false
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var gold: Int = 0
    @Enumerated(EnumType.STRING)
    val food: Food? = null
    @Enumerated(EnumType.STRING)
    val wantedFood: Food? = null
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var foodCount: Int = 0
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var eatTotal: Int = 0
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var alkoCount: Int = 0
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var drinkedTotal: Int = 0
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var drinkedWeek: Int = 0
    @Column
    var fightClubWins: Int = 0
    @Column
    var brewCount: Int = 0
    @Column
    var fightTime: Date? = null
    @Enumerated(EnumType.STRING)
    var location: Location? = null
    @Column(name = "fightWithUser")
    @ManyToOne
    var fightWithUser: User? = null
    @Column
    var curseTime: Date? = null
    @Column
    var voteFor: String? = null
    @Column(name = "danceWithUserID")
    @ManyToOne
    var danceWithUser: Int? = null
    @Column
    val danceTime: Date? = null

    val drinkedTotalNormalized: Int
        get() = drinkedTotal / 2
    val drinkedWeekNormalized: Int
        get() = drinkedWeek / 2

    val isBarmenOrAdmin: Boolean
        get() = isBarmen || isAdmin

    override fun toString(): String {
        return if (!StringUtils.isEmpty(nick)) {
            "@" + nick!!
        } else {
            name ?: ""
        }
    }

    internal fun incFightClubWins() {
        fightClubWins++
    }

    fun incBrewCount() {
        brewCount++
    }

    fun incGold() {
        gold++
    }

    fun inTavern(): Boolean {
        return location == Location.TAVERN
    }

    fun onQuest(): Boolean {
        return location == Location.QUEST
    }

    companion object {


//        val all: List<User>
//            get() {
//                var res: List<User> = ArrayList()
//                val factory = HibernateConfig.getSessionFactory()
//                try {
//                    factory.openSession().use { session ->
//                        val query = session.createQuery("FROM User", User::class.java)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
//
//        val top: List<User>
//            get() {
//                var res: List<User> = ArrayList()
//                val factory = HibernateConfig.getSessionFactory()
//                try {
//                    factory.openSession().use { session ->
//                        val query = session.createQuery("FROM User order by drinkedTotal desc", User::class.java)
//                        query.setMaxResults(12)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
//
//        val weekTop: List<User>
//            get() {
//                var res: List<User> = ArrayList()
//                val factory = HibernateConfig.getSessionFactory()
//                try {
//                    factory.openSession().use { session ->
//                        val query = session.createQuery("FROM User order by drinkedWeek desc", User::class.java)
//                        query.setMaxResults(12)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
//
//        val barmenTop: List<User>
//            get() {
//                var res: List<User> = ArrayList()
//                val factory = HibernateConfig.getSessionFactory()
//                try {
//                    factory.openSession().use { session ->
//                        val query = session.createQuery("FROM User order by brewCount desc", User::class.java)
//                        query.setMaxResults(12)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
//
//        val bkTop: List<User>
//            get() {
//                var res: List<User> = ArrayList()
//                val factory = HibernateConfig.getSessionFactory()
//                try {
//                    factory.openSession().use { session ->
//                        val query = session.createQuery("FROM User order by fightClubWins desc", User::class.java)
//                        query.setMaxResults(12)
//                        res = query.list()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//
//                return res
//            }
//
//        fun getVotersForCount(vote: String): Int {
//            var res = 0
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val query = session.createQuery("select count(1) FROM User usr where usr.voteFor = :voteFor", Long::class.java)
//                    query.setParameter("voteFor", vote)
//                    query.setMaxResults(12)
//                    res = query.uniqueResult().toInt()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//            return res
//        }
//
//        internal fun flushVotes() {
//            val factory = HibernateConfig.getSessionFactory()
//            try {
//                factory.openSession().use { session ->
//                    val transaction = session.beginTransaction()
//                    val query = session.createQuery("UPDATE User usr SET usr.voteFor = :voteFor")
//                    query.setParameter("voteFor", null)
//                    query.executeUpdate()
//                    transaction.commit()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//
//        }
    }
}

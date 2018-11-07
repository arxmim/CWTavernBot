package ru.nia.tavern.model

import org.apache.commons.lang3.tuple.MutablePair
import org.apache.commons.lang3.tuple.Pair
import org.nia.db.HibernateConfig
import sun.awt.windows.ThemeReader.getPosition
import java.util.*
import java.util.stream.Collectors
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author Иван, 11.03.2017.
 */
@Entity
@Table(name = "cwt_TournamentUsers")
class TournamentUsers {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int = 0
    @ManyToOne
    @JoinColumn(name = "TournamentID", nullable = false)
    lateinit var tournament: Tournament
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    lateinit var user: User
    @Column(nullable = false)
    var position: Int = 0
    @Column(columnDefinition = "INT DEFAULT 1")
    var round = 1
    @Column(columnDefinition = "INT DEFAULT 0")
    var score = 0
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    var inFight = false
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    var lose = false

    internal fun incRound() {
        round++
    }

    internal fun getFinalResult(another: TournamentUsers): Int {
        return tournament!!.getType().evalFinalResult(this, another)
    }

    companion object {

        fun register(tournament: Tournament, user: User): String {
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val query = session.createQuery("FROM TournamentUsers WHERE tournament.publicID = " + tournament.getPublicID(), TournamentUsers::class.java)
                    val list = query.list()
                    if (list.stream().filter { tu -> tu.getUser().getUserID() === user.userID }.findFirst().isPresent) {
                        return user.toString() + ", ты уже зарегистрировался на турнир!"
                    } else {
                        val has = list.stream().map(Function<TournamentUsers, Any> { getPosition() }).collect(Collectors.toSet<Any>())
                        val newCount = has.size + 1
                        if (newCount > tournament.getMaxUsers()) {
                            return "Извини, $user, но места для участников уже все заняты. В следующий раз соображай быстрее!"
                        }
                        val hasNot = ArrayList<Int>()
                        for (i in 1..tournament.getMaxUsers()) {
                            if (!has.contains(i)) {
                                hasNot.add(i)
                            }
                        }
                        val position = hasNot[Random().nextInt(hasNot.size)]
                        val tu = TournamentUsers()
                        tu.setTournament(tournament)
                        tu.setPosition(position)
                        tu.setUser(user)
                        tu.save()
                        return user.toString() + ", ты успешно зарегистрирован на турнир, твой номер - " + position + ", уже зарегистрировано - " + newCount
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "Что-то пошло не так. Регистрация не удалась"
            }

        }

        internal fun getAllString(tournament: Tournament, round: Int): String {
            val sb = StringBuilder()
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val query = session.createQuery("FROM TournamentUsers " +
                            "WHERE tournament.publicID = " + tournament.getPublicID() + " " +
                            "and round = " + round + " order by position", TournamentUsers::class.java)
                    var i = 0
                    for (tournamentUsers in query.list()) {
                        i++
                        sb.append(tournamentUsers.getPosition()).append(" - ").append(tournamentUsers.getUser()).append("\n")
                        if (i % 2 == 0) {
                            sb.append("\n")
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return sb.toString()
        }

        internal fun getTwoUsers(tournament: Tournament): Pair<TournamentUsers, TournamentUsers>? {
            var res: Pair<TournamentUsers, TournamentUsers>? = null
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val query = session.createQuery("FROM TournamentUsers " +
                            "WHERE lose = false and tournament.publicID = " + tournament.getPublicID() +
                            "  order by inFight desc, round, position", TournamentUsers::class.java)
                    query.maxResults = 2
                    val usersList = query.list()
                    if (usersList.size == 2) {
                        res = MutablePair(usersList[0], usersList[1])
                    } else if (usersList.size == 1) {
                        res = MutablePair(usersList[0], null)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return res
        }

        fun getCurrentByUserID(userID: Int): TournamentUsers? {
            var res: TournamentUsers? = null
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val current = Tournament.current
                    if (current != null) {
                        val query = session.createQuery("FROM TournamentUsers " +
                                "WHERE tournament.publicID = " + current!!.getPublicID() +
                                " and user.userID = " + userID + " order by inFight desc", TournamentUsers::class.java)
                        query.maxResults = 1
                        val list = query.list()
                        if (!list.isEmpty()) {
                            res = list[0]
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return res
        }
    }
}

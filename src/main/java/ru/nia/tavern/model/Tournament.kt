package ru.nia.tavern.model

import org.nia.db.HibernateConfig
import ru.nia.tavern.model.types.TournamentState
import ru.nia.tavern.model.types.TournamentType
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
 * @author Иван, 11.03.2017.
 */
@Entity
@Table(name = "cwt_Tournament")
class Tournament {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int? = null
    @Column(nullable = false)
    lateinit var registrationDateTime: Date
    @Enumerated(EnumType.STRING)
    lateinit var type: TournamentType
    @Enumerated(EnumType.STRING)
    lateinit var state: TournamentState
    @Column(nullable = false)
    var maxUsers: Int = 0
    @ManyToOne
    @JoinColumn(name = "winner")
    var winner: User? = null
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var round = 0

    val isAnnounced: Boolean
        get() = state == TournamentState.ANOUNCE

    val isRegistration: Boolean
        get() = state == TournamentState.REGISTRATION

    val isInProgress: Boolean
        get() = state == TournamentState.PROGRESS

    override fun toString(): String {
        return "Tournament{" +
                "publicID=" + publicID +
                ", registrationDateTime=" + registrationDateTime +
                ", type=" + type +
                ", state=" + state +
                ", maxUsers=" + maxUsers +
                ", round=" + round +
                '}'.toString()
    }


    companion object {

        val current: Tournament?
            get() {
                var res: Tournament? = null
                val factory = HibernateConfig.getSessionFactory()
                try {
                    factory.openSession().use { session ->
                        var query = session.createQuery("FROM Tournament WHERE state in (:states)", Tournament::class.java)
                        query.setParameterList("states", Arrays.asList(TournamentState.REGISTRATION, TournamentState.PROGRESS))
                        query.maxResults = 1
                        var list = query.list()
                        if (!list.isEmpty()) {
                            res = list[0]
                        } else {
                            query = session.createQuery("FROM Tournament WHERE state =:state order by registrationDateTime", Tournament::class.java)
                            query.setParameter("state", TournamentState.ANOUNCE)
                            query.maxResults = 1
                            list = query.list()
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

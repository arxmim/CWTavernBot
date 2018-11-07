package ru.nia.tavern.model

import org.nia.db.HibernateConfig
import org.nia.strings.Emoji
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author Иван, 18.03.2017.
 */
@Entity
@Table(name = "cwt_TournamentBet")
class TournamentBet {
    @Id
    @Column
    @GeneratedValue
    private val publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "TournamentID", nullable = false)
    private val tournament: Tournament? = null
    @ManyToOne
    @JoinColumn(name = "fromID", nullable = false)
    private val from: User? = null
    @ManyToOne
    @JoinColumn(name = "toID", nullable = false)
    private val to: TournamentUsers? = null
    @Column
    private val sum: Int = 0

    companion object {

        fun getCurrentBetsByUserID(current: Int?, user: User): List<TournamentBet> {
            var res: List<TournamentBet> = ArrayList()
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val query = session.createQuery("FROM TournamentBet tb" + " WHERE tb.from.userID = :uID AND tb.tournament.publicID = :tID", TournamentBet::class.java)
                    query.setParameter("tID", current)
                    query.setParameter("uID", user.userID)
                    res = query.list()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return res
        }

        fun evalTournamentResults(winner: TournamentUsers): String {
            val tbList = TournamentBet.getAllByTournament(winner.getTournament())
            //        int pool =  tbList.stream().mapToInt(tb -> tb.sum).sum();
            val pool = BigDecimal.valueOf(tbList.stream().mapToInt { tb -> tb.sum }.sum().toLong())
            if (pool == BigDecimal.ZERO) {
                return ""
            }
            var tournamentBetStream = tbList.stream().filter { tb -> tb.to!!.getPublicID() === winner.getPublicID() }
            val sb = StringBuilder()
            //        int poolOnWinner = tournamentBetStream.mapToInt(tb -> tb.sum).sum();
            val poolOnWinner = BigDecimal.valueOf(tournamentBetStream.mapToInt { tb -> tb.sum }.sum().toLong())
            tournamentBetStream = tbList.stream().filter { tb -> tb.to!!.getPublicID() === winner.getPublicID() }
            if (poolOnWinner == BigDecimal.ZERO) {
                //        if (poolOnWinner == 0) {
                sb.append("Никто не поставил на ").append(winner.getUser()).append(", очень зря. Все ставки .(").append(pool).append(Emoji.GOLD).append(") сгорели!")
            } else {
                sb.append("Нашлись разумные люди, которые поставили на ").append(winner.getUser()).append("! Все они получат часть призовых (").append(pool).append(Emoji.GOLD).append(") соответственно своему вкладу!")
                tournamentBetStream.forEach { tb ->
                    //                int winned = BigDecimal.valueOf(pool * tb.sum / poolOnWinner).intValue();
                    val winned = pool.multiply(BigDecimal.valueOf(tb.sum.toLong())).divide(poolOnWinner, RoundingMode.HALF_UP).toInt()
                    tb.from!!.gold = tb.from.gold + winned
                    tb.from.save()
                    sb.append("\n").append(tb.from).append(" - ").append(winned).append(Emoji.GOLD)
                }
            }
            return sb.toString()
        }

        private fun getAllByTournament(tournament: Tournament): List<TournamentBet> {
            var res: List<TournamentBet> = ArrayList()
            val factory = HibernateConfig.getSessionFactory()
            try {
                factory.openSession().use { session ->
                    val query = session.createQuery("FROM TournamentBet WHERE tournament.publicID = " + tournament.getPublicID(), TournamentBet::class.java)
                    res = query.list()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return res
        }
    }
}

package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.strings.Emoji;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Иван, 18.03.2017.
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_TournamentBet")
public class TournamentBet extends AbstractEntity {
    @Id
    @Column()
    @GeneratedValue
    private Integer publicID;
    @ManyToOne
    @JoinColumn(name = "TournamentID", nullable = false)
    private Tournament tournament;
    @ManyToOne
    @JoinColumn(name = "fromID", nullable = false)
    private User from;
    @ManyToOne
    @JoinColumn(name = "toID", nullable = false)
    private TournamentUsers to;
    @Column()
    private int sum;

    public static List<TournamentBet> getCurrentBetsByUserID(Integer current, User user) {
        List<TournamentBet> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<TournamentBet> query = session.createQuery("FROM TournamentBet tb" +
                    " WHERE tb.from.userID = :uID AND tb.tournament.publicID = :tID", TournamentBet.class);
            query.setParameter("tID", current);
            query.setParameter("uID", user.getUserID());
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static String evalTournamentResults(TournamentUsers winner) {
        List<TournamentBet> tbList = TournamentBet.getAllByTournament(winner.getTournament());
//        int pool =  tbList.stream().mapToInt(tb -> tb.sum).sum();
        BigDecimal pool = BigDecimal.valueOf(tbList.stream().mapToInt(tb -> tb.sum).sum());
        if (pool.equals(BigDecimal.ZERO)) {
            return "";
        }
        Stream<TournamentBet> tournamentBetStream = tbList.stream().filter(tb -> tb.to.getPublicID() == winner.getPublicID());
        StringBuilder sb = new StringBuilder();
//        int poolOnWinner = tournamentBetStream.mapToInt(tb -> tb.sum).sum();
        BigDecimal poolOnWinner = BigDecimal.valueOf(tournamentBetStream.mapToInt(tb -> tb.sum).sum());
        tournamentBetStream = tbList.stream().filter(tb -> tb.to.getPublicID() == winner.getPublicID());
        if (poolOnWinner.equals(BigDecimal.ZERO)) {
//        if (poolOnWinner == 0) {
            sb.append("Никто не поставил на ").append(winner.getUser()).append(", очень зря. Все ставки .(").append(pool).append(Emoji.GOLD).append(") сгорели!");
        } else {
            sb.append("Нашлись разумные люди, которые поставили на ").append(winner.getUser()).append("! Все они получат часть призовых (").append(pool).append(Emoji.GOLD).append(") соответственно своему вкладу!");
            tournamentBetStream.forEach(tb -> {
//                int winned = BigDecimal.valueOf(pool * tb.sum / poolOnWinner).intValue();
                int winned = pool.multiply(BigDecimal.valueOf(tb.sum)).divide(poolOnWinner, RoundingMode.HALF_UP).intValue();
                tb.from.setGold(tb.from.getGold() + winned);
                tb.from.save();
                sb.append("\n").append(tb.from).append(" - ").append(winned).append(Emoji.GOLD);
            });
        }
        return sb.toString();
    }

    private static List<TournamentBet> getAllByTournament(Tournament tournament) {
        List<TournamentBet> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<TournamentBet> query = session.createQuery("FROM TournamentBet WHERE tournament.publicID = " + tournament.getPublicID(), TournamentBet.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}

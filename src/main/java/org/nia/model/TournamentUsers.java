package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Иван, 11.03.2017.
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_TournamentUsers")
public class TournamentUsers extends AbstractEntity {
    @Id
    @Column()
    @GeneratedValue
    private int publicID;
    @ManyToOne
    @JoinColumn(name = "TournamentID", nullable = false)
    private Tournament tournament;
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
    @Column(nullable = false)
    private int position;
    @Column(columnDefinition = "INT DEFAULT 1")
    private int round = 1;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int score = 0;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean inFight = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean lose = false;

    public static String register(Tournament tournament, User user) {
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<TournamentUsers> query = session.createQuery("FROM TournamentUsers WHERE tournament.publicID = " + tournament.getPublicID(), TournamentUsers.class);
            List<TournamentUsers> list = query.list();
            if (list.stream().filter(tu -> tu.getUser().getUserID() == user.getUserID()).findFirst().isPresent()) {
                return user + ", ты уже зарегистрировался на турнир!";
            } else {
                Set<Integer> has = list.stream().map(TournamentUsers::getPosition).collect(Collectors.toSet());
                int newCount = has.size() + 1;
                if (newCount > tournament.getMaxUsers()) {
                    return "Извини, " + user + ", но места для участников уже все заняты. В следующий раз соображай быстрее!";
                }
                ArrayList<Integer> hasNot = new ArrayList<>();
                for (int i = 1; i <= tournament.getMaxUsers(); i++) {
                    if (!has.contains(i)) {
                        hasNot.add(i);
                    }
                }
                int position = hasNot.get(new Random().nextInt(hasNot.size()));
                TournamentUsers tu = new TournamentUsers();
                tu.setTournament(tournament);
                tu.setPosition(position);
                tu.setUser(user);
                tu.save();
                return user + ", ты успешно зарегистрирован на турнир, твой номер - " + position + ", уже зарегистрировано - " + newCount;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Что-то пошло не так. Регистрация не удалась";
        }
    }

    void incRound() {
        round++;
    }

    int getFinalResult(TournamentUsers another) {
        return tournament.getType().evalFinalResult(this, another);
    }

    static String getAllString(Tournament tournament, int round) {
        StringBuilder sb = new StringBuilder();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<TournamentUsers> query = session.createQuery("FROM TournamentUsers " +
                    "WHERE tournament.publicID = " + tournament.getPublicID() + " " +
                    "and round = " + round + " order by position", TournamentUsers.class);
            int i = 0;
            for (TournamentUsers tournamentUsers : query.list()) {
                i++;
                sb.append(tournamentUsers.getPosition()).append(" - ").append(tournamentUsers.getUser()).append("\n");
                if (i % 2 == 0) {
                    sb.append("\n");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    static Pair<TournamentUsers, TournamentUsers> getTwoUsers(Tournament tournament) {
        Pair<TournamentUsers, TournamentUsers> res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<TournamentUsers> query = session.createQuery("FROM TournamentUsers " +
                    "WHERE lose = false and tournament.publicID = " + tournament.getPublicID() +
                    "  order by inFight desc, round, position", TournamentUsers.class);
            query.setMaxResults(2);
            List<TournamentUsers> usersList = query.list();
            if (usersList.size() == 2) {
                res = new MutablePair<>(usersList.get(0), usersList.get(1));
            } else if (usersList.size() == 1) {
                res = new MutablePair<>(usersList.get(0), null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static TournamentUsers getCurrentByUserID(long userID) {
        TournamentUsers res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Tournament current = Tournament.getCurrent();
            if (current != null) {
                Query<TournamentUsers> query = session.createQuery("FROM TournamentUsers " +
                        "WHERE tournament.publicID = " + current.getPublicID() +
                        " and user.userID = " + userID + " order by inFight desc", TournamentUsers.class);
                query.setMaxResults(1);
                List<TournamentUsers> list = query.list();
                if (!list.isEmpty()) {
                    res = list.get(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}

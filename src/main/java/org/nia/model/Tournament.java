package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.bots.CWTavernBot;
import org.nia.bots.OficiantThread;
import org.nia.db.HibernateConfig;
import org.nia.logic.ServingMessage;
import org.nia.logic.lists.TournamentState;
import org.nia.logic.lists.TournamentType;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Иван, 11.03.2017.
 */
@Entity(name = "cwt_Tournament")
@Getter
@Setter
public class Tournament extends AbstractEntity {
    @Id
    @Column()
    @GeneratedValue
    private Integer publicID;
    @Column()
    private Date registrationDateTime;
    @Enumerated(EnumType.STRING)
    private TournamentType type;
    @Enumerated(EnumType.STRING)
    private TournamentState state;
    @Column()
    private int maxUsers;
    @ManyToOne
    @JoinColumn(name = "winner")
    private User winner;
    @Column()
    private int round = 0;

    @Transient
    public static int REGISTRATION_TIME = 15;

    public static Tournament getCurrent() {
        Tournament res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM cwt_Tournament WHERE state in (:states)");
            query.setParameterList("states", Arrays.asList(TournamentState.REGISTRATION.name(), TournamentState.PROGRESS.name()));
            query.setMaxResults(1);
            List list = query.list();
            if (!list.isEmpty()) {
                res = (Tournament) list.get(0);
            } else {
                query = session.createQuery("FROM cwt_Tournament WHERE state =" + TournamentState.ANOUNCE.name() + " order by registrationDateTime");
                query.setMaxResults(1);
                list = query.list();
                if (!list.isEmpty()) {
                    res = (Tournament) list.get(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "publicID=" + publicID +
                ", registrationDateTime=" + registrationDateTime +
                ", type=" + type +
                ", state=" + state +
                ", maxUsers=" + maxUsers +
                ", round=" + round +
                '}';
    }

    public boolean isAnnounced() {
        return state == TournamentState.ANOUNCE;
    }

    public boolean isRegistration() {
        return state == TournamentState.REGISTRATION;
    }

    public boolean isInProgress() {
        return state == TournamentState.PROGRESS;
    }

    public List<String> work() {
        ArrayList<String> res = new ArrayList<>();
        if (isAnnounced()) {
            state = TournamentState.REGISTRATION;
            save();
            res.add("Регистрация на турнир " + type + " открыта на "+REGISTRATION_TIME+" минут! Приходи в @drinkstardust и жми /register срочно!\nМаксимальное число участников - " + maxUsers + ". Торопитесь принять участие!" +
                    "\n\nКроме того, пока идет регистрация вы можете поставить ставку на зарегистрировавшихся участников командой /bet");
        } else if (isRegistration()) {
            Pair<TournamentUsers, TournamentUsers> pair = TournamentUsers.getTwoUsers(this);
            if (pair == null || pair.getRight() == null) {
                state = TournamentState.FINISHED;
                save();
                res.add("Турнира не будет. Не набралось и двух участников");
            } else {
                state = TournamentState.PROGRESS;
                save();
                res.add(Emoji.DRINKS + "Турнир " + type + " начинается!\nГлавный приз - почет и уважение!" + Emoji.DRINK + "\n\nПолный список участников:\n" + TournamentUsers.getAllString(this, round + 1) + "\n Первое состязание состоится через 1 минуту, всем занять свои места, МЫ НАЧИНАЕМ!");
            }
        } else if (isInProgress()) {
            Pair<TournamentUsers, TournamentUsers> pair = TournamentUsers.getTwoUsers(this);
            if (pair == null) {
                state = TournamentState.FINISHED;
                save();
                res.add("Увы, но на финал турнира не нашлось бойцов. Турнир окончен до выявления победителя.");
            } else {
                TournamentUsers left = pair.getLeft();
                TournamentUsers right = pair.getRight();
                if (right == null) {
                    winner = left.getUser();
                    state = TournamentState.FINISHED;
                    save();
                    try {
                        CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage( "Итак, сегодняший турнир окончен, победитель определен! Им стал " + left.getUser() + ". Дружище, ты лучше всех!"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    res.add(TournamentBet.evalTournamentResults(left));
                } else if (left.getRound() < right.getRound()) {
                    left.incRound();
                    left.getUser().incFightClubWins();
                    left.getUser().save();
                    left.save();
                    res.add(left.getUser() + ", тебе повезло, соперника не нашлось, и ты автоматически проходишь в следующий этап.");
                } else {
                    if (round < left.getRound()) {
                        round++;
                        save();
                        int phase = maxUsers / (Double.valueOf(Math.pow(2, round))).intValue();
                        String roundStr = "Начинается следующий этап!\n";
                        switch (phase) {
                            case 1:
                                roundStr = "Начинается финал!\n";
                                break;
                            case 2:
                                roundStr = "Начинается полуфинал!\n";
                                break;
                            case 4:
                                roundStr = "Начинается четвертьфинал!\n";
                                break;
                            case 8:
                                roundStr = "Начинается 1/8 финала!\n";
                                break;
                            case 16:
                                roundStr = "Начинается 1/16 финала!\n";
                                break;
                            case 32:
                                roundStr = "Начинается 1/32 финала!\n";
                                break;
                        }
                        res.add(roundStr + TournamentUsers.getAllString(this, round));
                    }
                    if (left.isInFight()) {
                        String fightResult;
                        if (left.getScore() == 0 && right.getScore() == 0) {
                            left.setLose(true);
                            right.setLose(true);
                            left.setInFight(false);
                            right.setInFight(false);
                            left.save();
                            right.save();
                            fightResult = "Оба участника пропустили состязание и были дисквалифицированы! " + left.getUser() + ", " + right.getUser() + " в следующий раз будете смелее!\n";
                        } else if (left.getScore() == 0 || right.getScore() == 0) {
                            TournamentUsers winner = left;
                            TournamentUsers loser = right;
                            if (left.getScore() == 0) {
                                winner = right;
                                loser = left;
                            }
                            loser.setLose(true);
                            loser.setInFight(false);
                            loser.save();
                            winner.incRound();
                            winner.getUser().incFightClubWins();
                            winner.getUser().save();
                            winner.setInFight(false);
                            winner.setScore(0);
                            winner.save();
                            fightResult = "Участник " + loser.getUser() + " трус, он не явился на состязание! Его соперник " + winner.getUser() + " автоматически проходит в следующий этап!\n";
                        } else {
                            TournamentUsers winner = null;
                            TournamentUsers loser = null;

                            int leftScore = left.getFinalResult(right);
                            int rightScore = right.getFinalResult(left);
                            if (leftScore < rightScore) {
                                winner = right;
                                loser = left;
                            } else if (leftScore > rightScore) {
                                winner = left;
                                loser = right;
                            }
                            if (winner != null) {
                                loser.setInFight(false);
                                loser.setLose(true);
                                loser.save();
                                fightResult = type.getWinPhrase(winner, loser) + "\n";
                                winner.incRound();
                                winner.getUser().incFightClubWins();
                                winner.getUser().save();
                                winner.setInFight(false);
                                winner.setScore(0);
                                winner.save();
                            } else {
                                left.setScore(0);
                                left.setInFight(false);
                                left.save();
                                right.setScore(0);
                                right.setInFight(false);
                                right.save();
                                fightResult = type.getTie(left, right) + "\n";
                            }
                        }
                        OficiantThread.INSTANCE.setTournamentPhase(true);
                        fightResult += "Следующее состязание вот-вот начнется!";
                        res.add(fightResult);
                        User.flushVotes();
                    } else {
                        left.setInFight(true);
                        right.setInFight(true);
                        left.save();
                        right.save();
                        res.add(left.getUser() + " - твой выход!\n\n" + left.getUser().getPublicFightClubStats());
                        res.add(right.getUser() + " - твой выход!\n\n" + right.getUser().getPublicFightClubStats());
                        res.add(type.getRule() + "\n\nРАУНД НАЧИНАЕТСЯ!");
                        type.remindUser(left.getUser());
                        type.remindUser(right.getUser());
                    }
                }
            }
        }
        return res;
    }
}

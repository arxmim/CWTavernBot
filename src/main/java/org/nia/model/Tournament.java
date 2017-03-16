package org.nia.model;

import org.apache.commons.lang3.tuple.Pair;
import org.nia.bots.OficiantThread;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.TournamentState;
import org.nia.logic.TournamentType;
import org.nia.strings.Emoji;

import java.sql.*;
import java.util.Date;
import java.util.Random;

/**
 * @author Иван, 11.03.2017.
 */
public class Tournament {
    private Integer publicID;
    private Date registrationDateTime;
    private TournamentType tournamentType;
    private TournamentState tournamentState;
    private int maxUsers;
    private User winner;
    private int round = 0;

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_Tournament set registrationDateTime = ?" +
                        ", tournamentType = ?" +
                        ", tournamentState = ?" +
                        ", maxUsers = ?" +
                        ", winner = ?" +
                        ", round = ?" +
                        " where PublicID = ?");
                preparedStatement.setTimestamp(1, new Timestamp(registrationDateTime.getTime()));
                preparedStatement.setString(2, tournamentType.name());
                preparedStatement.setString(3, tournamentState.name());
                preparedStatement.setInt(4, maxUsers);
                if (winner != null) {
                    preparedStatement.setInt(5, winner.getUserID());
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }
                preparedStatement.setInt(6, round);
                preparedStatement.setInt(7, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_Tournament (registrationDateTime, tournamentType, tournamentState, maxUsers, winner, round) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
                preparedStatement.setTimestamp(1, new Timestamp(registrationDateTime.getTime()));
                preparedStatement.setString(2, tournamentType.name());
                preparedStatement.setString(3, tournamentState.name());
                preparedStatement.setInt(4, maxUsers);
                if (winner != null) {
                    preparedStatement.setInt(5, winner.getUserID());
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }
                preparedStatement.setInt(6, round);
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_Tournament where registrationDateTime = ?" +
                        " and tournamentType = ? and tournamentState = ? and maxUsers = ?");
                preparedStatement.setTimestamp(1, new Timestamp(registrationDateTime.getTime()));
                preparedStatement.setString(2, tournamentType.name());
                preparedStatement.setString(3, tournamentState.name());
                preparedStatement.setInt(4, maxUsers);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                publicID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getPublicID() {
        return publicID;
    }

    public void setType(TournamentType tournamentType) {
        this.tournamentType = tournamentType;
    }

    public void setState(TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }

    public void setRegistrationDateTime(Date registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public static Tournament getCurrent() {
        Tournament res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select top 1 PublicID, registrationDateTime, tournamentType, tournamentState, maxUsers, round " +
                    "from cwt_Tournament where tournamentState in ('" + TournamentState.REGISTRATION.name() + "', '" + TournamentState.PROGRESS + "')");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new Tournament();
                res.publicID = resultSet.getInt(1);
                res.registrationDateTime = resultSet.getTimestamp(2);
                res.tournamentType = TournamentType.valueOf(resultSet.getString(3));
                res.tournamentState = TournamentState.valueOf(resultSet.getString(4));
                res.maxUsers = resultSet.getInt(5);
                res.round = resultSet.getInt(6);
            } else {
                preparedStatement = connectionDB.getPreparedStatement("select top 1 PublicID, registrationDateTime, tournamentType, tournamentState, maxUsers, round " +
                        "from cwt_Tournament where tournamentState ='" + TournamentState.ANOUNCE.name() + "' order by registrationDateTime");
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    res = new Tournament();
                    res.publicID = resultSet.getInt(1);
                    res.registrationDateTime = resultSet.getTimestamp(2);
                    res.tournamentType = TournamentType.valueOf(resultSet.getString(3));
                    res.tournamentState = TournamentState.valueOf(resultSet.getString(4));
                    res.maxUsers = resultSet.getInt(5);
                    res.round = resultSet.getInt(6);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "publicID=" + publicID +
                ", registrationDateTime=" + registrationDateTime +
                ", tournamentType=" + tournamentType +
                ", tournamentState=" + tournamentState +
                ", maxUsers=" + maxUsers +
                ", round=" + round +
                '}';
    }
    public boolean isAnnounced() {
        return tournamentState == TournamentState.ANOUNCE;
    }

    public boolean isRegistration() {
        return tournamentState == TournamentState.REGISTRATION;
    }

    public boolean isInProgress() {
        return tournamentState == TournamentState.PROGRESS;
    }

    public Date getRegistrationDateTime() {
        return registrationDateTime;
    }

    public String work() {
        String res = "";
        if (isAnnounced()) {
            tournamentState = TournamentState.REGISTRATION;
            save();
            res = "Регистрация на турнир " + tournamentType + " открыта на 5 минут! Жми /register срочно!\nМаксимальное число участников - " + maxUsers + ". Торопитесь принять участие!";
        } else if (isRegistration()) {
            Pair<TournamentUsers, TournamentUsers> pair = TournamentUsers.getTwoUsers(this);
            if (pair == null || pair.getRight() == null) {
                tournamentState = TournamentState.FINISHED;
                save();
                res = "Турнира не будет. Не набралось и двух участников";
            } else {
                tournamentState = TournamentState.PROGRESS;
                save();
                res = Emoji.DRINKS + "Турнир " + tournamentType + " начинается!\nГлавный приз - почет и уважение!" + Emoji.DRINK + "\n\nПолный список участников:\n" + TournamentUsers.getAllString(this, round + 1) + "\n Первое состязание состоится через 1 минуту, всем занять свои места, МЫ НАЧИНАЕМ!";
            }
        } else if (isInProgress()) {
            Pair<TournamentUsers, TournamentUsers> pair = TournamentUsers.getTwoUsers(this);
            if (pair == null) {
                tournamentState = TournamentState.FINISHED;
                save();
                res = "Увы, но на финал турнира не нашлось бойцов. Турнир окончен до выявления победителя.";
            } else {
                TournamentUsers left = pair.getLeft();
                TournamentUsers right = pair.getRight();
                if (right == null) {
                    winner = left.getUser();
                    tournamentState = TournamentState.FINISHED;
                    save();
                    res = "Итак, сегодняший турнир окончен, победитель определен! Им стал " + left.getUser() + ". Дружище, ты лучше всех!";
                } else if (left.getRound() < right.getRound()) {
                    left.incRound();
                    left.save();
                    res = left.getUser() + ", тебе повезло, соперника не нашлось, и ты автоматически проходишь в следующий этап.";
                } else {
                    if (round < left.getRound()) {
                        round++;
                        save();
                        int phase = maxUsers / (Double.valueOf(Math.pow(2, round))).intValue();
                        switch (phase) {
                            case 1:
                                res = "Начинается финал!\n";
                                break;
                            case 2:
                                res = "Начинается полуфинал!\n";
                                break;
                            case 4:
                                res = "Начинается четвертьфинал!\n";
                                break;
                            case 8:
                                res = "Начинается 1/8 финала!\n";
                                break;
                            case 16:
                                res = "Начинается 1/16 финала!\n";
                                break;
                            case 32:
                                res = "Начинается 1/32 финала!\n";
                                break;
                        }
                        res += TournamentUsers.getAllString(this, round);
                    }
                    if (left.InFight()) {
                        if (left.getScore() == 0 && right.getScore() == 0) {
                            left.setLose(true);
                            right.setLose(true);
                            left.save();
                            right.save();
                            res = "Оба участника пропустили состязание и были дисквалифицированы! " + left.getUser() + ", " + right.getUser() + " в следующий раз будете смелее!\n";
                        } else if (left.getScore() == 0 || right.getScore() == 0) {
                            TournamentUsers winner = left;
                            TournamentUsers loser = right;
                            if (left.getScore() == 0) {
                                winner = right;
                                loser = left;
                            }
                            loser.setLose(true);
                            loser.save();
                            winner.incRound();
                            winner.setInFight(false);
                            winner.setScore(0);
                            winner.save();
                            res = "Участник " + loser.getUser() + " трус, он не явился на состязание! Его соперник " + winner.getUser() + " автоматически проходит в следующий этап!\n";
                        } else {
                            TournamentUsers winner = null;
                            TournamentUsers loser = null;
                            Random random = new Random();

                            int leftScore = left.getScore() + random.nextInt(71);
                            int rightScore = right.getScore() + random.nextInt(71);
                            if (leftScore < rightScore) {
                                winner = right;
                                loser = left;
                            } else if (leftScore > rightScore) {
                                winner = left;
                                loser = right;
                            }
                            if (winner != null) {
                                loser.setLose(true);
                                loser.save();
                                winner.incRound();
                                winner.setInFight(false);
                                winner.setScore(0);
                                winner.save();
                                res = String.format(tournamentType.getWinPhrase(), winner.getUser(), loser.getUser()) + "\n";
                            } else {
                                left.setScore(0);
                                left.setInFight(false);
                                left.save();
                                right.setScore(0);
                                right.setInFight(false);
                                right.save();
                                res = String.format(tournamentType.getTie(), left.getUser(), right.getUser()) + "\n";
                            }
                        }
                        OficiantThread.INSTANCE.setTournamentPhase(true);
                        res += "Следующее состязание вот-вот начнется!";
                    } else {
                        left.setInFight(true);
                        right.setInFight(true);
                        left.save();
                        right.save();
                        res += left.getUser() + ", " + right.getUser() + " ваш выход! " + tournamentType.getRule() + "\n\nРАУНД НАЧИНАЕТСЯ!";
                    }
                }
            }
        }
        return res;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public TournamentType getType() {
        return tournamentType;
    }
}

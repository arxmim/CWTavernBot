package org.nia.model;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * @author Иван, 11.03.2017.
 */
public class TournamentUsers {
    private int publicID;
    private Tournament tournament;
    private User user;
    private int position;
    private int round = 1;
    private int score = 0;
    private boolean inFight = false;
    private boolean lose = false;

    public static String register(Tournament tournament, User user) {
        try {
//            if (user.getDrinkedTotal() > 30) {
//                return user + ", прости, ты слишком пьян, дай мандаринам подраться на их свадьбе";
//            }
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select 1 from cwt_TournamentUsers where userID = ? and TournamentID = ?");
            preparedStatement.setInt(1, user.getUserID());
            preparedStatement.setInt(2, tournament.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return user + ", ты уже зарегистрировался на турнир!";
            } else {
                preparedStatement = connectionDB.getPreparedStatement("select position from cwt_TournamentUsers where TournamentID = ?");
                preparedStatement.setInt(1, tournament.getPublicID());
                resultSet = preparedStatement.executeQuery();
                int newCount = 1;
                HashSet<Integer> has = new HashSet<>();
                ArrayList<Integer> hasNot = new ArrayList<>();
                while (resultSet.next()) {
                    newCount++;
                    has.add(resultSet.getInt(1));
                }
                if (newCount > tournament.getMaxUsers()) {
                    return "Извини, " + user + ", но места для участников уже все заняты. В следующий раз соображай быстрее!";
                }
                for (int i = 1; i <= tournament.getMaxUsers(); i++) {
                    if (!has.contains(i)) {
                        hasNot.add(i);
                    }
                }
                int position = hasNot.get(new Random().nextInt(hasNot.size()));
                preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_TournamentUsers (userID, TournamentID, position) " +
                        "VALUES (?, ?, ?)");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setInt(2, tournament.getPublicID());
                preparedStatement.setInt(3, position);
                preparedStatement.execute();
                return user + ", ты успешно зарегистрирован на турнир, твой номер - " + position + ", уже зарегистрировано - " + newCount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Что-то пошло не так. Регистрация не удалась";
        }
    }

    public User getUser() {
        return user;
    }

    public int getRound() {
        return round;
    }

    public void setInFight(boolean inFight) {
        this.inFight = inFight;
    }

    public void setLose(boolean lose) {
        this.lose = lose;
    }

    public void incRound() {
        round++;
    }

    public int getScore() {
        return score;
    }

    public boolean InFight() {
        return inFight;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_TournamentUsers set TournamentID = ?" +
                    ", userID = ?" +
                    ", round = ?" +
                    ", position = ?" +
                    ", score = ?" +
                    ", inFight = ?" +
                    ", lose = ?" +
                    " where publicID = ?");
            preparedStatement.setInt(1, tournament.getPublicID());
            preparedStatement.setInt(2, user.getUserID());
            preparedStatement.setInt(3, round);
            preparedStatement.setInt(4, position);
            preparedStatement.setInt(5, score);
            preparedStatement.setBoolean(6, inFight);
            preparedStatement.setBoolean(7, lose);
            preparedStatement.setInt(8, publicID);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getAllString(Tournament tournament, int round) {
        StringBuilder sb = new StringBuilder();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select userID, position from cwt_TournamentUsers where TournamentID = ? and round = ? order by position");
            preparedStatement.setInt(1, tournament.getPublicID());
            preparedStatement.setInt(2, round);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getInt(2)).append(" - ").append(User.getByID(resultSet.getInt(1))).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Pair<TournamentUsers, TournamentUsers> getTwoUsers(Tournament tournament) {
        Pair<TournamentUsers, TournamentUsers> res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select top 2 publicID, userID, round, position, score, inFight from cwt_TournamentUsers where TournamentID = ? and lose = 0 order by inFight desc, round, position");
            preparedStatement.setInt(1, tournament.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                TournamentUsers tu1 = new TournamentUsers();
                tu1.publicID = resultSet.getInt(1);
                tu1.user = User.getByID(resultSet.getInt(2));
                tu1.round = resultSet.getInt(3);
                tu1.position = resultSet.getInt(4);
                tu1.score = resultSet.getInt(5);
                tu1.inFight = resultSet.getBoolean(6);
                tu1.lose = false;
                tu1.tournament = tournament;
                TournamentUsers tu2 = null;
                if (resultSet.next()) {
                    tu2 = new TournamentUsers();
                    tu2.publicID = resultSet.getInt(1);
                    tu2.user = User.getByID(resultSet.getInt(2));
                    tu2.round = resultSet.getInt(3);
                    tu2.position = resultSet.getInt(4);
                    tu2.score = resultSet.getInt(5);
                    tu2.inFight = resultSet.getBoolean(6);
                    tu2.lose = false;
                    tu2.tournament = tournament;
                }
                res = new MutablePair<>(tu1, tu2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static TournamentUsers getCurrentByUserID(int userID) {
        TournamentUsers res = null;
        try {
            Tournament current = Tournament.getCurrent();
            if (current != null) {
                ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select top 1 publicID, round, position, score, inFight, lose from cwt_TournamentUsers where TournamentID = ? and userID = ?");
                preparedStatement.setInt(1, current.getPublicID());
                preparedStatement.setInt(2, userID);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    res = new TournamentUsers();
                    res.publicID = resultSet.getInt(1);
                    res.user = User.getByID(userID);
                    res.round = resultSet.getInt(2);
                    res.position = resultSet.getInt(3);
                    res.score = resultSet.getInt(4);
                    res.inFight = resultSet.getBoolean(5);
                    res.lose = resultSet.getBoolean(6);
                    res.tournament = current;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static TournamentUsers getByID(int publicID) {
        TournamentUsers res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select TournamentID, userID, round, position, score, inFight, lose from cwt_TournamentUsers where publicID = ?");
            preparedStatement.setInt(1, publicID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new TournamentUsers();
                res.publicID = publicID;
                res.tournament = Tournament.getByID(resultSet.getInt(1));
                res.user = User.getByID(resultSet.getInt(2));
                res.round = resultSet.getInt(3);
                res.position = resultSet.getInt(4);
                res.score = resultSet.getInt(5);
                res.inFight = resultSet.getBoolean(6);
                res.lose = resultSet.getBoolean(7);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int getPublicID() {
        return publicID;
    }
}

package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.strings.Emoji;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Иван, 18.03.2017.
 */
public class TournamentBet {
    private Integer publicID;
    private Tournament tournament;
    private User from;
    private TournamentUsers to;
    private int sum;


    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_TournamentBet set tournamentID = ?" +
                        ", fromID = ?" +
                        ", toID = ?" +
                        ", sum = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, tournament.getPublicID());
                preparedStatement.setInt(2, from.getUserID());
                preparedStatement.setInt(3, to.getPublicID());
                preparedStatement.setInt(4, sum);
                preparedStatement.setInt(5, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_TournamentBet (tournamentID, fromID, toID, sum) " +
                        "VALUES (?, ?, ?, ?)");
                preparedStatement.setInt(1, tournament.getPublicID());
                preparedStatement.setInt(2, from.getUserID());
                preparedStatement.setInt(3, to.getPublicID());
                preparedStatement.setInt(4, sum);
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_TournamentBet where tournamentID = ? and fromID = ?" +
                        " and toID = ?");
                preparedStatement.setInt(1, tournament.getPublicID());
                preparedStatement.setInt(2, from.getUserID());
                preparedStatement.setInt(3, to.getPublicID());
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                publicID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<TournamentBet> getCurrentBetsByUserID(User user) {
        List<TournamentBet> res = new ArrayList<>();
        try {
            Tournament current = Tournament.getCurrent();
            if (current != null) {
                ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select publicID, toID, sum from cwt_TournamentBet where TournamentID = ? and fromID = ?");
                preparedStatement.setInt(1, current.getPublicID());
                preparedStatement.setInt(2, user.getUserID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    TournamentBet tb = new TournamentBet();
                    tb.publicID = resultSet.getInt(1);
                    tb.to = TournamentUsers.getByID(resultSet.getInt(2));
                    tb.sum = resultSet.getInt(3);
                    tb.from = user;
                    tb.tournament = current;
                    res.add(tb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public TournamentUsers getTo() {
        return to;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(TournamentUsers to) {
        this.to = to;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public static String evalTournamentResults(TournamentUsers winner) {
        List<TournamentBet> tbList = TournamentBet.getAllByTournament(winner.getTournament());
//        int pool =  tbList.stream().mapToInt(tb -> tb.sum).sum();
        BigDecimal pool =  BigDecimal.valueOf(tbList.stream().mapToInt(tb -> tb.sum).sum());
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
            sb.append("Никто не поставил на ").append(winner.getUser()).append(", очень зря. Все ваши ставки сгорели!");
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
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select publicID, fromID, toID, sum from cwt_TournamentBet where TournamentID = ?");
            preparedStatement.setInt(1, tournament.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TournamentBet tb = new TournamentBet();
                tb.publicID = resultSet.getInt(1);
                tb.from = User.getByID(resultSet.getInt(2));
                tb.to = TournamentUsers.getByID(resultSet.getInt(3));
                tb.sum = resultSet.getInt(4);
                tb.tournament = tournament;
                res.add(tb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}

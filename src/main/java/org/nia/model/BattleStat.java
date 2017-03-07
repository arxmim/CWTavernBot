package org.nia.model;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.model.lists.CastleType;
import org.nia.strings.Emoji;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author IANazarov
 */
public class BattleStat {

    private Date battleDate;
    private Date reportDate;
    private Integer userID;
    private String name;
    private CastleType castleType;
    private int atk;
    private int def;
    private int lvl;
    private Integer exp;
    private Integer gold;
    private String getOrLostEquip;


    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    private static Date getBattleTime(Date timeAfter) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(timeAfter);
        int hour = gc.get(Calendar.HOUR_OF_DAY);
        if (hour < 4) {
            gc.set(Calendar.HOUR_OF_DAY, 0);
        } else if (hour < 8) {
            gc.set(Calendar.HOUR_OF_DAY, 4);
        } else if (hour < 12) {
            gc.set(Calendar.HOUR_OF_DAY, 8);
        } else if (hour < 16) {
            gc.set(Calendar.HOUR_OF_DAY, 12);
        } else if (hour < 20) {
            gc.set(Calendar.HOUR_OF_DAY, 16);
        } else if (hour < 24) {
            gc.set(Calendar.HOUR_OF_DAY, 20);
        }
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    public void setBattleDate(Date battleDate) {
        this.battleDate = getBattleTime(battleDate);
    }

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select UserID from cwo_BattleStat where Name = ? and BattleDate = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setTimestamp(2, new Timestamp(battleDate.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return;
            }
            preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwo_BattleStat (UserID, Name, Castle, ReportDate, BattleDate, atk, def, lvl, exp, gold, equip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, castleType.getName());
            preparedStatement.setTimestamp(4, new Timestamp(reportDate.getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(battleDate.getTime()));
            preparedStatement.setInt(6, atk);
            preparedStatement.setInt(7, def);
            preparedStatement.setInt(8, lvl);
            setObjOrNull(preparedStatement, 9, exp, Types.INTEGER);
            setObjOrNull(preparedStatement, 10, gold, Types.INTEGER);
            setObjOrNull(preparedStatement, 11, getOrLostEquip, Types.VARCHAR);
            preparedStatement.execute();
            Profile profile = Profile.get(name);
            if (profile == null) {
                profile = new Profile();
            }
            if (profile.getExp() == null) {
                profile.setUserID(userID);
                profile.setName(name);
                profile.setAtk(atk);
                profile.setCastleType(castleType);
                profile.setDef(def);
                profile.setLvl(lvl);
                profile.save();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public void setGetOrLostEquip(String getOrLostEquip) {
        this.getOrLostEquip = getOrLostEquip;
    }

    private void setObjOrNull(PreparedStatement statement, int index, Object val, int type) throws SQLException {
        if (val == null) {
            statement.setNull(index, type);
        } else if (val instanceof Integer) {
            statement.setInt(index, (int) val);
        } else if (val instanceof String) {
            statement.setString(index, (String) val);
        }
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setCastleType(CastleType castleType) {
        this.castleType = castleType;
    }

    public static List<Pair<BattleStat, Integer>> getTotal() {
        List<Pair<BattleStat, Integer>> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select Name, Castle, count(1), sum(gold), sum(exp), max(BattleDate) from cwo_BattleStat group by Name, Castle order by sum(gold) desc");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BattleStat battleStat = new BattleStat();
                battleStat.name = resultSet.getString(1);
                battleStat.castleType = CastleType.getByName(resultSet.getString(2));
                battleStat.gold = resultSet.getInt(4);
                battleStat.exp = resultSet.getInt(5);
                battleStat.battleDate = resultSet.getTimestamp(6);
                res.add(new MutablePair<>(battleStat, resultSet.getInt(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public String toShortString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM HH:mm");
        return castleType.getEmoji() + name + " - " + Emoji.GOLD + gold + ", " + Emoji.EXP + exp + ", последний бой - " + sdf.format(battleDate);
    }

    public static boolean updateTarget(Profile userProfile, String text) {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select PublicID from cwo_BattleStat where Name = ? order by ReportDate desc");
            preparedStatement.setString(1, userProfile.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int publicID = resultSet.getInt(1);
                Emoji e = Emoji.getFlagByText(text);
                if (e != null) {
                    preparedStatement = connectionDB.getPreparedStatement("update cwo_BattleStat set Target = ? where publicID = ?");
                    preparedStatement.setString(1, e.name());
                    preparedStatement.setInt(2, publicID);
                    preparedStatement.execute();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String statForTeamAndBattle(Team team, Date battleDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date actualBattleDate = getBattleTime(battleDate);
        StringBuilder sb = new StringBuilder();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select bs.Target, sum(bs.atk), sum(bs.def), count(1) " +
                    "from cwo_BattleStat bs join cwo_Profile p on p.Name = bs.Name where bs.BattleDate = ? and p.Team = ? and bs.Target is not null group by bs.Target");
            preparedStatement.setTimestamp(1, new Timestamp(actualBattleDate.getTime()));
            preparedStatement.setString(2, team.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Emoji e = Emoji.valueOf(resultSet.getString(1));
                sb.append("Цель: ").append(e.toString()).append("Участвовало бойцов: ").append(resultSet.getInt(4))
                        .append("\nСуммарная атака: ").append(Emoji.ATK.toString()).append(resultSet.getInt(2))
                        .append("\nСуммарная защита: ").append(Emoji.DEF.toString()).append(resultSet.getInt(3))
                        .append("\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String res = sb.toString();
        if (res.isEmpty()) {
            return "Нет статистики по запрошенному бою.";
        } else {
            return "Статистика по бою в " + sdf.format(actualBattleDate) + "\n\n" + res;
        }
    }
}

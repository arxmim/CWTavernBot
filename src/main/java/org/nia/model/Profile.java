package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.model.lists.CastleType;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

/**
 * @author IANazarov
 */
public class Profile {
    private Integer userID;
    private String name;
    private String team;
    private CastleType castleType;
    private int lvl;
    private Integer exp;
    private int atk;
    private int def;
    private Integer gold;
    private Integer crystalls;
    private Integer stamina;
    private Integer bag;
    private Set<Equipment> equipmentList = new HashSet<>();

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public void setCrystalls(Integer crystalls) {
        this.crystalls = crystalls;
    }

    public void setStamina(Integer stamina) {
        this.stamina = stamina;
    }

    public void setBag(Integer bag) {
        this.bag = bag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setCastleType(CastleType castleType) {
        this.castleType = castleType;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }


    public void setExp(int exp) {
        this.exp = exp;
    }

    public void addToEquipmentList(Equipment equipment) {
        equipmentList.add(equipment);
    }

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select name from cwo_Profile where Name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = resultSet.next();
            if (exists) {
                preparedStatement = connectionDB.getPreparedStatement("update cwo_Profile set UserID = ?" +
                        ", Castle = ?" +
                        ", Lvl = ?" +
                        ", Exp = ?" +
                        ", Atk = ?" +
                        ", Def = ?" +
                        ", Gold = ?" +
                        ", Crystalls = ?" +
                        ", Stamina = ?" +
                        ", Bag = ?" +
                        " where Name = ?");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, castleType.getName());
                preparedStatement.setInt(3, lvl);
                setIntOrNull(preparedStatement, 4, exp);
                preparedStatement.setInt(5, atk);
                preparedStatement.setInt(6, def);
                setIntOrNull(preparedStatement, 7, gold);
                setIntOrNull(preparedStatement, 8, crystalls);
                setIntOrNull(preparedStatement, 9, stamina);
                setIntOrNull(preparedStatement, 10, bag);
                preparedStatement.setString(11, name);
                preparedStatement.execute();
            } else {
                preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwo_Profile (UserID, Name, Castle, Lvl, Exp, Atk, Def, Gold, Crystalls, Stamina, Bag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, castleType.getName());
                preparedStatement.setInt(4, lvl);
                setIntOrNull(preparedStatement, 5, exp);
                preparedStatement.setInt(6, atk);
                preparedStatement.setInt(7, def);
                setIntOrNull(preparedStatement, 8, gold);
                setIntOrNull(preparedStatement, 9, crystalls);
                setIntOrNull(preparedStatement, 10, stamina);
                setIntOrNull(preparedStatement, 11, bag);
                preparedStatement.execute();
            }
            preparedStatement = connectionDB.getPreparedStatement("delete from cwo_Equipment where ProfileName = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            equipmentList.forEach(Equipment::save);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTeam(String name, String team) {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwo_Profile set Team = ? where Name = ?");
            preparedStatement.setString(1, team);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setIntOrNull(PreparedStatement statement, int index, Integer val) throws SQLException {
        if (val == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, val);
        }
    }

    private void setStringOrNull(PreparedStatement statement, int index, String val) throws SQLException {
        if (val == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, val);
        }
    }

    private static Integer getIntOrNull(ResultSet resultSet, int index) throws SQLException {
        int anInt = resultSet.getInt(index);
        if (resultSet.wasNull()) {
            return null;
        } else {
            return anInt;
        }
    }

    static Profile get(String name) {
        Profile res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select UserID, Castle, Lvl, Exp, Atk, Def, Gold, Crystalls, Stamina, Bag, Team from cwo_Profile where Name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new Profile();
                res.userID = resultSet.getInt(1);
                res.castleType = CastleType.getByName(resultSet.getString(2));
                res.lvl = resultSet.getInt(3);
                res.exp = getIntOrNull(resultSet, 4);
                res.atk = resultSet.getInt(5);
                res.def = resultSet.getInt(6);
                res.gold = getIntOrNull(resultSet, 7);
                res.crystalls = getIntOrNull(resultSet, 8);
                res.stamina = getIntOrNull(resultSet, 9);
                res.bag = getIntOrNull(resultSet, 10);
                res.team = resultSet.getString(11);
                res.name = name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    Integer getExp() {
        return exp;
    }

    public String getName() {
        return name;
    }

    public static Profile getUserProfile(Message message) {
        Profile res = null;
        int userID = message.getFrom().getId();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select Name, Castle, Lvl, Exp, Atk, Def, Gold, Crystalls, Stamina, Bag, Team from cwo_Profile where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new Profile();
                res.name = resultSet.getString(1);
                res.castleType = CastleType.getByName(resultSet.getString(2));
                res.lvl = resultSet.getInt(3);
                res.exp = getIntOrNull(resultSet, 4);
                res.atk = resultSet.getInt(5);
                res.def = resultSet.getInt(6);
                res.gold = getIntOrNull(resultSet, 7);
                res.crystalls = getIntOrNull(resultSet, 8);
                res.stamina = getIntOrNull(resultSet, 9);
                res.bag = getIntOrNull(resultSet, 10);
                res.team = resultSet.getString(11);
                res.userID = userID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String toReportString() {
        return castleType.getEmoji().toString() + name + "\n" +
                Emoji.ATK.toString() + "Атака: " + atk + "\t"+Emoji.DEF.toString() + "Защита:" + def + "\n" +
                "Уровень: " + lvl + "\n" + "Отряд: " + team;
    }
}

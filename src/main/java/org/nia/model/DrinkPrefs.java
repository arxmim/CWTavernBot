package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.lists.DrinkType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Иван, 11.03.2017.
 */
public class DrinkPrefs {
    private User user;
    private HashMap<DrinkType, DrinkPrefs.Pref> prefMap;

    public static DrinkPrefs getByUser(User usr) {
        DrinkPrefs res = null;
        try {
            int userID = usr.getUserID();
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select drinkType, toDrinkCount, toThrowCount, toBeThrownCount from cwt_DrinkPrefs where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            res = new DrinkPrefs();
            res.user = usr;
            res.prefMap = new HashMap<>();
            while (resultSet.next()) {
                try {
                    DrinkType drinkType = DrinkType.valueOf(resultSet.getString(1));
                    Pref pref = new Pref(resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
                    res.prefMap.put(drinkType, pref);
                } catch (Exception ignored) {
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Pref getByDrinkType(DrinkType dt) {
        Pref pref = prefMap.get(dt);
        if (pref == null) {
            pref = new Pref(0, 0, 0);
            prefMap.put(dt, pref);
        }
        return pref;
    }

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            for (Map.Entry<DrinkType, Pref> entry : prefMap.entrySet()) {
                DrinkType drinkType = entry.getKey();
                Pref pref = entry.getValue();
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select 1 from cwt_DrinkPrefs where UserID = ? and drinkType = ?");
                preparedStatement.setInt(1, this.user.getUserID());
                preparedStatement.setString(2, drinkType.name());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    preparedStatement = connectionDB.getPreparedStatement("update cwt_DrinkPrefs set toDrinkCount = ?" +
                            ", toThrowCount = ?" +
                            ", toBeThrownCount = ?" +
                            " where UserID = ? and drinkType = ?");
                    preparedStatement.setInt(1, pref.toDrink);
                    preparedStatement.setInt(2, pref.toThrow);
                    preparedStatement.setInt(3, pref.toBeThrown);
                    preparedStatement.setInt(4, user.getUserID());
                    preparedStatement.setString(5, drinkType.name());
                    preparedStatement.execute();
                } else {
                    preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_DrinkPrefs (UserID, drinkType, toDrinkCount, toThrowCount, toBeThrownCount) " +
                            "VALUES (?, ?, ?, ?, ?)");
                    preparedStatement.setInt(1, user.getUserID());
                    preparedStatement.setString(2, drinkType.name());
                    preparedStatement.setInt(3, pref.toDrink);
                    preparedStatement.setInt(4, pref.toThrow);
                    preparedStatement.setInt(5, pref.toBeThrown);
                    preparedStatement.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public HashMap<DrinkType, Pref> getPrefMap() {
        return prefMap;
    }

    public static void incDrink(User usr, DrinkType dt, Integer count) {
        DrinkPrefs byUser = DrinkPrefs.getByUser(usr);
        DrinkPrefs.Pref pref = byUser.getByDrinkType(dt);
        pref.incToDrink(count);
        byUser.save();
    }

    public static void incThrow(User usr, DrinkType dt) {
        DrinkPrefs byUser = DrinkPrefs.getByUser(usr);
        DrinkPrefs.Pref pref = byUser.getByDrinkType(dt);
        pref.incToThrow();
        byUser.save();
    }

    public static void incToBeThrown(User usr, DrinkType dt) {
        DrinkPrefs byUser = DrinkPrefs.getByUser(usr);
        DrinkPrefs.Pref pref = byUser.getByDrinkType(dt);
        pref.incToBeThrown();
        byUser.save();
    }

    public static class Pref {
        private int toDrink;
        private int toThrow;
        private int toBeThrown;

        public Pref(int toDrink, int toThrow, int toBeThrown) {
            this.toDrink = toDrink;
            this.toThrow = toThrow;
            this.toBeThrown = toBeThrown;
        }

        public int getToDrink() {
            return toDrink / 2;
        }

        public void incToDrink(int plus) {
            this.toDrink+=plus;
        }

        public int getToThrow() {
            return toThrow;
        }

        public void incToThrow() {
            this.toThrow++;
        }

        public int getToBeThrown() {
            return toBeThrown;
        }

        public void incToBeThrown() {
            this.toBeThrown++;
        }
    }
}

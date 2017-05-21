package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.lists.DrinkType;
import org.nia.logic.lists.Food;
import org.nia.logic.lists.Location;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import javax.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author IANazarov
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_User")
public class User {
    @Id
    @Column()
    private int userID;
    @Column()
    private String nick;
    @Column(nullable = false)
    private String name;
    @Column()
    private Date lastDrinkTime;
    @Column()
    private Date lastEatTime;
    @Enumerated(EnumType.STRING)
    private DrinkType drinkType;
    @Enumerated(EnumType.STRING)
    private DrinkType wanted;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isBarmen;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isAdmin;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int gold;
    @Enumerated(EnumType.STRING)
    private Food food;
    @Enumerated(EnumType.STRING)
    private Food wantedFood;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int foodCount;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int eatTotal;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int alkoCount;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int drinkedTotal;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int drinkedWeek;
    @Column()
    private int fightClubWins;
    @Column()
    private int brewCount;
    @Column()
    private Date fightTime;
    @Enumerated(EnumType.STRING)
    private Location location;
    @Column()
    private Integer fightWithUserID;
    @Column()
    private Date curseTime;
    @Column()
    private String voteFor;

    public static User getByID(Integer userID) {
        if (userID == null) {
            return null;
        }
        User res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, isBarmen, alkoCount, lastDrinkTime" +
                    ", drinkedTotal, drinkType, wanted, isAdmin, gold" +
                    ", fightTime, location, food, wantedFood, foodCount" +
                    ", eatTotal, fightClubWins, brewCount, lastEatTime, drinkedWeek" +
                    ", fightWithUserID, curseTime, voteFor" +
                    " from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.userID = userID;
                res.nick = resultSet.getString(1);
                res.name = resultSet.getString(2);
                res.isBarmen = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedTotal = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                res.isAdmin = resultSet.getBoolean(9);
                res.gold = resultSet.getInt(10);
                res.fightTime = resultSet.getTimestamp(11);
                res.location = Location.valueOf(resultSet.getString(12));
                try {
                    res.food = Food.valueOf(resultSet.getString(13));
                } catch (Exception ignored) {
                }
                try {
                    res.wantedFood = Food.valueOf(resultSet.getString(14));
                } catch (Exception ignored) {
                }
                res.foodCount = resultSet.getInt(15);
                res.eatTotal = resultSet.getInt(16);
                res.fightClubWins = resultSet.getInt(17);
                res.brewCount = resultSet.getInt(18);
                res.lastEatTime = resultSet.getTimestamp(19);
                res.drinkedWeek = resultSet.getInt(20);
                int fightWithUserID = resultSet.getInt(21);
                if (!resultSet.wasNull()) {
                    res.fightWithUserID = fightWithUserID;
                }
                res.curseTime = resultSet.getTimestamp(22);
                res.voteFor = resultSet.getString(23);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static User getFromMessage(Message message) {
        return getFromMessage(message.getFrom());
    }

    public static User getFromMessage(org.telegram.telegrambots.api.objects.User user) {
        int userID = user.getId();
        User res = getByID(userID);
        if (res == null) {
            res = new User();
            res.nick = user.getUserName();
            res.name = user.getFirstName();
            res.userID = userID;
            res.alkoCount = 0;
            res.isBarmen = false;
            res.drinkedTotal = 0;
            res.isAdmin = false;
            res.gold = 30;
            res.fightTime = null;
            res.curseTime = null;
            res.location = Location.TAVERN;
            res.foodCount = 0;
            res.eatTotal = 0;
            res.fightClubWins = 0;
            res.brewCount = 0;
            res.drinkedWeek = 0;
            res.voteFor = null;
            res.save();
        } else {
            res.nick = user.getUserName();
            res.name = user.getFirstName();
            res.save();
        }
        return res;
    }

    public static User getByNick(String nick) {
        User res = null;
        nick = nick.replace("@", "");
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID, name, isBarmen, alkoCount, lastDrinkTime" +
                    ", drinkedTotal, drinkType, wanted, isAdmin, gold" +
                    ", fightTime, location, food, wantedFood, foodCount" +
                    ", eatTotal, fightClubWins, brewCount, lastEatTime, drinkedWeek" +
                    ", fightWithUserID, curseTime, voteFor" +
                    " from cwt_User where nick = ?");
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.nick = nick;
                res.userID = resultSet.getInt(1);
                res.name = resultSet.getString(2);
                res.isBarmen = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedTotal = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                res.isAdmin = resultSet.getBoolean(9);
                res.gold = resultSet.getInt(10);
                res.fightTime = resultSet.getTimestamp(11);
                res.location = Location.valueOf(resultSet.getString(12));
                try {
                    res.food = Food.valueOf(resultSet.getString(13));
                } catch (Exception ignored) {
                }
                try {
                    res.wantedFood = Food.valueOf(resultSet.getString(14));
                } catch (Exception ignored) {
                }
                res.foodCount = resultSet.getInt(15);
                res.eatTotal = resultSet.getInt(16);
                res.fightClubWins = resultSet.getInt(17);
                res.brewCount = resultSet.getInt(18);
                res.lastEatTime = resultSet.getTimestamp(19);
                res.drinkedWeek = resultSet.getInt(20);
                int fightWithUserID = resultSet.getInt(21);
                if (!resultSet.wasNull()) {
                    res.fightWithUserID = fightWithUserID;
                }
                res.curseTime = resultSet.getTimestamp(22);
                res.voteFor = resultSet.getString(23);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<User> getAll() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "Select userID, nick, name, isBarmen, alkoCount" +
                            ", lastDrinkTime, drinkedTotal, drinkType, wanted, isAdmin" +
                            ", gold, fightTime, location, food, wantedFood" +
                            ", foodCount, eatTotal, fightClubWins, brewCount, lastEatTime" +
                            ", drinkedWeek, fightWithUserID, curseTime, voteFor from cwt_User");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.userID = resultSet.getInt(1);
                user.nick = resultSet.getString(2);
                user.name = resultSet.getString(3);
                user.isBarmen = resultSet.getBoolean(4);
                user.alkoCount = resultSet.getInt(5);
                user.lastDrinkTime = resultSet.getTimestamp(6);
                user.drinkedTotal = resultSet.getInt(7);
                try {
                    user.drinkType = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                try {
                    user.wanted = DrinkType.valueOf(resultSet.getString(9));
                } catch (Exception ignored) {
                }
                user.isAdmin = resultSet.getBoolean(10);
                user.gold = resultSet.getInt(11);
                user.fightTime = resultSet.getTimestamp(12);
                user.location = Location.valueOf(resultSet.getString(13));
                try {
                    user.food = Food.valueOf(resultSet.getString(14));
                } catch (Exception ignored) {
                }
                try {
                    user.wantedFood = Food.valueOf(resultSet.getString(15));
                } catch (Exception ignored) {
                }
                user.foodCount = resultSet.getInt(16);
                user.eatTotal = resultSet.getInt(17);
                user.fightClubWins = resultSet.getInt(18);
                user.brewCount = resultSet.getInt(19);
                user.lastEatTime = resultSet.getTimestamp(20);
                user.drinkedWeek = resultSet.getInt(21);
                int fightWithUserID = resultSet.getInt(22);
                if (!resultSet.wasNull()) {
                    user.fightWithUserID = fightWithUserID;
                }
                user.fightTime = resultSet.getTimestamp(23);
                user.voteFor = resultSet.getString(24);
                res.add(user);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean save() {
        boolean res = false;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = resultSet.next();
            if (exists) {
                preparedStatement = connectionDB.getPreparedStatement(
                        "update cwt_User set nick = ?, name = ?, isBarmen = ?, alkoCount = ?, lastDrinkTime = ?" +
                                ", drinkedTotal = ?, drinkType = ?, wanted = ?, isAdmin = ?, gold = ?" +
                                ", fightTime = ?, location = ?, food = ?, wantedFood = ?, foodCount = ?" +
                                ", eatTotal = ?, fightClubWins = ?, brewCount = ?, lastEatTime = ?, drinkedWeek = ?" +
                                ", fightWithUserID = ?, curseTime = ?, voteFor = ? where UserID = ?");
                preparedStatement.setString(1, nick);
                preparedStatement.setString(2, name);
                preparedStatement.setBoolean(3, isBarmen);
                preparedStatement.setInt(4, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(5, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(5, Types.TIMESTAMP);
                }
                preparedStatement.setInt(6, drinkedTotal);
                if (drinkType != null) {
                    preparedStatement.setString(7, drinkType.name());
                } else {
                    preparedStatement.setNull(7, Types.VARCHAR);
                }
                if (wanted != null) {
                    preparedStatement.setString(8, wanted.name());
                } else {
                    preparedStatement.setNull(8, Types.VARCHAR);
                }
                preparedStatement.setBoolean(9, isAdmin);
                preparedStatement.setInt(10, gold);
                if (fightTime != null) {
                    preparedStatement.setTimestamp(11, new Timestamp(fightTime.getTime()));
                } else {
                    preparedStatement.setNull(11, Types.TIMESTAMP);
                }
                preparedStatement.setString(12, location.name());
                if (food != null) {
                    preparedStatement.setString(13, food.name());
                } else {
                    preparedStatement.setNull(13, Types.VARCHAR);
                }
                if (wantedFood != null) {
                    preparedStatement.setString(14, wantedFood.name());
                } else {
                    preparedStatement.setNull(14, Types.VARCHAR);
                }
                preparedStatement.setInt(15, foodCount);
                preparedStatement.setInt(16, eatTotal);
                preparedStatement.setInt(17, fightClubWins);
                preparedStatement.setInt(18, brewCount);
                if (lastEatTime != null) {
                    preparedStatement.setTimestamp(19, new Timestamp(lastEatTime.getTime()));
                } else {
                    preparedStatement.setNull(19, Types.TIMESTAMP);
                }
                preparedStatement.setInt(20, drinkedWeek);
                if (fightWithUserID != null) {
                    preparedStatement.setInt(21, fightWithUserID);
                } else {
                    preparedStatement.setNull(21, Types.INTEGER);
                }
                if (curseTime != null) {
                    preparedStatement.setTimestamp(22, new Timestamp(curseTime.getTime()));
                } else {
                    preparedStatement.setNull(22, Types.TIMESTAMP);
                }
                if (voteFor != null) {
                    preparedStatement.setString(23, voteFor);
                } else {
                    preparedStatement.setNull(23, Types.VARCHAR);
                }
                preparedStatement.setInt(24, userID);
                preparedStatement.execute();
            } else {
                preparedStatement = connectionDB.getPreparedStatement(
                        "INSERT INTO cwt_User (UserID, nick, name, isBarmen, alkoCount" +
                                ", lastDrinkTime, drinkedTotal, drinkType, wanted, isAdmin" +
                                ", gold, fightTime, location, food, wantedFood" +
                                ", foodCount, eatTotal, fightClubWins, brewCount, lastEatTime" +
                                ", drinkedWeek, fightWithUserID, curseTime, voteFor) VALUES" +
                                " (?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?)");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, nick);
                preparedStatement.setString(3, name);
                preparedStatement.setBoolean(4, isBarmen);
                preparedStatement.setInt(5, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(6, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(6, Types.TIMESTAMP);
                }
                preparedStatement.setInt(7, drinkedTotal);
                if (drinkType != null) {
                    preparedStatement.setString(8, drinkType.name());
                } else {
                    preparedStatement.setNull(8, Types.VARCHAR);
                }
                if (wanted != null) {
                    preparedStatement.setString(9, wanted.name());
                } else {
                    preparedStatement.setNull(9, Types.VARCHAR);
                }
                preparedStatement.setBoolean(10, isAdmin);
                preparedStatement.setInt(11, gold);
                if (fightTime != null) {
                    preparedStatement.setTimestamp(12, new Timestamp(fightTime.getTime()));
                } else {
                    preparedStatement.setNull(12, Types.TIMESTAMP);
                }
                preparedStatement.setString(13, location.name());
                if (food != null) {
                    preparedStatement.setString(14, food.name());
                } else {
                    preparedStatement.setNull(14, Types.VARCHAR);
                }
                if (wantedFood != null) {
                    preparedStatement.setString(15, wantedFood.name());
                } else {
                    preparedStatement.setNull(15, Types.VARCHAR);
                }
                preparedStatement.setInt(16, foodCount);
                preparedStatement.setInt(17, eatTotal);
                preparedStatement.setInt(18, fightClubWins);
                preparedStatement.setInt(19, brewCount);
                if (lastEatTime != null) {
                    preparedStatement.setTimestamp(20, new Timestamp(lastEatTime.getTime()));
                } else {
                    preparedStatement.setNull(20, Types.TIMESTAMP);
                }
                preparedStatement.setInt(21, drinkedWeek);
                if (fightWithUserID != null) {
                    preparedStatement.setInt(22, fightWithUserID);
                } else {
                    preparedStatement.setNull(22, Types.INTEGER);
                }
                if (curseTime != null) {
                    preparedStatement.setTimestamp(23, new Timestamp(curseTime.getTime()));
                } else {
                    preparedStatement.setNull(23, Types.TIMESTAMP);
                }
                if (voteFor != null) {
                    preparedStatement.setString(24, voteFor);
                } else {
                    preparedStatement.setNull(24, Types.VARCHAR);
                }
                preparedStatement.execute();
            }
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String toString() {
        if (!StringUtils.isEmpty(nick)) {
            return "@" + nick;
        } else {
            return name;
        }
    }

    public static List<User> getTop() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, drinkedTotal from cwt_User order by drinkedTotal desc limit 12");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.name = resultSet.getString(2);
                user.drinkedTotal = resultSet.getInt(3) / 2;
                res.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<User> getWeekTop() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, drinkedWeek from cwt_User order by drinkedWeek desc limit 12");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.name = resultSet.getString(2);
                user.drinkedWeek = resultSet.getInt(3) / 2;
                res.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<User> getBarmenTop() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, brewCount from cwt_User where brewCount > 0 order by brewCount desc limit 12");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.name = resultSet.getString(2);
                user.brewCount = resultSet.getInt(3);
                res.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<User> getBkTop() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, fightClubWins from cwt_User order by fightClubWins desc limit 12");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.name = resultSet.getString(2);
                user.fightClubWins = resultSet.getInt(3);
                res.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int getVotersForCount(String vote) {
        int res = 0;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select count(1) from cwt_User where voteFor = ?");
            preparedStatement.setString(1, vote);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void flushVotes() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_User set voteFor = null");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getFightClubStatsSum() {
        List<DrinkPref> prefs = DrinkPref.getByUser(this);
        return getStr(prefs) + getAgi(prefs) + getCon(prefs) + getCha(prefs) + getKno();

    }

    public String getFightClubStats() {
        List<DrinkPref> prefs = DrinkPref.getByUser(this);
        return "Твои характеристики:\n" + Emoji.STR + "Сила: " + getStr(prefs)
                + "\n" + Emoji.AGI + "Ловкость: " + getAgi(prefs) + "\n" + Emoji.CHA + "Обаяние: " + getCha(prefs)
                + "\n" + Emoji.CON + "Стойкость: " + getCon(prefs) + "\n" + Emoji.KNO + "Знание таверны: " + getKno();
    }

    public String getPublicFightClubStats() {
        List<DrinkPref> prefs = DrinkPref.getByUser(this);
        return "Твои характеристики:\n" + Emoji.STR + "Сила: " + roundStatToString(getStr(prefs))
                + "\n" + Emoji.AGI + "Ловкость: " + roundStatToString(getAgi(prefs))
                + "\n" + Emoji.CHA + "Обаяние: " + roundStatToString(getCha(prefs))
                + "\n" + Emoji.CON + "Стойкость: " + roundStatToString(getCon(prefs))
                + "\n" + Emoji.KNO + "Знание таверны: " + roundStatToString(getKno());
    }

    public String roundStatToString(int stat) {
        String res;
        if (stat < 4) {
            res = "чуть меньше чем ничего";
        } else if (stat < 6) {
            res = "тебя превосходят даже голуби";
        } else if (stat < 8) {
            res = "как у ребенка";
        } else if (stat < 10) {
            res = "так мало, что даже стыдно";
        } else if (stat < 13) {
            res = "низко";
        } else if (stat < 17) {
            res = "чуть ниже нормы";
        } else if (stat < 21) {
            res = "нормально";
        } else if (stat < 25) {
            res = "выше среднего";
        } else if (stat < 29) {
            res = "высоко";
        } else if (stat < 34) {
            res = "очень высоко";
        } else if (stat < 38) {
            res = "практически нет равных";
        } else if (stat < 43) {
            res = "нет равных";
        } else if (stat < 48) {
            res = "почти как у бога";
        } else if (stat < 55) {
            res = "почти божественно";
        } else if (stat < 60) {
            res = "божественно";
        } else {
            res = "превосходит богов";
        }
        return res;
    }

    public int getStr(List<DrinkPref> prefs) {
        return 1 + (int) Math.sqrt(prefs.stream()
                    .filter(e -> Arrays.asList(DrinkType.AVE_WHITE, DrinkType.BEER, DrinkType.GHOST)
                            .contains(e.getDrinkType()))
                    .mapToInt(DrinkPref::getToDrinkNormalized).sum());
    }

    public int getAgi(List<DrinkPref> prefs) {
        return 1 + (int) Math.sqrt(prefs.stream().mapToInt(DrinkPref::getToThrow).sum());
    }

    public int getCha(List<DrinkPref> prefs) {
        int charism = 1;
        charism += (int) Math.sqrt(prefs.stream()
                .filter(e -> Arrays.asList(DrinkType.CHLEN, DrinkType.RED_POWER, DrinkType.MORDOR)
                        .contains(e.getDrinkType()))
                .mapToInt(DrinkPref::getToDrinkNormalized).sum());
        return charism;
    }

    public int getCon(List<DrinkPref> prefs) {
        int constitution = 1;
        constitution += (int) Math.sqrt(prefs.stream().mapToInt(e -> {
            int res = e.getToBeThrown();
            if (e.getDrinkType() == DrinkType.MANDARINE) {
                res += e.getToDrinkNormalized();
            }
            return res;
        }).sum());
        return constitution;
    }

    public int getKno() {
        return (int) Math.sqrt(this.getDrinkedTotal()) / 2;
    }

    void incFightClubWins() {
        fightClubWins++;
    }

    public void incBrewCount() {
        brewCount++;
    }

    public void incGold() {
        gold++;
    }

    public User getFightWithUser() {
        return User.getByID(fightWithUserID);
    }

    public void setFightWithUser(User fightWithUser) {
        if (fightWithUser == null) {
            fightWithUserID = null;
        } else {
            fightWithUserID = fightWithUser.getUserID();
        }
    }

    public void incDrink(DrinkType dt, Integer count) {
        DrinkPref pref = DrinkPref.getByUserAndDrinkType(this, dt);
        if (pref != null) {
            pref.incToDrink(count);
            pref.save();
        }
    }

    public void incThrow(DrinkType dt) {
        DrinkPref pref = DrinkPref.getByUserAndDrinkType(this, dt);
        if (pref != null) {
            pref.incToThrow();
            pref.save();
        }
    }

    public void incToBeThrown(DrinkType dt) {
        DrinkPref pref = DrinkPref.getByUserAndDrinkType(this, dt);
        if (pref != null) {
            pref.incToBeThrown();
            pref.save();
        }
    }

    public boolean inTavern() {
        return location == Location.TAVERN;
    }

    public boolean onQuest() {
        return location == Location.QUEST;
    }
}

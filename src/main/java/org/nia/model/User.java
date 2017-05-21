package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.logic.lists.DrinkType;
import org.nia.logic.lists.Food;
import org.nia.logic.lists.Location;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import javax.persistence.*;
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
public class User extends AbstractEntity {
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

    public static User getFromMessage(Message message) {
        return getFromMessage(message.getFrom());
    }

    public static User getFromMessage(org.telegram.telegrambots.api.objects.User user) {
        int userID = user.getId();
        User res = getByID(User.class, userID);
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
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE nick = " + nick, User.class);
            List<User> list = query.list();
            if (!list.isEmpty()) {
                res = list.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static List<User> getAll() {
        List<User> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
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
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User order by drinkedTotal desc", User.class);
            query.setMaxResults(12);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static List<User> getWeekTop() {
        List<User> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User order by drinkedWeek desc", User.class);
            query.setMaxResults(12);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static List<User> getBarmenTop() {
        List<User> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User order by brewCount desc", User.class);
            query.setMaxResults(12);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static List<User> getBkTop() {
        List<User> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<User> query = session.createQuery("FROM User order by fightClubWins desc", User.class);
            query.setMaxResults(12);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static int getVotersForCount(String vote) {
        int res = 0;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) FROM User where voteFor = " + vote, Long.class);
            query.setMaxResults(12);
            res = query.uniqueResult().intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    static void flushVotes() {
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("update User set voteFor = null");
            query.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private String roundStatToString(int stat) {
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
        return User.getByID(User.class, fightWithUserID);
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

    public int getDrinkedTotalNormalized() {
        return drinkedTotal / 2;
    }
    public int getDrinkedWeekNormalized() {
        return drinkedWeek / 2;
    }
}

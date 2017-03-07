package org.nia.model;

import org.nia.model.lists.BattleBalance;
import org.nia.model.lists.CastleType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author IANazarov
 */
public class Battle {
    private int publicID;
    private Date battleTime;
    private CastleType castleType;
    private Integer goldEarned;
    private BattleBalance battleBalance;
    private Long points;
    private CastleType fortOwner;

    public int getPublicID() {
        return publicID;
    }

    public void setPublicID(int publicID) {
        this.publicID = publicID;
    }

    public Date getBattleTime() {
        return battleTime;
    }

    public void setBattleTime(Date battleDate) {
        this.battleTime = getBattleTime(battleDate);
    }


    public CastleType getCastleType() {
        return castleType;
    }

    public void setCastleType(CastleType castleType) {
        this.castleType = castleType;
    }

    public Integer getGoldEarned() {
        return goldEarned;
    }

    public void setGoldEarned(Integer goldEarned) {
        this.goldEarned = goldEarned;
    }

    public BattleBalance getBattleBalance() {
        return battleBalance;
    }

    public void setBattleBalance(BattleBalance battleBalance) {
        this.battleBalance = battleBalance;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public CastleType getFortOwner() {
        return fortOwner;
    }

    public void setFortOwner(CastleType fortOwner) {
        this.fortOwner = fortOwner;
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

    @Override
    public String toString() {
        return "Battle{" +
                "battleTime=" + battleTime +
                ", castleType=" + castleType +
                ", goldEarned=" + goldEarned +
                ", battleBalance=" + battleBalance +
                ", points=" + points +
                ", fortOwner=" + fortOwner +
                '}';
    }
}

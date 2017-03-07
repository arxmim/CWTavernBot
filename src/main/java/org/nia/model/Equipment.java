package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author IANazarov
 */
public class Equipment {
    private Profile profile;
    private String name;
    private int atk;
    private int def;

    public Equipment(Profile profile, String name, int atk, int def) {
        this.profile = profile;
        this.name = name;
        this.atk = atk;
        this.def = def;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Equipment && (obj == this || name.equals(((Equipment) obj).name));
    }

    void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwo_Equipment (ProfileName, Name, atk, def) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, profile.getName());
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, atk);
            preparedStatement.setInt(4, def);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

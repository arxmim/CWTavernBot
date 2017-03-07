package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.model.lists.Resource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Иван, 27.02.2017.
 */
public class Donator {
    private Integer publicID;
    private String name;
    private String resource;
    private int count;
    private Integer total;

    public Donator(String name, String resource, int count) {
        this.name = name;
        this.resource = resource;
        this.count = count;
    }

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select publicID, count from cwo_Donator where Name = ? and Resource = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, resource);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int publicID = resultSet.getInt(1);
                int count = resultSet.getInt(2);
                int total = this.count + count;
                if (total == 0) {
                    preparedStatement = connectionDB.getPreparedStatement("delete from cwo_Donator where publicID = ?");
                    preparedStatement.setInt(1, publicID);
                    preparedStatement.execute();
                } else {
                    preparedStatement = connectionDB.getPreparedStatement("update cwo_Donator set count = ? where publicID = ?");
                    preparedStatement.setInt(1, total);
                    preparedStatement.setInt(2, publicID);
                    preparedStatement.execute();
                }
            } else {
                preparedStatement = connectionDB.getPreparedStatement("insert into cwo_Donator (Name, Resource, Count) VALUES (?, ?, ?)");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, resource);
                preparedStatement.setInt(3, count);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getTotal() {
        if (total == null) {
            total = 0;
        }
        return total;
    }

    public static HashMap<Donator, Integer> getTop() {
        HashMap<Donator, Integer> donators = new HashMap<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select Name, Resource, Count from cwo_Donator");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String donatorName = resultSet.getString(1);
                Resource res = Resource.getByName(resultSet.getString(2));
                int cnt = resultSet.getInt(3);
                Donator donator = new Donator(donatorName, res.getName(), cnt);
                Integer total = donators.get(donator);
                if (total == null) {
                    total = 0;
                }
                total += res.getPrice() * cnt;
                donators.put(donator, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        donators = donators.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        return donators;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Donator && (obj == this || name.equals(((Donator) obj).name));
    }

    public String getResource() {
        return resource;
    }

    public int getCount() {
        return count;
    }

    public static List<Donator> byName(String name) {
        ArrayList<Donator> donators = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("select Resource, count from cwo_Donator where Name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String resourceName = resultSet.getString(1);
                int count = resultSet.getInt(2);
                Donator donator = new Donator(name, resourceName, count);
                donators.add(donator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donators;
    }
}

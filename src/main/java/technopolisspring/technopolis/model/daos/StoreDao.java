package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Store;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class StoreDao extends Dao {

    protected StoreDao() throws SQLException {
    }

    public Store getStoreById(int id) throws SQLException {
        String sql = "SELECT name, address, work_hours, phone, free_parking, location_coordinates\n" +
                "FROM stores\n" +
                "WHERE id = " + id + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            Store store = new Store(id,
                    result.getString("name"),
                    result.getString("address"),
                    result.getString("work_hours"),
                    result.getString("phone"),
                    result.getBoolean("free_parking"),
                    result.getString("location_coordinates")
            );
            return store;
        }
    }

    public Map<Integer, Store> getAllStores() throws SQLException {
        String sql = "SELECT id, name, address, work_hours, phone, free_parking, location_coordinates\n" +
                "FROM stores;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            Map<Integer, Store> stores = new HashMap<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Store store = new Store(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("address"),
                        result.getString("work_hours"),
                        result.getString("phone"),
                        result.getBoolean("free_parking"),
                        result.getString("location_coordinates")
                );
                stores.put(store.getId(), store);
            }
            return stores;
        }
    }

}

package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CategoryDao extends Dao {

    public Map<Long, String> getAllCategories(int pageNumber) throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.categories\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * PAGE_SIZE);
            statement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            Map<Long, String> categories = new HashMap<>();
            while (result.next()) {
                String name = result.getString("name");
                long id = result.getLong("id");
                categories.put(id, name);
            }
            return categories;
        }
    }

    public Map<Long, String> getSubCategories(long categoryId, int pageNumber) throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.sub_categories\n" +
                "WHERE category_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            Map<Long, String> subCategories = new HashMap<>();
            while (result.next()) {
                String name = result.getString("name");
                long id = result.getLong("id");
                subCategories.put(id, name);
            }
            return subCategories;
        }
    }

    public Map<Long, String> getAllBrands(int pageNumber) throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.brands\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * PAGE_SIZE);
            statement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            Map<Long, String> brands = new HashMap<>();
            while (result.next()) {
                String name = result.getString("name");
                long id = result.getLong("id");
                brands.put(id, name);
            }
            return brands;
        }
    }

}

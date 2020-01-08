package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<Long, String> getAllCategories() throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.categories;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
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

    public Map<Long, String> getSubCategories(long categoryId) throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.sub_categories\n" +
                "WHERE category_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);
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

    public Map<Long, String> getAllBrands() throws SQLException {
        String sql = "SELECT id, name\n" +
                "FROM technopolis.brands;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
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

    public List<Product> getProductsByBrand(long brandId) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id\n" +
                "FROM `technopolis`.products AS p\n" +
                "WHERE p.brand_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, brandId);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
                        result.getLong("p.sub_category_id")
                );
                products.add(product);
            }
            return products;
        }
    }

}

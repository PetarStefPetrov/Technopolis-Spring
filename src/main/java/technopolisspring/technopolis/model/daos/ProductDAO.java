package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;

@Component
public class ProductDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id\n" +
                "FROM `technopolis`.products AS p\n" +
                "WHERE p.id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            Product product = new Product(
                    result.getLong("p.id"),
                    result.getString("p.description"),
                    result.getDouble("p.price"),
                    result.getString("p.picture_url"),
                    result.getLong("p.brand_id"),
                    result.getLong("p.sub_category_id")
            );
            return product;
        }
    }

    public Product addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`products` " +
                "(`id`, `description`, `price`, `picture_url`, `brand_id`, `sub_category_id`) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, product.getId());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getPictureUrl());
            statement.setLong(5, product.getBrandId());
            statement.setLong(6, product.getSubCategoryId());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(!resultSet.next()){
                return null;
            }
            product.setId(resultSet.getInt(1));
            return product;
        }
    }

    public Product deleteProduct(Product product) throws SQLException {
        String sql = "DELETE FROM `technopolis`.products WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, product.getId());
            statement.execute();
        }
        return product;
    }

}

package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, b.name, p.sub_category_id\n" +
                "FROM products AS p\n" +
                "JOIN brands AS b ON brand_id = b.id\n" +
                "WHERE p.id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            Product product = new Product(
                    result.getLong("p.id"),
                    result.getString("p.description"),
                    result.getDouble("p.price"),
                    result.getString("p.picture_url"),
                    result.getString("b.name"),
                    result.getLong("p.sub_category_id")
            );
            return product;
        }
    }

}

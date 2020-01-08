package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProductDAO productDAO;

    public Order addOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO `technopolis`.`orders` " +
                "(`user_id`, `address`, `price`) " +
                "VALUES (?, ?, ?);";
        String orderProductsSql = "INSERT INTO `technopolis`.`orders_have_products` " +
                "(`product_id`, `order_id`, `quantity`) " +
                "VALUES (?, ?, ?);";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement orderStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderProductsStatement = connection.prepareStatement(orderProductsSql)) {
            connection.setAutoCommit(false);
            orderStatement.setLong(1, order.getUserId());
            orderStatement.setString(2, order.getAddress());
            orderStatement.setDouble(3, order.getPrice());
            orderStatement.execute();
            ResultSet resultSet = orderStatement.getGeneratedKeys();
            resultSet.next();
            order.setId(resultSet.getInt(1));
            for (Map.Entry<Product, Integer> entry : order.getProducts().entrySet()) {
                orderProductsStatement.setLong(1, entry.getKey().getId());
                orderProductsStatement.setLong(2, order.getId());
                orderProductsStatement.setInt(3, entry.getValue());
                orderProductsStatement.execute();
            }
            connection.commit();
            return order;
        } catch (SQLException e) {
            connection.setAutoCommit(true);
            connection.rollback();
            throw e;
        }
        finally {
            connection.close();
        }
    }

    public Order getOrderById(long orderId) throws SQLException {
        String sql = "SELECT id, user_id, address, price\n" +
                "FROM technopolis.orders\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            Order order = new Order(
                    result.getLong("id"),
                    result.getLong("user_id"),
                    result.getString("address"),
                    getOrderProducts(orderId)
            );
            return order;
        }
    }

    public Map<Product, Integer> getOrderProducts(long orderId) throws SQLException {
        String sql = "SELECT product_id, order_id, quantity\n" +
                "FROM technopolis.orders_have_products\n" +
                "WHERE order_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            ResultSet result = statement.executeQuery();
            Map<Product, Integer> products = new HashMap<>();
            while (result.next()){
                products.put(productDAO.getProductById(result.getLong("product_id")),
                                                       result.getInt("quantity"));
            }
            return products;
        }
    }

}

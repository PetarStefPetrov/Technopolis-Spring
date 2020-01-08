package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password, phone, address)\n" +
                "VALUES (?,?,?,?,?);";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getLong("id"));
            statement.execute();
            return user;
        }
    }


    public User deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            statement.execute();
        }
        return user;
    }

    public User editUser(User user) throws SQLException {
        String sql = "UPDATE users\n" +
                "SET \n" +
                "first_name = ?,\n" +
                "last_name = ?,\n" +
                "email = ?,\n" +
                "phone = ?\n" +
                "WHERE id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setLong(5, user.getId());
            statement.executeUpdate();
        }
        return user;
    }


    public List<Review> getReviews(long userId) throws SQLException {
        String sql = "SELECT r.id, r.name, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id,\n" +
                "u.id, u.first_name, u.last_name, u.email, u.password, u.phone, u.create_time, u.address, u.is_admin\n" +
                "FROM reviews AS r\n" +
                "JOIN products AS p\n" +
                "JOIN users AS u\n" +
                "WHERE r.user_id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            List<Review> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getString("p.brand_id"),
                        result.getLong("p.sub_category_id")
                );
                User user = new User(
                        result.getLong("u.id"),
                        result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.email"),
                        result.getString("u.password"),
                        result.getString("u.phone"),
                        result.getTimestamp("u.create_time").toLocalDateTime(),
                        result.getString("u.address"),
                        result.getBoolean("u.is_admin")
                );
                Review review = new Review(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getString("title"),
                        result.getString("comment"),
                        product,
                        user
                );
                reviews.add(review);
            }
            return reviews;
        }
    }

    public List<Product> getFavourites(long userId) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, b.name, p.sub_category_id\n" +
                "FROM products AS p\n" +
                "JOIN users_like_products AS ulp ON p.id = ulp.product_id\n" +
                "JOIN brands AS b ON p.brand_id = b.id\n" +
                "WHERE ulp.user_id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            List<Product> favorites = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Product product = new Product(
                        result.getInt("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getString("b.name"),
                        result.getInt("p.sub_category_id")
                );
                favorites.add(product);
            }
            return favorites;
        }
    }
    public List<Order> getOrders(long userId) throws SQLException {
        String sql = "SELECT id, address, price\n" +
                "FROM orders \n" +
                "WHERE user_id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            List<Order> orders = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Order order = new Order(
                        result.getInt("id"),
                        userId,
                        result.getString("address"),
                        result.getDouble("price")
                );
                orders.add(order);
            }
            return orders;
        }
    }

}
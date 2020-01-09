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
        String sql = "INSERT INTO `technopolis`.users (first_name, last_name, email, password, phone, address,is_admin)\n" +
                "VALUES (?,?,?,?,?,?,?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            statement.setString(6,user.getAddress());
            statement.setBoolean(7,false);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getInt(1));

            return user;
        }
    }


    public User deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM `technopolis`.users WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            statement.execute();
        }
        return user;
    }

    public User editUser(User user) throws SQLException {
        String sql = "UPDATE `technopolis`.users\n" +
                "SET \n" +
                "first_name = ?,\n" +
                "last_name = ?,\n" +
                "email = ?,\n" +
                "phone = ?\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
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
                "FROM `technopolis`.reviews AS r\n" +
                "JOIN `technopolis`.products AS p ON r.product_id = p.id\n" +
                "JOIN `technopolis`.users AS u ON r.user_id = u.id\n" +
                "WHERE r.user_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            List<Review> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
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
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id\n" +
                "FROM `technopolis`.products AS p\n" +
                "JOIN `technopolis`.users_like_products AS ulp ON p.id = ulp.product_id\n" +
                "WHERE ulp.user_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            List<Product> favorites = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Product product = new Product(
                        result.getInt("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
                        result.getInt("p.sub_category_id")
                );
                favorites.add(product);
            }
            return favorites;
        }
    }

    public void addToFavorites(long productId, long userId) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`users_like_products` (`product_id`, `user_id`) VALUES (?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            statement.execute();
        }
    }

    public boolean removeFromFavorites(long productId, long userId) throws SQLException {
        String sql = "DELETE FROM `technopolis`.`users_like_products` " +
                "WHERE (`product_id` = ?) and (`user_id` = ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            if (statement.executeUpdate() == 0){
                return false;
            }
            return true;
        }
    }

    public boolean isAdmin(long userId) throws SQLException {
        String sql = "SELECT is_admin FROM `technopolis`.users WHERE id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getBoolean("is_admin");
        }
    }

    public List<Order> getOrders(long userId) throws SQLException {
        String sql = "SELECT id, address, price\n" +
                "FROM `technopolis`.orders \n" +
                "WHERE user_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
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

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, password, phone, create_time, address, is_admin\n" +
                "FROM `technopolis`.users\n" +
                "WHERE email = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            return new User(
                    result.getLong("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getString("password"),
                    result.getString("phone"),
                    result.getTimestamp("create_time").toLocalDateTime(),
                    result.getString("address"),
                    result.getBoolean("is_admin")
            );
        }
    }

    public User getUserById(long id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, password, phone, create_time, address, is_admin\n" +
                "FROM `technopolis`.users\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            return new User(
                    result.getLong("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getString("password"),
                    result.getString("phone"),
                    result.getTimestamp("create_time").toLocalDateTime(),
                    result.getString("address"),
                    result.getBoolean("is_admin")
            );
        }
    }

    public List<User> getAll() {
        String sql = "SELECT * FROM `technopolis`.users;";
        List<User> users = jdbcTemplate.query(sql, (result, i) -> new User(
                result.getLong("id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("password"),
                result.getString("phone"),
                result.getTimestamp("create_time").toLocalDateTime(),
                result.getString("address"),
                result.getBoolean("is_admin")
        ));
        return users;
    }

    public void makeAdmin(String email) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` SET `is_admin` = 1 WHERE `email` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.execute();
        }
    }

    public void removeAdmin(String email) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` SET `is_admin` = 0 WHERE `email` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.execute();
        }
    }

}

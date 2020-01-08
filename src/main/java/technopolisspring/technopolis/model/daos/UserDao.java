package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDao extends Dao {

    protected UserDao() throws SQLException {
    }

    public User getUserById(long id) throws SQLException {
        String sql = "SELECT first_name, last_name, email, password, phone, create_time, address\n" +
                "FROM users\n" +
                "WHERE id = " + id + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)){
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                User user = new User(id,
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getString("phone"),
                        result.getTimestamp("create_time").toLocalDateTime(),
                        result.getString("address"));
                return user;
            } else {
                return null;
            }
        }
    }

    public List<Review> getReviews(long userId) throws SQLException {
        String sql = "SELECT id, name, title, comment, product_id\n" +
                "FROM reviews\n" +
                "WHERE user_id = " + userId + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            List<Review> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Review review = new Review(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("title"),
                        result.getString("comment"),
                        result.getInt("product_id"),
                        userId
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
                "WHERE ulp.user_id = " + userId + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
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
                "FROM oredrs\n" +
                "WHERE user_id = " + userId + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            List<Order> orders = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
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

    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password, phone, address)\n" +
                "VALUES (?,?,?,?,?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            int changes = statement.executeUpdate();
            if (changes == 0){
                // throw new no changes exception
            }
        }
    }

    public void addAddress(int id, String address) throws SQLException {
        String sql = "UPDATE users\n" +
                "SET \n" +
                "address = '" + address + "'\n" +
                "WHERE id = " + id + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = " + id;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    public void editUser(int id,
                         String newFirstName,
                         String newLastName,
                         String newEmail,
                         String newPhone) throws SQLException {
        String sql = "UPDATE users\n" +
                "SET \n" +
                "first_name = '" + newFirstName + "',\n" +
                "last_name = '" + newLastName + "',\n" +
                "email = '" + newEmail + "',\n" +
                "phone = '" + newPhone + "'\n" +
                "WHERE id = " + id + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

}

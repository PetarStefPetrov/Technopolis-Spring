package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDao extends Dao  {

    protected UserDao() throws SQLException {
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

}

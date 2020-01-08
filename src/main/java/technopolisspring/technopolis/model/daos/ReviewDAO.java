package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
@Component
public class ReviewDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Review addReview(Review review, Product product, User user) throws SQLException {
        String sql = "INSERT INTO `technopolis`.reviews (name, title, comment, product_id, user_id)\n" +
                "VALUES (?,?,?,?,?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, review.getName());
            statement.setString(2, review.getTitle());
            statement.setString(3, review.getComment());
            statement.setLong(4, product.getId());
            statement.setLong(5, user.getId());
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            review.setId(resultSet.getLong("id"));
            return review;
        }
    }

    public void editReview(Review review) throws SQLException {
        String sql = "UPDATE `technopolis`.reviews\n" +
                "SET \n" +
                "name = ?,\n" +
                "title = ?,\n" +
                "comment = ?\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, review.getName());
            statement.setString(2, review.getTitle());
            statement.setString(3, review.getComment());
            statement.setLong(4, review.getId());
            statement.executeUpdate();
        }
    }

    public void deleteReview(int id) throws SQLException {
        String sql = "DELETE FROM `technopolis`.reviews WHERE id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
        }
    }
}


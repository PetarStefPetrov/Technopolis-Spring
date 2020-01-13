package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.ProductWithoutReviewsDto;
import technopolisspring.technopolis.model.dto.ReviewOfUserDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDao extends Dao {

    public Review addReview(Review review, long productId, UserWithoutPasswordDto user) throws SQLException {
        String sql = "INSERT INTO `technopolis`.reviews (title, comment, product_id, user_id)\n" +
                "VALUES (?,?,?,?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, review.getTitle());
            statement.setString(2, review.getComment());
            statement.setLong(3, productId);
            statement.setLong(4, user.getId());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            review.setId(resultSet.getInt(1));
            return review;
        }
    }

    public void editReview(Review review) throws SQLException {
        String sql = "UPDATE `technopolis`.reviews\n" +
                "SET \n" +
                "title = ?,\n" +
                "comment = ?\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, review.getTitle());
            statement.setString(2, review.getComment());
            statement.setLong(3, review.getId());
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

    public List<ReviewOfUserDto> getReviewsOfUser(long userId, int pageNumber) throws SQLException {
        String sql = "SELECT r.id, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id, p.offer_id\n" +
                "FROM `technopolis`.reviews AS r\n" +
                "JOIN `technopolis`.products AS p ON r.product_id = p.id\n" +
                "WHERE p.is_deleted = 0 AND r.user_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            List<ReviewOfUserDto> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                ProductWithoutReviewsDto product = new ProductWithoutReviewsDto(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
                        result.getLong("p.sub_category_id"),
                        result.getLong("p.offer_id")
                );
                ReviewOfUserDto review = new ReviewOfUserDto(
                        result.getLong("id"),
                        result.getString("title"),
                        result.getString("comment"),
                        product
                );
                reviews.add(review);
            }
            return reviews;
        }
    }

}


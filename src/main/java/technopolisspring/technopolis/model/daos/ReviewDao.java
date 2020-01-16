package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.EditReviewDto;
import technopolisspring.technopolis.model.dto.ReviewOfUserDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDao extends Dao {

    public void addReview(Review review, long productId, UserWithoutPasswordDto user) throws SQLException {
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
            review.setId(resultSet.getInt(1));
        }
    }

    public boolean editReview(EditReviewDto review) throws SQLException {
        String sql = "UPDATE `technopolis`.reviews\n" +
                "SET \n" +
                "title = ?,\n" +
                "comment = ?\n" +
                "WHERE id = ? AND user_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, review.getTitle());
            statement.setString(2, review.getComment());
            statement.setLong(3, review.getReviewId());
            statement.setLong(4, review.getUserId());
            return statement.executeUpdate() != 0;
        }
    }

    public void deleteReview(long reviewId) throws SQLException {
        String sql = "DELETE FROM `technopolis`.reviews WHERE id = ? AND user_id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reviewId);
            statement.executeUpdate();
        }
    }

    public List<ReviewOfUserDto> getReviewsOfUser(long userId, int pageNumber) throws SQLException {
        String sql = "SELECT r.id, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.brand_id, p.sub_category_id, p.offer_id\n" +
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
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
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

    public Review getReviewById(long reviewId) throws SQLException {
        String sql = "SELECT title, comment, product_id, user_id\n" +
                "FROM technopolis.reviews\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reviewId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()){
                return null;
            }
            return new Review(
                    reviewId,
                    resultSet.getString("title"),
                    resultSet.getString("comment"),
                    resultSet.getLong("product_id"),
                    resultSet.getLong("user_id")
            );
        }
    }

    public List<Review> getReviewsOfProduct(long productId, int pageNumber) {
        String sql = "SELECT id, title, comment, product_id, user_id\n" +
                "FROM technopolis.reviews\n" +
                "WHERE product_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql, ps -> {
            ps.setLong(1, productId);
            ps.setInt(2, pageNumber * PAGE_SIZE);
            ps.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
        }, (resultSet, i) -> new Review(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("comment"),
                resultSet.getLong("product_id"),
                resultSet.getLong("user_id")
        ));
    }

}


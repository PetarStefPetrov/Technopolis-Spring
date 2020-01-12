package technopolisspring.technopolis.model.daos;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.CreateOfferDto;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.*;
import java.util.List;

@Component
public class OfferDao extends Dao {

    public void addOffer(CreateOfferDto offer) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`offers` " +
                "(`name`, `discount_percent`, `start_date`, `end_date`) " +
                "VALUES (?, ?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, offer.getName());
            statement.setDouble(2, offer.getDiscountPercent());
            statement.setTimestamp(3, Timestamp.valueOf(offer.getStartDate()));
            statement.setTimestamp(4, Timestamp.valueOf(offer.getEndDate()));
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            offer.setId(resultSet.getInt(1));
        }
    }

    public List<Product> getAllProductsInOffers(int pageNumber){
        String sql = "SELECT id, 'description', price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM technopolis.products\n" +
                "WHERE is_deleted = 0 AND offer_id IS NOT NULL\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
            preparedStatement.setInt(1, pageNumber * PAGE_SIZE);
            preparedStatement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
        },
                (result, i) -> new Product(
                result.getLong("id"),
                result.getString("description"),
                result.getDouble("price"),
                result.getString("picture_url"),
                result.getLong("brand_id"),
                result.getLong("sub_category_id"),
                result.getLong("offer_id")
        ));
    }

    public boolean addProductToOffer(long productId, long offerId) throws SQLException {
        String sql = "UPDATE `technopolis`.`products` " +
                "SET `offer_id` = ? " +
                "WHERE is_deleted = 0 AND `id` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, offerId);
            statement.setLong(2, productId);
            return statement.executeUpdate() != 0;
        }
    }

    public double getProductWithDiscountedPrice(long productId) throws SQLException {
        String sql = "SELECT discount_percent, price, offer_id\n" +
                "FROM technopolis.offers AS o\n" +
                "RIGHT JOIN technopolis.products AS p ON o.id = offer_id\n" +
                "WHERE p.id = ?\n" +
                "LIMIT 1";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return 0.0;
            }
            if (resultSet.getLong("offer_id") != 0){
                return -1;
            }
            double price = resultSet.getDouble("price");
            double discountPercent = resultSet.getDouble("discount_percent");
            return price - (price * discountPercent);
        }
    }
}

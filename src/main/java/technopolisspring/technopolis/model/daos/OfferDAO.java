package technopolisspring.technopolis.model.daos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Offer;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.*;
import java.util.List;

@Component
    public class OfferDAO {

        @Autowired
        JdbcTemplate jdbcTemplate;

        public void addOffer(Offer offer) throws SQLException {
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

        public List<Product> getAllOffers(){
            String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id \n" +
                    "FROM technopolis.products AS p\n" +
                    "JOIN technopolis.offers_have_products AS ohp ON p.id = ohp.product_id\n" +
                    "GROUP BY p.id;";
            List<Product> products = jdbcTemplate.query(sql, (result, i) -> new Product(
                    result.getLong("p.id"),
                    result.getString("p.description"),
                    result.getDouble("p.price"),
                    result.getString("p.picture_url"),
                    result.getLong("p.brand_id"),
                    result.getLong("p.sub_category_id")
            ));
            return products;
        }

        public void addProductToOffer(long productId, long offerId) throws SQLException {
            String sql = "INSERT INTO `technopolis`.`offers_have_products` " +
                    "(`product_id`, `offer_id`) " +
                    "VALUES (?, ?);";
            try (Connection connection = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, productId);
                statement.setLong(1, offerId);
                statement.execute();
            }
        }

    }

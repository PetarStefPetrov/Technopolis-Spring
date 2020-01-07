package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Offer;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OfferDao extends Dao {

    protected OfferDao() throws SQLException {
    }

    private Map<Product, Double> addOfferProducts(int id) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, b.name, p.sub_category_id\n" +
                "FROM products as p\n" +
                "JOIN offers_have_products AS ohp ON p.id = ohp.product_id\n" +
                "JOIN brands AS b ON p.brand_id = b.id\n" +
                "WHERE ohp.offer_id = " + id + ";";
        Map<Product, Double> favorites = new HashMap<>();
        List<Product> products = jdbcTemplate.query(sql, (result, i) -> toProduct(result));
        for (Product product : products) {
            favorites.put(product, product.getPrice());
        }
        return favorites;
    }

    public List<Offer> getAllOffers() throws SQLException {
        String sql = "SELECT id, name, discount_percent, start_date, end_date\n" +
                "FROM offers;";
        List<Offer> offers = jdbcTemplate.query(sql, (resultSet, i) -> toOffer(resultSet));
        return offers;
    }

    private Offer toOffer(ResultSet result) throws SQLException {
        Offer offer = new Offer(
                result.getInt("id"),
                result.getString("name"),
                result.getDouble("discount_percent"),
                result.getTimestamp("start_date").toLocalDateTime(),
                result.getTimestamp("end_date").toLocalDateTime(),
                addOfferProducts(result.getInt("id"))
        );
        return offer;
    }

    private Product toProduct(ResultSet result) throws SQLException {
        Product product = new Product(
                result.getInt("p.id"),
                result.getString("p.description"),
                result.getDouble("p.price"),
                result.getString("p.picture_url"),
                result.getString("b.name"),
                result.getInt("p.sub_category_id")
        );
        return product;
    }

}

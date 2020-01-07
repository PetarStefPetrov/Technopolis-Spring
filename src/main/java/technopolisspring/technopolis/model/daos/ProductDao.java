package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductDao extends Dao {

    protected ProductDao() throws SQLException {
    }

    Product toProduct(ResultSet result) throws SQLException {
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

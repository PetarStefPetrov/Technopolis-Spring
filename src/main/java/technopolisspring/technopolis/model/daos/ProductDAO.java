package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id\n" +
                "FROM `technopolis`.products AS p\n" +
                "WHERE p.id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            Product product = new Product(
                    result.getLong("p.id"),
                    result.getString("p.description"),
                    result.getDouble("p.price"),
                    result.getString("p.picture_url"),
                    result.getLong("p.brand_id"),
                    result.getLong("p.sub_category_id")
            );
            return product;
        }
    }

    public Product addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`products` " +
                "(`id`, `description`, `price`, `picture_url`, `brand_id`, `sub_category_id`) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, product.getId());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getPictureUrl());
            statement.setLong(5, product.getBrandId());
            statement.setLong(6, product.getSubCategoryId());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(!resultSet.next()){
                return null;
            }
            product.setId(resultSet.getInt(1));
            return product;
        }
    }

    public Product deleteProduct(Product product) throws SQLException {
        String sql = "DELETE FROM `technopolis`.products WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, product.getId());
            statement.execute();
        }
        return product;
    }

    public List<Product> getAllProducts(){
        String sql = "SELECT * FROM `technopolis`.products;";
        List<Product> products = jdbcTemplate.query(sql, (result, i) -> new Product(
                result.getLong("id"),
                result.getString("description"),
                result.getDouble("price"),
                result.getString("picture_url"),
                result.getLong("brand_id"),
                result.getLong("sub_category_id")
        ));
        return products;
    }

    public List<Product> getProductsBySubCategory(long subCategoryId) throws SQLException {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id\n" +
                "FROM `technopolis`.products\n" +
                "WHERE sub_category_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, subCategoryId);
            ResultSet result = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                Product product = new Product(
                        result.getLong("id"),
                        result.getString("description"),
                        result.getDouble("price"),
                        result.getString("picture_url"),
                        result.getLong("brand_id"),
                        result.getLong("sub_category_id")
                );
                products.add(product);
            }
            return products;
        }
    }

    public List<Product> getProductsByCategory(long categoryId) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id\n" +
                "FROM `technopolis`.products AS p\n" +
                "JOIN `technopolis`.sub_categories AS sc ON sc.id = p.sub_category_id\n" +
                "JOIN `technopolis`.categories AS c ON c.id = sc.category_id\n" +
                "WHERE c.id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);
            ResultSet result = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
                        result.getLong("p.sub_category_id")
                );
                products.add(product);
            }
            return products;
        }
    }

    public List<Review> getReviews(long productId) throws SQLException {
        String sql = "SELECT r.id, r.name, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id,\n" +
                "u.id, u.first_name, u.last_name, u.email, u.password, u.phone, u.create_time, u.address, u.is_admin\n" +
                "FROM `technopolis`.reviews AS r\n" +
                "JOIN `technopolis`.products AS p ON r.product_id = p.id\n" +
                "JOIN `technopolis`.users AS u ON r.user_id = u.id\n" +
                "WHERE r.product_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            List<Review> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                // for some reason I'm constructing the product for which I'm asked of its reviews
                // todo remove that
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

    public List<Product> lookForProductByDescription(String description) {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id\n" +
                "FROM technopolis.products\n" +
                "WHERE description LIKE ?;";
        List<Product> products = jdbcTemplate.query(sql,
                ps -> ps.setString(1, "%" + description + "%"),
                (result, i) -> new Product(
                result.getLong("id"),
                result.getString("description"),
                result.getDouble("price"),
                result.getString("picture_url"),
                result.getLong("brand_id"),
                result.getLong("sub_category_id")
        ));
        return products;
    }

}

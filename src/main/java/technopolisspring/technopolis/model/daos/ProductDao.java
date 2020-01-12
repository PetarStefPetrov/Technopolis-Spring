package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDao extends Dao {

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM `technopolis`.products\n" +
                "WHERE is_deleted = 0 AND id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            Product product = new Product(
                    result.getLong("id"),
                    result.getString("description"),
                    result.getDouble("price"),
                    result.getString("picture_url"),
                    result.getLong("brand_id"),
                    result.getLong("sub_category_id"),
                    result.getLong("offer_id")
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

    public List<Product> getAllProducts(int pageNumber){
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM `technopolis`.products\n" +
                "WHERE is_deleted = 0\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        List<Product> products = jdbcTemplate.query(sql,
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
        return products;
    }

    public List<Product> getProductsBySubCategory(long subCategoryId, int pageNumber) throws SQLException {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM `technopolis`.products\n" +
                "WHERE is_deleted = 0 AND sub_category_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, subCategoryId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                Product product = new Product(
                        result.getLong("id"),
                        result.getString("description"),
                        result.getDouble("price"),
                        result.getString("picture_url"),
                        result.getLong("brand_id"),
                        result.getLong("sub_category_id"),
                        result.getLong("offer_id")
                );
                products.add(product);
            }
            return products;
        }
    }

    public List<Review> getReviews(long productId, int pageNumber) throws SQLException {
        String sql = "SELECT r.id, r.name, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id, p.offer_id,\n" +
                "u.id, u.first_name, u.last_name, u.email, u.password, u.phone, u.create_time," +
                " u.address, u.is_admin, u.is_subscribed\n" +
                "FROM `technopolis`.reviews AS r\n" +
                "JOIN `technopolis`.products AS p ON r.product_id = p.id\n" +
                "JOIN `technopolis`.users AS u ON r.user_id = u.id\n" +
                "WHERE p.is_deleted = 0 AND u.is_deleted = 0 AND r.product_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            List<Review> reviews = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Product product = new Product(
                        result.getLong("p.id"),
                        result.getString("p.description"),
                        result.getDouble("p.price"),
                        result.getString("p.picture_url"),
                        result.getLong("p.brand_id"),
                        result.getLong("p.sub_category_id"),
                        result.getLong("p.offer_id")
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
                        result.getBoolean("u.is_admin"),
                        result.getBoolean("u.is_subscribed")
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

    public List<Product> lookForProductByDescription(String description, int pageNumber) {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM technopolis.products\n" +
                "WHERE is_deleted = 0 AND description LIKE ?" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        List<Product> products = jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setString(1, "%" + description + "%");
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
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
        return products;
    }

    public List<Product> getProductsWithFilters(FilterForProductsDto filterForProductsDto, int pageNumber) {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM technopolis.products\n" +
                "WHERE is_deleted = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setString(1, filterSql(filterForProductsDto));
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> new Product(
                        result.getLong("id"),
                        result.getString("description"),
                        result.getDouble("price"),
                        result.getString("picture_url"),
                        result.getLong("brand_id"),
                        result.getLong("sub_category_id"),
                        result.getLong("offer_id")
                )
        );
    }

    private String filterSql(FilterForProductsDto filterForProductsDto) {
        StringBuilder filters = new StringBuilder("0");
        double minPrice = filterForProductsDto.getMinPrice();
        double maxPrice = filterForProductsDto.getMaxPrice();
        long subCategoryId = filterForProductsDto.getSubCategoryId();
        long brandId = filterForProductsDto.getBrandId();
        String sorted = filterForProductsDto.getSorted();
        boolean withoutPriceRange = minPrice == 0 && maxPrice == 0;
        if (!withoutPriceRange){
            filters.append(" AND (price BETWEEN ");
            filters.append(minPrice);
            filters.append(" AND ");
            filters.append(maxPrice);
            filters.append(")");
        }
        if (subCategoryId != 0){
            filters.append(" AND sub_category_id = ");
            filters.append(subCategoryId);
        }
        if (brandId != 0){
            filters.append(" AND brand_id = ");
            filters.append(brandId);
        }
        if (sorted != null && !sorted.trim().isEmpty()){
            if (sorted.equalsIgnoreCase("asc") || sorted.equalsIgnoreCase("ascending")){
                filters.append("\n");
                filters.append("ORDER BY price ASC");
                filters.append("\n");
            }
            if (sorted.equalsIgnoreCase("desc") || sorted.equalsIgnoreCase("descending")){
                filters.append("\n");
                filters.append("ORDER BY price DESC");
                filters.append("\n");
            }
        }
        return filters.toString();
    }

    public boolean deleteProduct(long productId) throws SQLException {
        String sql = "UPDATE `technopolis`.`products` SET `is_deleted` = '1' WHERE (`id` = ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            if (statement.executeUpdate() == 0) {
                return false;
            }
            return true;
        }
    }

}

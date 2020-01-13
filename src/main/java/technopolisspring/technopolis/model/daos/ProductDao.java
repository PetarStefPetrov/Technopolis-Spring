package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.dto.ProductWithoutReviewsDto;
import technopolisspring.technopolis.model.dto.ReviewOfProductDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDao extends Dao {

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT r.id, r.title, r.comment,\n" +
                "p.id, p.description, p.price, p.picture_url, p.brand_id, p.sub_category_id, p.offer_id,\n" +
                "u.id, u.first_name, u.last_name, u.email, u.phone, u.create_time," +
                " u.address, u.is_admin, u.is_subscribed\n" +
                "FROM `technopolis`.reviews AS r\n" +
                "JOIN `technopolis`.products AS p ON r.product_id = p.id\n" +
                "JOIN `technopolis`.users AS u ON r.user_id = u.id\n" +
                "WHERE p.is_deleted = 0 AND u.is_deleted = 0 AND r.product_id = ?\n";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            Product product =  new Product(
                    result.getLong("p.id"),
                    result.getString("p.description"),
                    result.getDouble("p.price"),
                    result.getString("p.picture_url"),
                    result.getLong("p.brand_id"),
                    result.getLong("p.sub_category_id"),
                    result.getLong("p.offer_id")
            );
            do{
                ReviewOfProductDto review = new ReviewOfProductDto(
                        result.getLong("r.id"),
                        result.getString("r.title"),
                        result.getString("r.comment"),
                        new UserWithoutPasswordDto(
                                result.getLong("u.id"),
                                result.getString("u.first_name"),
                                result.getString("u.last_name"),
                                result.getString("u.email"),
                                result.getString("u.phone"),
                                result.getTimestamp("u.create_time").toLocalDateTime(),
                                result.getString("u.address"),
                                result.getBoolean("u.is_admin"),
                                result.getBoolean("u.is_subscribed")
                        )
                );
                product.addReview(review);
            } while (result.next());
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

    public List<ProductWithoutReviewsDto> getAllProducts(int pageNumber){
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM `technopolis`.products\n" +
                "WHERE is_deleted = 0\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setInt(1, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> new ProductWithoutReviewsDto(
                        result.getLong("id"),
                        result.getString("description"),
                        result.getDouble("price"),
                        result.getString("picture_url"),
                        result.getLong("brand_id"),
                        result.getLong("sub_category_id"),
                        result.getLong("offer_id")
        ));
    }

    public List<ProductWithoutReviewsDto> getProductsBySubCategory(long subCategoryId, int pageNumber) throws SQLException {
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
            List<ProductWithoutReviewsDto> products = new ArrayList<>();
            while (result.next()) {
                ProductWithoutReviewsDto product = new ProductWithoutReviewsDto(
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

    public List<ProductWithoutReviewsDto> lookForProductsByDescription(String description, int pageNumber) {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM technopolis.products\n" +
                "WHERE is_deleted = 0 AND description LIKE ?" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setString(1, "%" + description + "%");
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> new ProductWithoutReviewsDto(
                        result.getLong("id"),
                        result.getString("description"),
                        result.getDouble("price"),
                        result.getString("picture_url"),
                        result.getLong("brand_id"),
                        result.getLong("sub_category_id"),
                        result.getLong("offer_id")
        ));
    }

    public List<ProductWithoutReviewsDto> getProductsWithFilters(FilterForProductsDto filterForProductsDto, int pageNumber) {
        String sql = "SELECT id, description, price, picture_url, brand_id, sub_category_id, offer_id\n" +
                "FROM technopolis.products\n" +
                "WHERE is_deleted = " + filterSql(filterForProductsDto) + "\n" +
                "ORDER BY " + checkSorting(filterForProductsDto) + "\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
//                    preparedStatement.setString(1, filterSql(filterForProductsDto));
                    preparedStatement.setInt(1, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> new ProductWithoutReviewsDto(
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

    private String checkSorting(FilterForProductsDto filterForProductsDto) {
        String sorted = filterForProductsDto.getSorted();
        String wayOfSorting = "id ASC";
        if (sorted != null && !sorted.trim().isEmpty()){
            if (sorted.equalsIgnoreCase("desc") || sorted.equalsIgnoreCase("descending")){
                wayOfSorting = "price DESC";
            }
            if (sorted.equalsIgnoreCase("asc") || sorted.equalsIgnoreCase("ascending")){
                wayOfSorting = "price ASC";
            }
        }
        return wayOfSorting;
    }

    private String filterSql(FilterForProductsDto filterForProductsDto) {
        StringBuilder filters = new StringBuilder("0");
        double minPrice = filterForProductsDto.getMinPrice();
        double maxPrice = filterForProductsDto.getMaxPrice();
        long subCategoryId = filterForProductsDto.getSubCategoryId();
        long brandId = filterForProductsDto.getBrandId();
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
        return filters.toString();
    }

    public boolean deleteProduct(long productId) throws SQLException {
        String sql = "UPDATE `technopolis`.`products` SET `is_deleted` = '1' WHERE (`id` = ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            return statement.executeUpdate() != 0;
        }
    }

}

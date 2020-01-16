package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.dto.ProductInOfferDto;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDao extends Dao {

    @Autowired
    OfferDao offerDao;

    public IProduct getProductById(long productId) throws SQLException {
        String sql = "SELECT p.id, description, price, picture_url, brand_id, sub_category_id, offer_id, " +
                "discount_percent\n" +
                "FROM `technopolis`.products AS p\n" +
                "LEFT JOIN `technopolis`.offers AS o ON o.id = offer_id\n" +
                "WHERE is_deleted = 0 AND p.id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            return getProductAccordingToOffer(result);
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

    public List<IProduct> getAllProducts(int pageNumber){
        String sql = "SELECT p.id, description, price, picture_url, brand_id, sub_category_id, offer_id, " +
                "discount_percent\n" +
                "FROM `technopolis`.products AS p\n" +
                "LEFT JOIN `technopolis`.offers AS o ON o.id = offer_id\n" +
                "WHERE is_deleted = 0\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setInt(1, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> getProductAccordingToOffer(result));
    }

    public List<IProduct> getProductsBySubCategory(long subCategoryId, int pageNumber) throws SQLException {
        String sql = "SELECT p.id, description, price, picture_url, brand_id, sub_category_id, offer_id," +
                " discount_percent\n" +
                "FROM `technopolis`.products AS p\n" +
                "LEFT JOIN `technopolis`.offers AS o ON o.id = offer_id\n" +
                "WHERE is_deleted = 0 AND sub_category_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, subCategoryId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            List<IProduct> products = new ArrayList<>();
            while (result.next()) {
                IProduct product = getProductAccordingToOffer(result);
                products.add(product);
            }
            return products;
        }
    }

    public List<IProduct> lookForProductsByDescription(String description, int pageNumber) {
        String sql = "SELECT p.id, description, price, picture_url, brand_id, sub_category_id, offer_id," +
                " discount_percent\n" +
                "FROM technopolis.products AS p\n" +
                "LEFT JOIN `technopolis`.offers AS o ON o.id = p.offer_id\n" +
                "WHERE is_deleted = 0 AND description LIKE ?" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setString(1, "%" + description + "%");
                    preparedStatement.setInt(2, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> getProductAccordingToOffer(result));
    }

    public List<IProduct> getProductsWithFilters(FilterForProductsDto filterForProductsDto, int pageNumber) {
        String sql = "SELECT p.id, description, price, picture_url, brand_id, sub_category_id, offer_id," +
                " o.discount_percent\n" +
                "FROM technopolis.products AS p\n" +
                "LEFT JOIN `technopolis`.offers AS o ON o.id = p.offer_id\n" +
                "WHERE is_deleted = 0 AND (price BETWEEN ? AND ?) \n" +
                "ORDER BY " + checkSorting(filterForProductsDto) + "\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
                    preparedStatement.setDouble(1, filterForProductsDto.getMinPrice());
                    preparedStatement.setDouble(2, filterForProductsDto.getMaxPrice());
                    preparedStatement.setInt(3, pageNumber * PAGE_SIZE);
                    preparedStatement.setInt(4, pageNumber * PAGE_SIZE - PAGE_SIZE);
                },
                (result, i) -> getProductAccordingToOffer(result)
        );
    }

    private String checkSorting(FilterForProductsDto filterForProductsDto) { // todo: make two separate gets for those
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
        StringBuilder filters = new StringBuilder();
        long subCategoryId = filterForProductsDto.getSubCategoryId();
        long brandId = filterForProductsDto.getBrandId();
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

    IProduct getProductAccordingToOffer(ResultSet result) throws SQLException {
        long offerId = result.getLong("offer_id");
        IProduct product;
        if (offerId != 0){
            double price = result.getDouble("price");
            double discountPercent = result.getDouble("discount_percent");
            product = new ProductInOfferDto(
                    result.getInt("p.id"),
                    result.getString("description"),
                    price,
                    offerDao.calculateDiscountedPrice(price, discountPercent),
                    result.getString("picture_url"),
                    result.getLong("brand_id"),
                    result.getInt("sub_category_id"),
                    offerId
            );
        }
        else {
            product = new Product(
                    result.getInt("p.id"),
                    result.getString("description"),
                    result.getDouble("price"),
                    result.getString("picture_url"),
                    result.getLong("brand_id"),
                    result.getInt("sub_category_id"),
                    offerId
            );
        }
        return product;
    }

}

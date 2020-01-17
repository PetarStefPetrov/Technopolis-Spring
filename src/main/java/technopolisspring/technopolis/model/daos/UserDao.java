package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.EditUserDto;
import technopolisspring.technopolis.model.dto.OrderWithoutProductsDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDao extends Dao {

    @Autowired
    OrderDao orderDao;
    @Autowired
    OfferDao offerDao;
    @Autowired
    ProductDao productDao;

    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO `technopolis`.users " +
                "(first_name, last_name, email, password, phone, create_time, address, is_admin)\n" +
                "VALUES (?,?,?,?,?,?,?,?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, user.getAddress());
            statement.setBoolean(8,false);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getInt(1));
        }
    }


    public boolean deleteUser(long userID) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` SET `is_deleted` = '1' WHERE (`id` = ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userID);
            return statement.executeUpdate() != 0;
        }
    }

    public void editUser(EditUserDto user) throws SQLException {
        String sql = "UPDATE `technopolis`.users\n" +
                "SET \n" +
                "first_name = ?,\n" +
                "last_name = ?,\n" +
                "email = ?,\n" +
                "phone = ?,\n" +
                "address = ?\n" +
                "WHERE is_deleted = 0 AND id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getAddress());
            statement.setLong(6, user.getId());
            statement.executeUpdate();
        }
    }

    public void changePassword(User user) throws SQLException {
        String sql = "UPDATE `technopolis`.users\n" +
                "SET password = ?\n" +
                "WHERE is_deleted = 0 AND id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getPassword());
            statement.executeUpdate();
        }
    }

    public List<IProduct> getFavourites(long userId, int pageNumber) throws SQLException {
        String sql = "SELECT p.id, p.description, p.price, p.brand_id, " +
                "p.sub_category_id, p.offer_id, o.discount_percent\n" +
                "FROM `technopolis`.products AS p\n" +
                "JOIN `technopolis`.users_like_products AS ulp ON p.id = ulp.product_id\n" +
                "LEFT JOIN `technopolis`.offers AS o ON p.offer_id = o.id\n" +
                "WHERE p.is_deleted = 0 AND ulp.user_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
            List<IProduct> favorites = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()){
                IProduct product = productDao.getProductAccordingToOffer(result);
                favorites.add(product);
            }
            return favorites;
        }
    }

    public void addToFavorites(long productId, long userId) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`users_like_products` (`product_id`, `user_id`) VALUES (?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            statement.execute();
        }
    }

    public boolean removeFromFavorites(long productId, long userId) throws SQLException {
        String sql = "DELETE FROM `technopolis`.`users_like_products` " +
                "WHERE (`product_id` = ?) and (`user_id` = ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            return statement.executeUpdate() != 0;
        }
    }

    public boolean isAdmin(long userId) throws SQLException {
        String sql = "SELECT is_admin FROM `technopolis`.users WHERE is_deleted = 0 AND id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getBoolean("is_admin");
        }
    }

    public List<OrderWithoutProductsDto> getOrders(long userId, int pageNumber) {
        String sql = "SELECT id, address, price\n" +
                "FROM `technopolis`.orders \n" +
                "WHERE user_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql, statement -> {
            statement.setLong(1, userId);
            statement.setInt(2, pageNumber * PAGE_SIZE);
            statement.setInt(3, pageNumber * PAGE_SIZE - PAGE_SIZE);
        },
                (result, i) -> new OrderWithoutProductsDto(
                        result.getLong("id"),
                        userId,
                        result.getString("address"),
                        result.getDouble("price")
                )
        );
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, password, phone," +
                " create_time, address, is_admin, is_subscribed\n" +
                "FROM `technopolis`.users\n" +
                "WHERE is_deleted = 0 AND email = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            return new User(
                    result.getLong("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getString("password"),
                    result.getString("phone"),
                    result.getTimestamp("create_time").toLocalDateTime(),
                    result.getString("address"),
                    result.getBoolean("is_admin"),
                    result.getBoolean("is_subscribed")
            );
        }
    }

    public User getUserById(long id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, password, phone, create_time," +
                " address, is_admin, is_subscribed\n" +
                "FROM `technopolis`.users\n" +
                "WHERE is_deleted = 0 AND id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                return null;
            }
            return new User(
                    result.getLong("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getString("password"),
                    result.getString("phone"),
                    result.getTimestamp("create_time").toLocalDateTime(),
                    result.getString("address"),
                    result.getBoolean("is_admin"),
                    result.getBoolean("is_subscribed")
            );
        }
    }

    public List<User> getAllUsers(int pageNumber) {
        String sql = "SELECT id, first_name, last_name, email, password, phone, create_time," +
                " address, is_admin, is_subscribed\n" +
                "FROM `technopolis`.users\n" +
                "WHERE is_deleted = 0\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        return jdbcTemplate.query(sql,
                preparedStatement -> {
            preparedStatement.setInt(1, pageNumber * PAGE_SIZE);
            preparedStatement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
            },
                (result, i) -> new User(
                result.getLong("id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("password"),
                result.getString("phone"),
                result.getTimestamp("create_time").toLocalDateTime(),
                result.getString("address"),
                result.getBoolean("is_admin"),
                result.getBoolean("is_subscribed")
        ));
    }

    public boolean makeAdmin(long userId) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` " +
                "SET `is_admin` = 1 " +
                "WHERE is_deleted = 0 AND `id` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() != 0;
        }
    }

    public boolean removeAdmin(long userId) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` " +
                "SET `is_admin` = 0 " +
                "WHERE is_deleted = 0 AND `id` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() != 0;
        }
    }

    public void subscribeUser(UserWithoutPasswordDto user) throws SQLException {
        String sql = "UPDATE `technopolis`.`users` " +
                "SET `is_subscribed` = ? " +
                "WHERE is_deleted = 0 AND `id` = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, !user.isSubscribed());
            statement.setLong(2, user.getId());
            statement.execute();
            user.setSubscribed(!user.isSubscribed());
        }
    }

    public List<User> getAllSubscribers(){
        String sql = "SELECT * \n" +
                "FROM technopolis.users\n" +
                "WHERE is_deleted = 0 AND is_subscribed > 0;";
        return jdbcTemplate.query(sql, (result, i) -> new User(
                result.getLong("id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("password"),
                result.getString("phone"),
                result.getTimestamp("create_time").toLocalDateTime(),
                result.getString("address"),
                result.getBoolean("is_admin"),
                result.getBoolean("is_subscribed")
                )
        );
    }

    public boolean checkIfProductAlreadyIsInFavorites(long userId, long productId) throws SQLException {
        String sql = "SELECT user_id, product_id\n" +
                "FROM technopolis.users_like_products\n" +
                "WHERE user_id = ? AND product_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            ResultSet result = statement.executeQuery();
            return result.next();
        }
    }
}

package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.dto.AddAttributeToProductDto;
import technopolisspring.technopolis.model.dto.AttributeWithoutValueDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeDao extends Dao {

    public List<AttributeWithoutValueDto> getAllAttributes(int pageNumber) throws SQLException {
        String sql = "SELECT id, name, sub_category_id\n" +
                "FROM technopolis.attributes AS a\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * PAGE_SIZE);
            statement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            List<AttributeWithoutValueDto> attributes = new ArrayList<>();
            while (result.next()){
                AttributeWithoutValueDto attribute = new AttributeWithoutValueDto(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getLong("sub_category_id")
                );
                attributes.add(attribute);
            }
            return attributes;
        }
    }

    public void addAttributeToProduct(AddAttributeToProductDto attribute, long productId) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`products_have_attriubtes` " +
                "(`product_id`, `attribute_id`, `value`) " +
                "VALUES (?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.setLong(2, attribute.getId());
            statement.setString(3, attribute.getValue());
            statement.execute();
        }
    }

    public void addAttribute(AttributeWithoutValueDto attribute) throws SQLException {
        String sql = "INSERT INTO `technopolis`.`attributes` " +
                "(`name`, `sub_category_id`) " +
                "VALUES (?, ?);";
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, attribute.getName());
            statement.setLong(2, attribute.getSubCategoryId());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            attribute.setId(resultSet.getInt(1));
        }
    }

    public boolean deleteAttribute(long attributeId) throws SQLException {
        String attributeSql = "DELETE FROM technopolis.attributes WHERE id = ?;";
        String phaSql = "DELETE FROM technopolis.products_have_attriubtes " +
                "WHERE attribute_id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement attributeStatement = connection.prepareStatement(attributeSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement phaStatement = connection.prepareStatement(phaSql)) {
            connection.setAutoCommit(false);
            phaStatement.setLong(1, attributeId);
            phaStatement.execute();
            attributeStatement.setLong(1, attributeId);
            int changesMade = attributeStatement.executeUpdate();
            connection.commit();
            return changesMade != 0;
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true); // is this really needed?
            connection.close();
        }
    }

    public AttributeWithoutValueDto getAttributeById(long attributeId) throws SQLException {
        String sql = "SELECT id, name, sub_category_id\n" +
                "FROM technopolis.attributes\n" +
                "WHERE id = ?;";
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, attributeId);
            ResultSet result = statement.executeQuery();
            if (!result.next()){
                return null;
            }
            return new AttributeWithoutValueDto(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getLong("sub_category_id")
            );
        }
    }


    public boolean removeAttributeFromProduct(long attributeId, long productId) throws SQLException {
        String sql = "DELETE FROM technopolis.products_have_attriubtes " +
                "WHERE attribute_id = ? AND product_id = ?;";
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, attributeId);
            statement.setLong(2, productId);
            return statement.executeUpdate() != 0;
        }
    }

    public AttributeWithoutValueDto getAttributeByName(String name) throws SQLException {
        String sql = "SELECT id, name, sub_category_id\n" +
                "FROM technopolis.attributes\n" +
                "WHERE name = ?;";
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (!result.next()){
                return null;
            }
            return new AttributeWithoutValueDto(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getLong("sub_category_id")
            );
        }
    }
}

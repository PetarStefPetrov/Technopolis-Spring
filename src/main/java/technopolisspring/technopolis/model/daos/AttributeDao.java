package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Attribute;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeDao extends Dao{

    public List<Attribute> getAllAttributes(int pageNumber) throws SQLException {
        String sql = "SELECT id, name, sub_category_id, value\n" +
                "FROM technopolis.attributes AS a\n" +
                "JOIN technopolis.products_have_attriubtes AS pha ON id = attribute_id\n" +
                "LIMIT ?\n" +
                "OFFSET ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * PAGE_SIZE);
            statement.setInt(2, pageNumber * PAGE_SIZE - PAGE_SIZE);
            ResultSet result = statement.executeQuery();
            List<Attribute> attributes = new ArrayList<>();
            while (result.next()){
                Attribute attribute = new Attribute(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getLong("sub_category_id"),
                        result.getString("value")
                );
                attributes.add(attribute);
            }
            return attributes;
        }
    }

    public void addAttribute(Attribute attribute, long productId) throws SQLException {
        String attributeSql = "INSERT INTO `technopolis`.`attributes` " +
                "(`name`, `sub_category_id`) " +
                "VALUES (?, ?);";
        String addValueSql = "INSERT INTO `technopolis`.`products_have_attriubtes` " +
                "(`product_id`, `attribute_id`, `value`) " +
                "VALUES (?, ?, ?);";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement attributeStatement = connection.prepareStatement(attributeSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement valueStatement = connection.prepareStatement(addValueSql)) {
            connection.setAutoCommit(false);
            attributeStatement.setString(1, attribute.getName());
            attributeStatement.setLong(2, attribute.getSubCategoryId());
            attributeStatement.execute();
            ResultSet resultSet = attributeStatement.getGeneratedKeys();
            resultSet.next();
            attribute.setId(resultSet.getInt(1));
            valueStatement.setLong(1, productId);
            valueStatement.setLong(2, attribute.getId());
            valueStatement.setString(3, attribute.getValue());
            valueStatement.execute();
            connection.commit();
        } catch (SQLException e){
            connection.setAutoCommit(true);
            connection.rollback();
            throw e;
        }
        finally {
            connection.close();
        }
    }

    public boolean deleteAttribute(long attributeId, long productId) throws SQLException {
        String attributeSql = "DELETE FROM `technopolis`.`attributes` WHERE `id` = ?;";
        String addValueSql = "DELETE FROM `technopolis`.`products_have_attriubtes` " +
                "WHERE (`product_id` = ?) and (`attribute_id` = ?);";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement attributeStatement = connection.prepareStatement(attributeSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement valueStatement = connection.prepareStatement(addValueSql)) {
            connection.setAutoCommit(false);
            valueStatement.setLong(1, productId);
            valueStatement.setLong(2, attributeId);
            valueStatement.execute();
            attributeStatement.setLong(1, attributeId);
            int changesMade = attributeStatement.executeUpdate();
            connection.commit();
            return changesMade != 0;
        } catch (SQLException e){
            connection.setAutoCommit(true);
            connection.rollback();
            throw e;
        }
        finally {
            connection.close();
        }
    }

}

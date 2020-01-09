package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Attribute> getAllAttributes() throws SQLException {
        String sql = "SELECT a.id, a.name, a.sub_category_id, pha.value\n" +
                "FROM technopolis.attributes AS a\n" +
                "JOIN technopolis.products_have_attriubtes AS pha ON pha.attribute_id = a.id;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            List<Attribute> attributes = new ArrayList<>();
            while (result.next()){
                Attribute attribute = new Attribute(
                        result.getLong("a.id"),
                        result.getString("a.name"),
                        result.getLong("a.sub_category_id"),
                        result.getString("pha.value")
                );
                attributes.add(attribute);
            }
            return attributes;
        }
    }

    public Attribute addAttribute(Attribute attribute, long productId) throws SQLException {
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
            connection.commit();
            return attribute;
        } catch (SQLException e){
            connection.setAutoCommit(true);
            connection.rollback();
            throw e;
        }
        finally {
            connection.close();
        }
    }

    public Attribute deleteAttribute(Attribute attribute, long productId) throws SQLException {
        String attributeSql = "DELETE FROM `technopolis`.`attributes` WHERE `id` = ?;";
        String addValueSql = "DELETE FROM `technopolis`.`products_have_attriubtes` " +
                "WHERE (`product_id` = ?) and (`attribute_id` = ?);";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement attributeStatement = connection.prepareStatement(attributeSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement valueStatement = connection.prepareStatement(addValueSql)) {
            connection.setAutoCommit(false);
            valueStatement.setLong(1, productId);
            valueStatement.setLong(2, attribute.getId());
            valueStatement.execute();
            attributeStatement.setLong(1, attribute.getId());
            attributeStatement.execute();
            connection.commit();
            return attribute;
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

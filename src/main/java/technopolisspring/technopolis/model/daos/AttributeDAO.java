package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Attribute getAttributeById(long id) throws SQLException {
        String sql = "SELECT a.id, a.name, sc.name\n" +
                "FROM attributes AS a\n" +
                "JOIN sub_categories AS sc ON sc.id = a.sub_category_id\n" +
                "WHERE a.id = ?;";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            return new Attribute(
                    id,
                    result.getString("a.name"),
                    result.getString("sc.name")
            );
        }
    }

    public List<Attribute> getAllAttributes() throws SQLException {
        String sql = "SELECT a.id, a.name, sc.name\n" +
                "FROM attributes AS a\n" +
                "JOIN sub_categories AS sc ON sc.id = a.sub_category_id\n";
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            List<Attribute> attributes = new ArrayList<>();
            while (result.next()){
                Attribute attribute = new Attribute(
                        result.getLong("id"),
                        result.getString("a.name"),
                        result.getString("sc.name")
                );
                attributes.add(attribute);
            }
            return attributes;
        }
    }

}

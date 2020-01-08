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
        String sql = "SELECT id, name, sub_category_id\n" +
                "FROM `technopolis`.attributes\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            return new Attribute(
                    id,
                    result.getString("name"),
                    result.getLong("sub_category_id")
            );
        }
    }

    public List<Attribute> getAllAttributes() throws SQLException {
        String sql = "SELECT id, name, sub_category_id\n" +
                "FROM `technopolis`.attributes\n" +
                "WHERE id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            List<Attribute> attributes = new ArrayList<>();
            while (result.next()){
                Attribute attribute = new Attribute(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getLong("sub_category_id")
                );
                attributes.add(attribute);
            }
            return attributes;
        }
    }

}

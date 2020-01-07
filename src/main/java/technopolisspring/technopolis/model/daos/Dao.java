package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;

abstract class Dao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    Connection connection =  jdbcTemplate.getDataSource().getConnection();

    protected Dao() throws SQLException {
    }
}

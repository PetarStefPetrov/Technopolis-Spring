package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

abstract class Dao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

}

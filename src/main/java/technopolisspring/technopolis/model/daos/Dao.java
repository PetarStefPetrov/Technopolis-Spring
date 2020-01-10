package technopolisspring.technopolis.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public abstract class Dao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    protected static final int PAGE_SIZE = 20;

}

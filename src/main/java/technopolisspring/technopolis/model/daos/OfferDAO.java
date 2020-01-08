package technopolisspring.technopolis.model.daos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.pojos.Offer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

    @Component
    public class OfferDAO {
        @Autowired
        private ProductDAO productDao;

        @Autowired
        JdbcTemplate jdbcTemplate;

    }

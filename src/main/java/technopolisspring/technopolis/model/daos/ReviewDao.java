package technopolisspring.technopolis.model.daos;

import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class ReviewDao extends Dao {

    public void addReview(String name,
                          String title,
                          String comment,
                          int product_id,
                          int user_id) throws SQLException {
        String sql = "INSERT INTO reviews (name, title, comment, product_id, user_id)\n" +
                "VALUES (?,?,?,?,?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, title);
            statement.setString(3, comment);
            statement.setInt(4, product_id);
            statement.setInt(5, user_id);
        }
    }

    public void editReview(int id,
                           String name,
                           String title,
                           String comment) throws SQLException {
        String sql = "UPDATE reviews\n" +
                "SET \n" +
                "name = '" + name + "',\n" +
                "title = '" + title + "',\n" +
                "comment = '" + comment + "'\n" +
                "WHERE id = " + id + ";";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public void deleteReview(int id) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = " + id;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

}

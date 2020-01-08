package technopolisspring.technopolis.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserWithAllAttributesDto {
        @Autowired
        private UserDao userDao;

        private long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDateTime createTime;
        private String address;
        private List<Review> reviews; // <review>
        private List<Product> favorites; // <product>
        private List<Order> orders; // <order>
        UserWithAllAttributesDto(User user) throws SQLException {
            id= user.getId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            email = user.getEmail();
            phone = user.getPhone();
            createTime = user.getCreateTime();
            address = user.getAddress();
            this.reviews = userDao.getReviews(this.id);
            this.favorites = userDao.getFavourites(this.id);
            this.orders = userDao.getOrders(this.id);
        }
}

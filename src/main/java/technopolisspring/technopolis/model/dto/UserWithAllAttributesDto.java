package technopolisspring.technopolis.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserWithAllAttributesDto {

    @Autowired
    private UserDAO userDao;
        private long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDateTime createTime;
        private String address;
        private List<Review> reviews;
        private List<Product> favorites;
        private List<Order> orders ;

        public UserWithAllAttributesDto(User user,List<Review> reviews,List<Product> favorites,List<Order> orders) throws SQLException {
            id= user.getId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            email = user.getEmail();
            phone = user.getPhone();
            createTime = user.getCreateTime();
            address = user.getAddress();
            this.orders = orders;
            this.favorites = favorites;
            this.reviews = reviews;

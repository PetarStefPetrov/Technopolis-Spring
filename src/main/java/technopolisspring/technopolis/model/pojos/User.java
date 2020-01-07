package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private LocalDateTime createTime;
    private String address;
    private ConcurrentHashMap<Integer, Review> reviews; // <review_id, review>
    private ConcurrentHashMap<Integer, Product> favorites; // <product_id, product>
    private ConcurrentHashMap<Integer, Order> orders; // <order_id, order>

    public User(int id,
                String firstName,
                String lastName,
                String email,
                String password,
                String phone,
                LocalDateTime createTime,
                String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createTime = createTime; // create time is now only the first time the user gets into db
        this.address = address;
        this.reviews = new ConcurrentHashMap<>();
        this.favorites = new ConcurrentHashMap<>();
        this.orders = new ConcurrentHashMap<>();
    }

    public User(int id,
                String firstName,
                String lastName,
                String email,
                String password,
                String phone,
                LocalDateTime createTime,
                String address,
                Map<Integer, Review> reviews,
                Map<Integer, Product> favorites,
                Map<Integer, Order> orders) {
        this(id, firstName, lastName, email, password, phone, createTime, address);
        this.reviews.putAll(reviews);
        this.favorites.putAll(favorites);
        this.orders.putAll(orders);
    }

    public void addReview(Review review){
        this.reviews.put(review.getId(), review);
    }

    public void addToFavorites(Product product){
        this.favorites.put(product.getId(), product);
    }

    public void addOrder(Order order){
        this.orders.put(order.getId(), order);
    }

}

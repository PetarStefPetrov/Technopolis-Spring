package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private long id;
    private long userId;
    private String address;
    private double price;
    private List<Product> products;

    private Order(long id, long userId, String address) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.products = new ArrayList<>();
    }

    public Order(long id, long userId, String address, double price) {
        this(id, userId, address);
        this.price = price;
    }
    // Second one is because I'm not sure if we would only create orders by getting them from the db.
    // In that case the first one is not needed.
    public Order(long id, int userId, String address, List<Product> products){
        this(id, userId, address);
        this.products.addAll(products);
        for (Product product : products) {
            this.price += product.getPrice();
        }
    }

    public void addProduct(Product product){
        this.products.add(product);
        this.price += product.getPrice();
    }

    public long getId() {
        return id;
    }

}


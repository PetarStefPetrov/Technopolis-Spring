package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private int id;
    private int userId;
    private String address;
    private double price;
    private ConcurrentHashMap<Product, Integer> products; // products and quantity

    private Order(int id, int userId, String address) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.products = new ConcurrentHashMap<>();
    }

    public Order(int id, int userId, String address, double price) {
        this(id, userId, address);
        this.price = price;
    }

    // Second one is because I'm not sure if we would only create orders by getting them from the db.
    // In that case the first one is not needed.
    public Order(int id, int userId, String address, Map<Product, Integer> products){
        this(id, userId, address);
        this.products.putAll(products);
        for (Map.Entry<Product, Integer> entry : products.entrySet()){
            this.price += entry.getKey().getPrice() * entry.getValue();
        }
    }

    public void addProduct(Product product){
        this.products.putIfAbsent(product, 0); // because Integer default is null
        this.products.put(product, this.products.get(product) + 1);
        this.price += product.getPrice();
    }

    public int getId() {
        return id;
    }

}


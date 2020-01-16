package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<IProduct, Integer> products; // < product , quantity >


    public Order(long userId, String address, Map<IProduct, Integer> products){
        this.userId = userId;
        this.address = address;
        this.products = products;
        for (Map.Entry<IProduct, Integer> entry : products.entrySet()) {
            this.price += entry.getKey().getPrice() * entry.getValue();
        }
    }

    public Order(long id, long userId, String address, Map<IProduct, Integer> products) {
        this(userId, address, products);
        this.id = id;
    }

}


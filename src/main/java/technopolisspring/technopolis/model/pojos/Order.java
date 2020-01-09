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
    private Map<Product,Integer> products; // < product_id , quantity >


    public Order(long userId, String address, Map<Product,Integer> products){
        this.userId = userId;
        this.address = address;
        this.products = products;
        //TODO PESGO FIX MAP FOR EACH ELEMENT  * PRICE
//        for (Product product : products) {
//            this.price += product.getPrice();
//        }
    }

    public Order(long id, long userId, String address, double price) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.price = price;
    }
}


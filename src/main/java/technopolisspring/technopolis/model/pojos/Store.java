package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class Store {

    private int id;
    private String name;
    private String address;
    private String workHours;
    private String phone;
    private boolean freeParking;
    private String locationCoordinates;
    private ConcurrentHashMap<Product, Integer> products; // products and quantity

    public Store(int id,
                 String name,
                 String address,
                 String workHours,
                 String phone,
                 boolean freeParking,
                 String locationCoordinates) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.workHours = workHours;
        this.phone = phone;
        this.freeParking = freeParking;
        this.locationCoordinates = locationCoordinates;
        this.products = new ConcurrentHashMap<>();
    }

    public Store(int id,
                 String name,
                 String address,
                 String workHours,
                 String phone,
                 boolean freeParking,
                 String locationCoordinates,
                 Map<Product, Integer> products) {
        this(id, name, address, workHours, phone, freeParking, locationCoordinates);
        this.products.putAll(products);
    }

    public void addProduct(Product product){
        this.products.putIfAbsent(product, 0); // because Integer default is null
        this.products.put(product, this.products.get(product) + 1);
    }

    public int getId() {
        return id;
    }
}


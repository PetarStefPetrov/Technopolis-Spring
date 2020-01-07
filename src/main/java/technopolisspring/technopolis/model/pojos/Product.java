package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private int id;
    private String description;
    private double price;
    private String pictureUrl;
    private String brand;
    private int subCategoryId;
    private ConcurrentHashMap<Integer, Review> reviews; // <review_id, review>

    public Product(int id, String description, double price, String pictureUrl, String brand, int subCategoryId) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.brand = brand;
        this.subCategoryId = subCategoryId;
        this.reviews = new ConcurrentHashMap<>();
    }

    public Product(int id, String description, double price, String pictureUrl, String brand,
                   int subCategoryId, Map<Integer, Review> reviews) {
        this(id, description, price, pictureUrl, brand, subCategoryId);
        this.reviews.putAll(reviews);
    }

    public void addReview(Review review){
        this.reviews.put(review.getId(), review);
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }
}

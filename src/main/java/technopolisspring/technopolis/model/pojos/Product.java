package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long id;
    private String description;
    private double price;
    private String pictureUrl;
    private String brand;
    private long subCategoryId;
    private List<Review> reviews; // <review_id, review>

    public Product(long id, String description, double price, String pictureUrl, String brand, long subCategoryId) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.brand = brand;
        this.subCategoryId = subCategoryId;
        this.reviews = new ArrayList<>();
    }

    public Product(long id, String description, double price, String pictureUrl, String brand,
                   long subCategoryId, List<Review> reviews) {
        this(id, description, price, pictureUrl, brand, subCategoryId);
        this.reviews.addAll(reviews);
    }

    public void addReview(Review review){
        this.reviews.add(review);
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }



}

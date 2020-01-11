package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.ProductDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long id;
    private String description;
    private double price;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
    private long offerId;
    private List<Review> reviews;

    public Product(long id, String description, double price, String pictureUrl, long brandId, long subCategoryId, long offerId) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.brandId = brandId;
        this.subCategoryId = subCategoryId;
        this.offerId = offerId;
        this.reviews = new ArrayList<>();
    }

    public Product(long id, String description, double price, String pictureUrl, long brandId,
                   long subCategoryId, long offerId, List<Review> reviews) {
        this(id, description, price, pictureUrl, brandId, subCategoryId, offerId);
        this.reviews.addAll(reviews);
    }

    public Product (ProductDto productDto){
        this.description = productDto.getDescription();
        this.brandId = productDto.getBrandId();
        this.subCategoryId = productDto.getSubCategoryId();
        this.price = productDto.getPrice();
        this.pictureUrl = productDto.getPictureUrl();
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

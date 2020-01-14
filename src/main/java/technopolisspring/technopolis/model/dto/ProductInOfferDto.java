package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.IProductWithReview;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInOfferDto implements IProductWithReview {

    private long id;
    private String description;
    private double price;
    private double priceAfterDiscount;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
    private long offerId;
    private List<ReviewOfProductDto> reviews;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductInOfferDto that = (ProductInOfferDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.description;
    }

    @Override
    public void addReview(ReviewOfProductDto review){
        this.reviews.add(review);
    }

}

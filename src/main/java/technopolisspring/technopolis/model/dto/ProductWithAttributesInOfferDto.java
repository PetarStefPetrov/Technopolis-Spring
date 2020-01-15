package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.IProductWithAttributes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithAttributesInOfferDto implements IProductWithAttributes {

    private long id;
    private String description;
    private double priceBeforeDiscount;
    private double price;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
    private long offerId;
    private List<Attribute> attributes;

    public ProductWithAttributesInOfferDto(long id, String description, double priceBeforeDiscount, double price, String pictureUrl, long brandId, long subCategoryId, long offerId) {
        this.id = id;
        this.description = description;
        this.priceBeforeDiscount = priceBeforeDiscount;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.brandId = brandId;
        this.subCategoryId = subCategoryId;
        this.offerId = offerId;
        this.attributes = new ArrayList<>();
    }
}

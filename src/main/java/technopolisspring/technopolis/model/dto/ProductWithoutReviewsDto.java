package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithoutReviewsDto {

    private long id;
    private String description;
    private double price;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
    private long offerId;

}

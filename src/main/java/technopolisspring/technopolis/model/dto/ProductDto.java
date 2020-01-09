package technopolisspring.technopolis.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private String description;
    private double price;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
}

package technopolisspring.technopolis.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilterForProductsDto {

    private double minPrice;
    private double maxPrice;
    private long subCategoryId;
    private long brandId;
    private String sorted;

}

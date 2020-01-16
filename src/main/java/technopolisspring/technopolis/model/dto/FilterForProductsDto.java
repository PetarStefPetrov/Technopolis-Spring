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
    private String sorted;

}

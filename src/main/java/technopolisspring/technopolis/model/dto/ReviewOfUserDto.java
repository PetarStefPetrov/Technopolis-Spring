package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.pojos.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOfUserDto {

    private long id;
    private String title;
    private String comment;
    private Product product;

}

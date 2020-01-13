package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOfUserDto {

    private long id;
    private String title;
    private String comment;
    private ProductWithoutReviewsDto product;

}

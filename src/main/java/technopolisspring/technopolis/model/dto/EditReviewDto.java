package technopolisspring.technopolis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditReviewDto {

    private long reviewId;
    private String title;
    private String comment;
    private long userId;

}

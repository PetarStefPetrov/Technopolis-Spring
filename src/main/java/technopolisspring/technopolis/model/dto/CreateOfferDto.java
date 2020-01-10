package technopolisspring.technopolis.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateOfferDto {

    private String name;
    private double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}

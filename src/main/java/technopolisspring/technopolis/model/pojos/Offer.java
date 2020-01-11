package technopolisspring.technopolis.model.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.CreateOfferDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class Offer {

    private long id;
    private String name;
    private double discountPercent;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;
    private ConcurrentHashMap<Product, Double> products; // <product, price after discount>

    public Offer(long id, String name, double discountPercent, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.products = new ConcurrentHashMap<>();
    }

    public Offer(long id, String name, double discountPercent, LocalDateTime startDate, LocalDateTime endDate, Map<Product, Double> products) {
        this(id, name, discountPercent, startDate, endDate);
        this.products.putAll(products);
    }

    public Offer(CreateOfferDto createOfferDto) {
        this.name = createOfferDto.getName();
        this.discountPercent = createOfferDto.getDiscountPercent();
        this.startDate = createOfferDto.getStartDate();
        this.endDate = createOfferDto.getEndDate();
    }

}

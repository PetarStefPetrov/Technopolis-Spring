package technopolisspring.technopolis.model.pojos;

import technopolisspring.technopolis.model.dto.ReviewOfProductDto;

public interface IProductWithReview extends IProduct {

    void addReview(ReviewOfProductDto review);

}

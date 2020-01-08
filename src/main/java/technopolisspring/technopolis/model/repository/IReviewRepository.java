package technopolisspring.technopolis.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import technopolisspring.technopolis.model.pojos.Review;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

    List<Review> getAllByUserId(long id);

}

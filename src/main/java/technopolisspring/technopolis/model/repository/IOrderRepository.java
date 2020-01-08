package technopolisspring.technopolis.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import technopolisspring.technopolis.model.pojos.Order;

import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    List<Order> getAllByUserId(long userID);

}

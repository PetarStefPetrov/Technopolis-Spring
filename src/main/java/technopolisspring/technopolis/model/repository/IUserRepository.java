package technopolisspring.technopolis.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    User getUserByEmail(String email);

}

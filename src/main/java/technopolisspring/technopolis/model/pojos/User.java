package technopolisspring.technopolis.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.UserRegistrableDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String email;
    @Column
    @JsonIgnore
    private String password;
    @Column
    private String phone;
    @Column
    private LocalDateTime createTime;
    @Column
    private String address;
    @Column
    private boolean isAdmin;

    public User (UserRegistrableDto reg){
        firstName = reg.getFirstName();
        lastName = reg.getLastName();
        email = reg.getEmail();
        password = reg.getPassword();
        phone = reg.getPhone();
        createTime = LocalDateTime.now();
        address = reg.getAddress();
    }
}

package technopolisspring.technopolis.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.UserRegistrableDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column
    @OneToMany(mappedBy = "reviews")
    private List<Review> reviews;

    public User (UserRegistrableDto reg){
        this.firstName = reg.getFirstName();
        this.lastName = reg.getLastName();
        this.email = reg.getEmail();
        this.password = reg.getPassword();
        this.phone = reg.getPhone();
        this.createTime = LocalDateTime.now();
        this.address = reg.getAddress();
    }
}

package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.UserRegistrableDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String password;
    @Column
    private String phone;
    @Column
    private LocalDateTime createTime;
    @Column
    private String address;
    public User (UserRegistrableDto reg){
        firstName = reg.getFirstName();
        lastName = reg.getLastName();
        email = reg.getEmail();
        password = reg.getPassword();
        phone = reg.getPhone();
        createTime = LocalDateTime.now();
        address = reg.getAddress();
    }
    public User(long id,
                String firstName,
                String lastName,
                String email,
                String password,
                String phone,
                LocalDateTime createTime,
                String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createTime = createTime; // create time is now only the first time the user gets into db
        this.address = address;
    }


}

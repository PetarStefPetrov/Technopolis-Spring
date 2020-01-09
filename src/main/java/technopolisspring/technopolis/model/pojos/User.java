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
public class User {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private String phone;
    private LocalDateTime createTime;
    private String address;
    @JsonIgnore
    private boolean isAdmin = false;

    public User (UserRegistrableDto reg){
        this.firstName = reg.getFirstName();
        this.lastName = reg.getLastName();
        this.email = reg.getEmail();
        this.password = reg.getPassword();
        this.phone = reg.getPhone();
        this.createTime = LocalDateTime.now();
        this.address = reg.getAddress();
    }

    public User(long userId,
                String firstName,
                String lastName,
                String email,
                String password,
                String phone,
                LocalDateTime createTime,
                String address,
                boolean isAdmin) {
        this.id =  userId;
        this.firstName =  firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createTime = createTime;
        this.address = address;
        this.isAdmin = isAdmin;
    }


}

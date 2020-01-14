package technopolisspring.technopolis.model.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.RegisterUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private String phone;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createTime;
    private String address;
    private boolean isAdmin = false;
    private boolean isSubscribed;

    public User (RegisterUserDto reg){
        this.firstName = reg.getFirstName();
        this.lastName = reg.getLastName();
        this.email = reg.getEmail();
        this.password = reg.getPassword();
        this.phone = reg.getPhone();
        this.createTime = LocalDateTime.now();
        this.address = reg.getAddress();
    }

}

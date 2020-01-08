package technopolisspring.technopolis.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.pojos.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserWithoutPasswordDto {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime createTime;
    private String address;
        public UserWithoutPasswordDto(User user){
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.address = user.getAddress();
        }
}

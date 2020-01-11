package technopolisspring.technopolis.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createTime;
    private String address;
    private boolean isSubscribed;

        public UserWithoutPasswordDto(User user){
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.createTime = user.getCreateTime();
            this.address = user.getAddress();
            this.isSubscribed = user.isSubscribed();
        }
}

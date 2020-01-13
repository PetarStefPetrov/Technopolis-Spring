package technopolisspring.technopolis.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.pojos.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean isAdmin;

        public UserWithoutPasswordDto(User user){
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.createTime = user.getCreateTime();
            this.address = user.getAddress();
            this.isSubscribed = user.isSubscribed();
            this.isAdmin = user.isAdmin();
        }

    public void edit(EditUserDto editUserDto) {
        this.firstName = editUserDto.getFirstName();
        this.lastName = editUserDto.getLastName();
        this.email = editUserDto.getEmail();
        this.phone = editUserDto.getPhone();
        this.address = editUserDto.getAddress();
    }
}

package technopolisspring.technopolis.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrableDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String checkPassword;
    private String phone;
    private String address;
}

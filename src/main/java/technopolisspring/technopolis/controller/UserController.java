package technopolisspring.technopolis.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;

@RestController
public class UserController {
    @PostMapping("/login")
    public UserWithoutPasswordDto login(){
        return null;
    }
}

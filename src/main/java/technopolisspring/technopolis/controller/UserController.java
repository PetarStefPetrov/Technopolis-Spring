package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.LoginUserDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.exception.InvalidArguments;
import technopolisspring.technopolis.model.pojos.User;


import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;

    @PostMapping("/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session){
        User user = null;
        //User user = userDao.getUserByMail(userDTO.getEmail());
        if(user == null){
            throw new InvalidArguments("Invalid email or password");
        }

        return null;
    }
}

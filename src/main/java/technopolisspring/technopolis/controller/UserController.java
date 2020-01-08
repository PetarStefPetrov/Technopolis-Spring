package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.LoginUserDTO;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.User;


import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;

    @PostMapping("/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDTO userDTO, HttpSession session){
        User user;
        //User user = userDao.getUserByMail(userDTO.getEmail());
        return null;
    }
}

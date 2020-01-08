package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.LoginUserDto;
import technopolisspring.technopolis.model.dto.UserRegistrableDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.InvalidArguments;
import technopolisspring.technopolis.model.pojos.User;
import technopolisspring.technopolis.model.repository.IUserRepository;


import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserRepository userRepository;
    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @PostMapping("users/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session){
        //TODO Cript password
        User user = userRepository.getUserByEmail(userDTO.getEmail());
        if(user == null || user.getPassword().equals(userDTO.getPassword())){
            throw new InvalidArguments("Invalid email or password");
        }
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        return userWithoutPasswordDto;
    }
    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody UserRegistrableDto userRegistrableDto,HttpSession session){
        //TODO Validation email and password
        User user = new User(userRegistrableDto);
        //TODO UserDao
        //userDao.registerUser(user);
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        return userWithoutPasswordDto;
    }
    @PostMapping("/users/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @PostMapping("users/delete")
    public void delete(HttpSession session){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        //TODO UserDao
        //userDao.delete(user);
    }


}

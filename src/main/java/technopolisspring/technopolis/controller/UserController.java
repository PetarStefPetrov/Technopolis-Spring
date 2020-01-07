package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.ChangePasswordDto;
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
        return new UserWithoutPasswordDto(user);
    }
    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody UserRegistrableDto userRegistrableDto,HttpSession session){
        //TODO Validation email and password
        User user = new User(userRegistrableDto);
        userRepository.save(user);
        return new UserWithoutPasswordDto(user);
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
        userRepository.delete(user);
    }

    @PostMapping("users/change_password")
    public UserWithoutPasswordDto changePassword(HttpSession session, @RequestBody ChangePasswordDto changePasswordDto){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        if (!user.getPassword().equals(changePasswordDto.getOldPassword())){
            throw new InvalidArguments("wrong password");
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
            throw new InvalidArguments("passwords don't match");
        }
        user.setPassword(changePasswordDto.getNewPassword());
        userRepository.save(user);
        return new UserWithoutPasswordDto(user);
    }



}

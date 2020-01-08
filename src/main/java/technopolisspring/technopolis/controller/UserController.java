package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.LoginUserDto;
import technopolisspring.technopolis.model.dto.UserRegistrableDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
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

    @PostMapping("/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session){
        User user = userRepository.getUserByEmail(userDTO.getEmail());
        if(user == null || user.getPassword().equals(userDTO.getPassword())){
            throw new InvalidArguments("Invalid email or password");
        }
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        return userWithoutPasswordDto;
    }
    @PostMapping("/registrate")
    public UserWithoutPasswordDto registrate(@RequestBody UserRegistrableDto userRegistrableDto,HttpSession session){
        //TODO Validation email and password
        User user = new User(userRegistrableDto);
        //userDao.registerUser(user);
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        return userWithoutPasswordDto;
    }
}

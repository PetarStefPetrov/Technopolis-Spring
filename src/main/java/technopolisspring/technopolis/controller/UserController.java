package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.InvalidArguments;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;
import technopolisspring.technopolis.model.repository.IUserRepository;


import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        //TODO Validation email and password ( is name and email is exist)
        User user = new User(userRegistrableDto);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
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
    @PostMapping("users/profile")
    public UserWithAllAttributesDto getProfile(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        return new UserWithAllAttributesDto(user);
    }
    @GetMapping("users/{id}")
    public User getUserById(HttpSession session,@PathVariable(name = "id") long id){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        if(!user.isAdmin()){
            throw new AuthorizationException("Must be admin");
        }
        Optional<User> save = userRepository.findById(id);
        if(save == null){
            throw new BadRequestException("Invalid id");
        }
        return  save.get();
    }
    @GetMapping("users/")
    public List<User> allUsers(HttpSession session){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        if(!user.isAdmin()){
            throw new AuthorizationException("Must be admin");
        }
        return userRepository.findAll();
    }
    @GetMapping("users/reviews")
    public List<Review> getReview(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        return userDao.getReviews(user.getId());
    }
    @GetMapping("users/orders")
    public List<Order> getOrders(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        return userDao.getOrders(user.getId());
    }
    @GetMapping("users/favorites")
    public List<Product> getFavourites(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be login");
        }
        return userDao.getFavourites(user.getId());
    }
    @PostMapping("users/add_review/{id}")
    public void addReview(HttpSession session,@PathVariable(name = "id") long id){
        //TODO
    }


}

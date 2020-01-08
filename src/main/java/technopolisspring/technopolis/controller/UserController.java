package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.exception.InvalidArguments;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;


import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController extends GlobalException {


    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/test")
    public void doSmth() throws SQLException {
        List l = userDAO.getOrders(1);
        System.out.println(l);
    }

//    @PostMapping("users/login")
//    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session){
//        //TODO Cript password
//        //TODO User user = userRepository.getUserByEmail(userDTO.getEmail());
//        if(user == null || user.getPassword().equals(userDTO.getPassword())){
//            throw new InvalidArguments("Invalid email or password");
//        }
//        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
//        return new UserWithoutPasswordDto(user);
//    }
//
//    @PostMapping("users/register")
//    public UserWithoutPasswordDto register(@RequestBody UserRegistrableDto userRegistrableDto, HttpSession session){
//        //TODO Validation email and password (valid name and email exists)
//        User user = new User(userRegistrableDto);
//        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
//        userRepository.save(user);
//        return new UserWithoutPasswordDto(user);
//    }
//
//    @PostMapping("/users/logout")
//    public void logout(HttpSession session){
//        session.invalidate();
//    }
//
//    @DeleteMapping("users/delete")
//    public void delete(HttpSession session){
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        userRepository.delete(user);
//    }
//
//    @PutMapping("users/change_password")
//    public UserWithoutPasswordDto changePassword(HttpSession session, @RequestBody ChangePasswordDto changePasswordDto){
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        if (!user.getPassword().equals(changePasswordDto.getOldPassword())){
//            throw new InvalidArguments("wrong password");
//        }
//        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
//            throw new InvalidArguments("passwords don't match");
//        }
//        user.setPassword(changePasswordDto.getNewPassword());
//        userRepository.save(user);
//        return new UserWithoutPasswordDto(user);
//    }
//
//    @PostMapping("users/profile")
//    public UserWithAllAttributesDto getProfile(HttpSession session) throws SQLException {
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        return new UserWithAllAttributesDto(user);
//    }
//
//    @GetMapping("users/{id}")
//    public User getUserById(HttpSession session, @PathVariable(name = "id") long id){
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        if(!user.isAdmin()){
//            throw new AuthorizationException("Must be admin");
//        }
//        Optional<User> save = userRepository.findById(id);
//        if(!save.isPresent()){
//            throw new BadRequestException("Invalid id");
//        }
//        return  save.get();
//    }
//
//    @GetMapping("users/")
//    public List<User> allUsers(HttpSession session) {
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        if(!user.isAdmin()){
//            throw new AuthorizationException("Must be admin");
//        }
//        return userRepository.findAll();
//    }
//
//    @GetMapping("users/reviews")
//    public List<Review> getReview(HttpSession session) {
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        return reviewRepository.getAllByUserId(user.getId());
//    }
//
//    @GetMapping("users/orders")
//    public List<Order> getOrders(HttpSession session) {
//        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
//        if(user == null){
//            throw new AuthorizationException("Must be logged in");
//        }
//        return orderRepository.getAllByUserId(user.getId());
//    }
//
////    @GetMapping("users/favorites")
////    public List<Product> getFavourites(HttpSession session) {
////        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
////        if(user == null){
////            throw new AuthorizationException("Must be logged in");
////        }
////        return userDao.getFavourites(user.getId());
////    }
//
//    @PostMapping("users/add_review/{id}")
//    public void addReview(HttpSession session, @PathVariable(name = "id") long id){
//        //TODO
//    }


}

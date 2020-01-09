package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.ProductDAO;
import technopolisspring.technopolis.model.daos.ReviewDAO;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.exception.InvalidArguments;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
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
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private ProductDAO productDAO;


    @PostMapping("users/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session) throws SQLException {
        //TODO Cript password
      User user = userDAO.getUserByEmail(userDTO.getEmail());
        if(user == null || !user.getPassword().equals(userDTO.getPassword())){
            throw new InvalidArguments("Invalid email or password");
        }
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        return new UserWithoutPasswordDto(user);
    }

    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody UserRegistrableDto userRegistrableDto, HttpSession session) throws SQLException {
        //TODO Cript password
        if(userDAO.getUserByEmail(userRegistrableDto.getEmail()) != null){
            throw  new BadRequestException("Email is exist");
        }
        if(!userRegistrableDto.getPassword().equals(userRegistrableDto.getConfirmPassword())){
            throw new BadRequestException("Password dont match");
        }
        User user = new User(userRegistrableDto);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        userDAO.registerUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @DeleteMapping("users/delete")
    public void delete(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        userDAO.deleteUser(user);
    }

    @PutMapping("users/change_password")
    public UserWithoutPasswordDto changePassword(HttpSession session, @RequestBody ChangePasswordDto changePasswordDto) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if (!user.getPassword().equals(changePasswordDto.getOldPassword())){
            throw new InvalidArguments("wrong password");
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
            throw new InvalidArguments("passwords don't match");
        }
        user.setPassword(changePasswordDto.getNewPassword());
        userDAO.editUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @GetMapping("users/profile/")
    public UserWithAllAttributesDto getProfile(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return new UserWithAllAttributesDto(user,
                                            userDAO.getReviews(user.getId()),
                                            userDAO.getFavourites(user.getId()),
                                            userDAO.getOrders(user.getId()));
    }

    @GetMapping("users/{id}")
    public User getUserById(HttpSession session, @PathVariable(name = "id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        User save = userDAO.getUserById(id);
        if(save == null){
            throw new BadRequestException("Invalid id");
        }
        return  save;
    }

    @GetMapping("users/")
    public List<User> allUsers(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        return userDAO.getAll();
    }

    @GetMapping("users/reviews")
    public List<Review> getReview(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getReviews(user.getId());
    }

    @GetMapping("users/orders")
    public List<Order> getOrders(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getOrders(user.getId());
    }

    @GetMapping("users/favorites")
    public List<Product> getFavourites(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getFavourites(user.getId());
    }

    @PostMapping("users/add_review/{product_id}")
    public Review addReview(@RequestBody Review review,HttpSession session, @PathVariable(name = "product_id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Product product = productDAO.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        Review reviews = reviewDAO.addReview(review, product, user);
        return reviews;
    }
    @PostMapping("users/add_to_favorites/{product_id}")
    public void addFavorite(HttpSession session ,@PathVariable(name = "product_id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Product product = productDAO.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        userDAO.addToFavorites(product.getId(),user.getId());
    }
    @PostMapping("users/remove_from_favorites/{product_id}")
    public void removeFavorite(HttpSession session ,@PathVariable(name = "product_id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Product product = productDAO.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(!userDAO.removeFromFavorites(product.getId(),user.getId())){
            throw new BadRequestException("Dont have this product, in yours favorites");
        }
    }
}

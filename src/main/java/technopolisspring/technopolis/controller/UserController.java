package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.ReviewDao;
import technopolisspring.technopolis.model.daos.UserDao;
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

@RestController
public class UserController extends GlobalException {

    public static final String SESSION_KEY_LOGGED_USER = "logged_user";
    @Autowired
    private UserDao userDAO;
    @Autowired
    private ReviewDao reviewDAO;
    @Autowired
    private ProductDao productDAO;


    @PostMapping("users/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDTO, HttpSession session) throws SQLException {
        User user = userDAO.getUserByEmail(userDTO.getEmail());
        if(user == null ){
            throw new InvalidArguments("Invalid email or password ");
        }
        if(!BCrypt.checkpw(userDTO.getPassword(),user.getPassword())){
            throw new InvalidArguments("Invalid password or password ");
        }
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        return new UserWithoutPasswordDto(user);
    }

    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody RegisterUserDto registerUserDto, HttpSession session) throws SQLException {
        if(registerUserDto.getPassword().length() < 8 ){
            throw  new BadRequestException("Password must be at least 8 symbols");
        }
        if(userDAO.getUserByEmail(registerUserDto.getEmail()) != null){
            throw  new BadRequestException("User with this email already exists");
        }
        if(!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())){
            throw new BadRequestException("Passwords dont match");
        }
        User user = new User(registerUserDto);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(user.getPassword());
        user.setPassword(password);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        userDAO.registerUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @DeleteMapping("users")
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
        if (!BCrypt.checkpw(changePasswordDto.getOldPassword(),user.getPassword())){
            throw new InvalidArguments("Wrong password");
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
            throw new InvalidArguments("Passwords don't match");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(changePasswordDto.getNewPassword());
        user.setPassword(password);
        userDAO.editUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @GetMapping("users/{id}")
    public User getUserById(HttpSession session, @PathVariable(name = "id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        User save = userDAO.getUserById(id);
        if(save == null){
            throw new BadRequestException("Invalid id");
        }
        return save;
    }

    @GetMapping("users/pages/{pageNumber}")
    public List<User> allUsers(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        return userDAO.getAll(pageNumber);
    }

    @GetMapping("users/reviews/pages/{pageNumber}")
    public List<Review> getReview(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getReviews(user.getId(), pageNumber);
    }

    @GetMapping("users/orders/pages/{pageNumber}")
    public List<Order> getOrders(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getOrders(user.getId(), pageNumber);
    }

    @GetMapping("users/favorites/pages/{pageNumber}")
    public List<Product> getFavourites(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return userDAO.getFavourites(user.getId(), pageNumber);
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
    public void addFavorites(HttpSession session ,@PathVariable(name = "product_id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Product product = productDAO.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        userDAO.addToFavorites(product.getId(), user.getId());
    }

    @PostMapping("users/remove_from_favorites/{product_id}")
    public void removeFavorites(HttpSession session, @PathVariable(name = "product_id") long id) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Product product = productDAO.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(!userDAO.removeFromFavorites(product.getId(), user.getId())){
            throw new BadRequestException("You don't have this product, in yours favorites");
        }
    }

    @PutMapping("users/subscribe")
    public String subscribe(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        userDAO.subscribeUser(user);
        return "Success!";
    }

}

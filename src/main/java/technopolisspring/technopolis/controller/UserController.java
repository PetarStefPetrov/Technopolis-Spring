package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.ReviewDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.InvalidArgumentsException;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;
import technopolisspring.technopolis.model.pojos.User;


import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController extends AbstractController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private ProductDao productDao;


    @PostMapping("users/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDto, HttpSession session) throws SQLException {
        User user = userDao.getUserByEmail(userDto.getEmail());
        if(user == null ){
            throw new InvalidArgumentsException("Invalid email or password");
        }
        if(!BCrypt.checkpw(userDto.getPassword(), user.getPassword())){
            throw new InvalidArgumentsException("Invalid email or password");
        }
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        session.setAttribute(SESSION_KEY_LOGGED_USER, userWithoutPasswordDto);
        return userWithoutPasswordDto;
    }

    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody RegisterUserDto registerUserDto, HttpSession session) throws SQLException {
        registerUserDto.setPassword(registerUserDto.getPassword().trim());
        if(registerUserDto.getPassword().length() < 8 ){
            throw  new BadRequestException("Password must be at least 8 symbols");
        }
        if(userDao.getUserByEmail(registerUserDto.getEmail()) != null){
            throw  new BadRequestException("User with this email already exists");
        }
        if(!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())){
            throw new BadRequestException("Passwords don't match");
        }
        User user = new User(registerUserDto);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(user.getPassword());
        user.setPassword(password);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        userDao.registerUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @DeleteMapping("users")
    public User delete(HttpSession session) throws SQLException {
        User user = checkIfUserIsLogged(session);
        if (!userDao.deleteUser(user.getId())){
            throw new BadRequestException("There is no such user");
        }
        logout(session);
        return user;
    }

    @PutMapping("users/change_password")
    public UserWithoutPasswordDto changePassword(HttpSession session, @RequestBody ChangePasswordDto changePasswordDto) throws SQLException {
        User userInSession = checkIfUserIsLogged(session);
        User user = userDao.getUserById(userInSession.getId());
        if (!BCrypt.checkpw(changePasswordDto.getOldPassword(), user.getPassword())){
            throw new InvalidArgumentsException("Wrong password");
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
            throw new InvalidArgumentsException("Passwords don't match");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(changePasswordDto.getNewPassword());
        user.setPassword(password);
        userDao.editUser(user);
        return new UserWithoutPasswordDto(user);
    }

    @GetMapping("users/{id}")
    public UserWithoutPasswordDto getUserById(HttpSession session, @PathVariable long id) throws SQLException {
        checkIfUserIsAdmin(session);
        if(userDao.getUserById(id) == null){
            throw new BadRequestException("Invalid id");
        }
        return new UserWithoutPasswordDto(userDao.getUserById(id));
    }

    @GetMapping("users/profile")
    public UserWithoutPasswordDto getMyProfile(HttpSession session) {
        User user = checkIfUserIsLogged(session);
        return new UserWithoutPasswordDto(user);
    }

    @GetMapping("users/page/{pageNumber}")
    public List<User> getAllUsers(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        checkIfUserIsAdmin(session);
        return userDao.getAllUsers(pageNumber); // todo: return list of users without passwords
    }

    @GetMapping("users/reviews/page/{pageNumber}")
    public List<Review> getReview(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = checkIfUserIsLogged(session);
        return userDao.getReviews(user.getId(), pageNumber);
    }

    @GetMapping("users/orders/pages/{pageNumber}")
    public List<Order> getOrders(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = checkIfUserIsLogged(session);
        return userDao.getOrders(user.getId(), pageNumber);
    }

    @GetMapping("users/favorites/page/{pageNumber}")
    public List<Product> getFavourites(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        User user = checkIfUserIsLogged(session);
        return userDao.getFavourites(user.getId(), pageNumber);
    }

    @PostMapping("users/add_review/{product_id}")
    public Review addReview(@RequestBody Review review, HttpSession session, @PathVariable(name = "product_id") long id) throws SQLException {
        User user = checkIfUserIsLogged(session);
        Product product = productDao.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        return reviewDao.addReview(review, id, user.getId());
    }

    @PostMapping("users/add_to_favorites/{product_id}")
    public Product addFavorites(HttpSession session, @PathVariable(name = "product_id") long id) throws SQLException {
        User user = checkIfUserIsLogged(session);
        Product product = productDao.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(userDao.checkIfProductAlreadyIsInFavorites(user.getId(), product.getId())){
            return product;
        }
        userDao.addToFavorites(product.getId(), user.getId());
        return product;
    }

    @PostMapping("users/remove_from_favorites/{product_id}")
    public Product removeFavorites(HttpSession session, @PathVariable(name = "product_id") long id) throws SQLException {
        User user = checkIfUserIsLogged(session);
        Product product = productDao.getProductById(id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(!userDao.removeFromFavorites(product.getId(), user.getId())){
            throw new BadRequestException("You don't have this product, in yours favorites");
        }
        return product;
    }

    @PutMapping("users/subscribe")
    public UserWithoutPasswordDto subscribe(HttpSession session) throws SQLException {
        User user = checkIfUserIsLogged(session);
        userDao.subscribeUser(user);
        return new UserWithoutPasswordDto(user);
    }

}

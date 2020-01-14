package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.ReviewDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.exception.NotFoundException;
import technopolisspring.technopolis.model.pojos.*;
import technopolisspring.technopolis.service.ValidationUtil;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@Validated
public class UserController extends AbstractController {

    public static final String YOU_DON_T_HAVE_THIS_PRODUCT_IN_YOURS_FAVORITES = "You don't have this product, in yours favorites";
    public static final String SUCCESS = "Success!";
    @Autowired
    private UserDao userDao;
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ValidationUtil validation;


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

    @SneakyThrows
    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody RegisterUserDto registerUserDto, HttpSession session) {
        if(userDao.getUserByEmail(registerUserDto.getEmail()) != null){
            throw  new BadRequestException("User with this email already exists");
        }
        String checkUserMsg = validation.checkUser(registerUserDto);
        if(checkUserMsg != null){
            throw new InvalidArgumentsException(checkUserMsg);
        }
        User user = new User(registerUserDto);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userDao.registerUser(user);
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        session.setAttribute(SESSION_KEY_LOGGED_USER, userWithoutPasswordDto);
        return userWithoutPasswordDto;
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @DeleteMapping("users")
    public UserWithoutPasswordDto delete(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        if (!userDao.deleteUser(user.getId())){
            throw new BadRequestException("There is no such user");
        }
        logout(session);
        return user;
    }

    @SneakyThrows
    @PutMapping("users/change_password")
    public UserWithoutPasswordDto changePassword(HttpSession session, @RequestBody ChangePasswordDto changePasswordDto) {
        UserWithoutPasswordDto userInSession = checkIfUserIsLogged(session); // todo: validations
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
        userDao.changePassword(user);
        return new UserWithoutPasswordDto(user);
    }

    @SneakyThrows
    @PutMapping("users")
    public UserWithoutPasswordDto editUser(@RequestBody EditUserDto editUserDto, HttpSession session) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session); // todo: validations
        editUserDto.setId(user.getId());
        String msg = validation.checkUser(editUserDto);
        if(msg != null){
            throw new InvalidArgumentsException(msg);
        }
        userDao.editUser(editUserDto);
        user.edit(editUserDto);
        return user;
    }

    @GetMapping("users/{id}")
    public UserWithoutPasswordDto getUserById(@PathVariable long id) throws SQLException {
        if(userDao.getUserById(id) == null){
            throw new BadRequestException("Invalid id");
        }
        return new UserWithoutPasswordDto(userDao.getUserById(id));
    }

    @GetMapping("users/profile")
    public UserWithoutPasswordDto getMyProfile(HttpSession session) {
        return checkIfUserIsLogged(session);
    }

    @GetMapping("users/page/{pageNumber}")
    public List<User> getAllUsers(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        checkIfUserIsAdmin(session);
        return userDao.getAllUsers(validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @PostMapping("users/reviews/{productId}")
    public Review addReview(@RequestBody Review review, HttpSession session, @PathVariable long productId) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        IProduct product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        return reviewDao.addReview(review, productId, user);
    }

    @GetMapping("users/reviews/page/{pageNumber}")
    public List<ReviewOfUserDto> getReviews(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return reviewDao.getReviewsOfUser(user.getId(), validatePageNumber(pageNumber));
    }

    @PutMapping("users/reviews")
    public EditReviewDto editReview(@RequestBody EditReviewDto review, HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        review.setUserId(user.getId());
        if (!reviewDao.editReview(review)){
            throw new InvalidArgumentsException("Invalid review");
        }
        return review;
    }

    @DeleteMapping("users/reviews/{reviewId}")
    public Review deleteReview(HttpSession session, @PathVariable long reviewId) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        Review review = reviewDao.getReviewById(reviewId);
        if (review == null){
            throw new NotFoundException("Review not found");
        }
        if (review.getUserId() != user.getId()){
            throw new BadRequestException("You can only delete your own reviews!");
        }
        reviewDao.deleteReview(reviewId);
        return review;
    }

    @GetMapping("users/orders/page/{pageNumber}")
    public List<OrderWithoutProductsDto> getOrders(HttpSession session, @PathVariable int pageNumber) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return userDao.getOrders(user.getId(), validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @GetMapping("users/favorites/page/{pageNumber}")
    public List<IProduct> getFavourites(HttpSession session, @PathVariable int pageNumber) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return userDao.getFavourites(user.getId(), validatePageNumber(pageNumber));
    }

    @PostMapping("users/add_to_favorites/{productId}")
    public IProduct addFavorites(HttpSession session, @PathVariable long productId) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        IProduct product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(userDao.checkIfProductAlreadyIsInFavorites(user.getId(), product.getId())){
            return product;
        }
        userDao.addToFavorites(product.getId(), user.getId());
        return product;
    }

    @SneakyThrows
    @PostMapping("users/remove_from_favorites/{productId}")
    public String removeFavorites(HttpSession session, @PathVariable long productId) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        if(!userDao.removeFromFavorites(productId, user.getId())){
            throw new BadRequestException(YOU_DON_T_HAVE_THIS_PRODUCT_IN_YOURS_FAVORITES);
        }
        return SUCCESS;
    }

    @PutMapping("users/subscribe")
    public UserWithoutPasswordDto subscribe(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        userDao.subscribeUser(user);
        return user;
    }

}

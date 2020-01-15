package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.*;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.User;
import technopolisspring.technopolis.utils.ValidationUtil;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
@Validated
public class UserController extends AbstractController {

    public static final String YOU_DONT_HAVE_THIS_PRODUCT_IN_YOUR_FAVORITES = "You don't have this product, in your favorites";
    public static final String SUCCESS = "Success!";
    private static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    private static final String EMAIL_ALREADY_EXISTS = "User with this email already exists";
    private static final String NO_SUCH_USER = "There is no such user";
    private static final String WRONG_PASSWORD = "Wrong password";
    public static final String PASSWORDS_DONT_MATCH = "Passwords don't match";
    private static final String INVALID_ID = "Invalid id";
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ValidationUtil validation;


    @PostMapping("users/login")
    public UserWithoutPasswordDto login(@RequestBody LoginUserDto userDto, HttpSession session) throws SQLException {
        User user = userDao.getUserByEmail(userDto.getEmail());
        if(user == null ){
            throw new InvalidArgumentsException(INVALID_EMAIL_OR_PASSWORD);
        }
        if(!BCrypt.checkpw(userDto.getPassword(), user.getPassword())){
            throw new InvalidArgumentsException(INVALID_EMAIL_OR_PASSWORD);
        }
        UserWithoutPasswordDto userWithoutPasswordDto = new UserWithoutPasswordDto(user);
        session.setAttribute(SESSION_KEY_LOGGED_USER, userWithoutPasswordDto);
        return userWithoutPasswordDto;
    }

    @SneakyThrows
    @PostMapping("users/register")
    public UserWithoutPasswordDto register(@RequestBody RegisterUserDto registerUserDto, HttpSession session) {
        if(userDao.getUserByEmail(registerUserDto.getEmail()) != null){
            throw  new BadRequestException(EMAIL_ALREADY_EXISTS);
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
            throw new BadRequestException(NO_SUCH_USER);
        }
        logout(session);
        return user;
    }

    @SneakyThrows
    @PutMapping("users/change_password")
    public UserWithoutPasswordDto changePassword(HttpSession session,
                                                 @RequestBody ChangePasswordDto changePasswordDto) {
        UserWithoutPasswordDto userInSession = checkIfUserIsLogged(session); // todo: validations
        User user = userDao.getUserById(userInSession.getId());
        if (!BCrypt.checkpw(changePasswordDto.getOldPassword(), user.getPassword())){
            throw new InvalidArgumentsException(WRONG_PASSWORD);
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())){
            throw new InvalidArgumentsException(PASSWORDS_DONT_MATCH);
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
            throw new BadRequestException(INVALID_ID);
        }
        return new UserWithoutPasswordDto(userDao.getUserById(id));
    }

    @GetMapping("users/profile")
    public UserWithoutPasswordDto getMyProfile(HttpSession session) {
        return checkIfUserIsLogged(session);
    }

    @SneakyThrows
    @GetMapping("users/page")
    public List<User> getAllUsers(HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        checkIfUserIsAdmin(session);
        return userDao.getAllUsers(pageNumber);
    }

    @GetMapping("users/orders/page")
    public List<OrderWithoutProductsDto> getOrders(HttpSession session,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return userDao.getOrders(user.getId(), pageNumber);
    }

    @SneakyThrows
    @GetMapping("users/favorites/page")
    public List<IProduct> getFavourites(HttpSession session,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return userDao.getFavourites(user.getId(), pageNumber);
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
            throw new BadRequestException(YOU_DONT_HAVE_THIS_PRODUCT_IN_YOUR_FAVORITES);
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

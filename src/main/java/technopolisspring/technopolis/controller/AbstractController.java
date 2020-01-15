package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import technopolisspring.technopolis.exception.AuthorizationException;
import technopolisspring.technopolis.exception.GlobalExceptionHandler;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.utils.ValidationUtil;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AbstractController extends GlobalExceptionHandler {

    protected static final String SESSION_KEY_LOGGED_USER = "logged_user";
    protected static final String DEFAULT_PAGE = "1";
    public static final String NEED_TO_BE_LOGGED_IN = "Need to be logged in";
    public static final String MUST_BE_ADMIN = "Must be admin";
    @Autowired
    UserDao userDao;
    @Autowired
    ValidationUtil validationUtil;

    protected UserWithoutPasswordDto checkIfUserIsLogged(HttpSession session){
        UserWithoutPasswordDto user = (UserWithoutPasswordDto) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException(NEED_TO_BE_LOGGED_IN);
        }
        return user;
    }

    protected UserWithoutPasswordDto checkIfUserIsAdmin(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        if(!userDao.isAdmin(user.getId())){
            throw new AuthorizationException(MUST_BE_ADMIN);
        }
        return user;
    }

}

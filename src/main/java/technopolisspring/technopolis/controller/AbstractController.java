package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.GlobalExceptionHandler;
import technopolisspring.technopolis.model.pojos.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AbstractController extends GlobalExceptionHandler {

    protected static final String SESSION_KEY_LOGGED_USER = "logged_user";
    @Autowired
    UserDao userDao;

    protected User checkIfUserIsLogged(HttpSession session){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return user;
    }

    protected User checkIfUserIsAdmin(HttpSession session) throws SQLException {
        User user = checkIfUserIsLogged(session);
        if(!userDao.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        return user;
    }

}
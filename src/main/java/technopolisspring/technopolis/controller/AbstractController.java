package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.exception.AuthorizationException;
import technopolisspring.technopolis.exception.GlobalExceptionHandler;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AbstractController extends GlobalExceptionHandler {

    protected static final String SESSION_KEY_LOGGED_USER = "logged_user";
    protected static final int DEFAULT_PAGE = 1;
    @Autowired
    UserDao userDao;

    protected UserWithoutPasswordDto checkIfUserIsLogged(HttpSession session){
        UserWithoutPasswordDto user = (UserWithoutPasswordDto) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        return user;
    }

    protected UserWithoutPasswordDto checkIfUserIsAdmin(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        if(!userDao.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        return user;
    }

    protected int validatePageNumber(int pageNumber){
        if (pageNumber < 1){
            return DEFAULT_PAGE;
        }
        return pageNumber;
    }

}

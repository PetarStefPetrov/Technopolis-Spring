package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.ProductDAO;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.dto.ProductDto;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class AdminController  extends GlobalException {
    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ProductDAO productDAO;

    @GetMapping("users/make_admin/{email}")
        public void makeAdmin(@PathVariable String email, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        User admin = userDAO.getUserByEmail(email);
        if(admin == null){
            throw new BadRequestException("Invalid email");
        }
        userDAO.makeAdmin(email);
    }
    @GetMapping("users/remove_admin/{email}")
    public void removeAdmin(@PathVariable String email, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        User admin = userDAO.getUserByEmail(email);
        if(admin == null){
            throw new BadRequestException("Invalid email");
        }
        userDAO.removeAdmin(email);
    }
    @PostMapping("products/")
    public Product addProduct(@RequestBody ProductDto productDto,HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        Product product = new Product(productDto);
        return productDAO.addProduct(product);
    }
    @DeleteMapping("products/{product_id}")
    public Product deleteProduct(@PathVariable long product_id,HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        Product product = productDAO.getProductById(product_id);
        if(product == null){
            throw new BadRequestException("Wrong id for product");
        }
        return productDAO.deleteProduct(product);
    }
}

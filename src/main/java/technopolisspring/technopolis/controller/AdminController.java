package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.CreateOfferDto;
import technopolisspring.technopolis.model.dto.ProductDto;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Offer;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class AdminController  extends GlobalException {

    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @Autowired
    private UserDao userDAO;
    @Autowired
    private ProductDao productDAO;
    @Autowired
    OfferDao offerDao;

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

    @PostMapping("offers")
    public CreateOfferDto addOffer(@RequestBody CreateOfferDto createOfferDto, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        offerDao.addOffer(createOfferDto);
        return createOfferDto;
    }

    @PostMapping("offers/{offerId}/products/{productId}")
    public String addProductToOffer(@PathVariable long productId, @PathVariable long offerId, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        if(!userDAO.isAdmin(user.getId())){
            throw new AuthorizationException("Must be admin");
        }
        double discount = offerDao.getOfferDiscount(offerId, productId);
        if (discount == 0.0){
            throw new BadRequestException("Offer or product doesn't exist");
        }
        if (discount == -1){
            throw new BadRequestException("Product is already in that offer");
        }
        if (!offerDao.addProductToOffer(productId, offerId, discount)){
            throw new BadRequestException("Offer or product doesn't exist");
        }
        return "Product was added successfully!";
    }

}

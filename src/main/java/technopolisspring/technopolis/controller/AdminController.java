package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.exception.NotFoundException;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.CreateOfferDto;
import technopolisspring.technopolis.model.dto.CreateProductDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.service.EmailService;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class AdminController extends AbstractController {

    @Autowired
    private UserDao userDAO;
    @Autowired
    private ProductDao productDAO;
    @Autowired
    OfferDao offerDao;
    @Autowired
    EmailService emailService;

    @PutMapping("users/make_admin/{userId}")
    public String makeAdmin(@PathVariable long userId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!userDAO.makeAdmin(userId)){
            throw new NotFoundException("User not found");
        }
        return ProductController.SUCCESS;
    }

    @PutMapping("users/remove_admin/{userId}")
    public String removeAdmin(@PathVariable long userId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!userDAO.removeAdmin(userId)){
            throw new InvalidArgumentsException("Invalid user");
        }
        return ProductController.SUCCESS;
    }

    @PostMapping("products")
    public Product addProduct(@RequestBody CreateProductDto createProductDto, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        Product product = new Product(createProductDto);
        return productDAO.addProduct(product);
    }

    @PostMapping("offers")
    public CreateOfferDto addOffer(@RequestBody CreateOfferDto createOfferDto, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (createOfferDto.getDiscountPercent() <= 0 || createOfferDto.getDiscountPercent() > 100){
            throw new InvalidArgumentsException("Discount percent must be between 0 and 1, 0 not included.");
        }
        createOfferDto.setDiscountPercent(createOfferDto.getDiscountPercent() / 100);
        this.offerDao.addOffer(createOfferDto);
        this.emailService.notifySubscribers(createOfferDto.getName(), createOfferDto.getDiscountPercent());
        return createOfferDto;
    }

    @PostMapping("offers/{offerId}/products/{productId}")
    public String addProductToOffer(@PathVariable long productId, @PathVariable long offerId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!offerDao.addProductToOffer(productId, offerId)){
            throw new BadRequestException("Offer or product doesn't exist");
        }
        return ProductController.SUCCESS;
    }

}

package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.exception.NotFoundException;
import technopolisspring.technopolis.model.daos.CategoryDao;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.dto.CreateOfferDto;
import technopolisspring.technopolis.model.dto.CreateProductDto;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.utils.EmailUtil;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class AdminController extends AbstractController {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_USER = "Invalid user";
    private static final int MAX_PERCENT = 100;
    @Autowired
    private UserDao userDAO;
    @Autowired
    private ProductDao productDAO;
    @Autowired
    private OfferDao offerDao;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private CategoryDao categoryDao;

    @PutMapping("users/make_admin/{userId}")
    public String makeAdmin(@PathVariable long userId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!userDAO.makeAdmin(userId)){
            throw new NotFoundException(USER_NOT_FOUND);
        }
        return ProductController.SUCCESS;
    }

    @PutMapping("users/remove_admin/{userId}")
    public String removeAdmin(@PathVariable long userId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!userDAO.removeAdmin(userId)){
            throw new InvalidArgumentsException(INVALID_USER);
        }
        return ProductController.SUCCESS;
    }

    @PostMapping("products")
    public CreateProductDto addProduct(@RequestBody CreateProductDto createProductDto, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if(!categoryDao.checkForBrand(createProductDto.getBrandId())){
            throw new BadRequestException("Invalid brand");
        }
        if(!categoryDao.checkForSubCategory(createProductDto.getSubCategoryId())){
            throw new BadRequestException("Invalid SubCategory");
        }
        productDAO.addProduct(createProductDto);
        return createProductDto;
    }

    @PostMapping("offers")
    public CreateOfferDto addOffer(@RequestBody CreateOfferDto createOfferDto, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (createOfferDto.getDiscountPercent() <= 0 || createOfferDto.getDiscountPercent() > MAX_PERCENT){
            throw new InvalidArgumentsException("Discount percent must be between 0 and 1, 0 not included.");
        }
        createOfferDto.setDiscountPercent(createOfferDto.getDiscountPercent() / MAX_PERCENT);
        this.offerDao.addOffer(createOfferDto);
        this.emailUtil.notifySubscribers(createOfferDto.getName(), createOfferDto.getDiscountPercent());
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

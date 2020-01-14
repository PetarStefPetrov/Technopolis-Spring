package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.dto.ProductWithoutReviewsDto;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.IProductWithReview;
import technopolisspring.technopolis.model.pojos.Product;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController extends AbstractController {

    public static final String INVALID_PRODUCT = "Invalid Product";
    public static final String INVALID_ARGUMENTS = "Invalid arguments";
    public static final String SUCCESS = "Success!";
    @Autowired
    private ProductDao productDao;
    @Autowired
    private OfferDao offerDao;

    @GetMapping("products/{productId}")
    public IProductWithReview getProduct(@PathVariable long productId) throws SQLException {
        IProductWithReview product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException(INVALID_PRODUCT);
        }
        return product;
    }

    @GetMapping("products/page/{pageNumber}")
    public List<IProduct> getAllProducts(@PathVariable int pageNumber){
        return productDao.getAllProducts(validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @GetMapping("products/sub_categories/{sub_category_id}/page/{pageNumber}")
    public List<IProduct> getAllProductsBySubCategory(@PathVariable long sub_category_id, @PathVariable int pageNumber) {
        return productDao.getProductsBySubCategory(sub_category_id, validatePageNumber(pageNumber));
    }

    @GetMapping("products/{description}/page/{pageNumber}")
    public List<IProduct> lookForProductsByDescription(@PathVariable String description, @PathVariable int pageNumber) {
        return productDao.lookForProductsByDescription(description, validatePageNumber(pageNumber));
    }

    @PostMapping("products/filters/page/{pageNumber}")
    public List<IProduct> getProductsWithFilters(@RequestBody FilterForProductsDto filterForProductsDto,
                                                 @PathVariable int pageNumber) {
        if (    filterForProductsDto.getBrandId() == 0 &&
                filterForProductsDto.getSubCategoryId() == 0 &&
                filterForProductsDto.getMaxPrice() == 0 &&
                filterForProductsDto.getMinPrice() == 0) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        if (    filterForProductsDto.getBrandId() < 0 ||
                filterForProductsDto.getSubCategoryId() < 0 ||
                filterForProductsDto.getMaxPrice() < 0 ||
                filterForProductsDto.getMinPrice() < 0 ||
                pageNumber < 1) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        if (filterForProductsDto.getMaxPrice() < filterForProductsDto.getMinPrice()) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        return productDao.getProductsWithFilters(filterForProductsDto, validatePageNumber(pageNumber));
    }

    @GetMapping("offers/page/{pageNumber}")
    public List<IProduct> getAllProductsInOffers(@PathVariable int pageNumber) {
        return offerDao.getAllProductsInOffers(validatePageNumber(pageNumber));
    }

    @DeleteMapping("products/{productId}")
    public String deleteProduct(@PathVariable long productId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!productDao.deleteProduct(productId)){
            throw new BadRequestException(INVALID_PRODUCT);
        }
        return SUCCESS;
    }

}

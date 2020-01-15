package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.pojos.IProduct;

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
    public IProduct getProduct(@PathVariable long productId) throws SQLException {
        IProduct product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException(INVALID_PRODUCT);
        }
        return product;
    }

    @GetMapping("products/page/{pageNumber}")
    public List<IProduct> getAllProducts(@RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber){
        return productDao.getAllProducts(pageNumber);
    }

    @SneakyThrows
    @GetMapping("products/sub_categories/{sub_category_id}/page/{pageNumber}")
    public List<IProduct> getAllProductsBySubCategory(@PathVariable long sub_category_id,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return productDao.getProductsBySubCategory(sub_category_id, pageNumber);
    }

    @GetMapping("products/{description}/page/{pageNumber}")
    public List<IProduct> lookForProductsByDescription(@PathVariable String description,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return productDao.lookForProductsByDescription(description, pageNumber);
    }

    @PostMapping("products/filters/page/{pageNumber}")
    public List<IProduct> getProductsWithFilters(@RequestBody FilterForProductsDto filterForProductsDto,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
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
        if (filterForProductsDto.getMinPrice() == 0 && filterForProductsDto.getMaxPrice() == 0){
            filterForProductsDto.setMaxPrice(Integer.MAX_VALUE);
        }
        return productDao.getProductsWithFilters(filterForProductsDto, pageNumber);
    }

    @GetMapping("offers/page/{pageNumber}")
    public List<IProduct> getAllProductsInOffers(@RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return offerDao.getAllProductsInOffers(pageNumber);
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

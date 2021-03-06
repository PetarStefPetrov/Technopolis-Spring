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
    private static final String INVALID_DESCRIPTION = "Invalid description";
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

    @GetMapping("products/page")
    public List<IProduct> getAllProducts(@RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber){
        return productDao.getAllProducts(validationUtil.validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @GetMapping("products/sub_categories/{sub_category_id}/page")
    public List<IProduct> getAllProductsBySubCategory(@PathVariable long sub_category_id,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return productDao.getProductsBySubCategory(sub_category_id, validationUtil.validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @GetMapping("products/brands/{brandId}/page")
    public List<IProduct> getAllProductsByBrand(@PathVariable long brandId,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return productDao.getProductsByBrand(brandId, validationUtil.validatePageNumber(pageNumber));
    }

    @GetMapping("products/description/page")
    public List<IProduct> lookForProductsByDescription(@RequestParam(required = false) String description,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        if (description == null){
            throw new BadRequestException(INVALID_DESCRIPTION);
        }
        description = description.trim();
        if (description.isEmpty() || validationUtil.invalidDescription(description)){
            throw new BadRequestException(INVALID_DESCRIPTION);
        }
        return productDao.lookForProductsByDescription(description, validationUtil.validatePageNumber(pageNumber));
    }

    @PostMapping("products/filters/page")
    public List<IProduct> getProductsByPriceRange(@RequestBody FilterForProductsDto filterForProductsDto,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        if (    filterForProductsDto.getMaxPrice() == 0 &&
                filterForProductsDto.getMinPrice() == 0) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        if (    filterForProductsDto.getMaxPrice() < 0 ||
                filterForProductsDto.getMinPrice() < 0) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        if (filterForProductsDto.getMaxPrice() < filterForProductsDto.getMinPrice()) {
            throw new BadRequestException(INVALID_ARGUMENTS);
        }
        if (filterForProductsDto.getMinPrice() == 0 && filterForProductsDto.getMaxPrice() == 0){
            filterForProductsDto.setMaxPrice(Integer.MAX_VALUE);
        }
        return productDao.getProductsByPriceRange(filterForProductsDto, validationUtil.validatePageNumber(pageNumber));
    }

    @GetMapping("offers/page")
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

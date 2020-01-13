package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController extends AbstractController {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private OfferDao offerDao;

    @GetMapping("products/{product_id}")
    public Product getProduct(@PathVariable long product_id) throws SQLException {
        Product product = productDao.getProductById(product_id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        return product;
    }

    @GetMapping("products/page/{pageNumber}")
    public List<Product> getAllProducts(@PathVariable int pageNumber){
        return productDao.getAllProducts(pageNumber);
    }

    @GetMapping("products/sub_categories/{sub_category_id}/page/{pageNumber}")
    public List<Product> getAllProductsBySubCategory(@PathVariable long sub_category_id, @PathVariable int pageNumber) throws SQLException {
        // todo: check if that category exists
        return productDao.getProductsBySubCategory(sub_category_id, pageNumber);
    }

    @GetMapping("products/reviews/{product_id}/page/{pageNumber}")
    public List<Review> getAllReviewsForProduct(@PathVariable long product_id, @PathVariable int pageNumber) throws SQLException {
        // todo: check if product exists
        return productDao.getReviews(product_id, pageNumber);
    }

    @GetMapping("products/{description}/page/{pageNumber}")
    public List<Product> lookForProductsByDescription(@PathVariable String description, @PathVariable int pageNumber) {
        return productDao.lookForProductByDescription(description, pageNumber);
    }

    @PostMapping("products/filters/page/{pageNumber}")
    public List<Product> getProductsWithFilters(@RequestBody FilterForProductsDto filterForProductsDto,
                                                @PathVariable int pageNumber) {
        if (    filterForProductsDto.getBrandId() == 0 &&
                filterForProductsDto.getSubCategoryId() == 0 &&
                filterForProductsDto.getMaxPrice() == 0 &&
                filterForProductsDto.getMinPrice() == 0) {
            throw new BadRequestException("Invalid arguments");
        }
        if (    filterForProductsDto.getBrandId() < 0 ||
                filterForProductsDto.getSubCategoryId() < 0 ||
                filterForProductsDto.getMaxPrice() < 0 ||
                filterForProductsDto.getMinPrice() < 0 ||
                pageNumber < 1) {
            throw new BadRequestException("Invalid arguments");
        }
        if (filterForProductsDto.getMaxPrice() < filterForProductsDto.getMinPrice()) {
            throw new BadRequestException("Invalid arguments");
        }
        return productDao.getProductsWithFilters(filterForProductsDto, pageNumber);
    }

    @GetMapping("offers/page/{pageNumber}")
    public List<Product> getAllProductsInOffers(@PathVariable int pageNumber) {
        return offerDao.getAllProductsInOffers(pageNumber);
    }

    @DeleteMapping("products/{productId}")
    public String deleteProduct(@PathVariable long productId, HttpSession session) throws SQLException {
        checkIfUserIsAdmin(session);
        if (!productDao.deleteProduct(productId)){
            throw new BadRequestException("There is no such product");
        }
        return "Success!";
    }

}

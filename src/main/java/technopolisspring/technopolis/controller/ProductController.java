package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OfferDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.FilterForProductsDto;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController extends GlobalException {

    @Autowired
    private ProductDao productDAO;
    @Autowired
    private OfferDao offerDao;

    @GetMapping("products/{product_id}")
    public Product getProduct(@PathVariable long product_id) throws SQLException {
        Product product = productDAO.getProductById(product_id);
        if(product == null){
            throw  new BadRequestException("Invalid Product");
        }
        return product;
    }

    @GetMapping("products/pages/{pageNumber}")
    public List<Product> getAllProducts(@PathVariable int pageNumber){
        return productDAO.getAllProducts(pageNumber);
    }

    @GetMapping("products/sub_categories/{sub_category_id}/pages/{pageNumber}")
    public List<Product> getAllProductBySubCategory(@PathVariable long sub_category_id, @PathVariable int pageNumber) throws SQLException {
        return productDAO.getProductsBySubCategory(sub_category_id, pageNumber);
    }

    @GetMapping("products/reviews/{product_id}/pages/{pageNumber}")
    public List<Review> getAllReviewForProduct(@PathVariable long product_id, @PathVariable int pageNumber) throws SQLException {
        return productDAO.getReviews(product_id, pageNumber);
    }

    @GetMapping("products/{description}/pages/{pageNumber}")
    public List<Product> lookForProductsByDescription(@PathVariable String description, @PathVariable int pageNumber) {
        return productDAO.lookForProductByDescription(description, pageNumber);
    }

    @PostMapping("products/filters/pages/{pageNumber}")
    public List<Product> getProductsWithFilters(FilterForProductsDto filterForProductsDto,
                                                @PathVariable int pageNumber) {
        if (    filterForProductsDto.getBrandId() == 0 &&
                filterForProductsDto.getSubCategoryId() == 0 &&
                filterForProductsDto.getMaxPrice() == 0 &&
                filterForProductsDto.getMinPrice() == 0) {
            throw new BadRequestException("invalid arguments");
        }
        if (    filterForProductsDto.getBrandId() < 0 ||
                filterForProductsDto.getSubCategoryId() < 0 ||
                filterForProductsDto.getMaxPrice() < 0 ||
                filterForProductsDto.getMinPrice() < 0 ||
                pageNumber < 1) {
            throw new BadRequestException("invalid arguments");
        }
        if (filterForProductsDto.getMaxPrice() < filterForProductsDto.getMinPrice()) {
            throw new BadRequestException("invalid arguments");
        }
        return productDAO.getProductsWithFilters(filterForProductsDto, pageNumber);
    }

    @GetMapping("offers/pages/{pageNumber}")
    public List<Product> getAllProductsInOffers(@PathVariable int pageNumber) throws SQLException {
        List<Product> products = offerDao.getAllProductsInOffers(pageNumber);
        return products;
    }

}

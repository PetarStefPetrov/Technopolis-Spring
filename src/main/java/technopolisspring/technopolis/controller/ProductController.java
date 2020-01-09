package technopolisspring.technopolis.controller;

import org.hibernate.event.internal.ReattachVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.ProductDAO;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.dto.ProductDto;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.Review;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@RestController
public class ProductController extends GlobalException {

    public static final int MAX_POSSIBLE_PRODUCT_PRICE = 1000000;
    @Autowired
    private ProductDAO productDAO;

    @GetMapping("products/{product_id}")
    public Product getProduct(@PathVariable long product_id) throws SQLException {
        Product product = productDAO.getProductById(product_id);
        if(product == null){
            throw  new BadRequestException("Invalid Product");
        }
        return product;
    }

    @GetMapping("products/")
    public List<Product> getAllProducts(){
        return productDAO.getAllProducts();
    }

    @GetMapping("products/categories/{category_id}")
    public List<Product> getAllProductByCategory(@PathVariable long category_id) throws SQLException {
        return productDAO.getProductsByCategory(category_id);
    }

    @GetMapping("products/sub_categories/{sub_category_id}")
    public List<Product> getAllProductBySubCategory(@PathVariable long sub_category_id) throws SQLException {
        return productDAO.getProductsBySubCategory(sub_category_id);
    }

    @GetMapping("products/reviews/{product_id}")
    public List<Review> getAllReviewForProduct(@PathVariable long product_id) throws SQLException {
        return productDAO.getReviews(product_id);
    }

    @GetMapping("products/{description}")
    public List<Product> lookForProductsByDescription(@PathVariable String description) {
        return productDAO.lookForProductByDescription(description);
    }

    @PutMapping("products/asc")
    public List<Product> sortByPriceAsc(List<Product> products){
        products.sort(Comparator.comparing(Product::getPrice));
        return products;
    }

    @PutMapping("products/desc")
    public List<Product> sortByPriceDesc(List<Product> products){
        products.sort(Comparator.comparing(Product::getPrice).reversed());
        return products;
    }

    @GetMapping("products/from/{lowerLimit}/to/{upperLimit}")
    public List<Product> getProductsWithPriceRange(@PathVariable double lowerLimit, @PathVariable double upperLimit) {
        if (lowerLimit < 0 || upperLimit > MAX_POSSIBLE_PRODUCT_PRICE){
            throw new BadRequestException("invalid price range");
        }
        return productDAO.getProductsWithPriceRange(lowerLimit, upperLimit);
    }

}

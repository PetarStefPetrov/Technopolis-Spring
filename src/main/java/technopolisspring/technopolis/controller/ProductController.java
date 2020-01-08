package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.ProductDAO;
import technopolisspring.technopolis.model.daos.UserDAO;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController extends GlobalException {
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
    @GetMapping("products/category/{category_id}")
    public List<Product> getAllProductByCategory(@PathVariable long category_id) throws SQLException {
        return productDAO.getProductByCategory(category_id);
    }
}

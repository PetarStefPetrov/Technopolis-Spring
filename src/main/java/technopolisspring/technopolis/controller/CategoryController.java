package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.CategoryDAO;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController  extends GlobalException {
    @Autowired
    public CategoryDAO categoryDAO;

    @GetMapping("categories/")
    public Map<Long,String> allCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }
    @GetMapping("categories/{category_id}")
    public Map<Long,String> allSubCategoriesById(@PathVariable long category_id) throws SQLException {
        return categoryDAO.getSubCategories(category_id);
    }
    @GetMapping("brands/")
    public Map<Long,String> getAllBrands() throws SQLException {
        return categoryDAO.getAllBrands();
    }
    @GetMapping("brands/{brand_id}")
    public List<Product> getAllProductsByBrand(@PathVariable long brand_id) throws SQLException {
        return categoryDAO.getProductsByBrand(brand_id);
    }


}

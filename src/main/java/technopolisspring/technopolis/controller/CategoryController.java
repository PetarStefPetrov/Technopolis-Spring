package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.CategoryDao;

import java.sql.SQLException;
import java.util.Map;

@RestController
public class CategoryController extends AbstractController {

    @Autowired
    public CategoryDao categoryDAO;

    @GetMapping("categories/page/{pageNumber}")
    public Map<Long,String> allCategories(@PathVariable int pageNumber) throws SQLException {
        return categoryDAO.getAllCategories(validatePageNumber(pageNumber));
    }

    @GetMapping("categories/{category_id}/page/{pageNumber}")
    public Map<Long,String> allSubCategoriesById(@PathVariable long category_id, @PathVariable int pageNumber) throws SQLException {
        return categoryDAO.getSubCategories(category_id, validatePageNumber(pageNumber));
    }

    @GetMapping("brands/page/{pageNumber}")
    public Map<Long,String> getAllBrands(@PathVariable int pageNumber) throws SQLException {
        return categoryDAO.getAllBrands(validatePageNumber(pageNumber));
    }

}

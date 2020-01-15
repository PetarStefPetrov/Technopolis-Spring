package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.daos.CategoryDao;

import java.util.Map;

@RestController
public class CategoryController extends AbstractController {

    @Autowired
    public CategoryDao categoryDAO;

    @SneakyThrows
    @GetMapping("categories/page")
    public Map<Long,String> allCategories(@RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return categoryDAO.getAllCategories(pageNumber);
    }

    @SneakyThrows
    @GetMapping("categories/{category_id}/page")
    public Map<Long,String> allSubCategoriesById(@PathVariable long category_id,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return categoryDAO.getSubCategories(category_id, pageNumber);
    }

    @SneakyThrows
    @GetMapping("brands/page")
    public Map<Long,String> getAllBrands(@RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        return categoryDAO.getAllBrands(pageNumber);
    }

}

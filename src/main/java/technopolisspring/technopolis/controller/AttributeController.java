package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.AttributeDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.Product;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class AttributeController extends AbstractController {
    @Autowired
    AttributeDao attributeDao;
    @Autowired
    ProductDao productDao;

    @PostMapping("products/{productId}/attributes")
     public Attribute addAttribute(@RequestBody Attribute attribute, HttpSession session, @PathVariable Long productId) throws SQLException {
        checkIfUserIsAdmin(session);
        Product product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid product");
        }
        attributeDao.addAttribute(attribute,productId);
        return attribute;
    }

    @GetMapping("attributes/page/{pageNumber}")
    public List<Attribute> getAllAttributes(HttpSession session, @PathVariable int pageNumber) throws SQLException {
        checkIfUserIsAdmin(session);
        return attributeDao.getAllAttributes(validatePageNumber(pageNumber));
    }

    @DeleteMapping("products/{productId}/attributes/{attributeId}/")
    public void  removeAttribute(@PathVariable Long attributeId, @PathVariable Long productId) throws SQLException {
        Product product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(!attributeDao.deleteAttribute(attributeId, productId)){
            throw new BadRequestException("Invalid attribute");
        }
    }
}

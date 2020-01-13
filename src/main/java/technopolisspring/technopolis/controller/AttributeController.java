package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.AttributeDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.Product;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public class AttributeController extends AbstractController {
    @Autowired
    AttributeDao attributeDao;
    @Autowired
    ProductDao productDao;

    @PostMapping("products/{product_id}/attributes/")
     public  void addAttribute(@RequestBody Attribute attribute, HttpSession session, @PathVariable Long productId) throws SQLException {
        checkIfUserIsAdmin(session);
        checkIfUserIsLogged(session);
        Product product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid product");
        }
        attributeDao.addAttribute(attribute,productId);
    }
    @GetMapping("attributes/page/{page_number}")
    public List<Attribute> allAttributes(HttpSession session, @PathVariable Long pageNumber) throws SQLException {
        checkIfUserIsLogged(session);
        checkIfUserIsAdmin(session);
        return attributeDao.getAllAttributes();
    }
    @DeleteMapping("products/{productId}/attributes/{attributesId}/")
    public void  removeAttribute(@PathVariable Long attributesId,@PathVariable Long productId) throws SQLException {
        Product product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        if(!attributeDao.deleteAttribute(attributesId,productId)){
            throw new BadRequestException("Invalid attribute");
        }
    }
}

package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.model.daos.AttributeDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.AddAttributeToProductDto;
import technopolisspring.technopolis.model.dto.AttributeWithoutValueDto;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.IProduct;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class AttributeController extends AbstractController {

    private static final String INVALID_ATTRIBUTE = "Invalid attribute";
    @Autowired
    AttributeDao attributeDao;
    @Autowired
    ProductDao productDao;

    @SneakyThrows
    @PostMapping("products/{productId}/attributes")
     public Attribute addAttributeToProduct(@RequestBody AddAttributeToProductDto attributeToAdd,
                                                           HttpSession session,
                                                           @PathVariable long productId) {
        checkIfUserIsAdmin(session);
        IProduct product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException(ProductController.INVALID_PRODUCT);
        }
        AttributeWithoutValueDto attributeWithoutValueDto = attributeDao.getAttributeById(attributeToAdd.getId());
        if (attributeWithoutValueDto == null){
            throw new BadRequestException(INVALID_ATTRIBUTE);
        }
        attributeDao.addAttributeToProduct(attributeToAdd, productId);
        return new Attribute(
                attributeWithoutValueDto.getId(),
                attributeWithoutValueDto.getName(),
                attributeWithoutValueDto.getSubCategoryId(),
                attributeToAdd.getValue()
        );
     }

    @SneakyThrows
    @GetMapping("attributes/page/{pageNumber}")
    public List<AttributeWithoutValueDto> getAllAttributes(HttpSession session, @PathVariable int pageNumber) {
        checkIfUserIsAdmin(session);
        return attributeDao.getAllAttributes(validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @DeleteMapping("attributes/{attributeId}")
    public String deleteAttribute(@PathVariable Long attributeId, HttpSession session) {
        checkIfUserIsAdmin(session);
        if(!attributeDao.deleteAttribute(attributeId)){
            throw new BadRequestException(INVALID_ATTRIBUTE);
        }
        return ProductController.SUCCESS;
    }

    @SneakyThrows
    @PostMapping("attributes")
    public AttributeWithoutValueDto addAttribute(@RequestBody AttributeWithoutValueDto attribute,
                                                 HttpSession session){
        checkIfUserIsAdmin(session);
        attributeDao.addAttribute(attribute);
        return attribute;
    }

    // todo: remove attribute from product and attach the attributes to the products
}

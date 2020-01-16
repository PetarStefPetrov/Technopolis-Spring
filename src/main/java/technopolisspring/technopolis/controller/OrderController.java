package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.model.daos.OrderDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.IProduct;
import technopolisspring.technopolis.model.pojos.Order;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController extends AbstractController {

    public static final String SESSION_KEY_BASKET_USER = "user_basket";
    private static final String BASKET_IS_EMPTY = "Basket is empty";
    private static final String INVALID_PRODUCT = "Invalid Product";
    private static final String INVALID_REQUEST = "Invalid request";
    @Autowired
    public ProductDao productDAO;
    @Autowired
    public OrderDao orderDao;

    @PostMapping("/orders")
    public Order addOrder(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        Map<IProduct, Integer> basket = (Map<IProduct, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException(BASKET_IS_EMPTY);
        }
        Order order = new Order(user.getId(), user.getAddress(), basket);
        orderDao.addOrder(order);
        session.removeAttribute(SESSION_KEY_BASKET_USER);
        return order;
    }

    @PostMapping("orders/products/{product_id}")
    public Map<IProduct, Integer> addProductToBasket(@PathVariable long product_id, HttpSession session) throws SQLException {
        IProduct product = productDAO.getProductById(product_id);
        if(product == null){
            throw new BadRequestException(INVALID_PRODUCT);
        }
        Map<IProduct, Integer> basket = (Map<IProduct, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            basket = new HashMap<>();
        }
        if(!basket.containsKey(product)){
            basket.put(product,1);
        } else {
            Integer number = basket.get(product);
            number++;
            basket.put(product,number);
        }
        session.setAttribute(SESSION_KEY_BASKET_USER,basket);
        return basket;
    }

    @DeleteMapping("orders/products/{product_id}")
    public Map<IProduct, Integer> removeProductFromBasket(@PathVariable long product_id, HttpSession session) throws SQLException {
        IProduct product = productDAO.getProductById(product_id);
        if(product == null){
            throw new BadRequestException(INVALID_PRODUCT);
        }
        Map<IProduct, Integer> basket = (Map<IProduct, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException(BASKET_IS_EMPTY);
        }
        if(!basket.containsKey(product)){
            throw new BadRequestException(INVALID_REQUEST);
        } else {
            int quantity = basket.get(product);
            if (quantity == 1){
                basket.remove(product);
            } else {
                basket.put(product,--quantity);
            }
        }
        session.removeAttribute(SESSION_KEY_BASKET_USER);
        session.setAttribute(SESSION_KEY_BASKET_USER, basket);
        return basket;
    }

    @GetMapping("orders/products")
    public Map<IProduct, Integer> getProducts(HttpSession session) {
        Map<IProduct, Integer> basket = (Map<IProduct, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            basket = new HashMap<>();
        }
        return basket;
    }
}

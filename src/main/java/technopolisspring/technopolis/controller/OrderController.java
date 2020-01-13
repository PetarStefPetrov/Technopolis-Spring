package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OrderDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController extends AbstractController {

    public static final String SESSION_KEY_BASKET_USER = "user_basket";
    @Autowired
    public ProductDao productDAO;
    @Autowired
    public OrderDao orderDao;

    @PostMapping("/orders")
    public Order addOrder(HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException("Basket is empty");
        }
        Order order = new Order(user.getId(), user.getAddress(), basket);
        orderDao.addOrder(order);
        session.removeAttribute(SESSION_KEY_BASKET_USER);
        return order;
    }

    @PostMapping("orders/products/{product_id}")
    public Map<Product, Integer> addProductToBasket(@PathVariable long product_id, HttpSession session) throws SQLException {
        Product product = productDAO.getProductById(product_id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
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
    public Map<Product, Integer> removeProductFromBasket(@PathVariable long product_id, HttpSession session) throws SQLException {
        Product product = productDAO.getProductById(product_id);
        if(product == null){
            throw new BadRequestException("Invalid Product");
        }
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException("Basket is empty");
        }
        if(!basket.containsKey(product)){
            throw new BadRequestException("Invalid request");
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
    public Map<Product, Integer> getProducts(HttpSession session) {
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException("Basket is empty");
        }
        return basket;
    }
}

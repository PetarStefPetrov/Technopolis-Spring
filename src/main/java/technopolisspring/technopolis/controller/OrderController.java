package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.model.daos.OrderDao;
import technopolisspring.technopolis.model.daos.ProductDAO;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.exception.BadRequestException;
import technopolisspring.technopolis.model.exception.GlobalException;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.Product;
import technopolisspring.technopolis.model.pojos.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController extends GlobalException {


    public static final String SESSION_KEY_LOGGED_USER = "logged_user";
    public static final String SESSION_KEY_BASKET_USER = "user_basket";
    @Autowired
    public ProductDAO productDAO;
    @Autowired
    public OrderDao orderDao;

    @PostMapping("/orders/add")
    public Order addOrder(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException("Basket is empty");
        }
        Order order = new Order(user.getId(), user.getAddress(), basket);
        Order orderWithId = orderDao.addOrder(order);
       //TODO return orderWithID after fix
        return order;
    }

    @PostMapping("orders/products/add/{product_id}")
    public Map<Product, Integer> addProductToBuy(@PathVariable long product_id,HttpSession session) throws SQLException {
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

    @DeleteMapping("orders/products/remove/{product_id}")
    public Map<Product, Integer> removeProductToBuy(@PathVariable long product_id,HttpSession session) throws SQLException {
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
            basket.remove(product);
        }
        session.removeAttribute(SESSION_KEY_BASKET_USER);
        session.setAttribute(SESSION_KEY_BASKET_USER,basket);
        return basket;
    }

    @GetMapping("orders/products")
    public Map<Product, Integer> removeProductToBuy(HttpSession session) throws SQLException {
        Map<Product,Integer> basket = (Map<Product, Integer>) session.getAttribute(SESSION_KEY_BASKET_USER);
        if(basket == null){
            throw new BadRequestException("Basket is empty");
        }
        return basket;
    }
}

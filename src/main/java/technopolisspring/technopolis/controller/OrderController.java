package technopolisspring.technopolis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import technopolisspring.technopolis.model.exception.AuthorizationException;
import technopolisspring.technopolis.model.pojos.Order;
import technopolisspring.technopolis.model.pojos.User;
import technopolisspring.technopolis.model.repository.IOrderRepository;

import javax.servlet.http.HttpSession;

@RestController
public class OrderController {

    @Autowired
    private IOrderRepository orderRepository;

    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @PostMapping("/orders/add")
    public Order addOrder(HttpSession session){
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException("Must be logged in");
        }

        return null;
    }



}

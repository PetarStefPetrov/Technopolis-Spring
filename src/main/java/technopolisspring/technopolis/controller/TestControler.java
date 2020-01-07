package technopolisspring.technopolis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControler {

    @GetMapping("/hi")
    public String sayHi(){
        return "hi";
    }

}

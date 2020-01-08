package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
public class Review {

    private long id;
    private String name;
    private String title;
    private String comment;
    private Product product;
    private User user;

    public Review(long id, String name, String title, String comment, Product product, User user) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.comment = comment;
        this.product = product;
        this.user = user;
    }

    public long getId() {
        return id;
    }
}

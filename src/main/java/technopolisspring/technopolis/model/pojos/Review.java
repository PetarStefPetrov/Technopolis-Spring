package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String title;
    @Column
    private String comment;
    @Column
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Review(int id, String name, String title, String comment, Product product, User user) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.comment = comment;
        this.product = product;
        this.user = user;
    }

    public int getId() {
        return id;
    }
}

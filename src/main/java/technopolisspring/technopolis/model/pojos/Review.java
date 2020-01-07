package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Review {

    private int id;
    private String name;
    private String title;
    private String comment;
    private int productID;
    private int userID;

    public Review(int id, String name, String title, String comment, int productID, int userID) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.comment = comment;
        this.productID = productID;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }
}

package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Attribute {

    private int id;
    private String name;
    private String subCategory;

    public Attribute(int id, String name, String subCategory) {
        this.id = id;
        this.name = name;
        this.subCategory = subCategory;
    }

}

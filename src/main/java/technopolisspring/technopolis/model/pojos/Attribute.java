package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Attribute {

    private long id;
    private String name;
    private String subCategory;

    public Attribute(long id, String name, String subCategory) {
        this.id = id;
        this.name = name;
        this.subCategory = subCategory;
    }

}

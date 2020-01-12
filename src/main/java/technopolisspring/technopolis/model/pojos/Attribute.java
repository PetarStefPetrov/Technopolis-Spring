package technopolisspring.technopolis.model.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    private long id;
    private String name;
    private long subCategoryId;
    private long productId;
    private String value;

    public Attribute(String name, long subCategoryId) {
        this.name = name;
        this.subCategoryId = subCategoryId;
    }

    public Attribute(long id, String name, long subCategoryId) {
        this(name, subCategoryId);
        this.id = id;
    }
}

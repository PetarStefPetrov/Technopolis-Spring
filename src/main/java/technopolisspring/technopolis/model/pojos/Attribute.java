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
    private String value;

}

package technopolisspring.technopolis.model.pojos;

import java.util.List;

public interface IProductWithAttributes extends IProduct {

    List<Attribute> getAttributes();

    void setAttributes(List<Attribute> attributes);

}

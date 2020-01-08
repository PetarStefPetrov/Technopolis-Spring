package technopolisspring.technopolis.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import technopolisspring.technopolis.model.pojos.Product;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

    Product getProductByBrandId(long brandId);
    Product getProductBySubCategoryId(long subCategoryId);
//    Product getProductByCategoryId(long categoryId); todo create a dao for that
    Product getProductByPriceBetween(double lowerLimit, double upperLimit);

}

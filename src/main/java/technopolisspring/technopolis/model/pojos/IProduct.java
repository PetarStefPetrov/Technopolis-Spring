package technopolisspring.technopolis.model.pojos;

public interface IProduct {

    public long getId();

    public void setId(long id);

    public String getDescription();

    public void setDescription(String description);

    public double getPrice();

    public void setPrice(double price);

    public String getPictureUrl();

    public void setPictureUrl(String pictureUrl);

    public long getBrandId();

    public void setBrandId(long brandId);

    public long getSubCategoryId();

    public void setSubCategoryId(long subCategoryId);

    public long getOfferId();

    public void setOfferId(long offerId);

}

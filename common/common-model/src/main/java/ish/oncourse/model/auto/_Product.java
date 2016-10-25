package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.ExpiryType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.ProductItem;

/**
 * Class _Product was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Product extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String EXPIRY_DAYS_PROPERTY = "expiryDays";
    public static final String EXPIRY_TYPE_PROPERTY = "expiryType";
    public static final String IS_ON_SALE_PROPERTY = "isOnSale";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NOTES_PROPERTY = "notes";
    public static final String PRICE_EX_TAX_PROPERTY = "priceExTax";
    public static final String SKU_PROPERTY = "sku";
    public static final String TAX_ADJUSTMENT_PROPERTY = "taxAdjustment";
    public static final String TAX_AMOUNT_PROPERTY = "taxAmount";
    public static final String TYPE_PROPERTY = "type";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String PRODUCT_ITEMS_PROPERTY = "productItems";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DESCRIPTION = new Property<String>("description");
    public static final Property<Integer> EXPIRY_DAYS = new Property<Integer>("expiryDays");
    public static final Property<ExpiryType> EXPIRY_TYPE = new Property<ExpiryType>("expiryType");
    public static final Property<Boolean> IS_ON_SALE = new Property<Boolean>("isOnSale");
    public static final Property<Boolean> IS_WEB_VISIBLE = new Property<Boolean>("isWebVisible");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> NOTES = new Property<String>("notes");
    public static final Property<Money> PRICE_EX_TAX = new Property<Money>("priceExTax");
    public static final Property<String> SKU = new Property<String>("sku");
    public static final Property<Money> TAX_ADJUSTMENT = new Property<Money>("taxAdjustment");
    public static final Property<Money> TAX_AMOUNT = new Property<Money>("taxAmount");
    public static final Property<Integer> TYPE = new Property<Integer>("type");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<ProductItem>> PRODUCT_ITEMS = new Property<List<ProductItem>>("productItems");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setExpiryDays(Integer expiryDays) {
        writeProperty("expiryDays", expiryDays);
    }
    public Integer getExpiryDays() {
        return (Integer)readProperty("expiryDays");
    }

    public void setExpiryType(ExpiryType expiryType) {
        writeProperty("expiryType", expiryType);
    }
    public ExpiryType getExpiryType() {
        return (ExpiryType)readProperty("expiryType");
    }

    public void setIsOnSale(Boolean isOnSale) {
        writeProperty("isOnSale", isOnSale);
    }
    public Boolean getIsOnSale() {
        return (Boolean)readProperty("isOnSale");
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty("isWebVisible", isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty("isWebVisible");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setNotes(String notes) {
        writeProperty("notes", notes);
    }
    public String getNotes() {
        return (String)readProperty("notes");
    }

    public void setPriceExTax(Money priceExTax) {
        writeProperty("priceExTax", priceExTax);
    }
    public Money getPriceExTax() {
        return (Money)readProperty("priceExTax");
    }

    public void setSku(String sku) {
        writeProperty("sku", sku);
    }
    public String getSku() {
        return (String)readProperty("sku");
    }

    public void setTaxAdjustment(Money taxAdjustment) {
        writeProperty("taxAdjustment", taxAdjustment);
    }
    public Money getTaxAdjustment() {
        return (Money)readProperty("taxAdjustment");
    }

    public void setTaxAmount(Money taxAmount) {
        writeProperty("taxAmount", taxAmount);
    }
    public Money getTaxAmount() {
        return (Money)readProperty("taxAmount");
    }

    public void setType(Integer type) {
        writeProperty("type", type);
    }
    public Integer getType() {
        return (Integer)readProperty("type");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToProductItems(ProductItem obj) {
        addToManyTarget("productItems", obj, true);
    }
    public void removeFromProductItems(ProductItem obj) {
        removeToManyTarget("productItems", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProductItems() {
        return (List<ProductItem>)readProperty("productItems");
    }


    protected abstract void onPostAdd();

}

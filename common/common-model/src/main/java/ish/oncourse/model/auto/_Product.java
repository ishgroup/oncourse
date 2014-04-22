package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

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

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDescription(String description) {
        writeProperty(DESCRIPTION_PROPERTY, description);
    }
    public String getDescription() {
        return (String)readProperty(DESCRIPTION_PROPERTY);
    }

    public void setExpiryDays(Integer expiryDays) {
        writeProperty(EXPIRY_DAYS_PROPERTY, expiryDays);
    }
    public Integer getExpiryDays() {
        return (Integer)readProperty(EXPIRY_DAYS_PROPERTY);
    }

    public void setExpiryType(ExpiryType expiryType) {
        writeProperty(EXPIRY_TYPE_PROPERTY, expiryType);
    }
    public ExpiryType getExpiryType() {
        return (ExpiryType)readProperty(EXPIRY_TYPE_PROPERTY);
    }

    public void setIsOnSale(Boolean isOnSale) {
        writeProperty(IS_ON_SALE_PROPERTY, isOnSale);
    }
    public Boolean getIsOnSale() {
        return (Boolean)readProperty(IS_ON_SALE_PROPERTY);
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty(IS_WEB_VISIBLE_PROPERTY, isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty(IS_WEB_VISIBLE_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setNotes(String notes) {
        writeProperty(NOTES_PROPERTY, notes);
    }
    public String getNotes() {
        return (String)readProperty(NOTES_PROPERTY);
    }

    public void setPriceExTax(Money priceExTax) {
        writeProperty(PRICE_EX_TAX_PROPERTY, priceExTax);
    }
    public Money getPriceExTax() {
        return (Money)readProperty(PRICE_EX_TAX_PROPERTY);
    }

    public void setSku(String sku) {
        writeProperty(SKU_PROPERTY, sku);
    }
    public String getSku() {
        return (String)readProperty(SKU_PROPERTY);
    }

    public void setTaxAdjustment(Money taxAdjustment) {
        writeProperty(TAX_ADJUSTMENT_PROPERTY, taxAdjustment);
    }
    public Money getTaxAdjustment() {
        return (Money)readProperty(TAX_ADJUSTMENT_PROPERTY);
    }

    public void setTaxAmount(Money taxAmount) {
        writeProperty(TAX_AMOUNT_PROPERTY, taxAmount);
    }
    public Money getTaxAmount() {
        return (Money)readProperty(TAX_AMOUNT_PROPERTY);
    }

    public void setType(Integer type) {
        writeProperty(TYPE_PROPERTY, type);
    }
    public Integer getType() {
        return (Integer)readProperty(TYPE_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void addToProductItems(ProductItem obj) {
        addToManyTarget(PRODUCT_ITEMS_PROPERTY, obj, true);
    }
    public void removeFromProductItems(ProductItem obj) {
        removeToManyTarget(PRODUCT_ITEMS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProductItems() {
        return (List<ProductItem>)readProperty(PRODUCT_ITEMS_PROPERTY);
    }

    protected abstract void onPostAdd();

}

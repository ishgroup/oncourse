package ish.oncourse.model.auto;

import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.Product;

/**
 * Class _MembershipProduct was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _MembershipProduct extends Product {

    private static final long serialVersionUID = 1L; 

    public static final String DISCOUNT_MEMBERSHIP_PRODUCTS_PROPERTY = "discountMembershipProducts";
    public static final String DISCOUNTS_AVAILABLE_PROPERTY = "discountsAvailable";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<List<DiscountMembership>> DISCOUNT_MEMBERSHIP_PRODUCTS = Property.create("discountMembershipProducts", List.class);
    public static final Property<List<Discount>> DISCOUNTS_AVAILABLE = Property.create("discountsAvailable", List.class);

    public void addToDiscountMembershipProducts(DiscountMembership obj) {
        addToManyTarget("discountMembershipProducts", obj, true);
    }
    public void removeFromDiscountMembershipProducts(DiscountMembership obj) {
        removeToManyTarget("discountMembershipProducts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembership> getDiscountMembershipProducts() {
        return (List<DiscountMembership>)readProperty("discountMembershipProducts");
    }


    public void addToDiscountsAvailable(Discount obj) {
        addToManyTarget("discountsAvailable", obj, true);
    }
    public void removeFromDiscountsAvailable(Discount obj) {
        removeToManyTarget("discountsAvailable", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Discount> getDiscountsAvailable() {
        return (List<Discount>)readProperty("discountsAvailable");
    }


}

package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountMembershipRelationType;
import ish.oncourse.model.MembershipProduct;

/**
 * Class _DiscountMembership was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DiscountMembership extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String APPLY_TO_MEMBER_ONLY_PROPERTY = "applyToMemberOnly";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String DISCOUNT_PROPERTY = "discount";
    @Deprecated
    public static final String DISCOUNT_MEMBERSHIP_RELATION_TYPES_PROPERTY = "discountMembershipRelationTypes";
    @Deprecated
    public static final String MEMBERSHIP_PRODUCT_PROPERTY = "membershipProduct";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Boolean> APPLY_TO_MEMBER_ONLY = new Property<Boolean>("applyToMemberOnly");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Discount> DISCOUNT = new Property<Discount>("discount");
    public static final Property<List<DiscountMembershipRelationType>> DISCOUNT_MEMBERSHIP_RELATION_TYPES = new Property<List<DiscountMembershipRelationType>>("discountMembershipRelationTypes");
    public static final Property<MembershipProduct> MEMBERSHIP_PRODUCT = new Property<MembershipProduct>("membershipProduct");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setApplyToMemberOnly(Boolean applyToMemberOnly) {
        writeProperty("applyToMemberOnly", applyToMemberOnly);
    }
    public Boolean getApplyToMemberOnly() {
        return (Boolean)readProperty("applyToMemberOnly");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setDiscount(Discount discount) {
        setToOneTarget("discount", discount, true);
    }

    public Discount getDiscount() {
        return (Discount)readProperty("discount");
    }


    public void addToDiscountMembershipRelationTypes(DiscountMembershipRelationType obj) {
        addToManyTarget("discountMembershipRelationTypes", obj, true);
    }
    public void removeFromDiscountMembershipRelationTypes(DiscountMembershipRelationType obj) {
        removeToManyTarget("discountMembershipRelationTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembershipRelationType> getDiscountMembershipRelationTypes() {
        return (List<DiscountMembershipRelationType>)readProperty("discountMembershipRelationTypes");
    }


    public void setMembershipProduct(MembershipProduct membershipProduct) {
        setToOneTarget("membershipProduct", membershipProduct, true);
    }

    public MembershipProduct getMembershipProduct() {
        return (MembershipProduct)readProperty("membershipProduct");
    }


}

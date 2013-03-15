package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.ContactRelationType;
import ish.oncourse.model.DiscountMembership;

/**
 * Class _DiscountMembershipRelationType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DiscountMembershipRelationType extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_RELATION_TYPE_PROPERTY = "contactRelationType";
    public static final String DISCOUNT_MEMBERSHIP_PROPERTY = "discountMembership";

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

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setContactRelationType(ContactRelationType contactRelationType) {
        setToOneTarget(CONTACT_RELATION_TYPE_PROPERTY, contactRelationType, true);
    }

    public ContactRelationType getContactRelationType() {
        return (ContactRelationType)readProperty(CONTACT_RELATION_TYPE_PROPERTY);
    }


    public void setDiscountMembership(DiscountMembership discountMembership) {
        setToOneTarget(DISCOUNT_MEMBERSHIP_PROPERTY, discountMembership, true);
    }

    public DiscountMembership getDiscountMembership() {
        return (DiscountMembership)readProperty(DISCOUNT_MEMBERSHIP_PROPERTY);
    }


}

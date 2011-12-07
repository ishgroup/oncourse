package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.DiscountMembershipRelationType;

/**
 * Class _ContactRelationType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ContactRelationType extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String FROM_CONTACT_NAME_PROPERTY = "fromContactName";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String TO_CONTACT_NAME_PROPERTY = "toContactName";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_RELATIONS_PROPERTY = "contactRelations";
    public static final String MEMBESHIP_DISCOUNT_RELATIONS_PROPERTY = "membeshipDiscountRelations";

    public static final String ID_PK_COLUMN = "id";

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

    public void setFromContactName(String fromContactName) {
        writeProperty("fromContactName", fromContactName);
    }
    public String getFromContactName() {
        return (String)readProperty("fromContactName");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setToContactName(String toContactName) {
        writeProperty("toContactName", toContactName);
    }
    public String getToContactName() {
        return (String)readProperty("toContactName");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToContactRelations(ContactRelation obj) {
        addToManyTarget("contactRelations", obj, true);
    }
    public void removeFromContactRelations(ContactRelation obj) {
        removeToManyTarget("contactRelations", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getContactRelations() {
        return (List<ContactRelation>)readProperty("contactRelations");
    }


    public void addToMembeshipDiscountRelations(DiscountMembershipRelationType obj) {
        addToManyTarget("membeshipDiscountRelations", obj, true);
    }
    public void removeFromMembeshipDiscountRelations(DiscountMembershipRelationType obj) {
        removeToManyTarget("membeshipDiscountRelations", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembershipRelationType> getMembeshipDiscountRelations() {
        return (List<DiscountMembershipRelationType>)readProperty("membeshipDiscountRelations");
    }


}

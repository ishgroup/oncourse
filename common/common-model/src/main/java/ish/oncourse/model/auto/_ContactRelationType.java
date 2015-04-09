package ish.oncourse.model.auto;

import ish.oncourse.model.College;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.DiscountMembershipRelationType;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;
import java.util.List;

/**
 * Class _ContactRelationType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ContactRelationType extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DELEGATED_ACCESS_TO_CONTACT_PROPERTY = "delegatedAccessToContact";
    @Deprecated
    public static final String FROM_CONTACT_NAME_PROPERTY = "fromContactName";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String TO_CONTACT_NAME_PROPERTY = "toContactName";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String CONTACT_RELATIONS_PROPERTY = "contactRelations";
    @Deprecated
    public static final String MEMBESHIP_DISCOUNT_RELATIONS_PROPERTY = "membeshipDiscountRelations";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Boolean> DELEGATED_ACCESS_TO_CONTACT = new Property<Boolean>("delegatedAccessToContact");
    public static final Property<String> FROM_CONTACT_NAME = new Property<String>("fromContactName");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> TO_CONTACT_NAME = new Property<String>("toContactName");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<ContactRelation>> CONTACT_RELATIONS = new Property<List<ContactRelation>>("contactRelations");
    public static final Property<List<DiscountMembershipRelationType>> MEMBESHIP_DISCOUNT_RELATIONS = new Property<List<DiscountMembershipRelationType>>("membeshipDiscountRelations");

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

    public void setDelegatedAccessToContact(Boolean delegatedAccessToContact) {
        writeProperty("delegatedAccessToContact", delegatedAccessToContact);
    }
    public Boolean getDelegatedAccessToContact() {
        return (Boolean)readProperty("delegatedAccessToContact");
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


    protected abstract void onPostAdd();

}

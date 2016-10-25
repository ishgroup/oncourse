package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelationType;

/**
 * Class _ContactRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ContactRelation extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String FROM_CONTACT_PROPERTY = "fromContact";
    public static final String RELATION_TYPE_PROPERTY = "relationType";
    public static final String TO_CONTACT_PROPERTY = "toContact";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Contact> FROM_CONTACT = new Property<Contact>("fromContact");
    public static final Property<ContactRelationType> RELATION_TYPE = new Property<ContactRelationType>("relationType");
    public static final Property<Contact> TO_CONTACT = new Property<Contact>("toContact");

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


    public void setFromContact(Contact fromContact) {
        setToOneTarget("fromContact", fromContact, true);
    }

    public Contact getFromContact() {
        return (Contact)readProperty("fromContact");
    }


    public void setRelationType(ContactRelationType relationType) {
        setToOneTarget("relationType", relationType, true);
    }

    public ContactRelationType getRelationType() {
        return (ContactRelationType)readProperty("relationType");
    }


    public void setToContact(Contact toContact) {
        setToOneTarget("toContact", toContact, true);
    }

    public Contact getToContact() {
        return (Contact)readProperty("toContact");
    }


}

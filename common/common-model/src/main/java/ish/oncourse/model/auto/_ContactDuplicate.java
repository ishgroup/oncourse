package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.ContactDuplicateStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.SystemUser;

/**
 * Class _ContactDuplicate was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ContactDuplicate extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CONTACT_TO_DELETE_ANGEL_ID_PROPERTY = "contactToDeleteAngelId";
    @Deprecated
    public static final String CONTACT_TO_DELETE_ID_PROPERTY = "contactToDeleteId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DESCRIPTION_PROPERTY = "description";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String STATUS_PROPERTY = "status";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String CONTACT_TO_UPDATE_PROPERTY = "contactToUpdate";
    @Deprecated
    public static final String CREATED_BY_USER_PROPERTY = "createdByUser";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Long> CONTACT_TO_DELETE_ANGEL_ID = new Property<Long>("contactToDeleteAngelId");
    public static final Property<Long> CONTACT_TO_DELETE_ID = new Property<Long>("contactToDeleteId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DESCRIPTION = new Property<String>("description");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<ContactDuplicateStatus> STATUS = new Property<ContactDuplicateStatus>("status");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Contact> CONTACT_TO_UPDATE = new Property<Contact>("contactToUpdate");
    public static final Property<SystemUser> CREATED_BY_USER = new Property<SystemUser>("createdByUser");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setContactToDeleteAngelId(Long contactToDeleteAngelId) {
        writeProperty("contactToDeleteAngelId", contactToDeleteAngelId);
    }
    public Long getContactToDeleteAngelId() {
        return (Long)readProperty("contactToDeleteAngelId");
    }

    public void setContactToDeleteId(Long contactToDeleteId) {
        writeProperty("contactToDeleteId", contactToDeleteId);
    }
    public Long getContactToDeleteId() {
        return (Long)readProperty("contactToDeleteId");
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

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setStatus(ContactDuplicateStatus status) {
        writeProperty("status", status);
    }
    public ContactDuplicateStatus getStatus() {
        return (ContactDuplicateStatus)readProperty("status");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setContactToUpdate(Contact contactToUpdate) {
        setToOneTarget("contactToUpdate", contactToUpdate, true);
    }

    public Contact getContactToUpdate() {
        return (Contact)readProperty("contactToUpdate");
    }


    public void setCreatedByUser(SystemUser createdByUser) {
        setToOneTarget("createdByUser", createdByUser, true);
    }

    public SystemUser getCreatedByUser() {
        return (SystemUser)readProperty("createdByUser");
    }


}

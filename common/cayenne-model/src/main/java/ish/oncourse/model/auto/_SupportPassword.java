package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Contact;

/**
 * Class _SupportPassword was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SupportPassword extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String CREATED_ON_PROPERTY = "createdOn";
    public static final String EXPIRES_ON_PROPERTY = "expiresOn";
    public static final String MODIFIED_ON_PROPERTY = "modifiedOn";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String CONTACT_PROPERTY = "contact";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED_ON = Property.create("createdOn", Date.class);
    public static final Property<Date> EXPIRES_ON = Property.create("expiresOn", Date.class);
    public static final Property<Date> MODIFIED_ON = Property.create("modifiedOn", Date.class);
    public static final Property<String> PASSWORD = Property.create("password", String.class);
    public static final Property<Contact> CONTACT = Property.create("contact", Contact.class);

    public void setCreatedOn(Date createdOn) {
        writeProperty("createdOn", createdOn);
    }
    public Date getCreatedOn() {
        return (Date)readProperty("createdOn");
    }

    public void setExpiresOn(Date expiresOn) {
        writeProperty("expiresOn", expiresOn);
    }
    public Date getExpiresOn() {
        return (Date)readProperty("expiresOn");
    }

    public void setModifiedOn(Date modifiedOn) {
        writeProperty("modifiedOn", modifiedOn);
    }
    public Date getModifiedOn() {
        return (Date)readProperty("modifiedOn");
    }

    public void setPassword(String password) {
        writeProperty("password", password);
    }
    public String getPassword() {
        return (String)readProperty("password");
    }

    public void setContact(Contact contact) {
        setToOneTarget("contact", contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty("contact");
    }


}

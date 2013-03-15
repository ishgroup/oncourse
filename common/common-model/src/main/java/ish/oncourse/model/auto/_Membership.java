package ish.oncourse.model.auto;

import java.util.Date;

import ish.oncourse.model.Contact;
import ish.oncourse.model.ProductItem;

/**
 * Class _Membership was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Membership extends ProductItem {

    public static final String EXPIRY_DATE_PROPERTY = "expiryDate";
    public static final String CONTACT_PROPERTY = "contact";

    public static final String ID_PK_COLUMN = "id";

    public void setExpiryDate(Date expiryDate) {
        writeProperty(EXPIRY_DATE_PROPERTY, expiryDate);
    }
    public Date getExpiryDate() {
        return (Date)readProperty(EXPIRY_DATE_PROPERTY);
    }

    public void setContact(Contact contact) {
        setToOneTarget(CONTACT_PROPERTY, contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty(CONTACT_PROPERTY);
    }


}

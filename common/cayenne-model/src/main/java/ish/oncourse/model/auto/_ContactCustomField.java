package ish.oncourse.model.auto;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;

/**
 * Class _ContactCustomField was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ContactCustomField extends CustomField {

    private static final long serialVersionUID = 1L; 

    public static final String RELATED_OBJECT_PROPERTY = "relatedObject";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Contact> RELATED_OBJECT = Property.create("relatedObject", Contact.class);

    public void setRelatedObject(Contact relatedObject) {
        setToOneTarget("relatedObject", relatedObject, true);
    }

    public Contact getRelatedObject() {
        return (Contact)readProperty("relatedObject");
    }


}

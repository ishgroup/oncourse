package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomFieldType;

/**
 * Class _CustomField was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CustomField extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String VALUE_PROPERTY = "value";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String CUSTOM_FIELD_TYPE_PROPERTY = "customFieldType";
    @Deprecated
    public static final String RELATED_OBJECT_PROPERTY = "relatedObject";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> VALUE = new Property<String>("value");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<CustomFieldType> CUSTOM_FIELD_TYPE = new Property<CustomFieldType>("customFieldType");
    public static final Property<Contact> RELATED_OBJECT = new Property<Contact>("relatedObject");

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

    public void setValue(String value) {
        writeProperty("value", value);
    }
    public String getValue() {
        return (String)readProperty("value");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCustomFieldType(CustomFieldType customFieldType) {
        setToOneTarget("customFieldType", customFieldType, true);
    }

    public CustomFieldType getCustomFieldType() {
        return (CustomFieldType)readProperty("customFieldType");
    }


    public void setRelatedObject(Contact relatedObject) {
        setToOneTarget("relatedObject", relatedObject, true);
    }

    public Contact getRelatedObject() {
        return (Contact)readProperty("relatedObject");
    }


    protected abstract void onPostAdd();

}

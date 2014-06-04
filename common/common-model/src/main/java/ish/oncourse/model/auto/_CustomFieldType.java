package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.CustomField;

/**
 * Class _CustomFieldType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CustomFieldType extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DEFAULT_VALUE_PROPERTY = "defaultValue";
    public static final String IS_MANDATORY_PROPERTY = "isMandatory";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CUSTOM_FIELDS_PROPERTY = "customFields";

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

    public void setDefaultValue(String defaultValue) {
        writeProperty(DEFAULT_VALUE_PROPERTY, defaultValue);
    }
    public String getDefaultValue() {
        return (String)readProperty(DEFAULT_VALUE_PROPERTY);
    }

    public void setIsMandatory(Boolean isMandatory) {
        writeProperty(IS_MANDATORY_PROPERTY, isMandatory);
    }
    public Boolean getIsMandatory() {
        return (Boolean)readProperty(IS_MANDATORY_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void addToCustomFields(CustomField obj) {
        addToManyTarget(CUSTOM_FIELDS_PROPERTY, obj, true);
    }
    public void removeFromCustomFields(CustomField obj) {
        removeToManyTarget(CUSTOM_FIELDS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CustomField> getCustomFields() {
        return (List<CustomField>)readProperty(CUSTOM_FIELDS_PROPERTY);
    }


    protected abstract void onPostAdd();

}

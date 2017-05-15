package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Field;
import ish.oncourse.model.FieldConfiguration;

/**
 * Class _FieldHeading was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _FieldHeading extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String ORDER_PROPERTY = "order";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String FIELD_CONFIGURATION_PROPERTY = "fieldConfiguration";
    public static final String FIELDS_PROPERTY = "fields";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DESCRIPTION = new Property<String>("description");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<Integer> ORDER = new Property<Integer>("order");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<FieldConfiguration> FIELD_CONFIGURATION = new Property<FieldConfiguration>("fieldConfiguration");
    public static final Property<List<Field>> FIELDS = new Property<List<Field>>("fields");

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

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setOrder(Integer order) {
        writeProperty("order", order);
    }
    public Integer getOrder() {
        return (Integer)readProperty("order");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setFieldConfiguration(FieldConfiguration fieldConfiguration) {
        setToOneTarget("fieldConfiguration", fieldConfiguration, true);
    }

    public FieldConfiguration getFieldConfiguration() {
        return (FieldConfiguration)readProperty("fieldConfiguration");
    }


    public void addToFields(Field obj) {
        addToManyTarget("fields", obj, true);
    }
    public void removeFromFields(Field obj) {
        removeToManyTarget("fields", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Field> getFields() {
        return (List<Field>)readProperty("fields");
    }


}

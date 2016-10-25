package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.EntityEvent;
import ish.common.types.SystemEventType;
import ish.common.types.TriggerType;
import ish.oncourse.model.College;

/**
 * Class _Script was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Script extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String ENABLED_PROPERTY = "enabled";
    public static final String ENTITY_CLASS_PROPERTY = "entityClass";
    public static final String ENTITY_EVENT_TYPE_PROPERTY = "entityEventType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String RESULT_PROPERTY = "result";
    public static final String SCHEDULE_PROPERTY = "schedule";
    public static final String SCRIPT_PROPERTY = "script";
    public static final String SYSTEM_EVENT_TYPE_PROPERTY = "systemEventType";
    public static final String TRIGGER_TYPE_PROPERTY = "triggerType";
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DESCRIPTION = new Property<String>("description");
    public static final Property<Boolean> ENABLED = new Property<Boolean>("enabled");
    public static final Property<String> ENTITY_CLASS = new Property<String>("entityClass");
    public static final Property<EntityEvent> ENTITY_EVENT_TYPE = new Property<EntityEvent>("entityEventType");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> RESULT = new Property<String>("result");
    public static final Property<String> SCHEDULE = new Property<String>("schedule");
    public static final Property<String> SCRIPT = new Property<String>("script");
    public static final Property<SystemEventType> SYSTEM_EVENT_TYPE = new Property<SystemEventType>("systemEventType");
    public static final Property<TriggerType> TRIGGER_TYPE = new Property<TriggerType>("triggerType");
    public static final Property<College> COLLEGE = new Property<College>("college");

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

    public void setEnabled(Boolean enabled) {
        writeProperty("enabled", enabled);
    }
    public Boolean getEnabled() {
        return (Boolean)readProperty("enabled");
    }

    public void setEntityClass(String entityClass) {
        writeProperty("entityClass", entityClass);
    }
    public String getEntityClass() {
        return (String)readProperty("entityClass");
    }

    public void setEntityEventType(EntityEvent entityEventType) {
        writeProperty("entityEventType", entityEventType);
    }
    public EntityEvent getEntityEventType() {
        return (EntityEvent)readProperty("entityEventType");
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

    public void setResult(String result) {
        writeProperty("result", result);
    }
    public String getResult() {
        return (String)readProperty("result");
    }

    public void setSchedule(String schedule) {
        writeProperty("schedule", schedule);
    }
    public String getSchedule() {
        return (String)readProperty("schedule");
    }

    public void setScript(String script) {
        writeProperty("script", script);
    }
    public String getScript() {
        return (String)readProperty("script");
    }

    public void setSystemEventType(SystemEventType systemEventType) {
        writeProperty("systemEventType", systemEventType);
    }
    public SystemEventType getSystemEventType() {
        return (SystemEventType)readProperty("systemEventType");
    }

    public void setTriggerType(TriggerType triggerType) {
        writeProperty("triggerType", triggerType);
    }
    public TriggerType getTriggerType() {
        return (TriggerType)readProperty("triggerType");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


}

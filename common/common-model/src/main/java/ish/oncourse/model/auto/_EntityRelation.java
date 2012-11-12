package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.EntityRelationType;
import ish.oncourse.model.College;

/**
 * Class _EntityRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _EntityRelation extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String FROM_ENTITY_IDENTIFIER_PROPERTY = "fromEntityIdentifier";
    public static final String FROM_ENTITY_WILLOW_ID_PROPERTY = "fromEntityWillowId";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String TO_ENTITY_IDENTIFIER_PROPERTY = "toEntityIdentifier";
    public static final String TO_ENTITY_WILLOW_ID_PROPERTY = "toEntityWillowId";
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

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

    public void setFromEntityIdentifier(EntityRelationType fromEntityIdentifier) {
        writeProperty("fromEntityIdentifier", fromEntityIdentifier);
    }
    public EntityRelationType getFromEntityIdentifier() {
        return (EntityRelationType)readProperty("fromEntityIdentifier");
    }

    public void setFromEntityWillowId(Long fromEntityWillowId) {
        writeProperty("fromEntityWillowId", fromEntityWillowId);
    }
    public Long getFromEntityWillowId() {
        return (Long)readProperty("fromEntityWillowId");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setToEntityIdentifier(EntityRelationType toEntityIdentifier) {
        writeProperty("toEntityIdentifier", toEntityIdentifier);
    }
    public EntityRelationType getToEntityIdentifier() {
        return (EntityRelationType)readProperty("toEntityIdentifier");
    }

    public void setToEntityWillowId(Long toEntityWillowId) {
        writeProperty("toEntityWillowId", toEntityWillowId);
    }
    public Long getToEntityWillowId() {
        return (Long)readProperty("toEntityWillowId");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


}

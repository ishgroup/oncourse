package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;

/**
 * Class _Preference was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Preference extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String EXPLANATION_PROPERTY = "explanation";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String SQL_TYPE_PROPERTY = "sqlType";
    public static final String VALUE_PROPERTY = "value";
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

    public void setExplanation(String explanation) {
        writeProperty("explanation", explanation);
    }
    public String getExplanation() {
        return (String)readProperty("explanation");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
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

    public void setSqlType(Integer sqlType) {
        writeProperty("sqlType", sqlType);
    }
    public Integer getSqlType() {
        return (Integer)readProperty("sqlType");
    }

    public void setValue(byte[] value) {
        writeProperty("value", value);
    }
    public byte[] getValue() {
        return (byte[])readProperty("value");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


}

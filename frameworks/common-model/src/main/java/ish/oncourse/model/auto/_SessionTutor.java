package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;

/**
 * Class _SessionTutor was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SessionTutor extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String TYPE_PROPERTY = "type";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String SESSION_PROPERTY = "session";
    public static final String TUTOR_PROPERTY = "tutor";

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

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setType(Integer type) {
        writeProperty("type", type);
    }
    public Integer getType() {
        return (Integer)readProperty("type");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setSession(Session session) {
        setToOneTarget("session", session, true);
    }

    public Session getSession() {
        return (Session)readProperty("session");
    }


    public void setTutor(Tutor tutor) {
        setToOneTarget("tutor", tutor, true);
    }

    public Tutor getTutor() {
        return (Tutor)readProperty("tutor");
    }


}

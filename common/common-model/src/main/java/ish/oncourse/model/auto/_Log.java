package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.WillowUser;

/**
 * Class _Log was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Log extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ACTION_PROPERTY = "action";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PAGE_PROPERTY = "page";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String USER_PROPERTY = "user";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ACTION = new Property<String>("action");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> PAGE = new Property<String>("page");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<WillowUser> USER = new Property<WillowUser>("user");

    public void setAction(String action) {
        writeProperty("action", action);
    }
    public String getAction() {
        return (String)readProperty("action");
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

    public void setPage(String page) {
        writeProperty("page", page);
    }
    public String getPage() {
        return (String)readProperty("page");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setUser(WillowUser user) {
        setToOneTarget("user", user, true);
    }

    public WillowUser getUser() {
        return (WillowUser)readProperty("user");
    }


}

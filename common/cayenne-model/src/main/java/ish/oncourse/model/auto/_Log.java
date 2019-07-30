package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.WillowUser;

/**
 * Class _Log was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Log extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ACTION_PROPERTY = "action";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PAGE_PROPERTY = "page";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String USER_PROPERTY = "user";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ACTION = Property.create("action", String.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> PAGE = Property.create("page", String.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<WillowUser> USER = Property.create("user", WillowUser.class);

    protected String action;
    protected Date created;
    protected Date modified;
    protected String page;

    protected Object college;
    protected Object user;

    public void setAction(String action) {
        beforePropertyWrite("action", this.action, action);
        this.action = action;
    }

    public String getAction() {
        beforePropertyRead("action");
        return this.action;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setPage(String page) {
        beforePropertyWrite("page", this.page, page);
        this.page = page;
    }

    public String getPage() {
        beforePropertyRead("page");
        return this.page;
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

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "action":
                return this.action;
            case "created":
                return this.created;
            case "modified":
                return this.modified;
            case "page":
                return this.page;
            case "college":
                return this.college;
            case "user":
                return this.user;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "action":
                this.action = (String)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "page":
                this.page = (String)val;
                break;
            case "college":
                this.college = val;
                break;
            case "user":
                this.user = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.action);
        out.writeObject(this.created);
        out.writeObject(this.modified);
        out.writeObject(this.page);
        out.writeObject(this.college);
        out.writeObject(this.user);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.action = (String)in.readObject();
        this.created = (Date)in.readObject();
        this.modified = (Date)in.readObject();
        this.page = (String)in.readObject();
        this.college = in.readObject();
        this.user = in.readObject();
    }

}

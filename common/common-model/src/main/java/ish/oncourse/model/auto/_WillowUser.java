package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Log;

/**
 * Class _WillowUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WillowUser extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String EMAIL_PROPERTY = "email";
    @Deprecated
    public static final String FAILED_LOGIN_COUNT_PROPERTY = "failedLoginCount";
    @Deprecated
    public static final String FIRST_NAME_PROPERTY = "firstName";
    @Deprecated
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    @Deprecated
    public static final String LAST_FAILED_LOGIN_PROPERTY = "lastFailedLogin";
    @Deprecated
    public static final String LAST_LOGIN_PROPERTY = "lastLogin";
    @Deprecated
    public static final String LAST_NAME_PROPERTY = "lastName";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String PASSWORD_PROPERTY = "password";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String LOGS_PROPERTY = "logs";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> EMAIL = new Property<String>("email");
    public static final Property<Integer> FAILED_LOGIN_COUNT = new Property<Integer>("failedLoginCount");
    public static final Property<String> FIRST_NAME = new Property<String>("firstName");
    public static final Property<Boolean> IS_ACTIVE = new Property<Boolean>("isActive");
    public static final Property<Date> LAST_FAILED_LOGIN = new Property<Date>("lastFailedLogin");
    public static final Property<Date> LAST_LOGIN = new Property<Date>("lastLogin");
    public static final Property<String> LAST_NAME = new Property<String>("lastName");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> PASSWORD = new Property<String>("password");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<Log>> LOGS = new Property<List<Log>>("logs");

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setEmail(String email) {
        writeProperty("email", email);
    }
    public String getEmail() {
        return (String)readProperty("email");
    }

    public void setFailedLoginCount(Integer failedLoginCount) {
        writeProperty("failedLoginCount", failedLoginCount);
    }
    public Integer getFailedLoginCount() {
        return (Integer)readProperty("failedLoginCount");
    }

    public void setFirstName(String firstName) {
        writeProperty("firstName", firstName);
    }
    public String getFirstName() {
        return (String)readProperty("firstName");
    }

    public void setIsActive(Boolean isActive) {
        writeProperty("isActive", isActive);
    }
    public Boolean getIsActive() {
        return (Boolean)readProperty("isActive");
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        writeProperty("lastFailedLogin", lastFailedLogin);
    }
    public Date getLastFailedLogin() {
        return (Date)readProperty("lastFailedLogin");
    }

    public void setLastLogin(Date lastLogin) {
        writeProperty("lastLogin", lastLogin);
    }
    public Date getLastLogin() {
        return (Date)readProperty("lastLogin");
    }

    public void setLastName(String lastName) {
        writeProperty("lastName", lastName);
    }
    public String getLastName() {
        return (String)readProperty("lastName");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setPassword(String password) {
        writeProperty("password", password);
    }
    public String getPassword() {
        return (String)readProperty("password");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToLogs(Log obj) {
        addToManyTarget("logs", obj, true);
    }
    public void removeFromLogs(Log obj) {
        removeToManyTarget("logs", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Log> getLogs() {
        return (List<Log>)readProperty("logs");
    }


}

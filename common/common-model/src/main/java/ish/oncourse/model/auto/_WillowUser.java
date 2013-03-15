package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Log;

/**
 * Class _WillowUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WillowUser extends CayenneDataObject {

    public static final String CREATED_PROPERTY = "created";
    public static final String EMAIL_PROPERTY = "email";
    public static final String FAILED_LOGIN_COUNT_PROPERTY = "failedLoginCount";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    public static final String LAST_FAILED_LOGIN_PROPERTY = "lastFailedLogin";
    public static final String LAST_LOGIN_PROPERTY = "lastLogin";
    public static final String LAST_NAME_PROPERTY = "lastName";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String LOGS_PROPERTY = "logs";

    public static final String ID_PK_COLUMN = "id";

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setEmail(String email) {
        writeProperty(EMAIL_PROPERTY, email);
    }
    public String getEmail() {
        return (String)readProperty(EMAIL_PROPERTY);
    }

    public void setFailedLoginCount(Integer failedLoginCount) {
        writeProperty(FAILED_LOGIN_COUNT_PROPERTY, failedLoginCount);
    }
    public Integer getFailedLoginCount() {
        return (Integer)readProperty(FAILED_LOGIN_COUNT_PROPERTY);
    }

    public void setFirstName(String firstName) {
        writeProperty(FIRST_NAME_PROPERTY, firstName);
    }
    public String getFirstName() {
        return (String)readProperty(FIRST_NAME_PROPERTY);
    }

    public void setIsActive(Boolean isActive) {
        writeProperty(IS_ACTIVE_PROPERTY, isActive);
    }
    public Boolean getIsActive() {
        return (Boolean)readProperty(IS_ACTIVE_PROPERTY);
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        writeProperty(LAST_FAILED_LOGIN_PROPERTY, lastFailedLogin);
    }
    public Date getLastFailedLogin() {
        return (Date)readProperty(LAST_FAILED_LOGIN_PROPERTY);
    }

    public void setLastLogin(Date lastLogin) {
        writeProperty(LAST_LOGIN_PROPERTY, lastLogin);
    }
    public Date getLastLogin() {
        return (Date)readProperty(LAST_LOGIN_PROPERTY);
    }

    public void setLastName(String lastName) {
        writeProperty(LAST_NAME_PROPERTY, lastName);
    }
    public String getLastName() {
        return (String)readProperty(LAST_NAME_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPassword(String password) {
        writeProperty(PASSWORD_PROPERTY, password);
    }
    public String getPassword() {
        return (String)readProperty(PASSWORD_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void addToLogs(Log obj) {
        addToManyTarget(LOGS_PROPERTY, obj, true);
    }
    public void removeFromLogs(Log obj) {
        removeToManyTarget(LOGS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Log> getLogs() {
        return (List<Log>)readProperty(LOGS_PROPERTY);
    }


}

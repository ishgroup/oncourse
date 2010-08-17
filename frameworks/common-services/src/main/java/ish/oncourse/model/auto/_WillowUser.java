package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Log;
import ish.oncourse.model.WebNode;

/**
 * Class _WillowUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WillowUser extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String EMAIL_PROPERTY = "email";
    public static final String FAILED_LOGIN_COUNT_PROPERTY = "failedLoginCount";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String FLAG1_PROPERTY = "flag1";
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String IS_SUPER_USER_PROPERTY = "isSuperUser";
    public static final String LAST_FAILED_LOGIN_PROPERTY = "lastFailedLogin";
    public static final String LAST_LOGIN_PROPERTY = "lastLogin";
    public static final String LAST_NAME_PROPERTY = "lastName";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String PASSWORD_HASH_PROPERTY = "passwordHash";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String LOGS_PROPERTY = "logs";
    public static final String WEB_NODES_PROPERTY = "webNodes";

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

    public void setFlag1(Integer flag1) {
        writeProperty("flag1", flag1);
    }
    public Integer getFlag1() {
        return (Integer)readProperty("flag1");
    }

    public void setIsActive(Boolean isActive) {
        writeProperty("isActive", isActive);
    }
    public Boolean getIsActive() {
        return (Boolean)readProperty("isActive");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
    }

    public void setIsSuperUser(Boolean isSuperUser) {
        writeProperty("isSuperUser", isSuperUser);
    }
    public Boolean getIsSuperUser() {
        return (Boolean)readProperty("isSuperUser");
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

    public void setPasswordHash(String passwordHash) {
        writeProperty("passwordHash", passwordHash);
    }
    public String getPasswordHash() {
        return (String)readProperty("passwordHash");
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


    public void addToWebNodes(WebNode obj) {
        addToManyTarget("webNodes", obj, true);
    }
    public void removeFromWebNodes(WebNode obj) {
        removeToManyTarget("webNodes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebNode> getWebNodes() {
        return (List<WebNode>)readProperty("webNodes");
    }


}

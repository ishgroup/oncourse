package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.model.Site;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _SystemUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SystemUser extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String EDIT_CMS_PROPERTY = "editCMS";
    public static final String EDIT_TARA_PROPERTY = "editTara";
    public static final String EMAIL_PROPERTY = "email";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    public static final String IS_ADMIN_PROPERTY = "isAdmin";
    public static final String LAST_LOGIN_IP_PROPERTY = "lastLoginIP";
    public static final String LAST_LOGIN_ON_PROPERTY = "lastLoginOn";
    public static final String LOGIN_PROPERTY = "login";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String SURNAME_PROPERTY = "surname";
    public static final String TOKEN_PROPERTY = "token";
    public static final String TOKEN_SCRATCH_CODES_PROPERTY = "tokenScratchCodes";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String DEFAULT_ADMINISTRATION_CENTRE_PROPERTY = "defaultAdministrationCentre";
    public static final String DOCUMENT_VERSIONS_PROPERTY = "documentVersions";
    public static final String SITES_DEPLOYED_PROPERTY = "sitesDeployed";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Boolean> EDIT_CMS = Property.create("editCMS", Boolean.class);
    public static final Property<Boolean> EDIT_TARA = Property.create("editTara", Boolean.class);
    public static final Property<String> EMAIL = Property.create("email", String.class);
    public static final Property<String> FIRST_NAME = Property.create("firstName", String.class);
    public static final Property<Boolean> IS_ACTIVE = Property.create("isActive", Boolean.class);
    public static final Property<Boolean> IS_ADMIN = Property.create("isAdmin", Boolean.class);
    public static final Property<String> LAST_LOGIN_IP = Property.create("lastLoginIP", String.class);
    public static final Property<Date> LAST_LOGIN_ON = Property.create("lastLoginOn", Date.class);
    public static final Property<String> LOGIN = Property.create("login", String.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> PASSWORD = Property.create("password", String.class);
    public static final Property<String> SURNAME = Property.create("surname", String.class);
    public static final Property<String> TOKEN = Property.create("token", String.class);
    public static final Property<String> TOKEN_SCRATCH_CODES = Property.create("tokenScratchCodes", String.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Site> DEFAULT_ADMINISTRATION_CENTRE = Property.create("defaultAdministrationCentre", Site.class);
    public static final Property<List<DocumentVersion>> DOCUMENT_VERSIONS = Property.create("documentVersions", List.class);
    public static final Property<List<WebSiteVersion>> SITES_DEPLOYED = Property.create("sitesDeployed", List.class);

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

    public void setEditCMS(Boolean editCMS) {
        writeProperty("editCMS", editCMS);
    }
    public Boolean getEditCMS() {
        return (Boolean)readProperty("editCMS");
    }

    public void setEditTara(Boolean editTara) {
        writeProperty("editTara", editTara);
    }
    public Boolean getEditTara() {
        return (Boolean)readProperty("editTara");
    }

    public void setEmail(String email) {
        writeProperty("email", email);
    }
    public String getEmail() {
        return (String)readProperty("email");
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

    public void setIsAdmin(Boolean isAdmin) {
        writeProperty("isAdmin", isAdmin);
    }
    public Boolean getIsAdmin() {
        return (Boolean)readProperty("isAdmin");
    }

    public void setLastLoginIP(String lastLoginIP) {
        writeProperty("lastLoginIP", lastLoginIP);
    }
    public String getLastLoginIP() {
        return (String)readProperty("lastLoginIP");
    }

    public void setLastLoginOn(Date lastLoginOn) {
        writeProperty("lastLoginOn", lastLoginOn);
    }
    public Date getLastLoginOn() {
        return (Date)readProperty("lastLoginOn");
    }

    public void setLogin(String login) {
        writeProperty("login", login);
    }
    public String getLogin() {
        return (String)readProperty("login");
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

    public void setSurname(String surname) {
        writeProperty("surname", surname);
    }
    public String getSurname() {
        return (String)readProperty("surname");
    }

    public void setToken(String token) {
        writeProperty("token", token);
    }
    public String getToken() {
        return (String)readProperty("token");
    }

    public void setTokenScratchCodes(String tokenScratchCodes) {
        writeProperty("tokenScratchCodes", tokenScratchCodes);
    }
    public String getTokenScratchCodes() {
        return (String)readProperty("tokenScratchCodes");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setDefaultAdministrationCentre(Site defaultAdministrationCentre) {
        setToOneTarget("defaultAdministrationCentre", defaultAdministrationCentre, true);
    }

    public Site getDefaultAdministrationCentre() {
        return (Site)readProperty("defaultAdministrationCentre");
    }


    public void addToDocumentVersions(DocumentVersion obj) {
        addToManyTarget("documentVersions", obj, true);
    }
    public void removeFromDocumentVersions(DocumentVersion obj) {
        removeToManyTarget("documentVersions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DocumentVersion> getDocumentVersions() {
        return (List<DocumentVersion>)readProperty("documentVersions");
    }


    public void addToSitesDeployed(WebSiteVersion obj) {
        addToManyTarget("sitesDeployed", obj, true);
    }
    public void removeFromSitesDeployed(WebSiteVersion obj) {
        removeToManyTarget("sitesDeployed", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebSiteVersion> getSitesDeployed() {
        return (List<WebSiteVersion>)readProperty("sitesDeployed");
    }


}

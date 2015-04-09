package ish.oncourse.model.auto;

import ish.oncourse.model.College;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.model.Site;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;
import java.util.List;

/**
 * Class _SystemUser was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SystemUser extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String EDIT_CMS_PROPERTY = "editCMS";
    @Deprecated
    public static final String EDIT_TARA_PROPERTY = "editTara";
    @Deprecated
    public static final String EMAIL_PROPERTY = "email";
    @Deprecated
    public static final String FIRST_NAME_PROPERTY = "firstName";
    @Deprecated
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    @Deprecated
    public static final String IS_ADMIN_PROPERTY = "isAdmin";
    @Deprecated
    public static final String LAST_LOGIN_IP_PROPERTY = "lastLoginIP";
    @Deprecated
    public static final String LAST_LOGIN_ON_PROPERTY = "lastLoginOn";
    @Deprecated
    public static final String LOGIN_PROPERTY = "login";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String PASSWORD_PROPERTY = "password";
    @Deprecated
    public static final String SURNAME_PROPERTY = "surname";
    @Deprecated
    public static final String TOKEN_PROPERTY = "token";
    @Deprecated
    public static final String TOKEN_SCRATCH_CODES_PROPERTY = "tokenScratchCodes";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String DEFAULT_ADMINISTRATION_CENTRE_PROPERTY = "defaultAdministrationCentre";
    @Deprecated
    public static final String DOCUMENT_VERSIONS_PROPERTY = "documentVersions";
    @Deprecated
    public static final String SITES_DEPLOYED_PROPERTY = "sitesDeployed";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Boolean> EDIT_CMS = new Property<Boolean>("editCMS");
    public static final Property<Boolean> EDIT_TARA = new Property<Boolean>("editTara");
    public static final Property<String> EMAIL = new Property<String>("email");
    public static final Property<String> FIRST_NAME = new Property<String>("firstName");
    public static final Property<Boolean> IS_ACTIVE = new Property<Boolean>("isActive");
    public static final Property<Boolean> IS_ADMIN = new Property<Boolean>("isAdmin");
    public static final Property<String> LAST_LOGIN_IP = new Property<String>("lastLoginIP");
    public static final Property<Date> LAST_LOGIN_ON = new Property<Date>("lastLoginOn");
    public static final Property<String> LOGIN = new Property<String>("login");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> PASSWORD = new Property<String>("password");
    public static final Property<String> SURNAME = new Property<String>("surname");
    public static final Property<String> TOKEN = new Property<String>("token");
    public static final Property<String> TOKEN_SCRATCH_CODES = new Property<String>("tokenScratchCodes");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Site> DEFAULT_ADMINISTRATION_CENTRE = new Property<Site>("defaultAdministrationCentre");
    public static final Property<List<DocumentVersion>> DOCUMENT_VERSIONS = new Property<List<DocumentVersion>>("documentVersions");
    public static final Property<List<WebSiteVersion>> SITES_DEPLOYED = new Property<List<WebSiteVersion>>("sitesDeployed");

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

package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    protected Long angelId;
    protected Date created;
    protected Boolean editCMS;
    protected Boolean editTara;
    protected String email;
    protected String firstName;
    protected Boolean isActive;
    protected Boolean isAdmin;
    protected String lastLoginIP;
    protected Date lastLoginOn;
    protected String login;
    protected Date modified;
    protected String password;
    protected String surname;
    protected String token;
    protected String tokenScratchCodes;

    protected Object college;
    protected Object defaultAdministrationCentre;
    protected Object documentVersions;
    protected Object sitesDeployed;

    public void setAngelId(Long angelId) {
        beforePropertyWrite("angelId", this.angelId, angelId);
        this.angelId = angelId;
    }

    public Long getAngelId() {
        beforePropertyRead("angelId");
        return this.angelId;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setEditCMS(Boolean editCMS) {
        beforePropertyWrite("editCMS", this.editCMS, editCMS);
        this.editCMS = editCMS;
    }

    public Boolean getEditCMS() {
        beforePropertyRead("editCMS");
        return this.editCMS;
    }

    public void setEditTara(Boolean editTara) {
        beforePropertyWrite("editTara", this.editTara, editTara);
        this.editTara = editTara;
    }

    public Boolean getEditTara() {
        beforePropertyRead("editTara");
        return this.editTara;
    }

    public void setEmail(String email) {
        beforePropertyWrite("email", this.email, email);
        this.email = email;
    }

    public String getEmail() {
        beforePropertyRead("email");
        return this.email;
    }

    public void setFirstName(String firstName) {
        beforePropertyWrite("firstName", this.firstName, firstName);
        this.firstName = firstName;
    }

    public String getFirstName() {
        beforePropertyRead("firstName");
        return this.firstName;
    }

    public void setIsActive(Boolean isActive) {
        beforePropertyWrite("isActive", this.isActive, isActive);
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        beforePropertyRead("isActive");
        return this.isActive;
    }

    public void setIsAdmin(Boolean isAdmin) {
        beforePropertyWrite("isAdmin", this.isAdmin, isAdmin);
        this.isAdmin = isAdmin;
    }

    public Boolean getIsAdmin() {
        beforePropertyRead("isAdmin");
        return this.isAdmin;
    }

    public void setLastLoginIP(String lastLoginIP) {
        beforePropertyWrite("lastLoginIP", this.lastLoginIP, lastLoginIP);
        this.lastLoginIP = lastLoginIP;
    }

    public String getLastLoginIP() {
        beforePropertyRead("lastLoginIP");
        return this.lastLoginIP;
    }

    public void setLastLoginOn(Date lastLoginOn) {
        beforePropertyWrite("lastLoginOn", this.lastLoginOn, lastLoginOn);
        this.lastLoginOn = lastLoginOn;
    }

    public Date getLastLoginOn() {
        beforePropertyRead("lastLoginOn");
        return this.lastLoginOn;
    }

    public void setLogin(String login) {
        beforePropertyWrite("login", this.login, login);
        this.login = login;
    }

    public String getLogin() {
        beforePropertyRead("login");
        return this.login;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setPassword(String password) {
        beforePropertyWrite("password", this.password, password);
        this.password = password;
    }

    public String getPassword() {
        beforePropertyRead("password");
        return this.password;
    }

    public void setSurname(String surname) {
        beforePropertyWrite("surname", this.surname, surname);
        this.surname = surname;
    }

    public String getSurname() {
        beforePropertyRead("surname");
        return this.surname;
    }

    public void setToken(String token) {
        beforePropertyWrite("token", this.token, token);
        this.token = token;
    }

    public String getToken() {
        beforePropertyRead("token");
        return this.token;
    }

    public void setTokenScratchCodes(String tokenScratchCodes) {
        beforePropertyWrite("tokenScratchCodes", this.tokenScratchCodes, tokenScratchCodes);
        this.tokenScratchCodes = tokenScratchCodes;
    }

    public String getTokenScratchCodes() {
        beforePropertyRead("tokenScratchCodes");
        return this.tokenScratchCodes;
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

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "angelId":
                return this.angelId;
            case "created":
                return this.created;
            case "editCMS":
                return this.editCMS;
            case "editTara":
                return this.editTara;
            case "email":
                return this.email;
            case "firstName":
                return this.firstName;
            case "isActive":
                return this.isActive;
            case "isAdmin":
                return this.isAdmin;
            case "lastLoginIP":
                return this.lastLoginIP;
            case "lastLoginOn":
                return this.lastLoginOn;
            case "login":
                return this.login;
            case "modified":
                return this.modified;
            case "password":
                return this.password;
            case "surname":
                return this.surname;
            case "token":
                return this.token;
            case "tokenScratchCodes":
                return this.tokenScratchCodes;
            case "college":
                return this.college;
            case "defaultAdministrationCentre":
                return this.defaultAdministrationCentre;
            case "documentVersions":
                return this.documentVersions;
            case "sitesDeployed":
                return this.sitesDeployed;
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
            case "angelId":
                this.angelId = (Long)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "editCMS":
                this.editCMS = (Boolean)val;
                break;
            case "editTara":
                this.editTara = (Boolean)val;
                break;
            case "email":
                this.email = (String)val;
                break;
            case "firstName":
                this.firstName = (String)val;
                break;
            case "isActive":
                this.isActive = (Boolean)val;
                break;
            case "isAdmin":
                this.isAdmin = (Boolean)val;
                break;
            case "lastLoginIP":
                this.lastLoginIP = (String)val;
                break;
            case "lastLoginOn":
                this.lastLoginOn = (Date)val;
                break;
            case "login":
                this.login = (String)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "password":
                this.password = (String)val;
                break;
            case "surname":
                this.surname = (String)val;
                break;
            case "token":
                this.token = (String)val;
                break;
            case "tokenScratchCodes":
                this.tokenScratchCodes = (String)val;
                break;
            case "college":
                this.college = val;
                break;
            case "defaultAdministrationCentre":
                this.defaultAdministrationCentre = val;
                break;
            case "documentVersions":
                this.documentVersions = val;
                break;
            case "sitesDeployed":
                this.sitesDeployed = val;
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
        out.writeObject(this.angelId);
        out.writeObject(this.created);
        out.writeObject(this.editCMS);
        out.writeObject(this.editTara);
        out.writeObject(this.email);
        out.writeObject(this.firstName);
        out.writeObject(this.isActive);
        out.writeObject(this.isAdmin);
        out.writeObject(this.lastLoginIP);
        out.writeObject(this.lastLoginOn);
        out.writeObject(this.login);
        out.writeObject(this.modified);
        out.writeObject(this.password);
        out.writeObject(this.surname);
        out.writeObject(this.token);
        out.writeObject(this.tokenScratchCodes);
        out.writeObject(this.college);
        out.writeObject(this.defaultAdministrationCentre);
        out.writeObject(this.documentVersions);
        out.writeObject(this.sitesDeployed);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.editCMS = (Boolean)in.readObject();
        this.editTara = (Boolean)in.readObject();
        this.email = (String)in.readObject();
        this.firstName = (String)in.readObject();
        this.isActive = (Boolean)in.readObject();
        this.isAdmin = (Boolean)in.readObject();
        this.lastLoginIP = (String)in.readObject();
        this.lastLoginOn = (Date)in.readObject();
        this.login = (String)in.readObject();
        this.modified = (Date)in.readObject();
        this.password = (String)in.readObject();
        this.surname = (String)in.readObject();
        this.token = (String)in.readObject();
        this.tokenScratchCodes = (String)in.readObject();
        this.college = in.readObject();
        this.defaultAdministrationCentre = in.readObject();
        this.documentVersions = in.readObject();
        this.sitesDeployed = in.readObject();
    }

}

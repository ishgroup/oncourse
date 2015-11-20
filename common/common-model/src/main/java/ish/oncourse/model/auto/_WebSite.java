package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebSite was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebSite extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String COURSES_ROOT_TAG_NAME_PROPERTY = "coursesRootTagName";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String GOOGLE_DIRECTIONS_FROM_PROPERTY = "googleDirectionsFrom";
    @Deprecated
    public static final String GOOGLE_TAGMANAGER_ACCOUNT_PROPERTY = "googleTagmanagerAccount";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NAME_PROPERTY = "name";
    @Deprecated
    public static final String SITE_KEY_PROPERTY = "siteKey";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String COLLEGE_DOMAINS_PROPERTY = "collegeDomains";
    @Deprecated
    public static final String INVOICES_PROPERTY = "invoices";
    @Deprecated
    public static final String LICENSE_FEES_PROPERTY = "licenseFees";
    @Deprecated
    public static final String VERSIONS_PROPERTY = "versions";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> COURSES_ROOT_TAG_NAME = new Property<String>("coursesRootTagName");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> GOOGLE_DIRECTIONS_FROM = new Property<String>("googleDirectionsFrom");
    public static final Property<String> GOOGLE_TAGMANAGER_ACCOUNT = new Property<String>("googleTagmanagerAccount");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> SITE_KEY = new Property<String>("siteKey");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<WebHostName>> COLLEGE_DOMAINS = new Property<List<WebHostName>>("collegeDomains");
    public static final Property<List<Invoice>> INVOICES = new Property<List<Invoice>>("invoices");
    public static final Property<List<LicenseFee>> LICENSE_FEES = new Property<List<LicenseFee>>("licenseFees");
    public static final Property<List<WebSiteVersion>> VERSIONS = new Property<List<WebSiteVersion>>("versions");

    public void setCoursesRootTagName(String coursesRootTagName) {
        writeProperty("coursesRootTagName", coursesRootTagName);
    }
    public String getCoursesRootTagName() {
        return (String)readProperty("coursesRootTagName");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setGoogleDirectionsFrom(String googleDirectionsFrom) {
        writeProperty("googleDirectionsFrom", googleDirectionsFrom);
    }
    public String getGoogleDirectionsFrom() {
        return (String)readProperty("googleDirectionsFrom");
    }

    public void setGoogleTagmanagerAccount(String googleTagmanagerAccount) {
        writeProperty("googleTagmanagerAccount", googleTagmanagerAccount);
    }
    public String getGoogleTagmanagerAccount() {
        return (String)readProperty("googleTagmanagerAccount");
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

    public void setSiteKey(String siteKey) {
        writeProperty("siteKey", siteKey);
    }
    public String getSiteKey() {
        return (String)readProperty("siteKey");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToCollegeDomains(WebHostName obj) {
        addToManyTarget("collegeDomains", obj, true);
    }
    public void removeFromCollegeDomains(WebHostName obj) {
        removeToManyTarget("collegeDomains", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebHostName> getCollegeDomains() {
        return (List<WebHostName>)readProperty("collegeDomains");
    }


    public void addToInvoices(Invoice obj) {
        addToManyTarget("invoices", obj, true);
    }
    public void removeFromInvoices(Invoice obj) {
        removeToManyTarget("invoices", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoices() {
        return (List<Invoice>)readProperty("invoices");
    }


    public void addToLicenseFees(LicenseFee obj) {
        addToManyTarget("licenseFees", obj, true);
    }
    public void removeFromLicenseFees(LicenseFee obj) {
        removeToManyTarget("licenseFees", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<LicenseFee> getLicenseFees() {
        return (List<LicenseFee>)readProperty("licenseFees");
    }


    public void addToVersions(WebSiteVersion obj) {
        addToManyTarget("versions", obj, true);
    }
    public void removeFromVersions(WebSiteVersion obj) {
        removeToManyTarget("versions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebSiteVersion> getVersions() {
        return (List<WebSiteVersion>)readProperty("versions");
    }


}

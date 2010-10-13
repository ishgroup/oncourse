package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

/**
 * Class _WebHostName was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebHostName extends CayenneDataObject {

    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String WEB_SITE_PROPERTY = "webSite";
    public static final String WEB_SITE_ARRAY_PROPERTY = "webSiteArray";

    public static final String ID_PK_COLUMN = "id";

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

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget("webSite", webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty("webSite");
    }


    public void addToWebSiteArray(WebSite obj) {
        addToManyTarget("webSiteArray", obj, true);
    }
    public void removeFromWebSiteArray(WebSite obj) {
        removeToManyTarget("webSiteArray", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebSite> getWebSiteArray() {
        return (List<WebSite>)readProperty("webSiteArray");
    }


}

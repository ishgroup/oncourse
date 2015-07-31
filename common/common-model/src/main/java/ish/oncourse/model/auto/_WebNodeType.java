package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebNodeType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebNodeType extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NAME_PROPERTY = "name";
    @Deprecated
    public static final String WEB_CONTENT_VISIBILITIES_PROPERTY = "webContentVisibilities";
    @Deprecated
    public static final String WEB_NODES_PROPERTY = "webNodes";
    @Deprecated
    public static final String WEB_SITE_LAYOUT_PROPERTY = "webSiteLayout";
    @Deprecated
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<List<WebContentVisibility>> WEB_CONTENT_VISIBILITIES = new Property<List<WebContentVisibility>>("webContentVisibilities");
    public static final Property<List<WebNode>> WEB_NODES = new Property<List<WebNode>>("webNodes");
    public static final Property<WebSiteLayout> WEB_SITE_LAYOUT = new Property<WebSiteLayout>("webSiteLayout");
    public static final Property<WebSiteVersion> WEB_SITE_VERSION = new Property<WebSiteVersion>("webSiteVersion");

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

    public void addToWebContentVisibilities(WebContentVisibility obj) {
        addToManyTarget("webContentVisibilities", obj, true);
    }
    public void removeFromWebContentVisibilities(WebContentVisibility obj) {
        removeToManyTarget("webContentVisibilities", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebContentVisibility> getWebContentVisibilities() {
        return (List<WebContentVisibility>)readProperty("webContentVisibilities");
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


    public void setWebSiteLayout(WebSiteLayout webSiteLayout) {
        setToOneTarget("webSiteLayout", webSiteLayout, true);
    }

    public WebSiteLayout getWebSiteLayout() {
        return (WebSiteLayout)readProperty("webSiteLayout");
    }


    public void setWebSiteVersion(WebSiteVersion webSiteVersion) {
        setToOneTarget("webSiteVersion", webSiteVersion, true);
    }

    public WebSiteVersion getWebSiteVersion() {
        return (WebSiteVersion)readProperty("webSiteVersion");
    }


    protected abstract void onPostAdd();

}

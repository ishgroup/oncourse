package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebUrlAlias was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebUrlAlias extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DEFAULT_PROPERTY = "default";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String REDIRECT_TO_PROPERTY = "redirectTo";
    @Deprecated
    public static final String URL_PATH_PROPERTY = "urlPath";
    @Deprecated
    public static final String WEB_NODE_PROPERTY = "webNode";
    @Deprecated
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Boolean> DEFAULT = new Property<Boolean>("default");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> REDIRECT_TO = new Property<String>("redirectTo");
    public static final Property<String> URL_PATH = new Property<String>("urlPath");
    public static final Property<WebNode> WEB_NODE = new Property<WebNode>("webNode");
    public static final Property<WebSiteVersion> WEB_SITE_VERSION = new Property<WebSiteVersion>("webSiteVersion");

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDefault(boolean _default) {
        writeProperty("default", _default);
    }
	public boolean isDefault() {
        Boolean value = (Boolean)readProperty("default");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setRedirectTo(String redirectTo) {
        writeProperty("redirectTo", redirectTo);
    }
    public String getRedirectTo() {
        return (String)readProperty("redirectTo");
    }

    public void setUrlPath(String urlPath) {
        writeProperty("urlPath", urlPath);
    }
    public String getUrlPath() {
        return (String)readProperty("urlPath");
    }

    public void setWebNode(WebNode webNode) {
        setToOneTarget("webNode", webNode, true);
    }

    public WebNode getWebNode() {
        return (WebNode)readProperty("webNode");
    }


    public void setWebSiteVersion(WebSiteVersion webSiteVersion) {
        setToOneTarget("webSiteVersion", webSiteVersion, true);
    }

    public WebSiteVersion getWebSiteVersion() {
        return (WebSiteVersion)readProperty("webSiteVersion");
    }


    protected abstract void onPostAdd();

}

package ish.oncourse.model.auto;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.CayenneDataObject;

import java.util.Date;

/**
 * Class _WebUrlAlias was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebUrlAlias extends CayenneDataObject {

    public static final String CREATED_PROPERTY = "created";
    public static final String DEFAULT_PROPERTY = "default";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REDIRECT_TO_PROPERTY = "redirectTo";
    public static final String URL_PATH_PROPERTY = "urlPath";
    public static final String WEB_NODE_PROPERTY = "webNode";
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDefault(boolean _default) {
        writeProperty(DEFAULT_PROPERTY, _default);
    }
	public boolean isDefault() {
        Boolean value = (Boolean)readProperty(DEFAULT_PROPERTY);
        return (value != null) ? value.booleanValue() : false;
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setRedirectTo(String redirectTo) {
        writeProperty(REDIRECT_TO_PROPERTY, redirectTo);
    }
    public String getRedirectTo() {
        return (String)readProperty(REDIRECT_TO_PROPERTY);
    }

    public void setUrlPath(String urlPath) {
        writeProperty(URL_PATH_PROPERTY, urlPath);
    }
    public String getUrlPath() {
        return (String)readProperty(URL_PATH_PROPERTY);
    }

    public void setWebNode(WebNode webNode) {
        setToOneTarget(WEB_NODE_PROPERTY, webNode, true);
    }

    public WebNode getWebNode() {
        return (WebNode)readProperty(WEB_NODE_PROPERTY);
    }


    public void setWebSiteVersion(WebSiteVersion webSiteVersion) {
        setToOneTarget(WEB_SITE_VERSION_PROPERTY, webSiteVersion, true);
    }

    public WebSiteVersion getWebSiteVersion() {
        return (WebSiteVersion)readProperty(WEB_SITE_VERSION_PROPERTY);
    }


    protected abstract void onPostAdd();

}

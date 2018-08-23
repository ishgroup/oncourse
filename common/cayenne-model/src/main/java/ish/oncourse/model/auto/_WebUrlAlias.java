package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.common.types.RequestMatchType;
import ish.common.types.SpecialWebPage;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebUrlAlias was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebUrlAlias extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String CREATED_PROPERTY = "created";
    public static final String DEFAULT_PROPERTY = "default";
    public static final String MATCH_TYPE_PROPERTY = "matchType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REDIRECT_TO_PROPERTY = "redirectTo";
    public static final String SPECIAL_PAGE_PROPERTY = "specialPage";
    public static final String URL_PATH_PROPERTY = "urlPath";
    public static final String WEB_NODE_PROPERTY = "webNode";
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Boolean> DEFAULT = Property.create("default", Boolean.class);
    public static final Property<RequestMatchType> MATCH_TYPE = Property.create("matchType", RequestMatchType.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> REDIRECT_TO = Property.create("redirectTo", String.class);
    public static final Property<SpecialWebPage> SPECIAL_PAGE = Property.create("specialPage", SpecialWebPage.class);
    public static final Property<String> URL_PATH = Property.create("urlPath", String.class);
    public static final Property<WebNode> WEB_NODE = Property.create("webNode", WebNode.class);
    public static final Property<WebSiteVersion> WEB_SITE_VERSION = Property.create("webSiteVersion", WebSiteVersion.class);

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

    public void setMatchType(RequestMatchType matchType) {
        writeProperty("matchType", matchType);
    }
    public RequestMatchType getMatchType() {
        return (RequestMatchType)readProperty("matchType");
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

    public void setSpecialPage(SpecialWebPage specialPage) {
        writeProperty("specialPage", specialPage);
    }
    public SpecialWebPage getSpecialPage() {
        return (SpecialWebPage)readProperty("specialPage");
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

package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebContent was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebContent extends CayenneDataObject {

    public static final String CONTENT_PROPERTY = "content";
    public static final String CONTENT_TEXTILE_PROPERTY = "contentTextile";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String WEB_CONTENT_VISIBILITIES_PROPERTY = "webContentVisibilities";
    public static final String WEB_SITE_PROPERTY = "webSite";
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public void setContent(String content) {
        writeProperty(CONTENT_PROPERTY, content);
    }
    public String getContent() {
        return (String)readProperty(CONTENT_PROPERTY);
    }

    public void setContentTextile(String contentTextile) {
        writeProperty(CONTENT_TEXTILE_PROPERTY, contentTextile);
    }
    public String getContentTextile() {
        return (String)readProperty(CONTENT_TEXTILE_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void addToWebContentVisibilities(WebContentVisibility obj) {
        addToManyTarget(WEB_CONTENT_VISIBILITIES_PROPERTY, obj, true);
    }
    public void removeFromWebContentVisibilities(WebContentVisibility obj) {
        removeToManyTarget(WEB_CONTENT_VISIBILITIES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebContentVisibility> getWebContentVisibilities() {
        return (List<WebContentVisibility>)readProperty(WEB_CONTENT_VISIBILITIES_PROPERTY);
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget(WEB_SITE_PROPERTY, webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty(WEB_SITE_PROPERTY);
    }


    public void setWebSiteVersion(WebSiteVersion webSiteVersion) {
        setToOneTarget(WEB_SITE_VERSION_PROPERTY, webSiteVersion, true);
    }

    public WebSiteVersion getWebSiteVersion() {
        return (WebSiteVersion)readProperty(WEB_SITE_VERSION_PROPERTY);
    }


    protected abstract void onPostAdd();

    protected abstract void onPreUpdate();

}

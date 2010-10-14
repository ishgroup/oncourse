package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebSite;

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
    public static final String WEB_CONTENT_VISIBILITY_PROPERTY = "webContentVisibility";
    public static final String WEB_SITE_PROPERTY = "webSite";

    public static final String ID_PK_COLUMN = "id";

    public void setContent(String content) {
        writeProperty("content", content);
    }
    public String getContent() {
        return (String)readProperty("content");
    }

    public void setContentTextile(String contentTextile) {
        writeProperty("contentTextile", contentTextile);
    }
    public String getContentTextile() {
        return (String)readProperty("contentTextile");
    }

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

    public void setWebContentVisibility(WebContentVisibility webContentVisibility) {
        setToOneTarget("webContentVisibility", webContentVisibility, true);
    }

    public WebContentVisibility getWebContentVisibility() {
        return (WebContentVisibility)readProperty("webContentVisibility");
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget("webSite", webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty("webSite");
    }


}

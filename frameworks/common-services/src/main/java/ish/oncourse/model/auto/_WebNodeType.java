package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebTheme;

/**
 * Class _WebNodeType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebNodeType extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NUMBER_OF_REGIONS_PROPERTY = "numberOfRegions";
    public static final String TEMPLATE_KEY_PROPERTY = "templateKey";
    public static final String DEFAULT_WEB_THEME_PROPERTY = "defaultWebTheme";
    public static final String WEB_NODES_PROPERTY = "webNodes";
    public static final String WEB_SITE_PROPERTY = "webSite";

    public static final String ID_PK_COLUMN = "id";

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

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
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

    public void setNumberOfRegions(Integer numberOfRegions) {
        writeProperty("numberOfRegions", numberOfRegions);
    }
    public Integer getNumberOfRegions() {
        return (Integer)readProperty("numberOfRegions");
    }

    public void setTemplateKey(String templateKey) {
        writeProperty("templateKey", templateKey);
    }
    public String getTemplateKey() {
        return (String)readProperty("templateKey");
    }

    public void setDefaultWebTheme(WebTheme defaultWebTheme) {
        setToOneTarget("defaultWebTheme", defaultWebTheme, true);
    }

    public WebTheme getDefaultWebTheme() {
        return (WebTheme)readProperty("defaultWebTheme");
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


    public void setWebSite(WebSite webSite) {
        setToOneTarget("webSite", webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty("webSite");
    }


}

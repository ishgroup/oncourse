package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.WebNode;
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
    public static final String DELETED_PROPERTY = "deleted";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String DEFAULT_WEB_THEME_PROPERTY = "defaultWebTheme";
    public static final String NODES_PROPERTY = "nodes";

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

    public void setDeleted(Boolean deleted) {
        writeProperty("deleted", deleted);
    }
    public Boolean getDeleted() {
        return (Boolean)readProperty("deleted");
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

    public void setDefaultWebTheme(WebTheme defaultWebTheme) {
        setToOneTarget("defaultWebTheme", defaultWebTheme, true);
    }

    public WebTheme getDefaultWebTheme() {
        return (WebTheme)readProperty("defaultWebTheme");
    }


    public void addToNodes(WebNode obj) {
        addToManyTarget("nodes", obj, true);
    }
    public void removeFromNodes(WebNode obj) {
        removeToManyTarget("nodes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebNode> getNodes() {
        return (List<WebNode>)readProperty("nodes");
    }


}

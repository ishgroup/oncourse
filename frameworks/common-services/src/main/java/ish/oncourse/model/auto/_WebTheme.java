package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.WebNodeType;

/**
 * Class _WebTheme was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebTheme extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String DESCRIPTION_TEXTILE_PROPERTY = "descriptionTextile";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String THEME_KEY_PROPERTY = "themeKey";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String WEB_NODE_TYPES_PROPERTY = "webNodeTypes";

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

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setDescriptionTextile(String descriptionTextile) {
        writeProperty("descriptionTextile", descriptionTextile);
    }
    public String getDescriptionTextile() {
        return (String)readProperty("descriptionTextile");
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

    public void setThemeKey(String themeKey) {
        writeProperty("themeKey", themeKey);
    }
    public String getThemeKey() {
        return (String)readProperty("themeKey");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToWebNodeTypes(WebNodeType obj) {
        addToManyTarget("webNodeTypes", obj, true);
    }
    public void removeFromWebNodeTypes(WebNodeType obj) {
        removeToManyTarget("webNodeTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebNodeType> getWebNodeTypes() {
        return (List<WebNodeType>)readProperty("webNodeTypes");
    }


}

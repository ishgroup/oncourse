package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;

/**
 * Class _WebMenu was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebMenu extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NAME_PROPERTY = "name";
    @Deprecated
    public static final String URL_PROPERTY = "url";
    @Deprecated
    public static final String WEIGHT_PROPERTY = "weight";
    @Deprecated
    public static final String CHILDREN_MENUS_PROPERTY = "childrenMenus";
    @Deprecated
    public static final String PARENT_WEB_MENU_PROPERTY = "parentWebMenu";
    @Deprecated
    public static final String WEB_NODE_PROPERTY = "webNode";
    @Deprecated
    public static final String WEB_SITE_VERSION_PROPERTY = "webSiteVersion";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> URL = new Property<String>("url");
    public static final Property<Integer> WEIGHT = new Property<Integer>("weight");
    public static final Property<List<WebMenu>> CHILDREN_MENUS = new Property<List<WebMenu>>("childrenMenus");
    public static final Property<WebMenu> PARENT_WEB_MENU = new Property<WebMenu>("parentWebMenu");
    public static final Property<WebNode> WEB_NODE = new Property<WebNode>("webNode");
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

    public void setUrl(String url) {
        writeProperty("url", url);
    }
    public String getUrl() {
        return (String)readProperty("url");
    }

    public void setWeight(Integer weight) {
        writeProperty("weight", weight);
    }
    public Integer getWeight() {
        return (Integer)readProperty("weight");
    }

    public void addToChildrenMenus(WebMenu obj) {
        addToManyTarget("childrenMenus", obj, true);
    }
    public void removeFromChildrenMenus(WebMenu obj) {
        removeToManyTarget("childrenMenus", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebMenu> getChildrenMenus() {
        return (List<WebMenu>)readProperty("childrenMenus");
    }


    public void setParentWebMenu(WebMenu parentWebMenu) {
        setToOneTarget("parentWebMenu", parentWebMenu, true);
    }

    public WebMenu getParentWebMenu() {
        return (WebMenu)readProperty("parentWebMenu");
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

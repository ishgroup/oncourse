package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;

/**
 * Class _WebMenu was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebMenu extends CayenneDataObject {

    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String URL_PROPERTY = "url";
    public static final String WEIGHT_PROPERTY = "weight";
    public static final String TO_WEB_MENU_PROPERTY = "toWebMenu";
    public static final String TO_WEB_NODE_PROPERTY = "toWebNode";
    public static final String TO_WEB_SITE_PROPERTY = "toWebSite";
    public static final String WEB_MENU_ARRAY_PROPERTY = "webMenuArray";

    public static final String ID_PK_COLUMN = "id";

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

    public void setToWebMenu(WebMenu toWebMenu) {
        setToOneTarget("toWebMenu", toWebMenu, true);
    }

    public WebMenu getToWebMenu() {
        return (WebMenu)readProperty("toWebMenu");
    }


    public void setToWebNode(WebNode toWebNode) {
        setToOneTarget("toWebNode", toWebNode, true);
    }

    public WebNode getToWebNode() {
        return (WebNode)readProperty("toWebNode");
    }


    public void setToWebSite(WebSite toWebSite) {
        setToOneTarget("toWebSite", toWebSite, true);
    }

    public WebSite getToWebSite() {
        return (WebSite)readProperty("toWebSite");
    }


    public void addToWebMenuArray(WebMenu obj) {
        addToManyTarget("webMenuArray", obj, true);
    }
    public void removeFromWebMenuArray(WebMenu obj) {
        removeToManyTarget("webMenuArray", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebMenu> getWebMenuArray() {
        return (List<WebMenu>)readProperty("webMenuArray");
    }


}

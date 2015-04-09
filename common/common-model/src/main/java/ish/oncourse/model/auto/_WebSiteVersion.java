package ish.oncourse.model.auto;

import ish.oncourse.model.*;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;
import java.util.List;

/**
 * Class _WebSiteVersion was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebSiteVersion extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String DEPLOYED_ON_PROPERTY = "deployedOn";
    @Deprecated
    public static final String CONTENTS_PROPERTY = "contents";
    @Deprecated
    public static final String DEPLOYED_BY_PROPERTY = "deployedBy";
    @Deprecated
    public static final String LAYOUTS_PROPERTY = "layouts";
    @Deprecated
    public static final String MENUS_PROPERTY = "menus";
    @Deprecated
    public static final String WEB_NODE_TYPES_PROPERTY = "webNodeTypes";
    @Deprecated
    public static final String WEB_NODES_PROPERTY = "webNodes";
    @Deprecated
    public static final String WEB_SITE_PROPERTY = "webSite";
    @Deprecated
    public static final String WEB_URLALIASES_PROPERTY = "webURLAliases";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> DEPLOYED_ON = new Property<Date>("deployedOn");
    public static final Property<List<WebContent>> CONTENTS = new Property<List<WebContent>>("contents");
    public static final Property<SystemUser> DEPLOYED_BY = new Property<SystemUser>("deployedBy");
    public static final Property<List<WebSiteLayout>> LAYOUTS = new Property<List<WebSiteLayout>>("layouts");
    public static final Property<List<WebMenu>> MENUS = new Property<List<WebMenu>>("menus");
    public static final Property<List<WebNodeType>> WEB_NODE_TYPES = new Property<List<WebNodeType>>("webNodeTypes");
    public static final Property<List<WebNode>> WEB_NODES = new Property<List<WebNode>>("webNodes");
    public static final Property<WebSite> WEB_SITE = new Property<WebSite>("webSite");
    public static final Property<List<WebUrlAlias>> WEB_URLALIASES = new Property<List<WebUrlAlias>>("webURLAliases");

    public void setDeployedOn(Date deployedOn) {
        writeProperty("deployedOn", deployedOn);
    }
    public Date getDeployedOn() {
        return (Date)readProperty("deployedOn");
    }

    public void addToContents(WebContent obj) {
        addToManyTarget("contents", obj, true);
    }
    public void removeFromContents(WebContent obj) {
        removeToManyTarget("contents", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebContent> getContents() {
        return (List<WebContent>)readProperty("contents");
    }


    public void setDeployedBy(SystemUser deployedBy) {
        setToOneTarget("deployedBy", deployedBy, true);
    }

    public SystemUser getDeployedBy() {
        return (SystemUser)readProperty("deployedBy");
    }


    public void addToLayouts(WebSiteLayout obj) {
        addToManyTarget("layouts", obj, true);
    }
    public void removeFromLayouts(WebSiteLayout obj) {
        removeToManyTarget("layouts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebSiteLayout> getLayouts() {
        return (List<WebSiteLayout>)readProperty("layouts");
    }


    public void addToMenus(WebMenu obj) {
        addToManyTarget("menus", obj, true);
    }
    public void removeFromMenus(WebMenu obj) {
        removeToManyTarget("menus", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebMenu> getMenus() {
        return (List<WebMenu>)readProperty("menus");
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


    public void addToWebURLAliases(WebUrlAlias obj) {
        addToManyTarget("webURLAliases", obj, true);
    }
    public void removeFromWebURLAliases(WebUrlAlias obj) {
        removeToManyTarget("webURLAliases", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebUrlAlias> getWebURLAliases() {
        return (List<WebUrlAlias>)readProperty("webURLAliases");
    }


}

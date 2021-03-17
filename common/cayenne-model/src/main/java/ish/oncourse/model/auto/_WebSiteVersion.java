package ish.oncourse.model.auto;

import ish.oncourse.model.*;
import org.apache.cayenne.exp.Property;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Class _WebSiteVersion was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WebSiteVersion extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String DEPLOYED_ON_PROPERTY = "deployedOn";
    public static final String SITE_VERSION_PROPERTY = "siteVersion";
    public static final String CONTENTS_PROPERTY = "contents";
    public static final String DEPLOYED_BY_PROPERTY = "deployedBy";
    public static final String LAYOUTS_PROPERTY = "layouts";
    public static final String MENUS_PROPERTY = "menus";
    public static final String WEB_NODE_TYPES_PROPERTY = "webNodeTypes";
    public static final String WEB_NODES_PROPERTY = "webNodes";
    public static final String WEB_SITE_PROPERTY = "webSite";
    public static final String WEB_URLALIASES_PROPERTY = "webURLAliases";
    public static final String WEB_LAYOUT_PATHS_PROPERTY = "webLayoutPaths";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> DEPLOYED_ON = Property.create("deployedOn", Date.class);
    public static final Property<Long> SITE_VERSION = Property.create("siteVersion", Long.class);
    public static final Property<List<WebContent>> CONTENTS = Property.create("contents", List.class);
    public static final Property<SystemUser> DEPLOYED_BY = Property.create("deployedBy", SystemUser.class);
    public static final Property<List<WebSiteLayout>> LAYOUTS = Property.create("layouts", List.class);
    public static final Property<List<WebMenu>> MENUS = Property.create("menus", List.class);
    public static final Property<List<WebNodeType>> WEB_NODE_TYPES = Property.create("webNodeTypes", List.class);
    public static final Property<List<WebNode>> WEB_NODES = Property.create("webNodes", List.class);
    public static final Property<WebSite> WEB_SITE = Property.create("webSite", WebSite.class);
    public static final Property<List<WebUrlAlias>> WEB_URLALIASES = Property.create("webURLAliases", List.class);
    public static final Property<List<WebLayoutPath>> WEB_LAYOUT_PATHS = Property.create("webLayoutPaths", List.class);

    protected Date deployedOn;
    protected Long siteVersion;

    protected Object contents;
    protected Object deployedBy;
    protected Object layouts;
    protected Object menus;
    protected Object webNodeTypes;
    protected Object webNodes;
    protected Object webSite;
    protected Object webURLAliases;
    protected Object webLayoutPaths;

    public void setDeployedOn(Date deployedOn) {
        beforePropertyWrite("deployedOn", this.deployedOn, deployedOn);
        this.deployedOn = deployedOn;
    }

    public Date getDeployedOn() {
        beforePropertyRead("deployedOn");
        return this.deployedOn;
    }

    public void setSiteVersion(Long siteVersion) {
        beforePropertyWrite("siteVersion", this.siteVersion, siteVersion);
        this.siteVersion = siteVersion;
    }

    public Long getSiteVersion() {
        beforePropertyRead("siteVersion");
        return this.siteVersion;
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

    public void addToWebLayoutPaths(WebLayoutPath obj) {
        addToManyTarget("webLayoutPaths", obj, true);
    }

    public void removeFromWebLayoutPaths(WebLayoutPath obj) {
        removeToManyTarget("webLayoutPaths", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<WebLayoutPath> getWebLayoutPaths() {
        return (List<WebLayoutPath>)readProperty("webLayoutPaths");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "deployedOn":
                return this.deployedOn;
            case "siteVersion":
                return this.siteVersion;
            case "contents":
                return this.contents;
            case "deployedBy":
                return this.deployedBy;
            case "layouts":
                return this.layouts;
            case "menus":
                return this.menus;
            case "webNodeTypes":
                return this.webNodeTypes;
            case "webNodes":
                return this.webNodes;
            case "webSite":
                return this.webSite;
            case "webURLAliases":
                return this.webURLAliases;
            case "webLayoutPaths":
                return this.webLayoutPaths;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "deployedOn":
                this.deployedOn = (Date)val;
                break;
            case "siteVersion":
                this.siteVersion = (Long)val;
                break;
            case "contents":
                this.contents = val;
                break;
            case "deployedBy":
                this.deployedBy = val;
                break;
            case "layouts":
                this.layouts = val;
                break;
            case "menus":
                this.menus = val;
                break;
            case "webNodeTypes":
                this.webNodeTypes = val;
                break;
            case "webNodes":
                this.webNodes = val;
                break;
            case "webSite":
                this.webSite = val;
                break;
            case "webURLAliases":
                this.webURLAliases = val;
                break;
            case "webLayoutPaths":
                this.webLayoutPaths = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.deployedOn);
        out.writeObject(this.siteVersion);
        out.writeObject(this.contents);
        out.writeObject(this.deployedBy);
        out.writeObject(this.layouts);
        out.writeObject(this.menus);
        out.writeObject(this.webNodeTypes);
        out.writeObject(this.webNodes);
        out.writeObject(this.webSite);
        out.writeObject(this.webURLAliases);
        out.writeObject(this.webLayoutPaths);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.deployedOn = (Date)in.readObject();
        this.siteVersion = (Long)in.readObject();
        this.contents = in.readObject();
        this.deployedBy = in.readObject();
        this.layouts = in.readObject();
        this.menus = in.readObject();
        this.webNodeTypes = in.readObject();
        this.webNodes = in.readObject();
        this.webSite = in.readObject();
        this.webURLAliases = in.readObject();
        this.webLayoutPaths = in.readObject();
    }

}

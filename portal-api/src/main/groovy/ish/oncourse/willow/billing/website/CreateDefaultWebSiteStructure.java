package ish.oncourse.willow.billing.website;

import ish.oncourse.model.*;
import ish.oncourse.specialpages.RequestMatchType;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

public class CreateDefaultWebSiteStructure {
    public final static String DEFAULT_HOME_PAGE_NAME = "Home page";

    private WebSiteVersion stagedVersion;
    private ObjectContext context;

    public void create() {
        Date now = new Date();
        WebSiteLayout webSiteLayout = context.newObject(WebSiteLayout.class);
        webSiteLayout.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY);
        webSiteLayout.setWebSiteVersion(stagedVersion);

        WebNodeType page = context.newObject(WebNodeType.class);
        page.setName(WebNodeType.PAGE);
        page.setCreated(now);
        page.setModified(now);
        page.setWebSiteLayout(webSiteLayout);
        page.setWebSiteVersion(stagedVersion);

        WebLayoutPath layoutPath = context.newObject(WebLayoutPath.class);
        layoutPath.setCreated(new Date());
        layoutPath.setModified(new Date());
        layoutPath.setWebSiteVersion(stagedVersion);
        layoutPath.setWebNodeType(page);
        layoutPath.setPath("/");
        layoutPath.setMatchType(RequestMatchType.STARTS_WITH);
        
        WebNode node = createNewNodeBy(page);
        node.setPublished(true);

        WebMenu menu = context.newObject(WebMenu.class);
        menu.setName("Home");
        menu.setCreated(now);
        menu.setModified(now);
        menu.setWebSiteVersion(stagedVersion);
        menu.setWeight(1);
        menu.setWebNode(node);
		
        context.commitChanges();

        WebUrlAlias urlAlias = context.newObject(WebUrlAlias.class);
        urlAlias.setWebSiteVersion(stagedVersion);
        urlAlias.setUrlPath("/");
        urlAlias.setWebNode(node);
        urlAlias.setDefault(true);
        context.commitChanges();
    }
    
    private WebNode createNewNodeBy(WebNodeType webNodeType) {
        ObjectContext ctx = stagedVersion.getObjectContext();
        WebNode newPageNode = ctx.newObject(WebNode.class);
        newPageNode.setName(DEFAULT_HOME_PAGE_NAME);
        newPageNode.setWebSiteVersion(stagedVersion);
        newPageNode.setNodeNumber(1);
        
        WebContent webContent = ctx.newObject(WebContent.class);
        webContent.setWebSiteVersion(stagedVersion);
        webContent.setContentTextile(DEFAULT_HOME_PAGE_NAME);
        webContent.setContent(DEFAULT_HOME_PAGE_NAME);

        WebContentVisibility webContentVisibility = ctx.newObject(WebContentVisibility.class);
        webContentVisibility.setWebNode(newPageNode);
        webContentVisibility.setRegionKey(RegionKey.content);
        webContentVisibility.setWebContent(webContent);

        return newPageNode;
    }

    public static CreateDefaultWebSiteStructure valueOf(WebSiteVersion stagedVersion, ObjectContext context) {
        CreateDefaultWebSiteStructure result = new CreateDefaultWebSiteStructure();
        result.stagedVersion = stagedVersion;
        result.context = context;
        return result;
    }
}

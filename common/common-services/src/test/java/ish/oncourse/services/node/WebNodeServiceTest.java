package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class WebNodeServiceTest  extends ServiceTest  {
    private ICayenneService cayenneService;
    private IWebNodeService webNodeService;
    private IWebSiteService webSiteService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
        new LoadDataSet().dataSetFile("ish/oncourse/services/node/WebNodeServiceTest.xml").load(testContext.getDS());
        this.cayenneService = getService(ICayenneService.class);
        this.webNodeService = getService(IWebNodeService.class);
        this.webSiteService = getService(IWebSiteService.class);
    }

    @Test
    public void testCreateWebNode()
    {
        ObjectContext context =  cayenneService.newNonReplicatingContext();
        WebNode webNode = webNodeService.createNewNode();
        assertWebNode(webNode);
    }

    @Test
    public void testCreateWebNodeBy()
    {
        ObjectContext context =  cayenneService.newNonReplicatingContext();

        WebSite site = context.newObject(WebSite.class);

		WebSiteVersion siteVersion = context.newObject(WebSiteVersion.class);
		siteVersion.setWebSite(site);
        siteVersion.setSiteVersion(1L);

        WebNodeType page = context.newObject(WebNodeType.class);
        page.setName("page");
        page.setWebSiteVersion(siteVersion);

        WebNode webNode = webNodeService.createNewNodeBy(siteVersion, "Test", "Test", 1);
        assertWebNode(webNode);
    }


    private void assertWebNode(WebNode webNode) {
        assertNotNull(webNode.getWebSiteVersion());
        assertNotNull(webNode.getWebContentVisibility());
        assertEquals(1,webNode.getWebContentVisibility().size());
        assertNotNull(webNode.getWebContentVisibility().get(0));
        assertNotNull(webNode.getWebContentVisibility().get(0).getWebContent());
    }

}

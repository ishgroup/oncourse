package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class WebNodeServiceTest  extends ServiceTest  {
    private ICayenneService cayenneService;
    private IWebNodeService webNodeService;
    private IWebSiteService webSiteService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
        InputStream st = WebNodeServiceTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/services/node/WebNodeServiceTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource dataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);

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

        WebNodeType page = context.newObject(WebNodeType.class);
        page.setName("page");
        page.setWebSiteVersion(siteVersion);

        WebNode webNode = webNodeService.createNewNodeBy(siteVersion, page, "Test", "Test", 1);
        assertWebNode(webNode);
    }


    private void assertWebNode(WebNode webNode) {
        assertNotNull(webNode.getWebSiteVersion());
        assertNotNull(webNode.getWebNodeType());
        assertNotNull(webNode.getWebContentVisibility());
        assertEquals(1,webNode.getWebContentVisibility().size());
        assertNotNull(webNode.getWebContentVisibility().get(0));
        assertNotNull(webNode.getWebContentVisibility().get(0).getWebContent());
    }

}

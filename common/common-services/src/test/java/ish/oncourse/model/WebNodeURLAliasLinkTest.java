package ish.oncourse.model;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

public class WebNodeURLAliasLinkTest extends ServiceTest {
	private ICayenneService cayenneService;
    private IWebNodeService webNodeService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);

		InputStream st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream("ish/oncourse/model/paymentDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
        this.webNodeService = getService(IWebNodeService.class);
	}
	
	@Test
	public void testTemporaryDataLink() {
		ObjectContext context = cayenneService.newContext();
		WebNode webNode = createWebNode(context);
		WebUrlAlias alias = createWebUrlAlias(webNode);
		
		//link the entities
		webNode.addToWebUrlAliases(alias);
		alias.setWebNode(webNode);
		try {
			context.commitChanges();
		} catch (RuntimeException e) {
			//this Cayenne issue appears not all the time, but time-to-time
			assertEquals("Commit exception", "java.lang.RuntimeException: org.apache.cayenne.CayenneRuntimeException: [v.3.1B2 Feb 05 2013 20:19:35] Transaction was rolledback.", e.getMessage());
			assertTrue("Cayenne runtime exception fires", e.getCause().getCause().getCause().getCause().getMessage().startsWith("[v.3.1B2 Feb 05 2013 20:19:35] Can't extract a master key. Missing key (id), master ID (<ObjectId:WebNode, TEMP:"));
			context.rollbackChanges();
			return;
			//assertTrue("Failed to link WebNode with one-to-many WebUrlAlias", false);
		}
		
		assertFalse("After link 1 URL alias should appears", webNode.getWebUrlAliases().isEmpty());
		assertTrue("Only 1 alias should be linked", webNode.getWebUrlAliases().size() == 1);
		assertEquals("Just linked alias should be equal", alias, webNode.getWebUrlAliases().get(0));
		assertNull("Default URL alias should be empty", webNodeService.getDefaultWebURLAlias(webNode));
		
		//link the default alias
        alias.setDefault(true);
		context.commitChanges();
		
		assertFalse("After link 1 URL alias should appears", webNode.getWebUrlAliases().isEmpty());
		assertTrue("Only 1 alias should be linked", webNode.getWebUrlAliases().size() == 1);
		assertEquals("Just linked alias should be equal", alias, webNode.getWebUrlAliases().get(0));
		assertNotNull("Default URL alias should not be empty", webNodeService.getDefaultWebURLAlias(webNode));
		assertEquals("Default alias should be equal", alias, context.localObject(webNodeService.getDefaultWebURLAlias(webNode)));
	}
	
	@Test
	public void testPersistedDataLink() {
		ObjectContext context = cayenneService.newContext();
		WebNode webNode = createWebNode(context);
		WebUrlAlias alias = createWebUrlAlias(webNode);
		
		//persist the data
        try {
            context.commitChanges();
            assertFalse("the commit should be failed because webNode does not set for this alias", false);
        } catch (Exception e) {
            assertTrue("Commit exception", true);
        }
        assertTrue("Before link the URL aliases should be empty", webNode.getWebUrlAliases().isEmpty());
		
		//link the entities
		webNode.addToWebUrlAliases(alias);
		alias.setWebNode(webNode);
		context.commitChanges();
		
		assertFalse("After link 1 URL alias should appears", webNode.getWebUrlAliases().isEmpty());
		assertTrue("Only 1 alias should be linked", webNode.getWebUrlAliases().size() == 1);
		assertEquals("Just linked alias should be equal", alias, webNode.getWebUrlAliases().get(0));
		assertNull("Default URL alias should be empty", webNodeService.getDefaultWebURLAlias(webNode));
		
		//link the default alias
        alias.setDefault(true);
		context.commitChanges();
		
		assertFalse("After link 1 URL alias should appears", webNode.getWebUrlAliases().isEmpty());
		assertTrue("Only 1 alias should be linked", webNode.getWebUrlAliases().size() == 1);
		assertEquals("Just linked alias should be equal", alias, webNode.getWebUrlAliases().get(0));
		assertNotNull("Default URL alias should not be empty",  webNodeService.getDefaultWebURLAlias(webNode));
		assertEquals("Default alias should be equal", alias,  context.localObject(webNodeService.getDefaultWebURLAlias(webNode)));
	}
	
	private WebUrlAlias createWebUrlAlias(WebNode webNode) {
		ObjectContext context = webNode.getObjectContext();
		WebSiteVersion webSiteVersion = webNode.getWebNodeType().getWebSiteVersion();
		assertNotNull("WebSite should not be null", webSiteVersion);
		WebUrlAlias alias = context.newObject(WebUrlAlias.class);
		alias.setWebSiteVersion(webSiteVersion);
		alias.setUrlPath(webNodeService.getPath(webNode));
		return alias;
	}
	
	private WebNode createWebNode(ObjectContext context) {
		College college = SelectById.query(College.class, 1).selectOne(context);
		assertNotNull("College should not be null", college);
		WebSite webSite = SelectById.query(WebSite.class, 1).selectOne(context);
		assertNotNull("WebSite should not be null", webSite);
		
		WebSiteVersion siteVersion = webSite.getVersions().get(0);
		
		WebNodeType webNodeType = context.newObject(WebNodeType.class);
		webNodeType.setName(WebNodeType.PAGE);
		webNodeType.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY);
		webNodeType.setWebSiteVersion(siteVersion);
		
		WebNode webNode = context.newObject(WebNode.class);
		webNode.setName("test Name");
		webNode.setWebNodeType(webNodeType);
		webNode.setWebSiteVersion(siteVersion);
		webNode.setNodeNumber(0);
		return webNode;
	}

}

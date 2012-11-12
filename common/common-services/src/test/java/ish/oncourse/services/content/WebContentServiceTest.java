package ish.oncourse.services.content;

import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.College;
import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import static org.junit.Assert.*;

public class WebContentServiceTest extends ServiceTest {
	private ICayenneService cayenneService;
	private IWebContentService service;
	private IWebSiteService webSiteService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		InputStream st = WebContentServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/node/WebNodeServiceTest.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		cayenneService = getService(ICayenneService.class);
		service = getService(IWebContentService.class);
		webSiteService = getService(IWebSiteService.class);
	}
	
	@Test
	public void testGetBlockVisibilityForRegionKey() {
		assertNotNull("WebContentService should be initialized", service);
		assertNull("Null should be returned if both parameter not specified", service.getBlockVisibilityForRegionKey(null, null));
		assertNull("Null should be returned for unassigned regionKey", service.getBlockVisibilityForRegionKey(null, RegionKey.unassigned));
		//service
		ObjectContext context = cayenneService.newContext();
		WebSite website = webSiteService.getCurrentWebSite();
		website = (WebSite) context.localObject(website.getObjectId(), null);
		//College college = website.getCollege();
		
		@SuppressWarnings("unchecked")
		List<WebNodeType> webnodes = context.performQuery(new SelectQuery(WebNodeType.class));
		context.deleteObjects(webnodes);
		context.commitChanges();
		
		WebNodeType webNodeType = context.newObject(WebNodeType.class);
		webNodeType.setName("test name");
		webNodeType.setLayoutKey("test key");
		webNodeType.setWebSite(website);
		context.commitChanges();
		assertTrue("Empty list should be returned if no data linked with region", 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).isEmpty());
		
		WebContent webContent = context.newObject(WebContent.class);
		webContent.setWebSite(website);
		WebContentVisibility visibility = context.newObject(WebContentVisibility.class);
		visibility.setWebNodeType(webNodeType);
		visibility.setRegionKey(RegionKey.content);
		visibility.setWebContent(webContent);
		visibility.setWeight(2);
		WebContentVisibility visibility2 = context.newObject(WebContentVisibility.class);
		visibility2.setWebNodeType(webNodeType);
		visibility2.setRegionKey(RegionKey.content);
		visibility2.setWebContent(webContent);
		visibility2.setWeight(1);
		context.commitChanges();
		assertFalse("2 elements should be returned", service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).isEmpty());
		assertEquals("2 elements should be returned", 2, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertEquals("First visibility should be returned", visibility, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(1));
		assertEquals("Second visibility should be returned", visibility2, 
				service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0));
	}

}

package ish.oncourse.services.content;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

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
		
		ObjectContext context = cayenneService.newContext();
		WebSite website = webSiteService.getCurrentWebSite();
		website = context.localObject(website);

		WebSiteVersion webSiteVersion = website.getVersions().get(0);
		
		@SuppressWarnings("unchecked")
		List<WebNodeType> webnodes = context.performQuery(new SelectQuery(WebNodeType.class));
		context.deleteObjects(webnodes);
		context.commitChanges();
		
		WebNodeType webNodeType = context.newObject(WebNodeType.class);
		webNodeType.setName("test name");
		webNodeType.setLayoutKey("test key");
		webNodeType.setWebSiteVersion(webSiteVersion);
		context.commitChanges();
		assertTrue("Empty list should be returned if no data linked with region", 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).isEmpty());
		
		WebContent webContent = context.newObject(WebContent.class);
		webContent.setWebSiteVersion(webSiteVersion);
        webContent.setName("Default");
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
	
	@Test
	public void testPutWebContentVisibilityToPosition() {
		assertNotNull("WebContentService should be initialized", service);
		
		ObjectContext context = cayenneService.newContext();
		WebSite website = webSiteService.getCurrentWebSite();
		website = context.localObject(website);

		WebSiteVersion webSiteVersion = website.getVersions().get(0);
		
		@SuppressWarnings("unchecked")
		List<WebNodeType> webnodes = context.performQuery(new SelectQuery(WebNodeType.class));
		context.deleteObjects(webnodes);
		context.commitChanges();
		
		WebNodeType webNodeType = context.newObject(WebNodeType.class);
		webNodeType.setName("test name");
		webNodeType.setLayoutKey("test key");
		webNodeType.setWebSiteVersion(webSiteVersion);
		WebContent webContent = context.newObject(WebContent.class);
        webContent.setName("Default");
        webContent.setWebSiteVersion(webSiteVersion);
		WebContentVisibility visibility = context.newObject(WebContentVisibility.class);
		visibility.setWebNodeType(webNodeType);
		visibility.setRegionKey(RegionKey.content);
		visibility.setWebContent(webContent);
		visibility.setWeight(2);
		WebContentVisibility visibility2 = context.newObject(WebContentVisibility.class);
		visibility2.setWebNodeType(webNodeType);
		visibility2.setRegionKey(RegionKey.content);
		visibility2.setWebContent(webContent);
		visibility2.setWeight(3);
		WebContentVisibility visibility3 = context.newObject(WebContentVisibility.class);
		visibility3.setWebNodeType(webNodeType);
		visibility3.setRegionKey(RegionKey.header);
		visibility3.setWebContent(webContent);
		visibility3.setWeight(7);
		
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.unassigned, visibility, 1);
		assertEquals("Nothing should be changed because unassigned requested as parameter", 2, visibility.getWeight().intValue());
		assertEquals("Nothing should be changed because unassigned requested as parameter", RegionKey.content, visibility.getRegionKey());
		
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.footer, visibility, 1);
		assertEquals("Region key for visibility should be changed on this request even for temporary webNodeType", RegionKey.footer, 
			visibility.getRegionKey());
		assertEquals("Weight for visibility should be changed on this request even for temporary webNodeType", 1, visibility.getWeight().intValue());
		context.commitChanges();
		
		assertTrue("Empty list should be returned because no data linked with region", 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.left).isEmpty());
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.left, visibility, 2);
		assertEquals("Region key for visibility should be changed on this request even if no contentVisibilities were linked with this region before", 
			RegionKey.left, visibility.getRegionKey());
		assertEquals("Weight for visibility should be changed on this request even if no contentVisibilities were linked with this region before", 
			2, visibility.getWeight().intValue());
		context.commitChanges();
		
		//test the sort re-ordering
		assertFalse("Not empty list should be returned because data linked with region", 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).isEmpty());
		assertEquals("Content should have 1 linked visibility", 1, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertEquals("visibility2 element should be linked with content region", visibility2, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0));
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.content, visibility, 3);//3 position will be replaced to maximum available 2
		assertEquals("Content should have 2 linked visibilities", 2, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertTrue("After re-ordering visibility weight should be higher then visibility2", visibility.getWeight() > visibility2.getWeight());
		assertEquals("Visibility weight should be 1", 1, visibility.getWeight().intValue());
		assertEquals("Visibility2 weight should be 0", 0, visibility2.getWeight().intValue());
		
		assertEquals("Visibility3 linked with header region", RegionKey.header, visibility3.getRegionKey());
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.content, visibility3, 1);
		assertEquals("Content should have 3 linked visibilities", 3, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertEquals("Visibility2 should be the first element in the list", visibility2, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0));
		assertEquals("Visibility3 should be the second element in the list", visibility3, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(1));
		assertEquals("Visibility should be the third element in the list", visibility, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(2));
		
		//test back re-ordering
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.content, visibility, 0);
		assertEquals("Content should have 3 linked visibilities", 3, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertEquals("Visibility2 should be the second element in the list", visibility2, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(1));
		assertEquals("Visibility3 should be the third element in the list", visibility3, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(2));
		assertEquals("Visibility should be the first element in the list", visibility, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0));
		context.commitChanges();
		
		//test that move visibility out of the block not change the previous container weight
		assertEquals("Visibility2 should be the second element in the list with weight 1", 1, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(1).getWeight().intValue());
		assertEquals("Visibility3 should be the third element in the list with weight 2", 2, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(2).getWeight().intValue());
		assertEquals("Visibility should be the first element in the list with weight 0", 0, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0).getWeight().intValue());
		assertEquals("Headed should be empty", 0, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.header).size());
		service.putWebContentVisibilityToPosition(webNodeType, RegionKey.header, visibility2, 0);
		assertEquals("Headed should containt 1 element", 1, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.header).size());
		assertEquals("Headed should containt visibility2", visibility2, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.header).get(0));
		assertEquals("Contant should containt 2 elements", 2, service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).size());
		assertEquals("Visibility3 should be the second element in the list with weight 2", 2, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(1).getWeight().intValue());
		assertEquals("Visibility should be the first element in the list with weight 0", 0, 
			service.getBlockVisibilityForRegionKey(webNodeType, RegionKey.content).get(0).getWeight().intValue());
		context.commitChanges();
	}

}

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services;

import ish.oncourse.cms.services.site.CMSWebSiteVersionService;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.time.DateUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CMSWebSiteVersionServiceTest extends ServiceTest {

	private ICayenneService cayenneService;

	private IWebSiteVersionService webSiteVersionService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.admin", "admin", CmsTestModule.class);
		InputStream st = CMSWebSiteVersionService.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/cms/services/webSiteVersionServiceOverrideTestDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		cayenneService = getService(ICayenneService.class);
		webSiteVersionService = getService(IWebSiteVersionService.class);
	}

	/**
	 * delete all revisions older than 60 days, but always to keep at least 5 revisions, even if they are older
	 */
	@Test
	public void testDeleteOldWebSiteVersions() {
		ObjectContext context = cayenneService.newContext();
		
		//get current wersion before
		WebSiteVersion currentVersion = webSiteVersionService.getCurrentVersion();

		//case 1 always to keep at least 5 revisions, even if they are older than 60 days
		SelectQuery selectQuery = new SelectQuery(WebSite.class, ExpressionFactory.matchDbExp(WebSite.ID_PK_COLUMN, 1));
		List<WebSite> webSites = context.performQuery(selectQuery);
		assertEquals(1, webSites.size());
		WebSite siteToDelete = webSites.get(0);

		selectQuery = new SelectQuery(WebSiteVersion.class, ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, siteToDelete));
		List<WebSiteVersion> allVersions = context.performQuery(selectQuery);

		assertEquals(12, allVersions.size());
		//number of revisions which younger than 60 days
		assertEquals(3, ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet (always one)
		assertEquals(1, ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null).filterObjects(allVersions).size());

		webSiteVersionService.removeOldWebSiteVersions(siteToDelete);

		allVersions = context.performQuery(selectQuery);

		//the number of remaining
		assertEquals(5, allVersions.size());
		//including number of revisions younger than 60 days (currently deployed too)
		assertEquals(3, ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet 
		assertEquals(1, ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null).filterObjects(allVersions).size());
		//check that current version is not removed
		assertTrue(webSiteVersionService.getCurrentVersion().equals(currentVersion));


		//case 2 delete all revisions older than 60 days (number of revisions which younger than 60 days more than 5)
		selectQuery = new SelectQuery(WebSite.class, ExpressionFactory.matchDbExp(WebSite.ID_PK_COLUMN, 2));
		webSites = context.performQuery(selectQuery);
		assertEquals(1, webSites.size());
		siteToDelete = webSites.get(0);

		selectQuery = new SelectQuery(WebSiteVersion.class, ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, siteToDelete));
		allVersions = context.performQuery(selectQuery);

		assertEquals(12, allVersions.size());
		//number of revisions which younger than 60 days
		assertEquals(7, ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet (always one)
		assertEquals(1, ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null).filterObjects(allVersions).size());

		webSiteVersionService.removeOldWebSiteVersions(siteToDelete);

		allVersions = context.performQuery(selectQuery);

		//the number of remaining
		assertEquals(8, allVersions.size());
		//including number of revisions younger than 60 days (currently deployed too)
		assertEquals(7, ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet 
		assertEquals(1, ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null).filterObjects(allVersions).size());

	}


	@Test
	public void testDeleteWebSiteVersion() {
		ObjectContext context = cayenneService.newContext();

		SelectQuery selectQuery = new SelectQuery(WebSiteVersion.class, ExpressionFactory.matchDbExp(WebSiteVersion.ID_PK_COLUMN, 3));
		List<WebSiteVersion> list = context.performQuery(selectQuery);
		assertEquals(1,list.size());
		WebSiteVersion versionToDelete = list.get(0);

		checkCountOfObjectBeforeMethod(context, versionToDelete);

		webSiteVersionService.deleteWebSiteVersion(versionToDelete);

		checkCountOfObjectAfterMethod(context, versionToDelete);
	}

	private void checkCountOfObjectBeforeMethod(ObjectContext context, WebSiteVersion versionToDelete) {
		SelectQuery selectQuery1 = new SelectQuery(WebSite.class);
		SelectQuery selectQuery2 = new SelectQuery(WebSiteVersion.class);
		SelectQuery selectQuery3 = new SelectQuery(WebSiteLayout.class);
		SelectQuery selectQuery4 = new SelectQuery(WebTemplate.class);
		SelectQuery selectQuery5 = new SelectQuery(WebNodeType.class);
		SelectQuery selectQuery6 = new SelectQuery(WebNode.class);
		SelectQuery selectQuery7 = new SelectQuery(WebUrlAlias.class);
		SelectQuery selectQuery8 = new SelectQuery(WebContent.class);
		SelectQuery selectQuery9 = new SelectQuery(WebContentVisibility.class);
		SelectQuery selectQuery0 = new SelectQuery(WebMenu.class);

		List<WebSite> list1 = context.performQuery(selectQuery1);
		List<WebSiteVersion> list2 = context.performQuery(selectQuery2);
		List<WebSiteLayout> list3 = context.performQuery(selectQuery3);
		List<WebTemplate> list4 = context.performQuery(selectQuery4);
		List<WebNodeType> list5 = context.performQuery(selectQuery5);
		List<WebNode> list6 = context.performQuery(selectQuery6);
		List<WebUrlAlias> list7 = context.performQuery(selectQuery7);
		List<WebContent> list8 = context.performQuery(selectQuery8);
		List<WebContentVisibility> list9 = context.performQuery(selectQuery9);
		List<WebMenu> list0 = context.performQuery(selectQuery0);

		assertEquals(2, list1.size());
		assertEquals(24, list2.size());
		assertEquals(10, list3.size());
		assertEquals(34, list4.size());
		assertEquals(6, list5.size());
		assertEquals(19, list6.size());
		assertEquals(21, list7.size());
		assertEquals(7, list8.size());
		assertEquals(16, list9.size());
		assertEquals(11, list0.size());

		List<WebSiteLayout> webSiteLayouts = ExpressionFactory.matchExp(WebSiteLayout.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list3);
		List<WebTemplate> webTemplates = ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, webSiteLayouts).filterObjects(list4);
		List<WebNodeType> webNodeTypes = ExpressionFactory.matchExp(WebNodeType.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list5);
		List<WebNode> webNodes = ExpressionFactory.matchExp(WebNode.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list6);
		List<WebUrlAlias> webUrlAliases = ExpressionFactory.matchExp(WebUrlAlias.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list7);
		List<WebContent> webContents = ExpressionFactory.matchExp(WebContent.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list8);
		List<WebContentVisibility> webContentVisibilities = ExpressionFactory.matchExp(WebContentVisibility.WEB_CONTENT_PROPERTY, webContents).filterObjects(list9);
		List<WebMenu> menuList = ExpressionFactory.matchExp(WebMenu.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list0);

		assertEquals(4, webSiteLayouts.size());
		assertEquals(18, webTemplates.size());
		assertEquals(3, webNodeTypes.size());
		assertEquals(9, webNodes.size());
		assertEquals(5, webUrlAliases.size());
		assertEquals(2, webContents.size());
		assertEquals(7, webContentVisibilities.size());
		assertEquals(5, menuList.size());
	}

	private void checkCountOfObjectAfterMethod(ObjectContext context, WebSiteVersion versionToDelete) {
		SelectQuery selectQuery1 = new SelectQuery(WebSite.class);
		SelectQuery selectQuery2 = new SelectQuery(WebSiteVersion.class);
		SelectQuery selectQuery3 = new SelectQuery(WebSiteLayout.class);
		SelectQuery selectQuery4 = new SelectQuery(WebTemplate.class);
		SelectQuery selectQuery5 = new SelectQuery(WebNodeType.class);
		SelectQuery selectQuery6 = new SelectQuery(WebNode.class);
		SelectQuery selectQuery7 = new SelectQuery(WebUrlAlias.class);
		SelectQuery selectQuery8 = new SelectQuery(WebContent.class);
		SelectQuery selectQuery9 = new SelectQuery(WebContentVisibility.class);
		SelectQuery selectQuery0 = new SelectQuery(WebMenu.class);

		List<WebSite> list1 = context.performQuery(selectQuery1);
		List<WebSiteVersion> list2 = context.performQuery(selectQuery2);
		List<WebSiteLayout> list3 = context.performQuery(selectQuery3);
		List<WebTemplate> list4 = context.performQuery(selectQuery4);
		List<WebNodeType> list5 = context.performQuery(selectQuery5);
		List<WebNode> list6 = context.performQuery(selectQuery6);
		List<WebUrlAlias> list7 = context.performQuery(selectQuery7);
		List<WebContent> list8 = context.performQuery(selectQuery8);
		List<WebContentVisibility> list9 = context.performQuery(selectQuery9);
		List<WebMenu> list0 = context.performQuery(selectQuery0);

		assertEquals(2, list1.size());
		assertEquals(23, list2.size());
		assertEquals(6, list3.size());
		assertEquals(16, list4.size());
		assertEquals(3, list5.size());
		assertEquals(10, list6.size());
		assertEquals(16, list7.size());
		assertEquals(5, list8.size());
		assertEquals(9, list9.size());
		assertEquals(6, list0.size());

		List<WebSiteLayout> webSiteLayouts = ExpressionFactory.matchExp(WebSiteLayout.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list3);
		List<WebTemplate> webTemplates = ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, webSiteLayouts).filterObjects(list4);
		List<WebNodeType> webNodeTypes = ExpressionFactory.matchExp(WebNodeType.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list5);
		List<WebNode> webNodes = ExpressionFactory.matchExp(WebNode.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list6);
		List<WebUrlAlias> webUrlAliases = ExpressionFactory.matchExp(WebUrlAlias.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list7);
		List<WebContent> webContents = ExpressionFactory.matchExp(WebContent.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list8);
		List<WebContentVisibility> webContentVisibilities = ExpressionFactory.matchExp(WebContentVisibility.WEB_CONTENT_PROPERTY, webContents).filterObjects(list9);
		List<WebMenu> menuList = ExpressionFactory.matchExp(WebMenu.WEB_SITE_VERSION_PROPERTY, versionToDelete).filterObjects(list0);

		assertEquals(0, webSiteLayouts.size());
		assertEquals(0, webTemplates.size());
		assertEquals(0, webNodeTypes.size());
		assertEquals(0, webNodes.size());
		assertEquals(0, webUrlAliases.size());
		assertEquals(0, webContents.size());
		assertEquals(0, webContentVisibilities.size());
		assertEquals(0, menuList.size());
	}
	
}

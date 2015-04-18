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
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
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

import static org.junit.Assert.*;

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
		WebSite siteToDelete = SelectById.query(WebSite.class, 1).selectOne(context);
		assertNotNull(siteToDelete);
		
		List<WebSiteVersion> allVersions = ObjectSelect.query(WebSiteVersion.class).
				where(WebSiteVersion.WEB_SITE.eq(siteToDelete)).
				select(context);
		assertEquals(12, allVersions.size());
		//number of revisions which younger than 60 days
		assertEquals(3, WebSiteVersion.DEPLOYED_ON.gt(DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet (always one)
		assertEquals(1, WebSiteVersion.DEPLOYED_ON.isNull().filterObjects(allVersions).size());

		webSiteVersionService.removeOldWebSiteVersions(siteToDelete);

		allVersions = ObjectSelect.query(WebSiteVersion.class).
				where(WebSiteVersion.WEB_SITE.eq(siteToDelete)).
				select(context);

		//the number of remaining
		assertEquals(5, allVersions.size());
		//including number of revisions younger than 60 days (currently deployed too)
		assertEquals(3, ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet 
		assertEquals(1, ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null).filterObjects(allVersions).size());
		//check that current version is not removed
		assertTrue(webSiteVersionService.getCurrentVersion().equals(currentVersion));


		//case 2 delete all revisions older than 60 days (number of revisions which younger than 60 days more than 5)
		siteToDelete = SelectById.query(WebSite.class, 2).selectOne(context);
		assertNotNull(siteToDelete);

		allVersions = ObjectSelect.query(WebSiteVersion.class).
				where(WebSiteVersion.WEB_SITE.eq(siteToDelete)).
				select(context);

		assertEquals(12, allVersions.size());
		//number of revisions which younger than 60 days
		assertEquals(7, WebSiteVersion.DEPLOYED_ON.gt(DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet (always one)
		assertEquals(1, WebSiteVersion.DEPLOYED_ON.isNull().filterObjects(allVersions).size());

		webSiteVersionService.removeOldWebSiteVersions(siteToDelete);

		allVersions = ObjectSelect.query(WebSiteVersion.class).
				where(WebSiteVersion.WEB_SITE.eq(siteToDelete)).
				select(context);

		//the number of remaining
		assertEquals(8, allVersions.size());
		//including number of revisions younger than 60 days (currently deployed too)
		assertEquals(7, WebSiteVersion.DEPLOYED_ON.gt(DateUtils.addDays(new Date(), -60)).filterObjects(allVersions).size());
		//unpublished revisions yet 
		assertEquals(1, WebSiteVersion.DEPLOYED_ON.isNull().filterObjects(allVersions).size());

	}


	@Test
	public void testDeleteWebSiteVersion() {
		ObjectContext context = cayenneService.newContext();

		WebSiteVersion versionToDelete = SelectById.query(WebSiteVersion.class, 3).selectOne(context);
		
		assertNotNull(versionToDelete);

		checkCountOfObjectBeforeMethod(context, versionToDelete);

		webSiteVersionService.delete(versionToDelete);

		checkCountOfObjectAfterMethod(context, versionToDelete);
	}

	private void checkCountOfObjectBeforeMethod(ObjectContext context, WebSiteVersion versionToDelete) {
		List<WebSite> list1 = ObjectSelect.query(WebSite.class).select(context);
		List<WebSiteVersion> list2 = ObjectSelect.query(WebSiteVersion.class).select(context);
		List<WebSiteLayout> list3 = ObjectSelect.query(WebSiteLayout.class).select(context);
		List<WebTemplate> list4 = ObjectSelect.query(WebTemplate.class).select(context);
		List<WebNodeType> list5 = ObjectSelect.query(WebNodeType.class).select(context);
		List<WebNode> list6 = ObjectSelect.query(WebNode.class).select(context);
		List<WebUrlAlias> list7 = ObjectSelect.query(WebUrlAlias.class).select(context);
		List<WebContent> list8 = ObjectSelect.query(WebContent.class).select(context);
		List<WebContentVisibility> list9 = ObjectSelect.query(WebContentVisibility.class).select(context);
		List<WebMenu> list0 = ObjectSelect.query(WebMenu.class).select(context);

		assertEquals(2, list1.size());
		assertEquals(24, list2.size());
		assertEquals(10, list3.size());
		assertEquals(34, list4.size());
		assertEquals(6, list5.size());
		assertEquals(19, list6.size());
		assertEquals(21, list7.size());
		assertEquals(7, list8.size());
		assertEquals(16, list9.size());
		assertEquals(16, list0.size());

		List<WebSiteLayout> webSiteLayouts = WebSiteLayout.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list3);
		List<WebTemplate> webTemplates = WebTemplate.LAYOUT.in(webSiteLayouts).filterObjects(list4);
		List<WebNodeType> webNodeTypes = WebNodeType.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list5);
		List<WebNode> webNodes = WebNode.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list6);
		List<WebUrlAlias> webUrlAliases = WebUrlAlias.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list7);
		List<WebContent> webContents = WebContent.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list8);
		List<WebContentVisibility> webContentVisibilities = WebContentVisibility.WEB_CONTENT.in(webContents).filterObjects(list9);
		List<WebMenu> menuList = WebMenu.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list0);

		assertEquals(4, webSiteLayouts.size());
		assertEquals(18, webTemplates.size());
		assertEquals(3, webNodeTypes.size());
		assertEquals(9, webNodes.size());
		assertEquals(5, webUrlAliases.size());
		assertEquals(2, webContents.size());
		assertEquals(7, webContentVisibilities.size());
		assertEquals(10, menuList.size());
	}

	private void checkCountOfObjectAfterMethod(ObjectContext context, WebSiteVersion versionToDelete) {
		List<WebSite> list1 = ObjectSelect.query(WebSite.class).select(context);
		List<WebSiteVersion> list2 = ObjectSelect.query(WebSiteVersion.class).select(context);
		List<WebSiteLayout> list3 = ObjectSelect.query(WebSiteLayout.class).select(context);
		List<WebTemplate> list4 = ObjectSelect.query(WebTemplate.class).select(context);
		List<WebNodeType> list5 = ObjectSelect.query(WebNodeType.class).select(context);
		List<WebNode> list6 = ObjectSelect.query(WebNode.class).select(context);
		List<WebUrlAlias> list7 = ObjectSelect.query(WebUrlAlias.class).select(context);
		List<WebContent> list8 = ObjectSelect.query(WebContent.class).select(context);
		List<WebContentVisibility> list9 = ObjectSelect.query(WebContentVisibility.class).select(context);
		List<WebMenu> list0 = ObjectSelect.query(WebMenu.class).select(context);

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

		List<WebSiteLayout> webSiteLayouts = WebSiteLayout.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list3);
		List<WebTemplate> webTemplates = WebTemplate.LAYOUT.in(webSiteLayouts).filterObjects(list4);
		List<WebNodeType> webNodeTypes = WebNodeType.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list5);
		List<WebNode> webNodes = WebNode.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list6);
		List<WebUrlAlias> webUrlAliases = WebUrlAlias.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list7);
		List<WebContent> webContents = WebContent.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list8);
		List<WebContentVisibility> webContentVisibilities = WebContentVisibility.WEB_CONTENT.in(webContents).filterObjects(list9);
		List<WebMenu> menuList = WebMenu.WEB_SITE_VERSION.eq(versionToDelete).filterObjects(list0);

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

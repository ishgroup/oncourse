package ish.oncourse.services.site;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DeleteVersionTest  extends ServiceTest {

	private ICayenneService cayenneService;
	private DeleteVersion deleteVersion;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/site/DeleteVersionTest.xml").load(testContext.getDS());
		this.cayenneService = getService(ICayenneService.class);
	}

	@Test
	public void test() {

		ObjectContext context = cayenneService.newNonReplicatingContext();
		WebSiteVersion version = SelectById.query(WebSiteVersion.class, 1).selectOne(context);
		deleteVersion = DeleteVersion.valueOf(version);

		testGetTableNameBy();

		List<WebSiteLayout> layouts = version.getLayouts();
		for (WebSiteLayout layout : layouts) {
			assertFalse(layout.getTemplates().isEmpty());
			deleteVersion.deleteTemplates(layout);
			layout = SelectById.query(WebSiteLayout.class, 1L).prefetch(WebSiteLayout.TEMPLATES.disjoint()).selectOne(context);
			assertTrue(layout.getTemplates().isEmpty());
		}

		assertFalse(version.getContents().isEmpty());
		deleteVersion.deleteAllContents();
		version = SelectById.query(WebSiteVersion.class, 1).prefetch(WebSiteVersion.CONTENTS.disjoint()).selectOne(context);
		assertTrue(version.getContents().isEmpty());

		assertFalse(version.getMenus().isEmpty());
		deleteVersion.deleteEntities(WebMenu.class);
		version = SelectById.query(WebSiteVersion.class, 1).prefetch(WebSiteVersion.MENUS.disjoint()).selectOne(context);
		assertTrue(version.getMenus().isEmpty());


		assertFalse(version.getWebURLAliases().isEmpty());
		assertFalse(version.getWebNodes().isEmpty());
		assertFalse(version.getWebNodeTypes().isEmpty());
		assertFalse(version.getLayouts().isEmpty());
		deleteVersion.deleteVersionRelatedObjects();
		version = SelectById.query(WebSiteVersion.class, 1)
				.prefetch(WebSiteVersion.WEB_URLALIASES.disjoint())
				.prefetch(WebSiteVersion.WEB_NODES.disjoint())
				.prefetch(WebSiteVersion.WEB_NODE_TYPES.disjoint())
				.prefetch(WebSiteVersion.LAYOUTS.disjoint())
				.selectOne(context);
		assertTrue(version.getWebURLAliases().isEmpty());
		assertTrue(version.getWebNodes().isEmpty());
		assertTrue(version.getWebNodeTypes().isEmpty());
		assertTrue(version.getLayouts().isEmpty());
	}

	private void testGetTableNameBy() {
		assertEquals("WebURLAlias", deleteVersion.getTableNameBy(WebUrlAlias.class));
	}
}

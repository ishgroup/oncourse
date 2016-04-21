package ish.oncourse.services.site;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.lifecycle.QueueableLifecycleListenerTest;
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
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteVersionTest  extends ServiceTest {

	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/site/DeleteVersionTest.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
	}

	@Test
	public void testDeleteTemplates() {

		ObjectContext context = cayenneService.newNonReplicatingContext();
		WebSiteVersion version = SelectById.query(WebSiteVersion.class, 1).selectOne(context);
		DeleteVersion deleteVersion = DeleteVersion.valueOf(version);

		List<WebSiteLayout> layouts = version.getLayouts();
		for (WebSiteLayout layout : layouts) {
			assertFalse(layout.getTemplates().isEmpty());
			deleteVersion.deleteTemplates(layout);
			layout = SelectById.query(WebSiteLayout.class, 1L).addPrefetch(WebSiteLayout.TEMPLATES.disjoint()).selectOne(context);
			assertTrue(layout.getTemplates().isEmpty());
		}

		assertFalse(version.getContents().isEmpty());
		deleteVersion.deleteAllContents();
		version = SelectById.query(WebSiteVersion.class, 1).addPrefetch(WebSiteVersion.CONTENTS.disjoint()).selectOne(context);
		assertTrue(version.getContents().isEmpty());

		assertFalse(version.getMenus().isEmpty());
		deleteVersion.deleteAllMenus();
		version = SelectById.query(WebSiteVersion.class, 1).addPrefetch(WebSiteVersion.MENUS.disjoint()).selectOne(context);
		assertTrue(version.getMenus().isEmpty());


		assertFalse(version.getWebURLAliases().isEmpty());
		assertFalse(version.getWebNodes().isEmpty());
		assertFalse(version.getWebNodeTypes().isEmpty());
		assertFalse(version.getLayouts().isEmpty());
		deleteVersion.deleteVersionRelatedObjects();
		version = SelectById.query(WebSiteVersion.class, 1)
				.addPrefetch(WebSiteVersion.WEB_URLALIASES.disjoint())
				.addPrefetch(WebSiteVersion.WEB_NODES.disjoint())
				.addPrefetch(WebSiteVersion.WEB_NODE_TYPES.disjoint())
				.addPrefetch(WebSiteVersion.LAYOUTS.disjoint())
				.selectOne(context);
		assertTrue(version.getWebURLAliases().isEmpty());
		assertTrue(version.getWebNodes().isEmpty());
		assertTrue(version.getWebNodeTypes().isEmpty());
		assertTrue(version.getLayouts().isEmpty());
	}
}

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;


import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/*Can not be tested properly because DeleteVersion.delete in WebSiteVersionDelete uses database cascade delete.*/
public class WebSiteVersionDeleteTest extends ServiceTest {

	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/site/webSiteVersionDeleteTestDataSet.xml").load(testContext.getDS());
		this.cayenneService = getService(ICayenneService.class);
	}

	@Ignore
	@Test
	public void testDeleteVersion() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		WebSiteVersion currentVersion = SelectById.query(WebSiteVersion.class, 1l).selectOne(context);
		WebSiteVersion deletingVersion = SelectById.query(WebSiteVersion.class, 2l).selectOne(context);
		WebSiteVersion deployedVersion = SelectById.query(WebSiteVersion.class, 3l).selectOne(context);
		WebSiteVersionDelete.valueOf(deletingVersion, currentVersion, deployedVersion, context).delete();

		assertEquals(PersistenceState.TRANSIENT, deletingVersion.getPersistenceState());

		WebSite site = Cayenne.objectForPK(context, WebSite.class, 1l);
		assertEquals(2, site.getVersions().size());

		assertNull(SelectById.query(WebSiteVersion.class,2l).selectOne(context));
		assertNull(SelectById.query(WebSiteLayout.class, 3l).selectOne(context));
		assertNull(SelectById.query(WebSiteLayout.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebTemplate.class, 3l).selectOne(context));
		assertNull(SelectById.query(WebTemplate.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebNodeType.class, 3l).selectOne(context));
		assertNull(SelectById.query(WebNodeType.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebNode.class, 3l).selectOne(context));
		assertNull(SelectById.query(WebNode.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebMenu.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebMenu.class, 5l).selectOne(context));
		assertNull(SelectById.query(WebMenu.class, 6l).selectOne(context));
		assertNull(SelectById.query(WebContent.class, 4l).selectOne(context));
		assertNull(SelectById.query(WebContent.class, 5l).selectOne(context));
		assertNull(SelectById.query(WebContent.class, 6l).selectOne(context));
		assertNull(SelectById.query(WebContentVisibility.class, 5l).selectOne(context));
		assertNull(SelectById.query(WebContentVisibility.class, 6l).selectOne(context));
		assertNull(SelectById.query(WebContentVisibility.class, 7l).selectOne(context));
		assertNull(SelectById.query(WebContentVisibility.class, 8l).selectOne(context));
		assertNull(SelectById.query(WebUrlAlias.class, 3l).selectOne(context));
		assertNull(SelectById.query(WebUrlAlias.class, 4l).selectOne(context));
	}

	@Ignore
	@Test
	public void testSite() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		WebSite site = Cayenne.objectForPK(context, WebSite.class, 1l);
		WebSiteDelete.valueOf(site, context).delete();

		assertEquals(PersistenceState.TRANSIENT, site.getPersistenceState());
		assertNull(Cayenne.objectForPK(context, WebSite.class, 1l));

		assertTrue(ObjectSelect.query(LicenseFee.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebHostName.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebSiteVersion.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebSiteLayout.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebTemplate.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebNodeType.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebNode.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebMenu.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebContent.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebContentVisibility.class).select(context).isEmpty());
		assertTrue(ObjectSelect.query(WebUrlAlias.class).select(context).isEmpty());

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 1l);
		assertNull(invoice.getWebSite());
	}
}

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.pages;

import edu.emory.mathcs.backport.java.util.Arrays;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.portal.service.TestModule;
import ish.oncourse.portal.services.MailingListHelper;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MailingListTest extends ServiceTest {

	
	private ObjectContext context;
	private Contact currentUser;
	private College college;
	private Set<Tag> tags;
	@Before
	public void setup() throws Exception {
		System.setProperty(TestUSIServiceEndpoint.USI_TEST_MODE, "true");

		initTest("ish.oncourse.portal", "portal", "src/main/resources/desktop/ish/oncourse/portal/pages", TestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/portal/pages/mailingListTestDataSet.xml").load(getDataSource("jdbc/oncourse"));

		ICayenneService cayenneService = getService(ICayenneService.class);
		context = cayenneService.sharedContext();
		currentUser = SelectById.query(Contact.class, 1l).selectOne(context);
		college = SelectById.query(College.class, 1l).selectOne(context);
		tags = new HashSet<>(ObjectSelect.query(Tag.class).where(Tag.NAME.eq("Name1")).select(context));
	}



	@Test
	public void testJustPressSave() {
	
		MailingListHelper.valueOf(getService(ITagService.class), Arrays.asList(new Long[] {2L}), tags, context, currentUser, college).saveSubscriptions();

		context.commitChanges();

		List<TaggableTag> tagRelations = ObjectSelect.query(TaggableTag.class).select(context);
		
		assertEquals(2, tagRelations.size());
		
		for (TaggableTag tagRelation : tagRelations) {
			assertEquals(2l, tagRelation.getTag().getId().longValue());
			assertEquals(1l, tagRelation.getTaggable().getEntityWillowId().longValue());
			assertEquals("Contact", tagRelation.getTaggable().getEntityIdentifier());
		}
	}


	@Test
	public void testUnsubscribe() {
		MailingListHelper.valueOf(getService(ITagService.class), new ArrayList<Long>(), tags, context, currentUser, college).saveSubscriptions();

		context.commitChanges();

		List<TaggableTag> tagRelations = ObjectSelect.query(TaggableTag.class).select(context);

		assertEquals(0, tagRelations.size());

	}

	@Test
	public void testSelectAll() {
		MailingListHelper.valueOf(getService(ITagService.class),  Arrays.asList(new Long[] {2L, 3L}), tags, context, currentUser, college).saveSubscriptions();

		context.commitChanges();

		List<TaggableTag> tagRelations = ObjectSelect.query(TaggableTag.class).select(context);

		assertEquals(3, tagRelations.size());

		tagRelations = ObjectSelect.query(TaggableTag.class).where(TaggableTag.TAG.dot(Tag.NAME).eq("Name1")).select(context);

		assertEquals(2, tagRelations.size());

		tagRelations = ObjectSelect.query(TaggableTag.class).where(TaggableTag.TAG.dot(Tag.NAME).eq("Name2")).select(context);
		assertEquals(1, tagRelations.size());

	}
}

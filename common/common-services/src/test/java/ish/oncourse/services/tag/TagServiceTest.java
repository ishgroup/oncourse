package ish.oncourse.services.tag;

import ish.common.types.NodeSpecialType;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tag;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest extends ServiceTest{

private static final String LEFT_SLASH_CHARACTER = "/";

private static final String UTF_8_ENCODING = "UTF-8";

private static final Logger logger = LogManager.getLogger();

	private static final String TAG_NAME = "tag";
	private static final String TAG_NAME_2 = "tag2";
	
	private static final String TAG_GROUP_NAME = "tag group";
	private static final String TAG_GROUP_NAME_2 = "tag group2";
	
	
	private ITagService tagService;
    private College college;

	@Mock
	private IWebSiteService webSiteService;

	private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);
        DataSource dataSource = getDataSource("jdbc/oncourse");

        this.cayenneService = getService(ICayenneService.class);
        ObjectContext context = this.cayenneService.newNonReplicatingContext();
		college = context.newObject(College.class);
		college.setName("testCollege");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);
		final Tag subjects = context.newObject(Tag.class);
		subjects.setName(Tag.SUBJECTS_TAG_NAME);
		subjects.setCollege(college);
		subjects.setIsTagGroup(true);
		subjects.setIsWebVisible(true);
		
		final Tag tagGroup = context.newObject(Tag.class);
		tagGroup.setName(TAG_GROUP_NAME);
		tagGroup.setCollege(college);
		tagGroup.setIsTagGroup(true);
		tagGroup.setIsWebVisible(true);
		tagGroup.setParent(subjects);
		
		final Tag alternativeTagGroup = context.newObject(Tag.class);
		alternativeTagGroup.setName(TAG_GROUP_NAME);
		alternativeTagGroup.setCollege(college);
		alternativeTagGroup.setIsTagGroup(true);
		alternativeTagGroup.setIsWebVisible(true);
		
		final Tag alternativeTagGroup2 = context.newObject(Tag.class);
		alternativeTagGroup2.setName(TAG_GROUP_NAME_2);
		alternativeTagGroup2.setCollege(college);
		alternativeTagGroup2.setIsTagGroup(true);
		alternativeTagGroup2.setIsWebVisible(true);
		
		final Tag tag = context.newObject(Tag.class);
		tag.setName(TAG_NAME);
		tag.setCollege(college);
		tag.setIsTagGroup(false);
		tag.setIsWebVisible(true);
		tag.setParent(tagGroup);
		
		final Tag alternativeTagLeaf = context.newObject(Tag.class);
		alternativeTagLeaf.setName(TAG_NAME);
		alternativeTagLeaf.setCollege(college);
		alternativeTagLeaf.setIsTagGroup(false);
		alternativeTagLeaf.setIsWebVisible(true);
		alternativeTagLeaf.setParent(alternativeTagGroup);
		
		final Tag alternativeTagLeaf2 = context.newObject(Tag.class);
		alternativeTagLeaf2.setName(TAG_NAME_2);
		alternativeTagLeaf2.setCollege(college);
		alternativeTagLeaf2.setIsTagGroup(false);
		alternativeTagLeaf2.setIsWebVisible(true);
		alternativeTagLeaf2.setParent(alternativeTagGroup2);

		context.commitChanges();

        tagService = new TagService(cayenneService, webSiteService);
        when(webSiteService.getCurrentCollege()).thenReturn(college);
    }


	@Test
	public void getSubjectsTagTest() {
		logger.info("check getSubjectsTag() method");
		Tag tag = tagService.getSubjectsTag();
		assertNotNull(tag);
		assertEquals(Tag.SUBJECTS_TAG_NAME, tag.getName());
		assertNull(tag.getParent());
	}

	/**
	 * Test scenario:
	 * <ol><li>find tag by path "Subjects/tag+group/tag";</li>
	 * <li>find tag by path "tag group/tag";</li>
	 * <li>find tag by path "tag group";</li>
	 * </ol>
	 * These should return requsted tag.
	 * 
	 * Then it finds tag just by name: "tag", but should return null, as the full path from the Subjects or the closest tagGroup reqired.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void getTagByFullPathTest() throws UnsupportedEncodingException {
		logger.info("check getTagByFullPath() method");
		//try to find by encoded and not encoded names
		Tag tag = tagService.getTagByFullPath(Tag.SUBJECTS_TAG_NAME + LEFT_SLASH_CHARACTER + URLEncoder.encode(TAG_GROUP_NAME, UTF_8_ENCODING) + LEFT_SLASH_CHARACTER + 
			TAG_NAME);
		assertNotNull(tag);
		assertEquals(TAG_NAME, tag.getName());
		assertNotNull(tag.getParent());
		assertNotNull(tag.getParent().getParent());
		assertNull(tag.getParent().getParent().getParent());//here we should receive Subjects null parent
		tag = tagService.getTagByFullPath(TAG_GROUP_NAME + LEFT_SLASH_CHARACTER + TAG_NAME);
		assertNotNull(tag);
		assertEquals(TAG_NAME, tag.getName());
		assertNotNull(tag.getParent());
		assertNotNull(tag.getParent().getParent());//here we should receive Subjects as parent because alternativeTagGroup should be hidden
		assertNull(tag.getParent().getParent().getParent());//here we should receive Subjects null parent 
		tag = tagService.getTagByFullPath(TAG_GROUP_NAME);
		assertNotNull(tag);
		assertEquals(TAG_GROUP_NAME, tag.getName());
		assertNotNull(tag.getParent());//here we should receive Subjects as parent because alternativeTagGroup should be hidden
		assertNull(tag.getParent().getParent());//here we should receive Subjects null parent 
		tag = tagService.getTagByFullPath(TAG_NAME);
		assertNull(tag);
		//and now check not subjects tags
		tag = tagService.getTagByFullPath(URLEncoder.encode(TAG_GROUP_NAME_2, UTF_8_ENCODING) + LEFT_SLASH_CHARACTER + TAG_NAME_2);
		assertNotNull(tag);
		assertEquals(TAG_NAME_2, tag.getName());
		assertNotNull(tag.getParent());
		assertNull(tag.getParent().getParent());
		tag = tagService.getTagByFullPath(TAG_GROUP_NAME_2);
		assertNotNull(tag);
		assertEquals(TAG_GROUP_NAME_2, tag.getName());
		assertNull(tag.getParent());
		tag = tagService.getTagByFullPath(TAG_NAME_2);
		assertNull(tag);
	}
	
	@Test
	public void testMailingListSubscribeUnsubscribe() {

        ObjectContext context = cayenneService.newNonReplicatingContext();
		final Student student = context.newObject(Student.class);
        College college = context.localObject(this.college);
		student.setCollege(college);
		
		final Contact contact = context.newObject(Contact.class);
		contact.setCollege(college);
		contact.setStudent(student);
		
		final Tag mailingLists = context.newObject(Tag.class);
		mailingLists.setCollege(college);
		mailingLists.setName("Mailing Lists");
		mailingLists.setSpecialType(NodeSpecialType.MAILING_LISTS);
		mailingLists.setIsTagGroup(true);
		mailingLists.setIsWebVisible(true);
		
		final Tag list = context.newObject(Tag.class);
		list.setCollege(college);
		list.setName("List1");
		list.setParent(mailingLists);
		list.setIsWebVisible(true);
		
		context.commitChanges();
		
		assertTrue(tagService.getMailingListsContactSubscribed(contact).isEmpty());
		
		tagService.subscribeContactToMailingList(contact, list);
		
		assertFalse(tagService.getMailingListsContactSubscribed(contact).isEmpty());
		assertEquals("List1", tagService.getMailingListsContactSubscribed(contact).get(0).getName());
		
		tagService.unsubscribeContactFromMailingList(contact, list);
		
		assertTrue(tagService.getMailingListsContactSubscribed(contact).isEmpty());		
	}

}

package ish.oncourse.services.tag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ContextUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {

private static final Logger LOGGER = Logger.getLogger(TagServiceTest.class);

	private static final String TAG_NAME = "tag";
	
	private static final String TAG_GROUP_NAME = "tag group";
	
	private ITagService tagService;
	
	private static ObjectContext context;
	
	private static College college;

	@Mock
	private IWebSiteService webSiteService;
	
	@Mock
	private ICayenneService cayenneService;

	@BeforeClass
	public static void initTest() throws Exception {
		LOGGER.info("init data for TagServiceTest");
		ContextUtils.setupDataSources();
		context = ContextUtils.createObjectContext();
		college = context.newObject(College.class);
		college.setName("testCollege");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		Tag subjects = context.newObject(Tag.class);
		subjects.setName(Tag.SUBJECTS_TAG_NAME);
		subjects.setCollege(college);
		subjects.setIsTagGroup(true);
		subjects.setIsWebVisible(true);

		Tag tagGroup = context.newObject(Tag.class);
		tagGroup.setName(TAG_GROUP_NAME);
		tagGroup.setCollege(college);
		tagGroup.setIsTagGroup(true);
		tagGroup.setIsWebVisible(true);
		tagGroup.setParent(subjects);

		Tag tag = context.newObject(Tag.class);
		tag.setName(TAG_NAME);
		tag.setCollege(college);
		tag.setIsTagGroup(false);
		tag.setIsWebVisible(true);
		tag.setParent(tagGroup);

		context.commitChanges();
	}

	@Before
	public void init() throws Exception {
		tagService = new TagService(cayenneService, webSiteService);
		when(cayenneService.sharedContext()).thenReturn(context);
		when(webSiteService.getCurrentCollege()).thenReturn(college);
	}

	@Test
	public void getSubjectsTagTest() {
		LOGGER.info("check getSubjectsTag() method");
		Tag tag = tagService.getSubjectsTag();
		assertNotNull(tag);
		assertEquals(Tag.SUBJECTS_TAG_NAME, tag.getName());
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
		LOGGER.info("check getTagByFullPath() method");
		//try to find by encoded and not encoded names
		String encodedTagName = URLEncoder.encode(TAG_GROUP_NAME, "UTF-8");
		Tag tag = tagService.getTagByFullPath(Tag.SUBJECTS_TAG_NAME + "/" + encodedTagName + "/" + TAG_NAME);
		assertNotNull(tag);
		assertEquals(TAG_NAME, tag.getName());
		tag = tagService.getTagByFullPath(TAG_GROUP_NAME + "/" + TAG_NAME);
		assertNotNull(tag);
		assertEquals(TAG_NAME, tag.getName());
		tag = tagService.getTagByFullPath(TAG_GROUP_NAME);
		assertNotNull(tag);
		assertEquals(TAG_GROUP_NAME, tag.getName());
		tag = tagService.getTagByFullPath(TAG_NAME);
		assertNull(tag);
	}
}

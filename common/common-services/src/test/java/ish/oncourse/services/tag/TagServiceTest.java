package ish.oncourse.services.tag;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
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
	public void testHasTag() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		Student student = context.newObject(Student.class);
		College college = context.localObject(this.college);
		student.setCollege(college);

		Contact contact = context.newObject(Contact.class);
		contact.setCollege(college);
		contact.setStudent(student);
		contact.setFamilyName("James");
		contact.setGivenName("Bond");

		Course course = context.newObject(Course.class);
		course.setCollege(college);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);

		CourseClass courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setIsActive(true);
		courseClass.setCancelled(false);
		courseClass.setMaximumPlaces(3);
		courseClass.setIsDistantLearningCourse(false);

		context.commitChanges();

		Tag contactTag = context.newObject(Tag.class);
		contactTag.setCollege(college);
		contactTag.setName("Contact Tag");
		contactTag.setIsTagGroup(true);
		contactTag.setIsWebVisible(true);

		Tag childTag = context.newObject(Tag.class);
		childTag.setCollege(college);
		childTag.setName("more than 1 year");
		childTag.setParent(contactTag);
		childTag.setIsWebVisible(true);

		Taggable taggable = context.newObject(Taggable.class);
		taggable.setCollege(college);
		taggable.setEntityIdentifier("Contact");
		taggable.setEntityWillowId(contact.getId());

		TaggableTag taggableTag = context.newObject(TaggableTag.class);
		taggableTag.setCollege(college);
		taggableTag.setTag(childTag);
		taggableTag.setTaggable(taggable);

		context.commitChanges();

		assertTrue(tagService.hasTag(contact, childTag.getDefaultPath()));
		assertFalse(tagService.hasTag(courseClass, childTag.getDefaultPath()));


		Tag classTag = context.newObject(Tag.class);
		classTag.setCollege(college);
		classTag.setName("Class Tag");
		classTag.setIsTagGroup(true);
		classTag.setIsWebVisible(true);

		Tag classChildTag = context.newObject(Tag.class);
		classChildTag.setCollege(college);
		classChildTag.setName("science");
		classChildTag.setParent(classTag);
		classChildTag.setIsWebVisible(true);

		Taggable taggable1 = context.newObject(Taggable.class);
		taggable1.setCollege(college);
		taggable1.setEntityIdentifier("CourseClass");
		taggable1.setEntityWillowId(courseClass.getId());

		TaggableTag taggableTag1 = context.newObject(TaggableTag.class);
		taggableTag1.setCollege(college);
		taggableTag1.setTag(classChildTag);
		taggableTag1.setTaggable(taggable1);

		context.commitChanges();

		assertTrue(tagService.hasTag(courseClass, classChildTag.getDefaultPath()));

	}


}

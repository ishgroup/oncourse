package ish.oncourse.model;

import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TagTest {
	public static final String TEST_TAG_NAME = "test\\tag";

	public static final String SECOND_TAG_NAME = "second/tag";

	public static final String FIRST_TAG_NAME = "first tag with +";

	public static final String ROOT_TAG_NAME = "Root & root";

	/**
	 * Data context for persistent objects.
	 */
	private static ObjectContext context;

	private static Tag testTag;

	public static final String ENCODED_PATH_FOR_TEST_TAG = "/Root+%26+root/first+tag+with+%2B/second%2Ftag/test%5Ctag";

	/**
	 * Initializes entities, commit needed changes.
	 * 
	 * Creates the tag with path /{@value #ROOT_TAG_NAME}/{@value #FIRST_TAG_NAME}/{@value #SECOND_TAG_NAME}/{@value #TEST_TAG_NAME}.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		
		ContextUtils.setupDataSources();

		context = ContextUtils.createObjectContext();

		College college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);

		Tag rootTag = context.newObject(Tag.class);
		rootTag.setCollege(college);
		rootTag.setName(ROOT_TAG_NAME);

		Tag firstTag = context.newObject(Tag.class);
		firstTag.setCollege(college);
		firstTag.setName(FIRST_TAG_NAME);
		firstTag.setParent(rootTag);

		Tag secondTag = context.newObject(Tag.class);
		secondTag.setCollege(college);
		secondTag.setName(SECOND_TAG_NAME);
		secondTag.setParent(firstTag);

		testTag = context.newObject(Tag.class);
		testTag.setCollege(college);
		testTag.setName(TEST_TAG_NAME);
		testTag.setParent(secondTag);

		context.commitChanges();

	}

	/**
	 * Test for the {@link Tag#getDefaultPath()} method.
	 * 
	 * Emulates the situation when tag has the path /{@value #ROOT_TAG_NAME}/
	 * {@value #FIRST_TAG_NAME}/{@value #SECOND_TAG_NAME}/
	 * {@value #TEST_TAG_NAME} which when encoded should be transformed into
	 * {@value #ENCODED_PATH_FOR_TEST_TAG}.
	 */
	@Test
	public void getDefaultPathTest() {
		String[] namesInPath = { ROOT_TAG_NAME, FIRST_TAG_NAME, SECOND_TAG_NAME, TEST_TAG_NAME };
		Tag testing = testTag;
		for (int i = namesInPath.length - 1; i >= 0; i--) {
			assertEquals(namesInPath[i], testing.getName());
			testing = testing.getParent();
		}
		assertEquals(ENCODED_PATH_FOR_TEST_TAG, testTag.getDefaultPath());
	}
	/**
	 * Test for the  {@link Tag#getLink(String)} and {@link Tag#getLink()}. Emulates the situation when parameter for the first method(the entity name) is "Course". 
	 * So, both methods should return /courses{@value #ENCODED_PATH_FOR_TEST_TAG} 
	 */
	@Test
	public void getLinkTest() {
		String entityType = Course.class.getSimpleName();
		assertEquals("/courses" + ENCODED_PATH_FOR_TEST_TAG, testTag.getLink(entityType));
		assertEquals("/courses" + ENCODED_PATH_FOR_TEST_TAG, testTag.getLink());
	}
}

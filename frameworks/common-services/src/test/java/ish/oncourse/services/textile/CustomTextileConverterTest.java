package ish.oncourse.services.textile;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomTextileConverterTest {

	private static final String TAGS = "{tags}";

	private static final String PAGE = "{page}";

	private static final String COURSE = "{course}";

	private static final String TEST_WEB_BLOCK_CONTENT = "web block content";

	private static final int TEST_BINARYINFO_REFERENCE_NUMBER = 123;

	private static final String IMAGE_BY_REF_NUMBER = "{image id:\""
			+ TEST_BINARYINFO_REFERENCE_NUMBER + "\"}";

	private static final String TEST_BLOCK_NAME = "blockName";

	private static final String BLOCK_BY_NAME = "{block name:\""
			+ TEST_BLOCK_NAME + "\"}";

	private ValidationErrors errors;

	@Mock
	private IBinaryDataService binaryDataService;

	@Mock
	private IWebContentService webContentService;

	@Mock
	private ICourseService courseService;

	@Mock
	private IPageRenderer pageRenderer;

	@Mock
	private IWebNodeService webNodeService;
	
	@Mock
	private ITagService tagService;

	private BinaryInfo binaryInfo;

	private ITextileConverter textileConverter;

	private BinaryData binaryData;

	private WebContent webContent;

	private Course course;

	private WebNode page;

	private Tag tag;

	@Before
	public void init() {
		errors = new ValidationErrors();
		textileConverter = new TextileConverter(binaryDataService,
				webContentService, courseService, pageRenderer, webNodeService,
				tagService);
	}

	@After
	public void clear() {
		errors.clear();
	}

	/**
	 * Emulates the situation when {image id:"123"} is converted, the binary
	 * info with reference number 123 exists. Should pass without errors.
	 */
	@Test
	public void smokeImageConvertTest() {
		binaryData = new BinaryData();
		binaryInfo = new BinaryInfo();
		binaryInfo.setReferenceNumber(TEST_BINARYINFO_REFERENCE_NUMBER);
		when(
				binaryDataService.getBinaryInfo(
						BinaryInfo.REFERENCE_NUMBER_PROPERTY,
						TEST_BINARYINFO_REFERENCE_NUMBER)).thenReturn(
				binaryInfo);
		when(binaryDataService.getBinaryData(binaryInfo))
				.thenReturn(binaryData);

		String result = textileConverter.convertCustomTextile(
				IMAGE_BY_REF_NUMBER, errors);
		assertEquals("<img src=\"/asset/binarydata?id=123\" />", result);
		assertFalse(errors.hasFailures());

	}

	/**
	 * Emulates the situation when {block name:"blockName"} is converted, the
	 * webContent with name "blockName" exists. Should pass without errors.
	 */
	@Test
	public void smokeBlockConvertTest() {
		webContent = new WebContent();
		webContent.setContent(TEST_WEB_BLOCK_CONTENT);
		when(
				webContentService.getWebContent(WebContent.NAME_PROPERTY,
						TEST_BLOCK_NAME)).thenReturn(webContent);

		String result = textileConverter.convertCustomTextile(BLOCK_BY_NAME,
				errors);
		assertEquals(TEST_WEB_BLOCK_CONTENT, result);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when {video id:"youtubeId" type:"youtube"} is
	 * converted. Should pass without errors.
	 */
	@Test
	public void smokeVideoConvertTest() {
		String result = textileConverter.convertCustomTextile(
				"{video id:\"youtubeId\" type:\"youtube\"}", errors);
		assertEquals(
				"<object width=\"425\" height=\"344\">"
						+ "<param name=\"movie\" value=\"http://www.youtube.com/v/youtubeId\"></param>"
						+ "<param name=\"allowFullScreen\" value=\"true\"></param>"
						+ "<param name=\"allowscriptaccess\" value=\"always\"></param> "
						+ "<embed type=\"application/x-shockwave-flash\" src=\"http://www.youtube.com/v/youtubeId\" "
						+ "allowscriptaccess=\"always\" allowfullscreen=\"true\"  width=\"425\" height=\"344\">"
						+ "</embed></object>", result);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when {course} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeCourseConvertTest() {
		course = new Course();
		String successfulResult = "successful rendered course block";
		when(courseService.getCourse(null, null)).thenReturn(course);
		String pageName = "ui/TextileCourse";
		testPageRenderParams(COURSE, pageName, successfulResult);
	}

	/**
	 * Emulates the situation when {page} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokePageConvertTest() {

		page = new WebNode();
		String successfulResult = "successful rendered page block";
		when(webNodeService.getNode(null, null)).thenReturn(page);
		String pageName = "ui/TextilePage";
		testPageRenderParams(PAGE, pageName, successfulResult);

	}

	/**
	 * Emulates the situation when {tags} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeTagsConvertTest() {
		tag = new Tag();
		String successfulResult = "successful rendered tags block";
		when(tagService.getRootTag()).thenReturn(tag);
		String pageName = "ui/TextileTags";
		testPageRenderParams(TAGS, pageName, successfulResult);

	}

	/**
	 * @param successfulResult
	 * @param pageName
	 * @param textile
	 */
	private void testPageRenderParams(String textile, String pageName,
			String successfulResult) {
		when(pageRenderer.renderPage(eq(pageName), anyMap())).thenReturn(
				successfulResult);
		String result = textileConverter.convertCustomTextile(textile, errors);
		assertEquals(successfulResult, result);
		assertFalse(errors.hasFailures());
	}

}

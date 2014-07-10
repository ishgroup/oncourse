package ish.oncourse.services.textile;

import ish.oncourse.model.*;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.course.Sort;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomTextileConverterTest {

	private static final String VIDEO = "{video id:\"youtubeId\" type:\"youtube\"}";

	private static final String TAGS = "{tags}";

	private static final String PAGE = "{page}";

	private static final String COURSE = "{course}";

	private static final String COURSE_LIST = "{courses}";

	private static final String FORM = "{form name:\"formname\" email:\"someemail@t.com\"}\n\t{text label:\"label\"}\n{form}";

	private static final String TEST_WEB_BLOCK_CONTENT = "web block content";

	private static final String TEST_BINARYINFO_NAME = "123";

	private static final String IMAGE_BY_REF_NUMBER = "{image name:\"" + TEST_BINARYINFO_NAME + "\"}";

	private static final String TEST_BLOCK_NAME = "blockName";

	private static final String BLOCK_BY_NAME = "{block name:\"" + TEST_BLOCK_NAME + "\"}";

	private static final String COMPLEX_WEB_BLOCK_CONTENT = IMAGE_BY_REF_NUMBER;
	
	private static final String TEST_ATTACHMENT_NAME = "attachmentName";
	
	private static final String ATTACHMENT_BY_NAME = "{attachment name:\"" + TEST_ATTACHMENT_NAME + "\"}";

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

	@Mock
	private DocumentVersion documentVersion;
	
	@Mock
	private Document document;

    @Mock
    private IFileStorageAssetService fileStorageAssetService;

	private ITextileConverter textileConverter;


	private WebContent webContent;

	private Course course;

	private WebNode page;

	private Tag tag;

	@Before
	public void init() {
		errors = new ValidationErrors();

		when(tagService.getSubjectsTag()).thenReturn(mock(Tag.class));
		textileConverter = new TextileConverter(binaryDataService, webContentService, courseService, pageRenderer,
				webNodeService, tagService,fileStorageAssetService);
	}

	/**
	 * Emulates the situation when {attachment name:"attachmentName"} is converted, 
	 * the binary info with name "attachmentName" exists. Should pass without errors.
	 */
	@Test
	public void smokeAttachmentConvertTest() {
		when(document.getName()).thenReturn(TEST_ATTACHMENT_NAME);
		when(binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, TEST_ATTACHMENT_NAME)).thenReturn(document);
        when(fileStorageAssetService.contains(documentVersion)).thenReturn(Boolean.TRUE);
        String successfulResult = "successfully rendered attachment block";
		testPageRenderParams(ATTACHMENT_BY_NAME, TextileUtil.TEXTILE_ATTACHMENT_PAGE, successfulResult);
	}

	/**
	 * Emulates the situation when {block name:"blockName"} is converted, the
	 * webContent with name "blockName" exists. Should pass without errors.
	 */
	@Test
	public void smokeBlockConvertTest() {
		webContent = new WebContent();
		webContent.setContent(TEST_WEB_BLOCK_CONTENT);
		when(webContentService.getWebContent(WebContent.NAME_PROPERTY, TEST_BLOCK_NAME)).thenReturn(webContent);

		String result = textileConverter.convertCustomTextile(BLOCK_BY_NAME, errors);
		assertEquals(TEST_WEB_BLOCK_CONTENT, result);
		assertFalse(errors.hasFailures());
	}

	/**
	 * when some block's content contains the another custom textile it will be
	 * converted as well
	 */
	@Test
	public void recursiveBlockConvertTest() {
		webContent = new WebContent();
		webContent.setContent(COMPLEX_WEB_BLOCK_CONTENT);
		when(webContentService.getWebContent(WebContent.NAME_PROPERTY, TEST_BLOCK_NAME)).thenReturn(webContent);
		reset(binaryDataService);
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, TEST_BINARYINFO_NAME)).thenReturn(document);
        when(fileStorageAssetService.contains(documentVersion)).thenReturn(Boolean.TRUE);

		String successfulResult = "successfully rendered image block";

		when(pageRenderer.renderPage(eq(TextileUtil.TEXTILE_IMAGE_PAGE), anyMap())).thenReturn(successfulResult);

		String result = textileConverter.convertCustomTextile(BLOCK_BY_NAME, errors);

		assertEquals(successfulResult, result);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when {video id:"youtubeId" type:"youtube"} is
	 * converted. Should pass without errors.
	 */
	@Test
	public void smokeVideoConvertTest() {
		String successfulResult = "successfully rendered video block";
        testPageRenderParams(VIDEO, TextileUtil.TEXTILE_VIDEO_PAGE, successfulResult);
	}

	/**
	 * Emulates the situation when {course} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeCourseConvertTest() {
		course = new Course();
		String successfulResult = "successfully rendered course block";
		when(courseService.getCourse(null)).thenReturn(course);
		testPageRenderParams(COURSE, TextileUtil.TEXTILE_COURSE_PAGE, successfulResult);
	}

	/**
	 * Emulates the situation when {courses} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeCourseListConvertTest() {
		List<Course> courses = new ArrayList<>();
		courses.add(new Course());
		String successfulResult = "successfully rendered courses block";
		when(courseService.getCourses(null, Sort.alphabetical, true, null)).thenReturn(courses);
		testPageRenderParams(COURSE_LIST, TextileUtil.TEXTILE_COURSE_LIST_PAGE, successfulResult);
	}

	/**
	 * Emulates the situation when {page} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokePageConvertTest() {
		page = new WebNode();
		String successfulResult = "successfully rendered page block";

		when(webNodeService.getNodeForNodeNumber(anyInt())).thenReturn(page);
		when(webNodeService.getRandomNode()).thenReturn(page);

		testPageRenderParams(PAGE, TextileUtil.TEXTILE_PAGE_PAGE, successfulResult);

	}

	/**
	 * Emulates the situation when {tags} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeTagsConvertTest() {
		tag = new Tag();
		String successfulResult = "successfully rendered tags block";
		when(tagService.getSubjectsTag()).thenReturn(tag);
		testPageRenderParams(TAGS, TextileUtil.TEXTILE_TAGS_PAGE, successfulResult);

	}

	/**
	 * Emulates the situation when {form} is converted. Should pass without
	 * errors.
	 */
	@Test
	public void smokeFormConvertTest() {
		String successfulResult = "successfully rendered form block";
		testPageRenderParams(FORM, TextileUtil.TEXTILE_FORM_PAGE, successfulResult);
	}

	/**
	 * @param successfulResult
	 * @param pageName
	 * @param textile
	 */
	private void testPageRenderParams(String textile, String pageName, String successfulResult) {

		when(pageRenderer.renderPage(eq(pageName), anyMap())).thenReturn(successfulResult);

		String result = textileConverter.convertCustomTextile(textile, errors);

		assertEquals(successfulResult, result);
		assertFalse(errors.hasFailures());
	}

	/**
	 * Emulates the situation when all the textiles in string have the incorrect
	 * format. Because of errors, the origin string stays unconverted
	 */
	@Test
	public void errorsInTextilesConvertTest() {
		String[] tags = new String[]{"{block test}","{course test}","{courses test}","{image test}","{page test}","{tags test}","{video test}"};
		for (String tag : tags) {
			String result = textileConverter.convertCustomTextile(tag, errors);
			assertTrue(errors.hasFailures());
			String error = TextileUtil.getReplacementForSyntaxErrorTag(tag,errors);
			assertEquals(error, result);
			errors.clear();
		}
		//TODO uncomment when the validation of {form} is needed, now we just pass all the text
		/*tag = "{form}{form}{form}{form}{form}";
		result = textileConverter.convertCustomTextile(tag, errors);
		expecting = new StringBuffer();
		// first it looks at pairs of forms
		expecting.append(TextileUtil.getReplacementForSyntaxErrorTag("{form}{form}"))
				.append(TextileUtil.getReplacementForSyntaxErrorTag("{form}{form}"))
				.append(TextileUtil.getReplacementForSyntaxErrorTag("{form}"));

		assertEquals(expecting.toString(), result);
		assertTrue(errors.hasFailures());

		errors.clear();
		tag = "{form}{text label:\"label\"}";
		result = textileConverter.convertCustomTextile(tag, errors);
		assertEquals(TextileUtil.getReplacementForSyntaxErrorTag(tag), result);
		assertTrue(errors.hasFailures());*/
	}

}

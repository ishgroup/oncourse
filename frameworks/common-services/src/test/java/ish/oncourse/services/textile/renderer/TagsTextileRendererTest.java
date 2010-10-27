package ish.oncourse.services.textile.renderer;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TagsTextileRendererTest {

	private static final String TEST_TAG_NAME = "tagName";

	private static final String SUCCESSFULLY_RENDERED = "success";
	/**
	 * service under the test
	 */
	private TagsTextileRenderer tagsTextileRenderer;

	private ValidationErrors errors;

	@Mock
	private ITagService tagService;

	@Mock
	private IPageRenderer pageRenderer;

	private Tag rootTag;

	private Tag tag;

	@Before
	public void init() {
		errors = new ValidationErrors();
		rootTag = new Tag();
		tag = new Tag();
		tag.setName(TEST_TAG_NAME);
		tagsTextileRenderer = new TagsTextileRenderer(tagService, pageRenderer);
		when(tagService.getRootTag()).thenReturn(rootTag);
		when(tagService.getSubTagByName(TEST_TAG_NAME)).thenReturn(tag);
		when(
				pageRenderer.renderPage(eq(TextileUtil.TEXTILE_TAGS_PAGE),
						anyMap())).thenReturn(SUCCESSFULLY_RENDERED);
	}

	/**
	 * Emulates rendering of tags with the root that has given tag name, ie
	 * {tags name:"tagName"}
	 */
	@Test
	public void renderPageWithNodeNumberTest() {
		String tag = "{tags name:\"" + TEST_TAG_NAME + "\"}";
		String result = tagsTextileRenderer.render(tag, errors);
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
}

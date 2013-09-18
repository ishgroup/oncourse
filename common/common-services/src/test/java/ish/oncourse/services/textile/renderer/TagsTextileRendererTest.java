package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.IPageRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TagsTextileRendererTest {

	private static final String TEST_TAG_NAME = "tagName";

	private static final String SUCCESSFULLY_RENDERED = "success";
	/**
	 * service under the test
	 */
	private TagsTextileRenderer renderer;

	@Mock
	private ITagService tagService;

	@Mock
	private IPageRenderer pageRenderer;

	private Tag rootTag;

	private Tag tag;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		rootTag = new Tag();
		tag = new Tag();
		tag.setName(TEST_TAG_NAME);
		renderer = new TagsTextileRenderer(tagService, pageRenderer, null);
		when(tagService.getSubjectsTag()).thenReturn(rootTag);
		when(tagService.getTagByFullPath(TEST_TAG_NAME)).thenReturn(tag);
		when(pageRenderer.renderPage(eq(TextileUtil.TEXTILE_TAGS_PAGE), anyMap())).thenReturn(
				SUCCESSFULLY_RENDERED);
	}

	/**
	 * Emulates rendering of tags with the root that has given tag name, ie
	 * {tags name:"tagName"}
	 */
	@Test
	public void renderPageWithNodeNumberTest() {
		String tag = "{tags name:\"" + TEST_TAG_NAME + "\"}";
		String result = renderer.render(tag);
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
}
